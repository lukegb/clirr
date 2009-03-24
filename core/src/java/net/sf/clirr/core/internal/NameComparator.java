package net.sf.clirr.core.internal;

import java.io.Serializable;
import java.util.Comparator;

import net.sf.clirr.core.spi.Named;

/**
 * Compares {@link Named named entities} by their name.
 *
 * @author Simon Kitching
 * @author lkuehne
 */
public final class NameComparator implements Comparator, Serializable
{
    private static final long serialVersionUID = -6730235504132614557L;

    public NameComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
        Named f1 = (Named) o1;
        Named f2 = (Named) o2;

        final String name1 = f1.getName();
        final String name2 = f2.getName();

        return name1.compareTo(name2);
    }
}

