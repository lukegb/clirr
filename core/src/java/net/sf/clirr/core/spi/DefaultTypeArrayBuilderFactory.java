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

package net.sf.clirr.core.spi;

import net.sf.clirr.core.internal.asm.AsmTypeArrayBuilder;

/**
 * Creates a default implementation of the TypeArrayBuilder interface.
 * 
 * By using this class, front end code can avoid instantiating clirr internal
 * implementations directly.
 * 
 * @author lk
 */
public final class DefaultTypeArrayBuilderFactory
{
    /**
     * Creates a new instance. Typically only one such instance is required in
     * an application.
     */
    public DefaultTypeArrayBuilderFactory()
    {
    }

    /**
     * Creates a new instance of a {@link TypeArrayBuilder}. Note that no
     * guarantees are made about the actual implementation type.
     * 
     * @return a new instance
     */
    public TypeArrayBuilder build()
    {
        return new AsmTypeArrayBuilder();
    }
}
