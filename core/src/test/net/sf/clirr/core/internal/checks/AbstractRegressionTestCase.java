package net.sf.clirr.core.internal.checks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.clirr.core.Checker;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;

/**
 * Abstract Baseclass for a regression test that covers a bug reported in our bug tracker.
 * @author lk
 */
public abstract class AbstractRegressionTestCase extends AbstractCheckerTestCase
{
    private Pattern STD_REGRESSION_TEST_PATTERN = Pattern.compile("^.*Regression(.*)Test$");
    
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

    protected ClassFilter createClassFilter() 
    {
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addPackageTree("testlib.regressions.bug" + getTrackerId());
        return classSelector;
    }

    protected Checker createChecker() 
    {
        final Checker checker = new Checker();
        checker.addDiffListener(getTestDiffListener());
        return checker;
    }

}
