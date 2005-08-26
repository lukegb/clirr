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

package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.Message;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.internal.AbstractDiffReporter;
import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.CoIterator;
import net.sf.clirr.core.internal.NameComparator;
import net.sf.clirr.core.spi.JavaType;

/**
 * Detects changes in the set of superclasses.
 *
 * @author lkuehne
 */
public final class ClassHierarchyCheck extends AbstractDiffReporter implements ClassChangeCheck
{
    private static final Message MSG_ADDED_CLASS_TO_SUPERCLASSES = new Message(5000);
    private static final Message MSG_REMOVED_CLASS_FROM_SUPERCLASSES = new Message(5001);

    /**
     * Create a new instance of this check.
     * @param dispatcher the diff dispatcher that distributes the detected changes to the listeners.
     */
    public ClassHierarchyCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    /** {@inheritDoc} */
    public boolean check(JavaType compatBaseline, JavaType currentVersion)
    {
        JavaType[] compatSupers = compatBaseline.getSuperClasses();
        JavaType[] currentSupers = currentVersion.getSuperClasses();

        boolean isThrowable = false;
        for (int i = 0; i < compatSupers.length; i++)
        {
            JavaType javaClass = compatSupers[i];
            if ("java.lang.Throwable".equals(javaClass.getName()))
            {
                isThrowable = true;
            }
        }

        final String className = compatBaseline.getName();

        CoIterator iter = new CoIterator(new NameComparator(), compatSupers, currentSupers);

        while (iter.hasNext())
        {
            iter.next();
            JavaType baselineSuper = (JavaType) iter.getLeft();
            JavaType currentSuper = (JavaType) iter.getRight();

            if (baselineSuper == null)
            {
                Severity severity;
                if (isThrowable)
                {
                    // report a warning, because a change to the set of types
                    // implemented by a thrown object can affect the
                    // exception-catching behaviour of a program.
                    severity = Severity.WARNING;
                }
                else
                {
                    severity = Severity.INFO;
                }

                log(MSG_ADDED_CLASS_TO_SUPERCLASSES,
                    getSeverity(compatBaseline, severity), className, null, null,
                    new String[] {currentSuper.getName()});
            }
            else if (currentSuper == null)
            {
                log(MSG_REMOVED_CLASS_FROM_SUPERCLASSES,
                    getSeverity(compatBaseline, Severity.ERROR), className, null, null,
                    new String[] {baselineSuper.getName()});
            }
        }

        return true;
    }
}
