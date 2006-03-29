package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.spi.Scope;

/**
 * Tests for the ClassModifierCheck class.
 */
public class ArraysTest extends AbstractCheckTestCase
{
    public void testReturnTypeChanges() throws Exception
    {
        ExpectedDiff[] expected = new ExpectedDiff[] {
                new ExpectedDiff("Return type of method 'public java.lang.String[][] arrayDimDecreases()' has been changed to java.lang.String[]",
                        Severity.ERROR, "testlib.arrays.Arrays", "public java.lang.String[][] arrayDimDecreases()", null),
                new ExpectedDiff("Return type of method 'public java.lang.String[] arrayDimIncreases()' has been changed to java.lang.String[][]",
                        Severity.ERROR, "testlib.arrays.Arrays", "public java.lang.String[] arrayDimIncreases()", null),
                new ExpectedDiff("Return type of method 'public java.lang.String[] objectArrayBecomesObject()' has been changed to java.lang.String",
                        Severity.ERROR, "testlib.arrays.Arrays", "public java.lang.String[] objectArrayBecomesObject()", null),
                new ExpectedDiff("Return type of method 'public int primitiveBecomesArray()' has been changed to int[]",
                        Severity.ERROR, "testlib.arrays.Arrays", "public int primitiveBecomesArray()", null),
                new ExpectedDiff("Return type of method 'public java.lang.String objectBecomesArray()' has been changed to java.lang.String[]",
                        Severity.ERROR, "testlib.arrays.Arrays", "public java.lang.String objectBecomesArray()", null),
                new ExpectedDiff("Return type of method 'public int[] primitiveArrayBecomesPrimitive()' has been changed to int",
                        Severity.ERROR, "testlib.arrays.Arrays", "public int[] primitiveArrayBecomesPrimitive()", null),
        };
        verify(expected);
    }

    protected ClassChangeCheck createCheck()
    {
        return new MethodSetCheck(getTestDiffListener(), new ScopeSelector(Scope.PROTECTED));
    }

    protected ClassFilter createClassFilter()
    {
        // only apply the check to classes in the testlib.modifiers package.
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addPackage("testlib.arrays");
        return classSelector;
    }
}
