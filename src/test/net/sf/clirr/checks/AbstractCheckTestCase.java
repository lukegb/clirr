package net.sf.clirr.checks;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;
import net.sf.clirr.Checker;
import net.sf.clirr.CheckerFactory;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.framework.ClassChangeCheck;
import org.apache.bcel.util.ClassSet;

/**
 * Abstract Baseclass to test individual Checks.
 * @author lkuehne
 */
public abstract class AbstractCheckTestCase extends TestCase
{
    protected final File getTestInputDir()
    {
        // property is set in project.properties
        return new File(System.getProperty("testinput"));
    }

    protected void verify(
            Checker checker,
            ClassSet baseline, ClassSet current,
            ApiDifference[] expected)
    {
    }

    protected File[] getBaseLine()
    {
        return new File[]{
            new File(getTestInputDir(), "testlib-v1.jar")
        };
    }

    protected File[] getCurrent()
    {
        return new File[]{
            new File(getTestInputDir(), "testlib-v2.jar")
        };
    }

    protected void verify(ApiDifference[] expected)
    {
        TestDiffListener tdl = new TestDiffListener();
        Checker checker = CheckerFactory.createChecker(createCheck(tdl));
        checker.reportDiffs(getBaseLine(), getCurrent(), new URLClassLoader(new URL[]{}), new URLClassLoader(new URL[]{}));
        tdl.checkExpected(expected);
    }

    /**
     * Creates a check and sets it up so ApiDifferences are reported to the test diff listener.
     *
     * @param tdl the test diff listener that records the recognized api changes.
     * @return the confiured check
     */
    protected abstract ClassChangeCheck createCheck(TestDiffListener tdl);
}
