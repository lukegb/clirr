package testlib.regression.bug1373831;

import java.io.InputStream;
import java.io.Reader;

import org.xml.sax.InputSource;

public class XMLConfiguration extends AbstractHierarchicalFileConfiguration
{
    public void load(InputStream in)
    {
    }

    public void load(Reader in)
    {
    }

    private void load(InputSource source)
    {
    }
}
