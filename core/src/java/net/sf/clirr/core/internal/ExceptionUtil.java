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

package net.sf.clirr.core.internal;

import java.lang.reflect.Method;

/**
 * A helper class to initialize the cause of an exception.
 *
 * This allows Clirr to be compile time compatible with JDK 1.3
 * at still taking advantage of the JDK 1.4 chained exceptions feature
 * if available.
 *
 * @author lkuehne
 */
public final class ExceptionUtil
{
    /** Disallow instantiation. */
    private ExceptionUtil()
    {
    }

    private static Method initCauseMethod;

    static
    {
        try
        {
            initCauseMethod = Throwable.class.getMethod("initCause", new Class[]{Throwable.class});
        }
        catch (NoSuchMethodException e)
        {
            // we're on JDK < 1.4, no cause data will be available in Exception stacktraces
            initCauseMethod = null;
        }
    }

    /**
     * Initializes the chained exception if possible.
     * Does nothing if chained exceptions are not available on the
     * current JDK (1.3 or lower).
     *
     * @param t the resulting exception (high abstraction level)
     * @param cause the underlying cause of t (low abstraction level)
     */
    public static void initCause(Throwable t, Throwable cause)
    {
        if (initCauseMethod == null)
        {
            return;
        }

        try
        {
            initCauseMethod.invoke(t, new Throwable[]{cause});
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
            {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("unable to initCause: " + e.toString());
        }
    }
}
