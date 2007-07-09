package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.spi.Scope;

/**
 * Tests for the ClassScopeCheck test.
 *
 * @author Simon Kitching
 */
public class ClassScopeCheckTest extends AbstractCheckTestCase
{
    public void testAccessChangesAreReported() throws Exception
    {
        // note that
        // - protected inner classes are technically public but have a protected constructor.
        // - private inner classes are technically package visible
        // Hence on the class level there are fewer changes than one might expect
        // Don't believe this? Try using the JDK's javap tool on the inner classes! 
        ExpectedDiff[] expected = new ExpectedDiff[] {

                // A1 is unchanged

                // A2 is still technically a public class in v2

                new ExpectedDiff("Decreased visibility of class from public to package", Severity.ERROR,
                        "testlib.scope.ClassScopeChange$A3", null, null),

                // A4 is technically package visible in V2
                new ExpectedDiff("Decreased visibility of class from public to package", Severity.ERROR,
                        "testlib.scope.ClassScopeChange$A4", null, null),

                // B1 is unchanged

                // B2 has technically been a public class in v1

                // B3 is technically public in v1
                new ExpectedDiff("Decreased visibility of class from public to package", Severity.ERROR,
                        "testlib.scope.ClassScopeChange$B3", null, null),

                // B4 is technically public in v1 and package visible in v2
                new ExpectedDiff("Decreased visibility of class from public to package", Severity.ERROR,
                        "testlib.scope.ClassScopeChange$B4", null, null),

                new ExpectedDiff("Increased visibility of class from package to public", Severity.INFO,
                        "testlib.scope.ClassScopeChange$C2", null, null),

                // B4 is technically package visible in v1 and public in v2
                new ExpectedDiff("Increased visibility of class from package to public", Severity.INFO,
                        "testlib.scope.ClassScopeChange$C3", null, null),

                // C4 is technically package visible in v2

                // D1 is technically package visible in v1

                new ExpectedDiff("Increased visibility of class from package to public", Severity.INFO,
                        "testlib.scope.ClassScopeChange$D2", null, null),

                new ExpectedDiff("Increased visibility of class from package to public", Severity.INFO,
                        "testlib.scope.ClassScopeChange$D3", null, null),

                // D4 is technically package visible in v1
        };
        verify(expected);
    }

    protected ClassChangeCheck createCheck()
    {
        ScopeSelector scopeSelector = new ScopeSelector(Scope.PRIVATE);
        return new ClassScopeCheck(getTestDiffListener(), scopeSelector);
    }

    protected ClassFilter createClassFilter()
    {
        // only check the testlib/scope/ClassScopeChange class.
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addClass("testlib.scope.ClassScopeChange");
        return classSelector;
    }
}