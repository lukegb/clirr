package net.sf.clirr.checks;

import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;

/**
 * TODO: Docs.
 *
 * @author lkuehne
 */
public class MethodSetCheckTest extends AbstractCheckTestCase
{
    public void testMethodCheck()
    {
        ApiDifference[] expected = new ApiDifference[] {

            // method visibility changes
            //new ApiDifference("Accessibility of method 'public int getPriv2()' has been weakened in testlib.MethodsChange",
            //        Severity.ERROR, "testlib.MethodsChange", "public int getPriv2()", null),

            // parameter type changes
            //new ApiDifference("Parameter type of method 'public void changeParamterType(java.lang.String)' has changed.",
            //        Severity.ERROR, "testlib.MethodsChange", "public void changeParamterType(java.lang.String)", null),
            //new ApiDifference("Parameter type of method 'public void weakenParamterType(java.lang.String)' has changed.",
            //        Severity.ERROR, "testlib.MethodsChange", "public void weakenParamterType(java.lang.String)", null),
            //new ApiDifference("Parameter type of method 'public void strengthenParamterType(java.lang.String)' has changed.",
            //        Severity.INFO, "testlib.MethodsChange", "public void strengthenParamterType(java.lang.Object)", null),

            // Constructor changes
            /*
            new ApiDifference("Method 'public void removedMethod(java.lang.String)' has been removed in testlib.MethodsChange",
                    Severity.ERROR, "testlib.MethodsChange", "public void removedMethod(java.lang.String)", null),
            new ApiDifference("Constructor 'protected MethodsChange(int)' has been removed in testlib.MethodsChange",
                    Severity.ERROR, "testlib.MethodsChange", "public MethodsChange(int)", null),
            new ApiDifference("Constructor 'protected MethodsChange(java.lang.Integer)' has been added in testlib.MethodsChange",
                    Severity.INFO, "testlib.MethodsChange", "public MethodsChange(java.lang.Integer)", null),
            new ApiDifference("Constructor 'protected MethodsChange(int, boolean)' has been added in testlib.MethodsChange",
                    Severity.INFO, "testlib.MethodsChange", "public MethodsChange(int, boolean)", null),
            */

            // return type changes
            /*
            new ApiDifference("Return type of Method 'public java.lang.Number getPrivAsNumber()' has been changed to java.lang.Integer",
                    Severity.INFO, "testlib.MethodsChange", "public java.lang.Number getPrivAsNumber()", null),
            new ApiDifference("Return type of Method 'public java.lang.Integer getPrivAsInteger()' has been changed to java.lang.Number",
                    Severity.ERROR, "testlib.MethodsChange", "public java.lang.Integer getPrivAsInteger()", null),
            */

            // parameter list changes
            // TODO

            // declared exceptions
            // TODO
        };
        verify(expected);
    }

    protected final ClassChangeCheck createCheck(TestDiffListener tdl)
    {
        return new MethodSetCheck(tdl);
    }
}
