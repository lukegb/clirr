package net.sf.clirr.checks;

import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.framework.ApiDiffDispatcher;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

/**
 * Checks the methods of a class.
 *
 * @author lkuehne
 */
public class MethodSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    public MethodSetCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public void check(JavaClass compatBaseline, JavaClass currentVersion)
    {
        Method[] baselineMethods = compatBaseline.getMethods();
        Method[] currentMethods = currentVersion.getMethods();

        for (int i = 0; i < currentMethods.length; i++)
        {
            Method currentMethod = currentMethods[i];
            System.out.println("currentMethod " + i + "= " + currentMethod);
        }
    }

    /**
     * Returns an identifier for the method. If the ID has't changed
     * @return
     */
    private String getMethodId()
    {
	return null;
    }
}
