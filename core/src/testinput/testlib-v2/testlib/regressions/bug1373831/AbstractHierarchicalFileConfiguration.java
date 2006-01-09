package testlib.regression.bug1373831;

import java.io.Reader;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractHierarchicalFileConfiguration
{
    public void load()
    {
    }

    public void load(String fileName)
    {
    }

    public void load(File file)
    {
    }

    public void load(URL url)
    {
    }

    public void load(InputStream in)
    {
    }

    public void load(InputStream in, String encoding)
    {
    }
}
