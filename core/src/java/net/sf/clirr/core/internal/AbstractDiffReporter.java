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

import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.Message;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.CheckerException;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.JavaClass;

public abstract class AbstractDiffReporter
{
    private static final Message MSG_UNABLE_TO_DETERMINE_CLASS_SCOPE = new Message(9000);

    private ApiDiffDispatcher dispatcher;

    public AbstractDiffReporter(ApiDiffDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    protected final ApiDiffDispatcher getApiDiffDispatcher()
    {
        return dispatcher;
    }

    protected final void log(
        Message msg,
        Severity severity,
        String clazz, Method method, Field field,
        String[] args)
    {
        final ApiDifference diff = new ApiDifference(
            msg, severity, clazz, null, null, args);
        getApiDiffDispatcher().fireDiff(diff);
    }

    /**
     * Determine whether the severity of the problem should be reduced
     * to INFO because the specified class is package or private accessibility.
     * Clirr reports changes at level INFO for all private and package
     * scoped objects.
     * <p>
     * Note that the class passed here should always be from the <i>old</i>
     * class version, because we're checking whether <i>existing</i> code
     * would have been able to access it (potential compatibility problems)
     * or not.
     *
     * @param clazz is the class the change is being reported about.
     * @param sev is the severity that should be reported for public/protected
     * scoped classes.
     *
     * @return param sev if the class is public/protected, and Severity.INFO
     * if the class is package or private scope.
     */
    protected final Severity getSeverity(JavaClass clazz, Severity sev)
    {
        // use ScopeSelector, not isPublic/isProtected, because the latter
        // methods don't correctly handle scopes of nested classes.
        ScopeSelector.Scope scope;

        try
        {
            scope = ScopeSelector.getClassScope(clazz);
        }
        catch (CheckerException ex)
        {
            // Note: this should never happen. If it does, it probably
            // means clirr has been passed a severely-stuffed-up jar
            // for analysis.
            log(MSG_UNABLE_TO_DETERMINE_CLASS_SCOPE,
                Severity.ERROR, clazz.getClassName(), null, null, null);
            return sev;
        }

        if ((scope == ScopeSelector.SCOPE_PUBLIC)
            || (scope == ScopeSelector.SCOPE_PROTECTED))
        {
            return sev;
        }
        else
        {
            return Severity.INFO;
        }
    }

    /**
     * Determine whether the severity of the problem should be reduced
     * to INFO because:
     * <ul>
     *  <li>the specified method is package or private accessibility, or</li>
     *  <li>the specified method is in a package or private class. </li
     * </ul>
     * <p>
     * Clirr reports changes at level INFO for all private and package
     * scoped objects.
     * <p>
     * Note that the method passed here should always be from the <i>old</i>
     * class version, because we're checking whether <i>existing</i> code
     * would have been able to access it (potential compatibility problems)
     * or not.
     *
     * @param clazz is the class containing the method of interest
     * @param method is the method the change is being reported about.
     * @param sev is the severity that should be reported for public/protected
     * scoped methods.
     *
     * @return param sev if the method is public/protected, and Severity.INFO
     * if the method is package or private scope.
     */
    protected final Severity getSeverity(JavaClass clazz, Method method, Severity sev)
    {
        if (method.isPublic() || method.isProtected())
        {
            return getSeverity(clazz, sev);
        }
        else
        {
            return Severity.INFO;
        }
    }

    /**
     * Determine whether the severity of the problem should be reduced
     * to INFO because:
     * <ul>
     * <li>the specified field is package or private accessibility, or</li>
     * <li>the specified field is in a package or private class. </li>
     * </ul>
     * <p>
     * Clirr reports changes at level INFO for all private and package
     * scoped objects.
     * <p>
     * Note that the field passed here should always be from the <i>old</i>
     * class version, because we're checking whether <i>existing</i> code
     * would have been able to access it (potential compatibility problems)
     * or not.
     *
     * @param clazz is the class containing the method of interest
     * @param field is the field the change is being reported about.
     * @param sev is the severity that should be reported for public/protected
     * scoped field.
     *
     * @return param sev if the field is public/protected, and Severity.INFO
     * if the field is package or private scope.
     */
    protected final Severity getSeverity(JavaClass clazz, Field field, Severity sev)
    {
        if (field.isPublic() || field.isProtected())
        {
            return getSeverity(clazz, sev);
        }
        else
        {
            return Severity.INFO;
        }
    }
}
