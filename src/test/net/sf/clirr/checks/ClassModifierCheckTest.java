package net.sf.clirr.checks;

import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;
import net.sf.clirr.event.ScopeSelector;
import net.sf.clirr.framework.ClassSelector;

/**
 * Tests for the ClassModifierCheck class.
 */
public class ClassModifierCheckTest extends AbstractCheckTestCase
{
    public void testAll()
    {
        ApiDifference[] expected = new ApiDifference[] {
            new ApiDifference("Added final modifier in class testlib.modifiers.NonFinalBecomesFinal", Severity.ERROR, "testlib.modifiers.NonFinalBecomesFinal", null, null),
            new ApiDifference("Added final modifier in class testlib.modifiers.EffectivelyFinal (but class was effectively final anyway)", Severity.INFO, "testlib.modifiers.EffectivelyFinal", null, null),
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
