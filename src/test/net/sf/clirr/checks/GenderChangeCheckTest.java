package net.sf.clirr.checks;

import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.ClassChangeCheck;

public class GenderChangeCheckTest extends AbstractCheckTestCase
{
    public void testGenderChangeCheckTest()
    {
        ApiDifference[] expected = new ApiDifference[] {
            new ApiDifference("Changed Gender of testlib.ClassBecomesInterface", Severity.ERROR),
            new ApiDifference("Changed Gender of testlib.InterfaceBecomesClass", Severity.ERROR),
        };
        verify(expected);
    }

    protected final ClassChangeCheck createCheck(TestDiffListener tdl) {
        return new GenderChangeCheck(tdl);
    }

}
