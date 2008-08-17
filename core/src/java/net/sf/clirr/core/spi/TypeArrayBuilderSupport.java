package net.sf.clirr.core.spi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class TypeArrayBuilderSupport implements TypeArrayBuilder
{

    protected ClassLoader createClassLoader(File[] jarFiles, ClassLoader thirdPartyClasses)
    {
        final URL[] jarUrls = new URL[jarFiles.length];
        for (int i = 0; i < jarFiles.length; i++)
        {
            File jarFile = jarFiles[i];
            try
            {
                URL url = jarFile.toURI().toURL();
                jarUrls[i] = url;
            }
            catch (MalformedURLException ex)
            {
                throw new IllegalArgumentException(
				        "Cannot create classloader with jar file " + jarFile, ex);
            }
        }
        final URLClassLoader jarsLoader = new URLClassLoader(jarUrls, thirdPartyClasses);
        
        return jarsLoader;
    }

}
