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

package net.sf.clirr.event;


/**
 * TODO: Add structure.
 * E.g.
 * - origfile, newfile
 * - Class-/MethodName
 * @author Lars
 */
public final class ApiDifference
{

    private String report;

    private Severity severity;

    private boolean expected;

    public ApiDifference(String report, Severity severity /*, boolean expected*/)
    {
        this.report = report;
        this.severity = severity;
        this.expected = expected;
    }

    public Severity getSeverity()
    {
        return severity;
    }

    public String getReport()
    {
        return report;
    }

    public boolean isExpected()
    {
        return expected;
    }

    public String toString()
    {
        return report;
    }
}
