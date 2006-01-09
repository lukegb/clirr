package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.Checker;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;

/**
 * Abstract Baseclass for a regression test that covers a bug reported in our bug tracker.
 * @author lk
 */
public abstract class AbstractRegressionTestCase extends AbstractCheckerTestCase
{
    protected abstract String getTrackerId();
    
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
