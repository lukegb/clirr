package net.sf.clirr.checks;

import java.util.Comparator;
import java.util.Arrays;

import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.framework.ApiDiffDispatcher;
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
        public int compare(Object o1, Object o2)
        {
            Method m1 = (Method) o1;
            Method m2 = (Method) o2;

            String name1 = m1.getName();
            String name2 = m2.getName();

            final int nameComparison = name1.compareTo(name2);
            // TODO: compare argcount
            return nameComparison;
        }
    }

    public MethodSetCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public void check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        Method[] baselineMethods = sort(compatBaseline.getMethods());
        Method[] currentMethods = sort(currentVersion.getMethods());

        for (int i = 0; i < baselineMethods.length; i++)
        {
            Method baselineMethod = baselineMethods[i];
            System.out.println("baselineMethod " + i + "= " + getMethodId(compatBaseline, baselineMethod));
        }
    }

    private Method[] sort(Method[] methods)
    {

        Method[] retval = new Method[methods.length];
        // TODO: filter public + protected
        // TODO: remove <clinit>
        System.arraycopy(methods, 0, retval, 0, methods.length);
        Arrays.sort(retval, new MethodComparator());
        return retval;
    }

    private String getMethodId(JavaClass clazz, Method method)
    {
        if (!method.isPublic() && !method.isProtected())
        {
            //throw new IllegalArgumentException();
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
}
