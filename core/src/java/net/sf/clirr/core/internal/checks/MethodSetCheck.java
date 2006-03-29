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

package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.Message;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.internal.AbstractDiffReporter;
import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.CoIterator;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Checks the methods of a class.
 *
 * @author lkuehne
 */
public class MethodSetCheck
    extends AbstractDiffReporter
    implements ClassChangeCheck
{
    private static final Message MSG_METHOD_NOW_IN_SUPERCLASS = new Message(7000);
    private static final Message MSG_METHOD_NOW_IN_INTERFACE = new Message(7001);
    private static final Message MSG_METHOD_REMOVED = new Message(7002);
    private static final Message MSG_METHOD_OVERRIDE_REMOVED = new Message(7003);
    private static final Message MSG_METHOD_ARGCOUNT_CHANGED = new Message(7004);
    private static final Message MSG_METHOD_PARAMTYPE_CHANGED = new Message(7005);
    private static final Message MSG_METHOD_RETURNTYPE_CHANGED = new Message(7006);
    private static final Message MSG_METHOD_DEPRECATED = new Message(7007);
    private static final Message MSG_METHOD_UNDEPRECATED = new Message(7008);
    private static final Message MSG_METHOD_LESS_ACCESSIBLE = new Message(7009);
    private static final Message MSG_METHOD_MORE_ACCESSIBLE = new Message(7010);
    private static final Message MSG_METHOD_ADDED = new Message(7011);
    private static final Message MSG_METHOD_ADDED_TO_INTERFACE = new Message(7012);
    private static final Message MSG_ABSTRACT_METHOD_ADDED = new Message(7013);
    private static final Message MSG_METHOD_NOW_FINAL = new Message(7014);
    private static final Message MSG_METHOD_NOW_NONFINAL = new Message(7015);

    private ScopeSelector scopeSelector;

    /**
     * Instantiates the check.
     *
     * @param dispatcher the dispatcher where detected differences shoudl be reported.
     * @param scopeSelector defines the scopes to look at when searching for differences.
     */
    public MethodSetCheck(ApiDiffDispatcher dispatcher, ScopeSelector scopeSelector)
    {
        super(dispatcher);
        this.scopeSelector = scopeSelector;
    }

    public final boolean check(JavaType compatBaseline, JavaType currentVersion)
    {
        // Dont't report method problems when gender has changed, as
        // really the whole API is a pile of crap then - let GenderChange check
        // do it's job, and that's it
        if (compatBaseline.isInterface() ^ currentVersion.isInterface())
        {
            return true;
        }

        Map bNameToMethod = buildNameToMethodMap(compatBaseline);
        Map cNameToMethod = buildNameToMethodMap(currentVersion);

        CoIterator iter = new CoIterator(null, bNameToMethod.keySet(), cNameToMethod.keySet());

        while (iter.hasNext())
        {
            iter.next();

            String baselineMethodName = (String) iter.getLeft();
            String currentMethodName = (String) iter.getRight();

            if (baselineMethodName == null)
            {
                // a new method name has been added in the new version
                List currentMethods = (List) cNameToMethod.get(currentMethodName);
                reportMethodsAdded(currentVersion, currentMethods);
            }
            else if (currentMethodName == null)
            {
                // all methods with name x have been removed from the old version
                List baselineMethods = (List) bNameToMethod.get(baselineMethodName);
                reportMethodsRemoved(compatBaseline, baselineMethods, currentVersion);
            }
            else
            {
                // assert baselineMethodName equals currentMethodName

                List baselineMethods = (List) bNameToMethod.get(baselineMethodName);
                List currentMethods = (List) cNameToMethod.get(currentMethodName);

                filterSoftMatchedMethods(
                    compatBaseline, baselineMethods,
                    currentVersion, currentMethods);

                filterChangedMethods(
                    baselineMethodName,
                    compatBaseline, baselineMethods,
                    currentVersion, currentMethods);

                // if any methods are left, they have no matching method in
                // the other version, so report as removed or added respectively.

                if (!baselineMethods.isEmpty())
                {
                    reportMethodsRemoved(compatBaseline, baselineMethods, currentVersion);
                }

                if (!currentMethods.isEmpty())
                {
                    reportMethodsAdded(currentVersion, currentMethods);
                }
            }
        }

        return true;
    }

    /**
     * Given a list of old and new methods for a particular method name,
     * find the (old, new) method pairs which have identical argument lists.
     * <p>
     * For these:
     * <ul>
     *  <li>report on changes in accessibility, return type, etc
     *  <li>remove from the list
     * </ul>
     *
     * On return from this method, the old and new method lists contain only
     * methods whose argument lists have changed between versions [or possibly,
     * methods which have been deleted while one or more new methods of the
     * same name have been added, depending on how you view it]. All other
     * situations have been dealt with.
     * <p>
     * Note that one or both method lists may be empty on return from
     * this method.
     */
    private void filterSoftMatchedMethods(
            JavaType compatBaseline,
            List baselineMethods,
            JavaType currentVersion,
            List currentMethods)
    {
        for (Iterator bIter = baselineMethods.iterator(); bIter.hasNext();)
        {
            Method bMethod = (Method) bIter.next();

            for (Iterator cIter = currentMethods.iterator(); cIter.hasNext();)
            {
                Method cMethod = (Method) cIter.next();

                if (isSoftMatch(bMethod, cMethod))
                {
                    check(compatBaseline, bMethod, cMethod);
                    bIter.remove();
                    cIter.remove();
                    break;
                }
            }
        }
    }

    /**
     * Two methods are a "soft" match if they have the same name and argument
     * list. No two methods on the same class are ever a "soft match" for
     * each other, because the compiler requires distinct parameter lists for
     * overloaded methods. This also implies that for a given method on an "old"
     * class version, there are either zero or one "soft matches" on the new
     * version.
     * <p>
     * However a "soft match" is not sufficient to ensure binary compatibility.
     * A change in the method return type will result in a link error when used
     * with code compiled against the previous version of the class.
     * <p>
     * There may also be other differences between methods that are regarded
     * as "soft matches": the exceptions thrown, the deprecation status of the
     * methods, their accessibility, etc.
     */
    private boolean isSoftMatch(Method oldMethod, Method newMethod)
    {
        String oldName = oldMethod.getName();
        String newName = newMethod.getName();

        if (!oldName.equals(newName))
        {
            return false;
        }

        StringBuffer buf = new StringBuffer();
        appendHumanReadableArgTypeList(oldMethod, buf);
        String oldArgs = buf.toString();

        buf.setLength(0);
        appendHumanReadableArgTypeList(newMethod, buf);
        String newArgs = buf.toString();

        return (oldArgs.equals(newArgs));
    }

    /**
     * For each method in the baselineMethods list, find the "best match"
     * in the currentMethods list, report the changes between this method
     * pair, then remove both methods from the lists.
     * <p>
     * On return, at least one of the method lists will be empty.
     */
    private void filterChangedMethods(
            String methodName,
            JavaType compatBaseline,
            List baselineMethods,
            JavaType currentVersion,
            List currentMethods)
    {
        // ok, we now have to deal with the tricky cases, where it is not
        // immediately obvious which old methods correspond to which new
        // methods.
        //
        // Here we build a similarity table, i.e. for new method i and old
        // method j we have number that charaterizes how similar the method
        // signatures are (0 means equal, higher number means more different)

        while (!baselineMethods.isEmpty() && !currentMethods.isEmpty())
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
        final JavaType[] m1Args = m1.getArgumentTypes();
        final JavaType[] m2Args = m2.getArgumentTypes();

        if (m1Args.length != m2Args.length)
        {
            return 1000 * Math.abs(m1Args.length - m2Args.length);
        }

        int retVal = 0;
        for (int i = 0; i < m1Args.length; i++)
        {
            if (!m1Args[i].getName().equals(m2Args[i].getName()))
            {
                retVal += 1;
            }
        }
        return retVal;
    }

    /**
     * Searches the class hierarchy for a method that has a certain signature.
     * @param methodSignature the sig we're looking for
     * @param clazz class where search starts
     * @return class name of a superclass of clazz, might be null
     */
    private String findSuperClassWithSignature(String methodSignature, JavaType clazz)
    {
        final JavaType[] superClasses = clazz.getSuperClasses();
        for (int i = 0; i < superClasses.length; i++)
        {
            JavaType superClass = superClasses[i];
            final Method[] superMethods = superClass.getMethods();
            for (int j = 0; j < superMethods.length; j++)
            {
                Method superMethod = superMethods[j];
                final String superMethodSignature = getMethodId(superClass, superMethod);
                if (methodSignature.equals(superMethodSignature))
                {
                    return superClass.getName();
                }
            }

        }
        return null;
    }

    /**
     * Searches the class hierarchy for a method that has a certtain signature.
     * @param methodSignature the sig we're looking for
     * @param clazz class where search starts
     * @return class name of a superinterface of clazz, might be null
     */
    private String findSuperInterfaceWithSignature(String methodSignature, JavaType clazz)
    {
        final JavaType[] superClasses = clazz.getAllInterfaces();
        for (int i = 0; i < superClasses.length; i++)
        {
            JavaType superClass = superClasses[i];
            final Method[] superMethods = superClass.getMethods();
            for (int j = 0; j < superMethods.length; j++)
            {
                Method superMethod = superMethods[j];
                final String superMethodSignature = getMethodId(superClass, superMethod);
                if (methodSignature.equals(superMethodSignature))
                {
                    return superClass.getName();
                }
            }

        }
        return null;
    }

    /**
     * Given a list of methods, report each one as being removed.
     */
    private void reportMethodsRemoved(
            JavaType baselineClass,
            List baselineMethods,
            JavaType currentClass)
    {
        for (Iterator i = baselineMethods.iterator(); i.hasNext();)
        {
            Method method = (Method) i.next();
            reportMethodRemoved(baselineClass, method, currentClass);
        }
    }

    /**
     * Report that a method has been removed from a class.
     * @param oldClass the class where the method was available
     * @param oldMethod the method that has been removed
     * @param currentClass the superclass where the method is now available, might be null
     */
    private void reportMethodRemoved(
            JavaType oldClass,
            Method oldMethod,
            JavaType currentClass)
    {
        if (!scopeSelector.isSelected(oldMethod))
        {
            return;
        }

        String signature = getMethodId(oldClass, oldMethod);

        String oldBaseClassForMethod = findSuperClassWithSignature(signature, oldClass);
        String oldInterfaceForMethod = findSuperInterfaceWithSignature(signature, oldClass);

        String newBaseClassForMethod = findSuperClassWithSignature(signature, currentClass);
        String newInterfaceForMethod = findSuperInterfaceWithSignature(signature, currentClass);

        boolean oldInheritedMethod = (oldBaseClassForMethod != null) || (oldInterfaceForMethod != null);
        boolean newInheritedMethod = (newBaseClassForMethod != null) || (newInterfaceForMethod != null);

        if (oldInheritedMethod && newInheritedMethod)
        {
            // Previously, this method overrode an inherited definition.
            // The current version of the class doesn't have this
            // method, but a parent class or interface still does, so this
            // does not cause an incompatibility.
            fireDiff(MSG_METHOD_OVERRIDE_REMOVED,
                    Severity.INFO,
                    oldClass, oldMethod, null);
        }
        else if (oldInheritedMethod)
        {
            // Previously, this method override an inherited definition.
            // It isn't present in the current class, though, and neither is
            // it present in the new class' ancestors. Best to just
            // report it as removed...
            fireDiff(MSG_METHOD_REMOVED,
                    getSeverity(oldClass, oldMethod, Severity.ERROR),
                    oldClass, oldMethod, null);
        }
        else if (newBaseClassForMethod != null)
        {
            // Previously, this method didn't override anything. The current
            // version of this class doesn't have this method any more,
            // but an ancestor class now *does*. This is an instance
            // of the pull-up refactoring pattern, where a method is moved
            // to an ancestor class.
            fireDiff(MSG_METHOD_NOW_IN_SUPERCLASS,
                    Severity.INFO, oldClass, oldMethod,
                    new String[] {newBaseClassForMethod});
        }
        else if (newInterfaceForMethod != null)
        {
            // Previously, this method didn't override anything. The current
            // version of this class doesn't have this method any more,
            // but one of the implemented interfaces now *does*. This is an
            // instance of the pull-up refactoring pattern, where a method is
            // moved to an interface.
            fireDiff(MSG_METHOD_NOW_IN_INTERFACE,
                    Severity.INFO, oldClass, oldMethod,
                    new String[] {newInterfaceForMethod});
        }
        else
        {
            // This method wasn't anything special in the old class, and
            // it isn't present in the new class either directly or via
            // inheritance.
            fireDiff(MSG_METHOD_REMOVED,
                    getSeverity(oldClass, oldMethod, Severity.ERROR),
                    oldClass, oldMethod, null);
        }
    }

    /**
     * Given a list of methods, report each one as being added.
     */
    private void reportMethodsAdded(
            JavaType currentClass,
            List currentMethods)
    {
        for (Iterator i = currentMethods.iterator(); i.hasNext();)
        {
            Method method = (Method) i.next();
            reportMethodAdded(currentClass, method);
        }
    }

    /**
     * Report that a method has been added to a class.
     */
    private void reportMethodAdded(JavaType newClass, Method newMethod)
    {
        if (!scopeSelector.isSelected(newMethod))
        {
            return;
        }

        if (newClass.isInterface())
        {
            // TODO: this is not an incompatibility if the new method
            // actually already exists on a parent interface of the
            // old interface. In that case, any class implementing the
            // old version of this interface must already have an
            // implementation of this method. See bugtracker #961217
            fireDiff(MSG_METHOD_ADDED_TO_INTERFACE,
                    getSeverity(newClass, newMethod, Severity.ERROR),
                    newClass, newMethod, null);
        }
        else if (newMethod.isAbstract())
        {
            // TODO: this is not an incompatibility if the new method
            // actually already exists on a parent interface of the
            // old interface and was abstract. In that case, any class
            // extending the old version of this class must already
            // have an implementation of this method.
            //
            // Note that abstract methods can never be package or private
            // scope, so we don't need to use the getSeverity method.
            fireDiff(MSG_ABSTRACT_METHOD_ADDED,
                    Severity.ERROR, newClass, newMethod, null);
        }
        else
        {
            // TODO:
            //. (a) check whether this method exists on a parent of the
            //  new class. If so, indicate that this new method is overriding
            //  some inherited method.
            //  (b) if not a, then check whether this method exists on a parent
            //  of the old class. If so, then report that the method has
            //  been moved from the parent to the child class. This is
            //  potentially useful info for the user.
            //
            // See bugtracker #959225
            fireDiff(MSG_METHOD_ADDED,
                    Severity.INFO, newClass, newMethod, null);
        }
    }

    /**
     * Builds a map from a method name to a List of methods.
     */
    private Map buildNameToMethodMap(JavaType clazz)
    {
        Method[] methods = clazz.getMethods();
        Map retVal = new HashMap();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];

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

    private void check(JavaType compatBaseline, Method baselineMethod, Method currentMethod)
    {
        if (!scopeSelector.isSelected(baselineMethod) && !scopeSelector.isSelected(currentMethod))
        {
            return;
        }

        checkParameterTypes(compatBaseline, baselineMethod, currentMethod);
        checkReturnType(compatBaseline, baselineMethod, currentMethod);
        checkDeclaredExceptions(compatBaseline, baselineMethod, currentMethod);
        checkDeprecated(compatBaseline, baselineMethod, currentMethod);
        checkVisibility(compatBaseline, baselineMethod, currentMethod);
        checkFinal(compatBaseline, baselineMethod, currentMethod);
    }

    private void checkParameterTypes(JavaType compatBaseline, Method baselineMethod, Method currentMethod)
    {
        JavaType[] bArgs = baselineMethod.getArgumentTypes();
        JavaType[] cArgs = currentMethod.getArgumentTypes();

        if (bArgs.length != cArgs.length)
        {
            fireDiff(MSG_METHOD_ARGCOUNT_CHANGED,
                    getSeverity(compatBaseline, baselineMethod, Severity.ERROR),
                    compatBaseline, baselineMethod, null);
            return;
        }

        for (int i = 0; i < bArgs.length; i++)
        {
            JavaType bArg = bArgs[i];
            JavaType cArg = cArgs[i];

            if (bArg.getName().equals(cArg.getName()))
            {
                continue;
            }

            // TODO: Check assignability...
            String[] args =
            {
                "" + (i + 1),
                cArg.getName()
            };
            fireDiff(MSG_METHOD_PARAMTYPE_CHANGED,
                    getSeverity(compatBaseline, baselineMethod, Severity.ERROR),
                    compatBaseline, baselineMethod, args);
        }
    }

    private void checkReturnType(JavaType compatBaseline, Method baselineMethod, Method currentMethod)
    {
        JavaType bReturnType = baselineMethod.getReturnType();
        JavaType cReturnType = currentMethod.getReturnType();

        if (bReturnType == null && cReturnType == null)
        {
            return;
        }
        
        // TODO: Check assignability. If the new return type is
        // assignable to the old type, then the code is source-code
        // compatible even when binary-incompatible.
        
        if (bReturnType != null && cReturnType != null && bReturnType.getName().equals(cReturnType.getName()))
        {
            return;
        }

        final String name = cReturnType == null ? "void" : cReturnType.getName();
        fireDiff(MSG_METHOD_RETURNTYPE_CHANGED,
                getSeverity(compatBaseline, baselineMethod, Severity.ERROR),
                compatBaseline, baselineMethod,
                new String[] {name});
    }

    private void checkDeclaredExceptions(
            JavaType compatBaseline,
            Method baselineMethod, Method currentMethod)
    {
        // TODO
    }

    private void checkDeprecated(
            JavaType compatBaseline,
            Method baselineMethod, Method currentMethod)
    {
        boolean bIsDeprecated = baselineMethod.isDeprecated();
        boolean cIsDeprecated = currentMethod.isDeprecated();

        if (bIsDeprecated && !cIsDeprecated)
        {
            fireDiff(MSG_METHOD_UNDEPRECATED,
                    Severity.INFO, compatBaseline, baselineMethod, null);
        }
        else if (!bIsDeprecated && cIsDeprecated)
        {
            fireDiff(MSG_METHOD_DEPRECATED,
                    Severity.INFO, compatBaseline, baselineMethod, null);
        }
    }

    /**
     * Report changes in the declared accessibility of a method
     * (public/protected/etc).
     */
    private void checkVisibility(JavaType compatBaseline, Method baselineMethod, Method currentMethod)
    {
        Scope bScope = baselineMethod.getEffectiveScope();
        Scope cScope = currentMethod.getEffectiveScope();

        if (cScope.isLessVisibleThan(bScope))
        {
            String[] args = {bScope.getDesc(), cScope.getDesc()};
            fireDiff(MSG_METHOD_LESS_ACCESSIBLE,
                    getSeverity(compatBaseline, baselineMethod, Severity.ERROR),
                    compatBaseline, baselineMethod, args);
        }
        else if (cScope.isMoreVisibleThan(bScope))
        {
            String[] args = {bScope.getDesc(), cScope.getDesc()};
            fireDiff(MSG_METHOD_MORE_ACCESSIBLE,
                    Severity.INFO, compatBaseline, baselineMethod, args);
        }
    }

    private void checkFinal(
            JavaType compatBaseline,
            Method baselineMethod, Method currentMethod)
    {
        boolean bIsFinal = baselineMethod.isFinal();
        boolean cIsFinal = currentMethod.isFinal();

        if (bIsFinal && !cIsFinal)
        {
            fireDiff(MSG_METHOD_NOW_NONFINAL,
                    Severity.INFO, compatBaseline, baselineMethod, null);
        }
        else if (!bIsFinal && cIsFinal)
        {
            fireDiff(MSG_METHOD_NOW_FINAL,
                    Severity.ERROR, compatBaseline, baselineMethod, null);
        }
    }

    /**
     * Creates a human readable String that is similar to the method signature
     * and identifies the method within a class.
     * @param clazz the container of the method
     * @param method the method to identify.
     * @return a human readable id, for example "public void print(java.lang.String)"
     */
    private String getMethodId(JavaType clazz, Method method)
    {
        StringBuffer buf = new StringBuffer();

        final String scopeDecl = method.getDeclaredScope().getDecl();
        if (scopeDecl.length() > 0)
        {
            buf.append(scopeDecl);
            buf.append(" ");
        }

        String name = method.getName();
        if ("<init>".equals(name))
        {
            final String className = clazz.getName();
            int idx = className.lastIndexOf('.');
            name = className.substring(idx + 1);
        }
        else
        {
            final JavaType returnType = method.getReturnType();
            buf.append(returnType == null ? "void" : returnType.getName());
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
        JavaType[] argTypes = method.getArgumentTypes();
        String argSeparator = "";
        for (int i = 0; i < argTypes.length; i++)
        {
            buf.append(argSeparator);
            buf.append(argTypes[i].getName());
            argSeparator = ", ";
        }
    }

    private void fireDiff(Message msg, Severity severity, JavaType clazz, Method method, String[] args)
    {
        final String className = clazz.getName();
        final ApiDifference diff =
            new ApiDifference(
                msg, severity, className, getMethodId(clazz, method), null, args);
        getApiDiffDispatcher().fireDiff(diff);
    }

}
