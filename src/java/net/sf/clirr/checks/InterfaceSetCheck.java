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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.clirr.AbstractDiffReporter;
import net.sf.clirr.ApiDiffDispatcher;
import net.sf.clirr.ClassChangeCheck;
import net.sf.clirr.event.Severity;
import org.apache.bcel.classfile.JavaClass;

public final class InterfaceSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    public InterfaceSetCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public void check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        JavaClass[] compatInterfaces = compatBaseline.getAllInterfaces();
        JavaClass[] currentInterfaces = currentVersion.getAllInterfaces();

        // Note: an interface has itself in the set of all interfaces
        // we have to consider that below to avoid funny messages for gender changes

        Set current = new HashSet();
        for (int i = 0; i < currentInterfaces.length; i++)
        {
            String currentInterface = currentInterfaces[i].getClassName();
            current.add(currentInterface);
        }

        final String className = compatBaseline.getClassName();
        for (int i = 0; i < compatInterfaces.length; i++)
        {
            String compatInterface = compatInterfaces[i].getClassName();
            if (!current.contains(compatInterface)
                    && !compatInterface.equals(className))
            {
                log("Removed " + compatInterface
                        + " from the set of interfaces implemented by "
                        + className,
                        Severity.ERROR);
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
                        Severity.INFO);
            }
        }
    }
}
