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

package net.sf.clirr.core;

import org.apache.bcel.classfile.JavaClass;

/**
 * A filter for Java classes.
 * <p>
 * Instances of this interface are passed to the
 * {@link Checker#reportDiffs} method of the {@link Checker} class.
 * </p>
 *
 * @see Checker#reportDiffs
 * @see java.io.FileFilter
 * @author lkuehne
 */
public interface ClassFilter
{
    /**
     * Tests whether or not a given java class is considered when reporting the API
     * differences between jar files.
     *
     * @param clazz a Java class
     * @return true if clazz should be considered by the Checker
     * in this object.
     */
    boolean isSelected(JavaClass clazz);
}
