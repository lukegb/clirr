package net.sf.clirr.core.internal.bcel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.internal.ExceptionUtil;
import net.sf.clirr.core.spi.JavaType;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;

public final class BcelTypeArrayBuilder 
{
    /**
     * Disallow instantiation.
     */
    private BcelTypeArrayBuilder()
    {
    }
    
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
    public static JavaType[] createClassSet(
        File[] jarFiles, ClassLoader thirdPartyClasses, ClassFilter classSelector)
        throws CheckerException
    {
        if (classSelector == null)
        {
            // create a class selector that selects all classes
            classSelector = new ClassSelector(ClassSelector.MODE_UNLESS);
        }

        ClassLoader classLoader = createClassLoader(jarFiles, thirdPartyClasses);

        Repository repository = new ClassLoaderRepository(classLoader);

        List selected = new ArrayList();

        for (int i = 0; i < jarFiles.length; i++)
        {
            File jarFile = jarFiles[i];
            ZipFile zip = null;
            try
            {
                zip = new ZipFile(jarFile, ZipFile.OPEN_READ);
            }
            catch (IOException ex)
            {
                throw new CheckerException(
                    "Cannot open " + jarFile + " for reading", ex);
            }
            Enumeration enumEntries = zip.entries();
            while (enumEntries.hasMoreElements())
            {
                ZipEntry zipEntry = (ZipEntry) enumEntries.nextElement();
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".class"))
                {
                    JavaClass clazz = extractClass(zipEntry, zip, repository);
                    if (classSelector.isSelected(clazz))
                    {
                        selected.add(new BcelJavaType(clazz));
                        repository.storeClass(clazz);
                    }
                }
            }
        }

        JavaType[] ret = new JavaType[selected.size()];
        selected.toArray(ret);
        return ret;
    }

    private static ClassLoader createClassLoader(
            File[] jarFiles, ClassLoader thirdPartyClasses)
        {
            final URL[] jarUrls = new URL[jarFiles.length];
            for (int i = 0; i < jarFiles.length; i++)
            {
                File jarFile = jarFiles[i];
                try
                {
                    URL url = jarFile.toURL();
                    jarUrls[i] = url;
                }
                catch (MalformedURLException ex)
                {
                    // this should never happen
                    final IllegalArgumentException illegalArgumentException =
                        new IllegalArgumentException(
                            "Cannot create classloader with jar file " + jarFile);
                    ExceptionUtil.initCause(illegalArgumentException, ex);
                    throw illegalArgumentException;
                }
            }
            final URLClassLoader jarsLoader = new URLClassLoader(jarUrls, thirdPartyClasses);

            return jarsLoader;
        }

    private static JavaClass extractClass(
            ZipEntry zipEntry, ZipFile zip, Repository repository)
            throws CheckerException
        {
            String name = zipEntry.getName();
            InputStream is = null;
            try
            {
                is = zip.getInputStream(zipEntry);

                ClassParser parser = new ClassParser(is, name);
                JavaClass clazz = parser.parse();
                clazz.setRepository(repository);
                return clazz;
            }
            catch (IOException ex)
            {
                throw new CheckerException(
                    "Cannot read " + zipEntry.getName() + " from " + zip.getName(),
                    ex);
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException ex)
                    {
                        throw new CheckerException("Cannot close " + zip.getName(), ex);
                    }
                }
            }
        }

}
