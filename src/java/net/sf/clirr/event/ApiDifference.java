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

    //private boolean expected;

    /**
     * Create a new API differnce representation.
     *
     * @param report a human readable string describing the change that was made.
     * @param severity the severity in terms of binary API compatibility.
     */
    public ApiDifference(String report, Severity severity /*, boolean expected*/)
    {
        this.report = report;
        this.severity = severity;
//        this.expected = expected;
    }

    /**
     * The Severity of the API difference. ERROR means that clients will
     * definately break, WARNING means that clients may break, depending
     * on how they use the library. See the eclipse paper for further
     * explanation.
     *
     * @return the severity of the API difference.
     */
    public Severity getSeverity()
    {
        return severity;
    }

    public String getReport()
    {
        return report;
    }

    /*
    public boolean isExpected()
    {
        return expected;
    }
    */

    /** {@inheritDoc} */
    public String toString()
    {
        return report + " (" + severity + ")";
    }

    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof ApiDifference))
        {
            return false;
        }

        final ApiDifference apiDifference = (ApiDifference) o;

        if (report != null ? !report.equals(apiDifference.report) : apiDifference.report != null)
        {
            return false;
        }
        if (severity != null ? !severity.equals(apiDifference.severity) : apiDifference.severity != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (report != null ? report.hashCode() : 0);
        result = 29 * result + (severity != null ? severity.hashCode() : 0);
        return result;
    }
}
