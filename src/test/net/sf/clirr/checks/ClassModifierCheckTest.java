package net.sf.clirr.checks;

import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.event.Severity;
import net.sf.clirr.framework.ClassSelector;

/**
 * Tests for the ClassModifierCheck class.
 */
public class ClassModifierCheckTest extends AbstractCheckTestCase
{
    public void testAll() throws Exception
    {
        ExpectedDiff[] expected = new ExpectedDiff[] {
            new ExpectedDiff("Added final modifier to class", Severity.ERROR, "testlib.modifiers.NonFinalBecomesFinal", null, null),
            new ExpectedDiff("Added final modifier to class, but class was effectively final anyway", Severity.INFO, "testlib.modifiers.EffectivelyFinal", null, null),
        };
        verify(expected);
    }

    protected ClassChangeCheck createCheck(TestDiffListener tdl)
    {
        return new ClassModifierCheck(tdl);
    }

    protected ClassSelector createClassSelector()
    {
        // only apply the check to classes in the testlib.modifiers package.
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addPackage("testlib.modifiers");
        return classSelector;
    }
}
