//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003 - 2005  Lars Kühne
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//////////////////////////////////////////////////////////////////////////////

package net.sf.clirr.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.CoIterator;
import net.sf.clirr.core.internal.NameComparator;
import net.sf.clirr.core.internal.checks.ClassHierarchyCheck;
import net.sf.clirr.core.internal.checks.ClassModifierCheck;
import net.sf.clirr.core.internal.checks.ClassScopeCheck;
import net.sf.clirr.core.internal.checks.FieldSetCheck;
import net.sf.clirr.core.internal.checks.GenderChangeCheck;
import net.sf.clirr.core.internal.checks.InterfaceSetCheck;
import net.sf.clirr.core.internal.checks.MethodSetCheck;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Scope;

/**
 * This is the main class to be used by Clirr frontends,
 * it implements the checking functionality of Clirr.
 * Frontends can create an instance of this class
 * and register themselves as DiffListeners, they are then
 * informed whenever an API change is detected by the
 * reportDiffs method.
 *
 * @author lkuehne
 */
public final class Checker implements ApiDiffDispatcher
{
    private static final Message MSG_CLASS_ADDED = new Message(8000);
    private static final Message MSG_CLASS_REMOVED = new Message(8001);

    private List listeners = new ArrayList();

    private List classChecks = new ArrayList();

    private ScopeSelector scopeSelector = new ScopeSelector();

    /**
     * Package visible constructor for unit testing.
     */
    Checker(ClassChangeCheck ccc)
    {
        if (ccc != null)
        {
            classChecks.add(ccc);
        }
    }

    /**
     * Creates a new Checker.
     */
    public Checker()
    {
        classChecks.add(new ClassScopeCheck(this, scopeSelector));
        classChecks.add(new GenderChangeCheck(this));
        classChecks.add(new ClassModifierCheck(this));
        classChecks.add(new InterfaceSetCheck(this));
        classChecks.add(new ClassHierarchyCheck(this));
        classChecks.add(new FieldSetCheck(this, scopeSelector));
        classChecks.add(new MethodSetCheck(this, scopeSelector));
    }

    public ScopeSelector getScopeSelector()
    {
        return scopeSelector;
    }

    public void addDiffListener(DiffListener listener)
    {
        listeners.add(listener);
    }

    private void fireStart()
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.start();
        }
    }

    private void fireStop()
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.stop();
        }
    }

    public void fireDiff(ApiDifference diff)
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.reportDiff(diff);
        }
    }

    /**
     * Checks two sets of classes for api changes and reports
     * them to the DiffListeners.
     * @param compatibilityBaseline the classes that form the
     *        compatibility baseline to check against
     * @param currentVersion the classes that are checked for
     *        compatibility with compatibilityBaseline
     */
    public void reportDiffs(
        JavaType[] compatibilityBaseline, JavaType[] currentVersion)
        throws CheckerException
    {
        fireStart();
        runClassChecks(compatibilityBaseline, currentVersion);
        fireStop();
    }

    private void runClassChecks(
        JavaType[] compat, JavaType[] current)
        throws CheckerException
    {
        CoIterator iter = new CoIterator(
            new NameComparator(), compat, current);

        while (iter.hasNext())
        {
            iter.next();

            JavaType compatBaselineClass = (JavaType) iter.getLeft();
            JavaType currentClass = (JavaType) iter.getRight();

            if (compatBaselineClass == null)
            {
                if (!scopeSelector.isSelected(currentClass.getEffectiveScope()))
                {
                    continue;   
                }
                final String className = currentClass.getName();
                final ApiDifference diff =
                    new ApiDifference(
                        MSG_CLASS_ADDED, Severity.INFO, className,
                        null, null, null);
                fireDiff(diff);
            }
            else if (currentClass == null)
            {
                final Scope classScope = compatBaselineClass.getEffectiveScope();
                if (!scopeSelector.isSelected(classScope))
                {
                    continue;   
                }
                final String className = compatBaselineClass.getName();
                final Severity severity = classScope.isLessVisibleThan(
                        Scope.PROTECTED) ? Severity.INFO : Severity.ERROR;
                final ApiDifference diff =
                    new ApiDifference(
                        MSG_CLASS_REMOVED, severity, className,
                        null, null, null);
                fireDiff(diff);
            }
            else
            {
                // class is available in both releases
                boolean continueTesting = true;
                for (Iterator it = classChecks.iterator(); it.hasNext() && continueTesting;)
                {
                    ClassChangeCheck classChangeCheck = (ClassChangeCheck) it.next();
                    continueTesting = classChangeCheck.check(
                        compatBaselineClass, currentClass);
                }
            }
        }
    }
}
