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

package net.sf.clirr.checks;

import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks the methods of a class.
 *
 * @author lkuehne
 */
public class MethodSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
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

        // The main problem here is to figure out which old method corresponds to which new method.

        // Methods that are named differently are trated as unrelated
        //
        // For Methods that differ only in their parameter list we build a similarity table, i.e.
        // for new method i and old method j we have number that charaterizes how similar
        // the method signatures are (0 means equal, higher number means more different)

        Map bNameToMethod = buildNameToMethodMap(compatBaseline);
        Map cNameToMethod = buildNameToMethodMap(currentVersion);

        checkAddedOrRemoved(bNameToMethod, cNameToMethod, compatBaseline, currentVersion);

        // now the key sets of the two maps are equal,
        // we only have collections of methods that have the same name

        // for each name analyse the differences
        for (Iterator it = bNameToMethod.keySet().iterator(); it.hasNext();)
        {
            String name = (String) it.next();

            List baselineMethods = (List) bNameToMethod.get(name);
            List currentMethods = (List) cNameToMethod.get(name);

            while (baselineMethods.size() * currentMethods.size() > 0)
            {
                int[][] similarityTable = buildSimilarityTable(baselineMethods, currentMethods);

                int min = Integer.MAX_VALUE;
                int iMin = baselineMethods.size();
                int jMin = currentMethods.size();
                for (int i = 0; i < baselineMethods.size(); i++)
                {
                    for (int j = 0; j < currentMethods.size(); j++)
                    {
                        final int tableEntry = similarityTable[i][j];
                        if (tableEntry < min)
                        {
                            min = tableEntry;
                            iMin = i;
                            jMin = j;
                        }
                    }
                }
                Method iMethod = (Method) baselineMethods.remove(iMin);
                Method jMethod = (Method) currentMethods.remove(jMin);
                check(compatBaseline, iMethod, jMethod);
            }
        }
    }

    private int[][] buildSimilarityTable(List baselineMethods, List currentMethods)
    {
        int[][] similarityTable = new int[baselineMethods.size()][currentMethods.size()];
        for (int i = 0; i < baselineMethods.size(); i++)
        {
            for (int j = 0; j < currentMethods.size(); j++)
            {
                final Method iMethod = (Method) baselineMethods.get(i);
                final Method jMethod = (Method) currentMethods.get(j);
                similarityTable[i][j] = distance(iMethod, jMethod);
            }
        }
        return similarityTable;
    }

    private int distance(Method m1, Method m2)
    {
        final Type[] m1Args = m1.getArgumentTypes();
        final Type[] m2Args = m2.getArgumentTypes();

        if (m1Args.length != m2Args.length)
        {
            return 1000 * Math.abs(m1Args.length - m2Args.length);
        }

        int retVal = 0;
        for (int i = 0; i < m1Args.length; i++)
        {
            if (!m1Args[i].toString().equals(m2Args[i].toString()))
            {
                retVal += 1;
            }
        }
        return retVal;
    }

    /**
     * Checks for added or removed methods, modifies the argument maps so their key sets are equal.
     */
    private void checkAddedOrRemoved(
            Map bNameToMethod,
            Map cNameToMethod,
            JavaClass compatBaseline,
            JavaClass currentVersion)
    {
        // create copies to avoid concurrent modification exception
        Set baselineNames = new TreeSet(bNameToMethod.keySet());
        Set currentNames = new TreeSet(cNameToMethod.keySet());

        for (Iterator it = baselineNames.iterator(); it.hasNext();)
        {
            String name = (String) it.next();
            if (!currentNames.contains(name))
            {
                Collection removedMethods = (Collection) bNameToMethod.get(name);
                for (Iterator rmIterator = removedMethods.iterator(); rmIterator.hasNext();)
                {
                    Method method = (Method) rmIterator.next();
                    reportMethodRemoved(compatBaseline, method);
                }
                bNameToMethod.remove(name);
            }
        }

        for (Iterator it = currentNames.iterator(); it.hasNext();)
        {
            String name = (String) it.next();
            if (!baselineNames.contains(name))
            {
                Collection addedMethods = (Collection) cNameToMethod.get(name);
                for (Iterator addIterator = addedMethods.iterator(); addIterator.hasNext();)
                {
                    Method method = (Method) addIterator.next();
                    reportMethodAdded(currentVersion, method);
                }
                cNameToMethod.remove(name);
            }
        }
    }

    private void reportMethodRemoved(JavaClass oldClass, Method oldMethod)
    {
        fireDiff("Method '"
                + getMethodId(oldClass, oldMethod)
                + "' has been removed",
                Severity.ERROR, oldClass, oldMethod);
    }

    private void reportMethodAdded(JavaClass newClass, Method newMethod)
    {

        final Severity severity = !newClass.isInterface() && (newClass.isFinal() || !newMethod.isAbstract())
                ? Severity.INFO
                : Severity.ERROR;

        fireDiff("Method '"
                + getMethodId(newClass, newMethod)
                + "' has been added",
                severity, newClass, newMethod);
    }

    /**
     * Builds a map from a method name to a List of methods.
     */
    private Map buildNameToMethodMap(JavaClass clazz)
    {
        Method[] methods = clazz.getMethods();
        Map retVal = new HashMap();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];

            if (!(method.isPublic() || method.isProtected()))
            {
                continue;
            }

            final String name = method.getName();
            List set = (List) retVal.get(name);
            if (set == null)
            {
                set = new ArrayList();
                retVal.put(name, set);
            }
            set.add(method);
        }
        return retVal;
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
        appendHumanReadableArgTypeList(method, buf);
        buf.append(')');
        return buf.toString();
    }

    private void appendHumanReadableArgTypeList(Method method, StringBuffer buf)
    {
        Type[] argTypes = method.getArgumentTypes();
        String argSeparator = "";
        for (int i = 0; i < argTypes.length; i++)
        {
            buf.append(argSeparator);
            buf.append(argTypes[i].toString());
            argSeparator = ", ";
        }
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
