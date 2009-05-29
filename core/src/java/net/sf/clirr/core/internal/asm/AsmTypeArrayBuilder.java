package net.sf.clirr.core.internal.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.TypeArrayBuilderSupport;

public class AsmTypeArrayBuilder extends TypeArrayBuilderSupport
{
    public AsmTypeArrayBuilder()
    {
    }

    public JavaType[] createClassSet(File[] classPathEntries, ClassLoader thirdPartyClasses, ClassFilter classSelector) throws CheckerException
    {
        if (classSelector == null)
        {
            // create a class selector that selects all classes
            classSelector = new ClassSelector(ClassSelector.MODE_UNLESS);
        }

        ClassLoader classLoader = createClassLoader(classPathEntries, thirdPartyClasses);

        Repository repository = new Repository(classLoader);

        List selected = new ArrayList();

        for (int i = 0; i < classPathEntries.length; i++)
        {
            File classPathEntry = classPathEntries[i];
            if (classPathEntry.isDirectory())
            {
                List classFiles = scanDirForClassFiles(classPathEntry);
                for (Iterator it = classFiles.iterator(); it.hasNext();)
                {
                    File classFile = (File) it.next();
                    final AsmJavaType javaType = extractClass(repository, classFile);
                    if (classSelector.isSelected(javaType))
                    {
                        selected.add(javaType);
                    }
                }
            }
            else
            {
                File jarFile = classPathEntry;
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
                        final AsmJavaType javaType = extractClass(repository, zipEntry, zip);
                        if (classSelector.isSelected(javaType))
                        {
                            selected.add(javaType);
                        }
                    }
                }
            }
        }

        JavaType[] ret = new JavaType[selected.size()];
        selected.toArray(ret);
        return ret;
    }

    private List scanDirForClassFiles(File rootDir) throws CheckerException
    {
        // implementation note: we need to avoid infinite loops
        // that are created by symbolic links in the file system

        try
        {
            List ret = new ArrayList();

            final Set canonicalPathsSeen = new HashSet();
            LinkedList dirQueue = new LinkedList();
            dirQueue.add(rootDir);
            canonicalPathsSeen.add(rootDir.getCanonicalPath());
            while (!dirQueue.isEmpty())
            {
                File dir = (File) dirQueue.removeLast();
                File[] files = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return !pathname.isDirectory() && pathname.getName().endsWith(".class");
                    }
                });
                for (int i = 0; i < files.length; i++) {
                    ret.add(files[i]);
                }

                File[] subdirs = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                for (int i = 0; i < subdirs.length; i++)
                {
                    File subdir = subdirs[i];
                    String canonicalPath = subdir.getCanonicalPath();
                    if (!canonicalPathsSeen.contains(canonicalPath))
                    {
                        dirQueue.add(subdir);
                        canonicalPathsSeen.add(canonicalPath);
                    }
                }
            }
            return ret;
        }
        catch (IOException ex)
        {
            throw new CheckerException("unable to scan directory " + rootDir + " for class files", ex);
        }
    }

    private AsmJavaType extractClass(Repository repository, ZipEntry zipEntry, ZipFile zip) throws CheckerException
    {
        String rootLocation = zip.getName();
        String streamName = zipEntry.getName();
        InputStream is = null;
        try
        {
            is = zip.getInputStream(zipEntry);
            return repository.readJavaTypeFromStream(is);
        }
        catch (IOException ex)
        {
            throw new CheckerException("Cannot read " + streamName + " from " + rootLocation, ex);
        }
        finally
        {
            close(is, streamName);
        }
    }

    private AsmJavaType extractClass(Repository repository, File classFile) throws CheckerException
    {
        String streamName = classFile.getPath();
        InputStream is = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(classFile));
            return repository.readJavaTypeFromStream(is);
        }
        catch (IOException ex)
        {
            throw new CheckerException("Cannot read " + streamName, ex);
        }
        finally
        {
            close(is, streamName);
        }
    }

    private void close(InputStream is, String streamName) throws CheckerException
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch (IOException ex)
            {
                throw new CheckerException("Cannot close " + streamName, ex);
            }
        }
    }


}
