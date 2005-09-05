package net.sf.clirr.core.internal.checks;

import junit.framework.TestCase;
import net.sf.clirr.core.Checker;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.CheckerFactory;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.bcel.BcelTypeArrayBuilder;
import net.sf.clirr.core.spi.JavaType;

import org.apache.bcel.util.ClassSet;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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

    protected void verify(ExpectedDiff[] expected)
    throws Exception
    {
        TestDiffListener tdl = new TestDiffListener();
        Checker checker = CheckerFactory.createChecker(createCheck(tdl));
        ClassFilter classSelector = createClassSelector();

        final JavaType[] origClasses =
            BcelTypeArrayBuilder.createClassSet(getBaseLine(), new URLClassLoader(new URL[]{}), classSelector);
        
        final JavaType[] newClasses =
            BcelTypeArrayBuilder.createClassSet(getCurrent(), new URLClassLoader(new URL[]{}), classSelector);
        
        checker.reportDiffs(origClasses, newClasses);

        tdl.checkExpected(expected);
    }

    /**
     * Creates an object which selects the appropriate classes from the
     * test jars for this test.
     * <p>
     * This base implementation returns a selector which selects all classes
     * in the base "testlib" package (but no sub-packages). Tests which wish
     * to select different classes from the test jars should override this
     * method.
     */
    protected ClassFilter createClassSelector()
    {
        // only check classes in the base "testlib" package of the jars
        ClassSelector classSelector = new ClassSelector(ClassSelector.MODE_IF);
        classSelector.addPackage("testlib");
        return classSelector;
    }

    /**
     * Creates a check and sets it up so ApiDifferences are reported to the test diff listener.
     *
     * @param tdl the test diff listener that records the recognized api changes.
     * @return the confiured check
     */
    protected abstract ClassChangeCheck createCheck(TestDiffListener tdl);
}
