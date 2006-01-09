package net.sf.clirr.core.internal.checks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.clirr.core.Severity;

/**
 * Regression test for bug 
 * <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1373831&group_id=89627&atid=590799">1373831</a>.
 *  
 * @author lkuehne
 */
public class Regression1373831Test extends AbstractRegressionTestCase
{
    Pattern STD_REGRESSION_TEST_PATTERN = Pattern.compile("^.*Regression(.*)Test$");
    
    protected String getTrackerId() 
    {
        final String fqTestName = this.getClass().getName();
        final Matcher matcher = STD_REGRESSION_TEST_PATTERN.matcher(fqTestName);
        if (matcher.matches())
        {
            final String group = matcher.group(1);
            return group;
        }
        throw new UnsupportedOperationException("The default implementation works only for classes that follow the naming scheme 'Regression<id>Test'");
    }

    public void testRegression()
    {
        runChecker();
        final TestDiffListener testDiffListener = getTestDiffListener();
        // TODO: fix the bug and enable the following assertions
        // assertEquals("false alarm (binary error)", 0, testDiffListener.countBinaryCompatibilityDiffs(Severity.ERROR));
        // assertEquals("false alarm (source error)", 0, testDiffListener.countSourceCompatibilityDiffs(Severity.ERROR));
        assertEquals("false alarm (binary warning)", 0, testDiffListener.countBinaryCompatibilityDiffs(Severity.WARNING));
        assertEquals("false alarm (source warning)", 0, testDiffListener.countSourceCompatibilityDiffs(Severity.WARNING));
    }
}
