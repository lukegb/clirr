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
package net.sf.clirr.event;

import org.apache.bcel.classfile.AccessFlags;

/**
 * Selects zero or more java scope values (public, protected, package,
 * private). An instance of this class is used when comparing two versions
 * of an application to indicate what items are of interest. When the target
 * audience is "normal" users of the applications, only changes to items
 * which have public or protected scope are relevant. When the audience is
 * developers of the applications, then package-scope and private-scope
 * changes are also of interest.
 *
 * @author Simon Kitching
 */
public final class ScopeSelector
{
    private boolean selectPublic;
    private boolean selectProtected;
    private boolean selectPrivate;
    private boolean selectPackage;

    /**
     * Construct an instance which selects public and protected objects and
     * ignores package and private objects. The selectXXX methods can later
     * be used to adjust this default behaviour.
     */
    public ScopeSelector()
    {
        selectPublic = true;
        selectProtected = true;
    }

    /** Enable/disable selection of public-scope objects. */
    public void selectPublic(boolean selected)
    {
        selectPublic = selected;
    }

    /** Enable/disable selection of protected-scope objects. */
    public void selectProtected(boolean selected)
    {
        selectProtected = selected;
    }

    /** Enable/disable selection of package-scope objects. */
    public void selectPackage(boolean selected)
    {
        selectPackage = selected;
    }

    /** Enable/disable selection of private-scope objects. */
    public void selectPrivate(boolean selected)
    {
        selectPrivate = selected;
    }

    /**
     * Return a string which indicates what scopes this object will consider
     * to be selected (ie relevant).
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        if (selectPublic)
        {
            buf.append("public");
        }

        if (selectProtected)
        {
            if (buf.length() != 0)
            {
                buf.append("+");
            }

            buf.append("protected");
        }

        if (selectPackage)
        {
            if (buf.length() != 0)
            {
                buf.append("+");
            }

            buf.append("package");
        }

        if (selectPrivate)
        {
            if (buf.length() != 0)
            {
                buf.append("+");
            }

            buf.append("private");
        }

        if (buf.length() == 0)
        {

            return "none";
        }
        else
        {

            return buf.toString();
        }
    }

    /**
     * Given a BCEL object, return true if ths object's scope is one of the
     * values this object is configured to match.
     * <p>
     * Note that BCEL classes JavaClass, Field and Method all inherit from
     * the AccessFlags base class and so are valid parameters to this
     * method.
     *
     * @param object is the object whose scope is to be checked.
     * @return true if the object is selected.
     */
    public boolean isSelected(AccessFlags object)
    {
        if (object.isPublic())
        {
            return selectPublic;
        }

        if (object.isProtected())
        {
            return selectProtected;
        }

        if (object.isPrivate())
        {
            return selectPrivate;
        }

        return selectPackage;
    }

    /**
     * Given a BCEL object, return the string which would be used in java
     * source code to declare that object's scope. <p>
     *
     * Note that BCEL classes JavaClass, Field and Method all inherit from
     * the AccessFlags base class and so are valid parameters to this
     * method.
     */
    public static String getScopeDecl(AccessFlags object)
    {
        if (object.isPublic())
        {
            return "public";
        }

        if (object.isProtected())
        {
            return "protected";
        }

        if (object.isPrivate())
        {
            return "private";
        }

        return "";
    }

    /**
     * Given a BCEL object, return a string indicating whether the object is
     * public/protected/private/package scope. This is similar to
     * getScopeName, except for package-scope objects where this method
     * returns the string "package". <p>
     *
     * Note that BCEL classes JavaClass, Field and Method all inherit from
     * the AccessFlags base class and so are valid parameters to this
     * method.
     */
    public static String getScopeDesc(AccessFlags object)
    {
        if (object.isPublic())
        {
            return "public";
        }

        if (object.isProtected())
        {
            return "protected";
        }

        if (object.isPrivate())
        {
            return "private";
        }

        return "package";
    }

    /**
     * Given a BCEL access flag field, return a rating indicating the
     * "visibility" of the security of that access right. Change of access
     * rights from lower to higher visibility rating is a binary-compatible
     * change. Public = 3 Protected = 2 Package = 1 private = 0 <p>
     *
     * Note that BCEL classes JavaClass, Field and Method all inherit from
     * the AccessFlags base class and so are valid parameters to this
     * method.
     */
    public static int getVisibilityRating(AccessFlags object)
    {
        if (object.isPublic())
        {
            return 3;
        }

        if (object.isProtected())
        {
            return 2;
        }

        if (object.isPrivate())
        {
            return 0;
        }

        return 1;
    }
}

