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

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Enumeration;

/**
 * ResourceBundle that implements the default locale by delegating to the english bundle.
 * This solves the bug described in
 * https://sourceforge.net/tracker/index.php?func=detail&aid=594469&group_id=29721&atid=397078
 * without having to duplicate any resource bundles, leading to a simpler build and a smaller jar.
 *
 * @author lkuehne
 */
public class EventMessages extends ResourceBundle
{
    /**
     * The base name of the resource bundle from which message descriptions
     * are read.
     */
    public static final String RESOURCE_NAME = EventMessages.class.getName();

    private ResourceBundle delegate = null;

    /**
     * Control variable used in synchronized blocks that delegate to the {@link #delegate} bundle.
     * The delegate bundle has "this" as it's parent.
     * To prevent infinite loops in the lookup process when searching for
     * non-existent keys we set isUsingDelegate to true to break out of the loop.
     */
    private boolean isUsingDelegate = false;

    /**
     * Constructor.
     * @deprecated Typical user code never calls this directly but uses
     * {@link java.util.ResourceBundle#getBundle(java.lang.String)} or one of it's variants instead.
     */
    public EventMessages()
    {
    }

    private ResourceBundle getDelegate()
    {
        if (delegate == null)
        {
            delegate = ResourceBundle.getBundle(RESOURCE_NAME, Locale.ENGLISH);
        }
        return delegate;
    }

    /** @see java.util.ResourceBundle#handleGetObject */
    protected final synchronized Object handleGetObject(String key)
    {
        try
        {
            if (isUsingDelegate)
            {
                // the underlying bundle is delegating the call back to us
                // this means that the key is unknown, so we return null
                return null;
            }
            else
            {
                isUsingDelegate = true;
                return getDelegate().getObject(key);
            }
        }
        finally
        {
            isUsingDelegate = false;
        }
    }

    /** @see java.util.ResourceBundle#getKeys */
    public final synchronized Enumeration getKeys()
    {
        try
        {
            if (isUsingDelegate)
            {
                // the underlying bundle is delegating the call back to us
                // this means that the key is unknown, so we return null
                return null;
            }
            else
            {
                isUsingDelegate = true;
                return getDelegate().getKeys();
            }
        }
        finally
        {
            isUsingDelegate = false;
        }
    }

    /** @see java.util.ResourceBundle#getLocale */
    public final Locale getLocale()
    {
        return getDelegate().getLocale();
    }

}
