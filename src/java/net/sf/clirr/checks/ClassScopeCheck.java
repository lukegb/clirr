//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003 - 2004  Lars K�hne
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

package net.sf.clirr.checks;

import net.sf.clirr.event.Severity;
import net.sf.clirr.event.ScopeSelector;
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.framework.CheckerException;

import org.apache.bcel.classfile.JavaClass;

/**
 * Detects changes in class access declaration, for both "top-level" classes,
 * and nested classes.
 * <p>
 * Java class files only ever contain scope specifiers of "public" or "package".
 * For top-level classes, this is expected: it is not possible to have a
 * top-level protected or private class.
 * <p>
 * However nested classes <i>can</i> be declared as protected or private. The
 * way to tell the real scope of a nested class is to ignore the scope in
 * the actual class file itself, and instead look in the "InnerClasses"
 * attribute stored on the enclosing class. This is exactly what the java
 * compiler does when compiling, and what the jvm does when verifying class
 * linkage at runtime.
 *
 * @author Simon Kitching
 */
public final class ClassScopeCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    private ScopeSelector scopeSelector;

   /**
     * Create a new instance of this check.
     * @param dispatcher the diff dispatcher that distributes the detected changes to the listeners.
     */
    public ClassScopeCheck(ApiDiffDispatcher dispatcher, ScopeSelector scopeSelector)
    {
        super(dispatcher);
        this.scopeSelector = scopeSelector;
    }

    /** {@inheritDoc} */
    public boolean check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        ScopeSelector.Scope bScope;
        try
        {
            bScope = ScopeSelector.getClassScope(compatBaseline);
        }
        catch (CheckerException ex)
        {
            log(ex.getMessage() + " in old class version",
                Severity.ERROR, compatBaseline.getClassName(), null, null);
            return false;
        }

        ScopeSelector.Scope cScope;
        try
        {
            cScope = ScopeSelector.getClassScope(currentVersion);
        }
        catch (CheckerException ex)
        {
            log(ex.getMessage() + " in new class version",
                Severity.ERROR, compatBaseline.getClassName(), null, null);
            return false;
        }

        if (!scopeSelector.isSelected(bScope) && !scopeSelector.isSelected(cScope))
        {
            // neither the old nor the new class are "visible" at the scope
            // the user of this class cares about, so just skip this test
            // and all following tests for this pair of classes.
            return false;
        }

        if (cScope.isMoreVisibleThan(bScope))
        {
            log(
                "Increased visibility of class from "
                + bScope.getDesc()
                + " to "
                + cScope.getDesc(),
                Severity.INFO, compatBaseline.getClassName(), null, null);
        }
        else if (cScope.isLessVisibleThan(bScope))
        {
            log(
                "Decreased visibility of class from "
                + bScope.getDesc()
                + " to "
                + cScope.getDesc(),
                Severity.ERROR, compatBaseline.getClassName(), null, null);
        }

        // Apply further checks only if both versions of the class have scopes
        // of interest. For example, when the user is only interested in
        // public & protected classes, then for classes which have just become
        // public/protected we just want to report that it is now "visible";
        // because the class was not visible before the differences since its
        // last version are not relevant. And for classes which are no longer
        // public/protected, we just want to report that the whole class is no
        // longer "visible"; as it is not visible to users any changes to it
        // are irrelevant.
        return scopeSelector.isSelected(bScope) && scopeSelector.isSelected(cScope);
    }

}