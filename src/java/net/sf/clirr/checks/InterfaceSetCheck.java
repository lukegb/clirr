//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003 - 2004  Lars Kühne
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import org.apache.bcel.classfile.JavaClass;

/**
 * Detects changes in the set of interfaces implemented by a class.
 *
 * @author lkuehne
 */
public final class InterfaceSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    /**
     * Create a new instance of this check.
     * @param dispatcher the diff dispatcher that distributes the detected changes to the listeners.
     */
    public InterfaceSetCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    /** {@inheritDoc} */
    public void check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        JavaClass[] compatInterfaces = compatBaseline.getAllInterfaces();
        JavaClass[] currentInterfaces = currentVersion.getAllInterfaces();

        // Note: an interface has itself in the set of all interfaces
        // we have to consider that below to avoid funny messages for gender changes

        // Note: getAllInterfaces might return multiple array entries with the same
        // interface, so we need to use sets to remove duplicates...
        Set compat = createClassSet(compatInterfaces);
        Set current = createClassSet(currentInterfaces);

        final String className = compatBaseline.getClassName();

        for (Iterator it = compat.iterator(); it.hasNext();)
        {
            String compatInterface = (String) it.next();

            if (!current.contains(compatInterface)
                    && !compatInterface.equals(className))
            {
                log("Removed " + compatInterface
                        + " from the set of interfaces implemented by "
                        + className,
                        Severity.ERROR, className, null, null);
            }
            else
            {
                current.remove(compatInterface);
            }
        }

        for (Iterator it = current.iterator(); it.hasNext();)
        {
            String name = (String) it.next();
            if (!name.equals(className))
            {
                log("Added " + name
                        + " to the set of implemented interfaces implemented by "
                        + className,
                        Severity.INFO, className, null, null);
            }
        }
    }

    private Set createClassSet(JavaClass[] classes)
    {
        Set current = new HashSet();
        for (int i = 0; i < classes.length; i++)
        {
            String currentInterface = classes[i].getClassName();
            current.add(currentInterface);
        }
        return current;
    }
}
