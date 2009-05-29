package net.sf.clirr.core.spi;

import java.io.File;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;

public interface TypeArrayBuilder
{
    /**
     * Creates a set of classes to check.
     *
     * @param classPathEntries a set of jar files and directories to scan for class files.
     *
     * @param thirdPartyClasses loads classes that are referenced
     * by the classes in the classPathEntries
     *
     * @param classSelector is an object that determines which classes reachable via the
     * classPathEntries are to be compared. This parameter may be null, in
     * which case all classes in the old and new jars are compared.
     */
    JavaType[] createClassSet(
            File[] classPathEntries, ClassLoader thirdPartyClasses, ClassFilter classSelector)
            throws CheckerException;

}
