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
package net.sf.clirr.framework;

import org.apache.bcel.classfile.JavaClass;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Given a JavaClass object, determines whether or not it is "selected",
 * based on its class or package. This is used to select subsets of the
 * classes available in a classpath for comparison or testing purposes.
 *
 * @author Simon Kitching
 */
public final class ClassSelector
{
    /** Class for implementing an enumeration. */
    public static final class Mode
    {
        private Mode()
        {
        }
    }

    /** positive selection. */
    public static final Mode MODE_IF = new Mode();
    /** negative selection. */
    public static final Mode MODE_UNLESS = new Mode();

    private Mode mode;

    private ArrayList packages = new ArrayList();
    private ArrayList packageTrees = new ArrayList();
    private ArrayList classes = new ArrayList();

    /**
     * Create a selector.
     * <p>
     * When mode is MODE_IF then a class is "selected" if-and-only-if
     * the class matches one of the criteria defined via the addXXX methods.
     * In other words, the criteria specify which classes are included
     * (selected) in the resulting class set.
     * <p>
     * When mode is MODE_UNLESS, then a class is "selected" unless the class
     * matches one of the criteria defined via the addXXX methods. In other
     * words, the criteria specify which classes are excluded from the
     * resulting class set.
     */
    public ClassSelector(Mode mode)
    {
        this.mode = mode;
    }

    /**
     * Matches any class which is in the named package.
     */
    public void addPackage(String packageName)
    {
        packages.add(packageName);
    }

    /**
     * Matches any class which is in the named package or any subpackage of it.
     */
    public void addPackageTree(String packageName)
    {
        packages.add(packageName);
    }

    /**
     * Matches the class with exactly this name, plus any of its inner classes.
     */
    public void addClass(String classname)
    {
        classes.add(classname);
    }

    /**
     * Return true if this class is one selected by the criteria stored
     * in this object.
     */
    public boolean isSelected(JavaClass clazz)
    {
        if (isAnonymousInnerClass(clazz))
        {
            return false;
        }

        boolean matches = matchesCriteria(clazz);
        if (mode == MODE_IF)
        {
            return matches;
        }
        else // mode == MODE_UNLESS
        {
            return !matches;
        }
    }

    /**
     * Return true if this class is an anonymous inner class.
     * Not even developers working on a package would be interested
     * in API changes in these classes...
     */
    private boolean isAnonymousInnerClass(JavaClass clazz)
    {
        String name = clazz.getClassName();
        int dollarPos = name.indexOf('$');
        if (dollarPos == -1)
        {
            return false;
        }

        for (int i = dollarPos + 1; i < name.length(); ++i)
        {
            if (!Character.isDigit(name.charAt(i)))
            {
                return false;
            }
        }

        // ok, we have a class name which contains a dollar sign, and
        // every subsequent character is a digit.
        return true;
    }

    /**
     * Return true if this class matches one of the criteria stored
     * in this object.
     */
    private boolean matchesCriteria(JavaClass clazz)
    {
        String packageName = clazz.getPackageName();
        if (packages.contains(packageName))
        {
            return true;
        }

        for (Iterator i = packageTrees.iterator(); i.hasNext();)
        {
            String entry = (String) i.next();
            if (packageName.startsWith(entry))
            {
                if (packageName.length() == entry.length())
                {
                    // they are exactly equal
                    return true;
                }

                if (packageName.charAt(entry.length()) == '.')
                {
                    return true;
                }

                // else packagename is like "com.acmegadgets" and entryname
                // is like "com.acme", which is not a match, so keep looking.
            }
        }

        String className = clazz.getClassName();
        for (Iterator i = classes.iterator(); i.hasNext();)
        {
            String entry = (String) i.next();

            if (className.startsWith(entry))
            {
                if (className.length() == entry.length())
                {
                    // they are exactly equal
                    return true;
                }

                if (className.charAt(entry.length()) == '$')
                {
                    // this is an inner class of the named class
                    return true;
                }
            }
        }

        return false;
    }
}

