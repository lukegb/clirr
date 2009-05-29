package net.sf.clirr.core.spi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class TypeArrayBuilderSupport implements TypeArrayBuilder
{

    protected ClassLoader createClassLoader(File[] classPathEntries, ClassLoader thirdPartyClasses)
    {
        final URL[] entryUrls = new URL[classPathEntries.length];
        for (int i = 0; i < classPathEntries.length; i++)
        {
            File entry = classPathEntries[i];
            try
            {
                URL url = entry.toURI().toURL();
                entryUrls[i] = url;
            }
            catch (MalformedURLException ex)
            {
                String fileType = entry.isDirectory() ? "directory" : "jar file";
                throw new IllegalArgumentException(
                        "Cannot create classloader with " + fileType + " " + entry, ex);
            }
        }
        final URLClassLoader loader = new URLClassLoader(entryUrls, thirdPartyClasses);

        return loader;
    }

}
