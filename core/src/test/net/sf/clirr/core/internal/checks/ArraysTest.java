package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.internal.checks.ClassModifierCheck;
import net.sf.clirr.core.internal.checks.AbstractCheckTestCase;

/**
 * Tests for the ClassModifierCheck class.
 */
public class ArraysTest extends AbstractCheckTestCase
{
    public void testAll() throws Exception
    {
        ExpectedDiff[] expected = new ExpectedDiff[] {
// TODO: enable the following expected messages:
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public java.lang.String[][] arrayDimDecreases()' has been changed to java.lang.String[]
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public java.lang.String[] arrayDimIncreases()' has been changed to java.lang.String[][]
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public java.lang.String[] objectArrayBecomesObject()' has been changed to java.lang.String
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public java.lang.String objectBecomesArray()' has been changed to java.lang.String[]
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public int[] primitiveArrayBecomesPrimitive()' has been changed to int
// ERROR: 7006: testlib.arrays.Arrays: Return type of method 'public int primitiveBecomesArray()' has been changed to int[]
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
        classSelector.addPackage("testlib.arrays");
        return classSelector;
    }
}
