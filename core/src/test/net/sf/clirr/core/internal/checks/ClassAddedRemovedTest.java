package net.sf.clirr.core.internal.checks;

import java.net.URL;
import java.net.URLClassLoader;

import net.sf.clirr.core.Checker;
import net.sf.clirr.core.CheckerFactory;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.spi.DefaultTypeArrayBuilderFactory;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.TypeArrayBuilder;

public class ClassAddedRemovedTest extends AbstractCheckTestCase
{
    public void testClassAdditionOrRemovalIsReported() throws Exception
    {
        Checker checker = CheckerFactory.createChecker(null);
        TestDiffListener tld = new TestDiffListener();
        checker.addDiffListener(tld);
        
        ClassFilter classSelector = createClassFilter();

        DefaultTypeArrayBuilderFactory tabFactory = new DefaultTypeArrayBuilderFactory();
        TypeArrayBuilder tabOrig = tabFactory.build();
        TypeArrayBuilder tabNew = tabFactory.build();
        final JavaType[] origClasses =
            tabOrig.createClassSet(getBaseLine(), new URLClassLoader(new URL[]{}), classSelector);
        
        final JavaType[] newClasses =
            tabNew.createClassSet(getCurrent(), new URLClassLoader(new URL[]{}), classSelector);
        
        checker.reportDiffs(origClasses, newClasses);

        ExpectedDiff[] expected = new ExpectedDiff[] {
                new ExpectedDiff("Class testlib.AddedClass added", Severity.INFO, "testlib.AddedClass", null, null),
                new ExpectedDiff("Class testlib.RemovedClass removed", Severity.ERROR, "testlib.RemovedClass", null, null),
            };
        
        tld.checkExpected(expected);
    }
    
    protected ClassChangeCheck createCheck()
    {
        // changes are reported directly by the Checker
        return null;
    }

}
