//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003  Lars Kühne
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
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import org.apache.bcel.classfile.JavaClass;

/**
 * Detects changes in class modifiers (abstract, final).
 *
 * @author lkuehne
 */
public final class ClassModifierCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    /**
     * Create a new instance of this check.
     * @param dispatcher the diff dispatcher that distributes the detected changes to the listeners.
     */
    public ClassModifierCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    /** {@inheritDoc} */
    public void check(JavaClass compatBaseLine, JavaClass currentVersion)
    {
        final boolean currentIsFinal = currentVersion.isFinal();
        final boolean compatIsFinal = compatBaseLine.isFinal();
        final boolean currentIsAbstract = currentVersion.isAbstract();
        final boolean compatIsAbstract = compatBaseLine.isAbstract();
        final boolean currentIsInterface = currentVersion.isInterface();
        final boolean compatIsInterface = compatBaseLine.isInterface();

        final String className = compatBaseLine.getClassName();

        // TODO: There are cases when nonfinal classes are effectively final
        // because they do not have public or protected ctors. For such
        // classes we should not emit errors when a final modifier is
        // introduced.

        if (compatIsFinal && !currentIsFinal)
        {
            log("Removed final modifier in class " + className,
                    Severity.INFO, className, null, null);
        }
        else if (!compatIsFinal && currentIsFinal)
        {
            log("Added final modifier in class " + className,
                    Severity.ERROR, className, null, null);
        }

        // interfaces are always abstract, don't report gender change here
        if (compatIsAbstract && !currentIsAbstract && !compatIsInterface)
        {
            log("Removed abstract modifier in class " + className,
                    Severity.INFO, className, null, null);
        }
        else if (!compatIsAbstract && currentIsAbstract && !currentIsInterface)
        {
            log("Added abstract modifier in class " + className,
                    Severity.ERROR, className, null, null);
        }
    }

}
