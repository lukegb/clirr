package net.sf.clirr.core.spi;

import java.io.File;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;

public interface TypeArrayBuilder
{
    /**
     * Creates a set of classes to check.
     *
     * @param jarFiles a set of jar filed to scan for class files.
     *
     * @param thirdPartyClasses loads classes that are referenced
     * by the classes in the jarFiles
     *
     * @param classSelector is an object which determines which classes from the
     * old and new jars are to be compared. This parameter may be null, in
     * which case all classes in the old and new jars are compared.
     */
    JavaType[] createClassSet(
            File[] jarFiles, ClassLoader thirdPartyClasses, ClassFilter classSelector)
            throws CheckerException;

}
