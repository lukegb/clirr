package net.sf.clirr.checks;

import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.ClassChangeCheck;

public class GenderChangeCheckTest extends AbstractCheckTestCase
{
    public void testGenderChangeCheckTest() throws Exception
    {
        ExpectedDiff[] expected = new ExpectedDiff[] {
            new ExpectedDiff("Changed from class to interface", Severity.ERROR, "testlib.ClassBecomesInterface", null, null),
            new ExpectedDiff("Changed from interface to class", Severity.ERROR, "testlib.InterfaceBecomesClass", null, null),
        };
        verify(expected);
    }

    protected final ClassChangeCheck createCheck(TestDiffListener tdl) {
        return new GenderChangeCheck(tdl);
    }

}
