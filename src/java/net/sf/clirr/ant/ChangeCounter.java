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

package net.sf.clirr.ant;

import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.DiffListenerAdapter;
import net.sf.clirr.event.Severity;

final class ChangeCounter extends DiffListenerAdapter
{
    private int binInfos = 0;
    private int binWarnings = 0;
    private int binErrors = 0;

    private int srcInfos = 0;
    private int srcWarnings = 0;
    private int srcErrors = 0;


    public ChangeCounter()
    {
    }

    public int getBinInfos()
    {
        return binInfos;
    }

    public int getBinWarnings()
    {
        return binWarnings;
    }

    public int getBinErrors()
    {
        return binErrors;
    }

    public int getSrcInfos()
    {
        return srcInfos;
    }

    public int getSrcWarnings()
    {
        return srcWarnings;
    }

    public int getSrcErrors()
    {
        return srcErrors;
    }

    public void reportDiff(ApiDifference difference)
    {
        final Severity binSeverity = difference.getBinaryCompatibilitySeverity();
        if (Severity.ERROR.equals(binSeverity))
        {
            binErrors += 1;
        }
        else if (Severity.WARNING.equals(binSeverity))
        {
            binWarnings += 1;
        }
        else if (Severity.INFO.equals(binSeverity))
        {
            binInfos += 1;
        }

        final Severity srcSeverity = difference.getSourceCompatibilitySeverity();
        if (Severity.ERROR.equals(srcSeverity))
        {
            srcErrors += 1;
        }
        else if (Severity.WARNING.equals(srcSeverity))
        {
            srcWarnings += 1;
        }
        else if (Severity.INFO.equals(srcSeverity))
        {
            srcInfos += 1;
        }

    }

}
