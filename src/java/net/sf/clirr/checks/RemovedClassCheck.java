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
import net.sf.clirr.framework.ApiDiffDispatcher;
import org.apache.bcel.util.ClassSet;

/**
 * Checks whether a class/interface has been removed from the public API.
 *
 * @author lkuehne
 */
public final class RemovedClassCheck
        extends AbstractClassSetChangeCheck
{
    /**
     * Create a new instance of this check.
     * @param dispatcher the diff dispatcher that distributes the detected changes to the listeners.
     */
    public RemovedClassCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    /** {@inheritDoc} */
    public void check(ClassSet compatBaseline, ClassSet currentVersion)
    {
        String[] oldClassNames = compatBaseline.getClassNames();
        String[] newClassNames = currentVersion.getClassNames();
        compareClassNameSets(oldClassNames, newClassNames, "Removed ", Severity.ERROR);
    }
}
