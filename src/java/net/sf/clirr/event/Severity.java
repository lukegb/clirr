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
 * Enumeration type for severity of an Api difference.
 *
 * @author lkuehne
 */
public final class Severity
{

    private String representation;

    private Severity(String representation)
    {
        this.representation = representation;
    }

    /** marks an api difference that is binary compatible. */
    public static final Severity INFO = new Severity("INFO");

    /** marks an api difference that might be binary incompatible. */
    public static final Severity WARNING = new Severity("WARNING");

    /** marks an api difference that is binary incompatible. */
    public static final Severity ERROR = new Severity("ERROR");

    /** @see Object#toString() */
    public String toString()
    {
        return representation;
    }
}
