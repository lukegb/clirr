package net.sf.clirr.core.internal.checks;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;
import net.sf.clirr.core.Checker;
import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.spi.DefaultTypeArrayBuilderFactory;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.TypeArrayBuilder;

/**
 * Abstract baseclass for unit tests that check the output of the {@link Checker}.
 *
 * @author lk
 */
public abstract class AbstractCheckerTestCase extends TestCase
{
    private final TestDiffListener tdl = new TestDiffListener();

    /**
     * Returns the directory where test input jars are located.
     * <p>
     * Note that this method requires a System property 'testinput' to point to the directory.
     * This is handled automatically during by the normal build but
     * requires manual intervention so it the tests can be run from the IDE.
     * <p>
     * For example in Eclipse, you need to add a VM argument like
     * <code>-Dtestinput=target/testinput</code> in your JUnit launch configuration.
     */
    protected final File getTestInputDir()
    {
        // property is set in project.properties
        return new File(System.getProperty("testinput"));
    }

    /**
     * Returns the baseline classpath entry files to test against (the old version).
     */
    protected File[] getBaseLine()
    {
        return new File[]{
            new File(getTestInputDir(), "testlib-v1.jar")
        };
    }

    /**
     * Returns the current set of classpath entry files (the new version).
     */
    protected File[] getCurrent()
    {
        return new File[]{
            new File(getTestInputDir(), "testinput/testlib-v2")
        };
    }

    /**
     * Call runChecker and verifies the comparison results.
     * @param expected the expected differences
     */
    protected void verify(ExpectedDiff[] expected)
    throws Exception
    {
        runChecker();

        tdl.checkExpected(expected);
    }

    /**
     * Creates a Checker, wires it with a test listener, executes the
     * checker.
     */
    protected void runChecker() throws CheckerException {
        Checker checker = createChecker();
        ClassFilter classSelector = createClassFilter();

        DefaultTypeArrayBuilderFactory tabFactory = new DefaultTypeArrayBuilderFactory();
        TypeArrayBuilder tabOrig = tabFactory.build();
        TypeArrayBuilder tabNew = tabFactory.build();
        final JavaType[] origClasses =
            tabOrig.createClassSet(getBaseLine(), new URLClassLoader(new URL[]{}), classSelector);

        final JavaType[] newClasses =
            tabNew.createClassSet(getCurrent(), new URLClassLoader(new URL[]{}), classSelector);

        checker.reportDiffs(origClasses, newClasses);
    }

    /**
     * Creates a filter which selects the appropriate classes from the
     * test jars for this unit test.
     */
    protected abstract ClassFilter createClassFilter();

    /**
     * Creates the Checker that is configured to run this test.
     */
    protected abstract Checker createChecker();

    protected final TestDiffListener getTestDiffListener()
    {
        return tdl;
    }

}
