package net.sf.clirr.core.internal.checks;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.Message;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.internal.AbstractDiffReporter;
import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.spi.JavaType;

public class ClassFormatVersionCheck extends AbstractDiffReporter implements ClassChangeCheck
{
    private static final Message MSG_CLASS_FORMAT_VERSION_INCREASED = new Message(10000);

    private static final Message MSG_CLASS_FORMAT_VERSION_DECREASED = new Message(10001);

    public ClassFormatVersionCheck(ApiDiffDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public boolean check(JavaType compatBaseline, JavaType currentVersion) throws CheckerException
    {
        final int oldClassFormatVersion = compatBaseline.getClassFormatVersion();
        final int newClassFormatVersion = currentVersion.getClassFormatVersion();
        final String className = compatBaseline.getName();

        final String[] args = new String[]{
                String.valueOf(oldClassFormatVersion), String.valueOf(newClassFormatVersion)};
        if (oldClassFormatVersion < newClassFormatVersion)
        {
            // don't use severity getSeverity(compatBaseline, Severity.ERROR) here,
            // as even classes that are not visible to the client code will trigger a
            // requirement for a higher JVM version.  
            log(MSG_CLASS_FORMAT_VERSION_INCREASED, Severity.ERROR, className, null, null, args);
        }
        else if (newClassFormatVersion < oldClassFormatVersion)
        {
            log(MSG_CLASS_FORMAT_VERSION_DECREASED, Severity.INFO, className, null, null, args);
        }
        return true;
    }
}
