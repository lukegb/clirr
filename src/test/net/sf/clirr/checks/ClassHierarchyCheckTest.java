package net.sf.clirr.checks;

import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;

/**
 * @author lkuehne
 */
public class ClassHierarchyCheckTest extends AbstractCheckTestCase
{
    public void testHierarchyChangesAreReported()
    {
        ApiDifference[] expected = new ApiDifference[] {
            new ApiDifference("Added java.lang.RuntimeException to the list of superclasses of testlib.ApplicationException", Severity.WARNING, "testlib.ApplicationException", null, null),
            new ApiDifference("Removed java.awt.event.MouseAdapter from the list of superclasses of testlib.ChangingHierarchy", Severity.ERROR, "testlib.ChangingHierarchy", null, null),
            new ApiDifference("Added java.awt.event.WindowAdapter to the list of superclasses of testlib.ChangingHierarchy", Severity.INFO, "testlib.ChangingHierarchy", null, null),
        };
        verify(expected);
    }

    protected ClassChangeCheck createCheck(TestDiffListener tdl)
    {
        return new ClassHierarchyCheck(tdl);
    }
}
