package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.internal.checks.ClassModifierCheck;
import net.sf.clirr.core.internal.checks.AbstractCheckTestCase;

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

    protected ClassChangeCheck createCheck()
    {
        return new ClassModifierCheck(getTestDiffListener());
    }

    protected ClassFilter createClassFilter()
    {
        // only apply the check to classes in the testlib.modifiers package.
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addPackage("testlib.modifiers");
        return classSelector;
    }
}
