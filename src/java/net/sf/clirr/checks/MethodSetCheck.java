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

package net.sf.clirr.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

/**
 * Checks the methods of a class.
 *
 * @author lkuehne
 */
public class MethodSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    private class MethodComparator implements Comparator
    {
        public final int compare(Object o1, Object o2)
        {
            Method m1 = (Method) o1;
            Method m2 = (Method) o2;

            String name1 = m1.getName();
            String name2 = m2.getName();

            final int nameComparison = name1.compareTo(name2);
            if (nameComparison == 0)
            {
                return m1.getArgumentTypes().length - m2.getArgumentTypes().length;
            }
            else
            {
                return nameComparison;
            }
        }
    }

    /** {@inheritDoc} */
    public MethodSetCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public final void check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        // Dont't report method problems when gender has changed, as
        // really the whole API is a pile of crap then - let GenderChange check
        // do it's job, and that's it
        if (compatBaseline.isInterface() ^ currentVersion.isInterface())
        {
            return;
        }

        Method[] baselineMethods = sort(compatBaseline.getMethods());
        Method[] currentMethods = sort(currentVersion.getMethods());

        for (int i = 0; i < baselineMethods.length; i++)
        {
            Method baselineMethod = baselineMethods[i];

            // TODO: We need a global perspective
            // to determine which current method matches which baseline method.

            // simply checking the name and arg numbers won't be enough in all cases
            // but I'll stick with it for now to get the framework out

            final int idx = Arrays.binarySearch(currentMethods, baselineMethod, new MethodComparator());
            if (idx < 0)
            {
                fireDiff("Method '" + getMethodId(compatBaseline, baselineMethod) + "' has been removed",
                        Severity.ERROR, compatBaseline, baselineMethod);
            }
            else
            {
                check(compatBaseline, baselineMethod, currentMethods[idx]);
            }
        }
    }

    private void check(JavaClass compatBaseline, Method baselineMethod, Method currentMethod)
    {
        checkParameterTypes(compatBaseline, baselineMethod, currentMethod);
        checkReturnType(compatBaseline, baselineMethod, currentMethod);
        checkDeclaredExceptions(compatBaseline, baselineMethod, currentMethod);
    }

    private void checkParameterTypes(JavaClass compatBaseline, Method baselineMethod, Method currentMethod)
    {
        Type[] bArgs = baselineMethod.getArgumentTypes();
        Type[] cArgs = currentMethod.getArgumentTypes();

        // TODO: This currently never fires because of our poor method matching algorithm
        if (bArgs.length != cArgs.length)
        {
            fireDiff("In Method '" + getMethodId(compatBaseline, baselineMethod)
                    + "' the number of arguments has changed",
                    Severity.ERROR, compatBaseline, baselineMethod);
            return;
        }

        //System.out.println("baselineMethod = " + getMethodId(compatBaseline, baselineMethod));
        for (int i = 0; i < bArgs.length; i++)
        {
            Type bArg = bArgs[i];
            Type cArg = cArgs[i];

            if (bArg.toString().equals(cArg.toString()))
            {
                continue;
            }

            // TODO: Check assignability...
            fireDiff("Parameter " + (i + 1) + " of '" + getMethodId(compatBaseline, baselineMethod)
                    + "' has changed it's type to " + cArg,
                    Severity.ERROR, compatBaseline, baselineMethod);
        }
    }

    private void checkReturnType(JavaClass compatBaseline, Method baselineMethod, Method currentMethod)
    {
        Type bReturnType = baselineMethod.getReturnType();
        Type cReturnType = currentMethod.getReturnType();

        // TODO: Check assignability...
        if (!bReturnType.toString().equals(cReturnType.toString()))
        {
            fireDiff("Return type of Method '" + getMethodId(compatBaseline, baselineMethod)
                    + "' has been changed to " + cReturnType,
                    Severity.ERROR, compatBaseline, baselineMethod);
        }


    }

    private void checkDeclaredExceptions(
            JavaClass compatBaseline, Method baselineMethod, Method currentMethod)
    {
        // TODO
    }

    private Method[] sort(Method[] methods)
    {
        List target = new ArrayList();
        for (int i = 0; i < methods.length; i++)
        {
            final Method method = methods[i];
            if ((method.isPublic() || method.isProtected())
                    && !"<clinit>".equals(method.getName()))
            {
                target.add(method);
            }
        }
        Method[] retval = new Method[target.size()];
        target.toArray(retval);
        Arrays.sort(retval, new MethodComparator());
        return retval;
    }

    /**
     * Creates a human readable String that is similar to the method signature
     * and identifies the method within a class.
     * @param clazz the container of the method
     * @param method the method to identify.
     * @return a human readable id, for example "public void print(java.lang.String)"
     */
    private String getMethodId(JavaClass clazz, Method method)
    {
        if (!method.isPublic() && !method.isProtected())
        {
            throw new IllegalArgumentException();
        }

        StringBuffer buf = new StringBuffer();

        buf.append(method.isPublic() ? "public" : "protected");
        buf.append(" ");

        String name = method.getName();
        if ("<init>".equals(name))
        {
            final String className = clazz.getClassName();
            int idx = className.lastIndexOf('.');
            name = className.substring(idx + 1);
        }
        else
        {
            buf.append(method.getReturnType());
            buf.append(' ');
        }
        buf.append(name);
        buf.append('(');
        Type[] argTypes = method.getArgumentTypes();
        String argSeparator = "";
        for (int i = 0; i < argTypes.length; i++)
        {
            buf.append(argSeparator);
            buf.append(argTypes[i].toString());
            argSeparator = ", ";
        }
        buf.append(')');
        return buf.toString();
    }

    private void fireDiff(String report, Severity severity, JavaClass clazz, Method method)
    {
        final String className = clazz.getClassName();
        final ApiDifference diff =
                new ApiDifference(report + " in " + className,
                        severity, className, getMethodId(clazz, method), null);
        getApiDiffDispatcher().fireDiff(diff);

    }

}
