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

package net.sf.clirr.ant;

import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.DiffListenerAdapter;
import net.sf.clirr.event.Severity;

final class ChangeCounter extends DiffListenerAdapter
{
    private int infos = 0;
    private int warnings = 0;
    private int errors = 0;

    public ChangeCounter()
    {
    }

    public int getInfos()
    {
        return infos;
    }

    public int getWarnings()
    {
        return warnings;
    }

    public int getErrors()
    {
        return errors;
    }

    public void reportDiff(ApiDifference difference)
    {
        if (Severity.ERROR.equals(difference.getSeverity()))
        {
            errors += 1;
        }
        else if (Severity.WARNING.equals(difference.getSeverity()))
        {
            warnings += 1;
        }
        else if (Severity.INFO.equals(difference.getSeverity()))
        {
            infos += 1;
        }
    }

}
