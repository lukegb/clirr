package net.sf.clirr.core.internal;

import java.util.ArrayList;
import junit.framework.TestCase;
import net.sf.clirr.core.internal.CoIterator;

/**
 * Tests for the CoIterator class.
 */
public class CoIteratorTest extends TestCase
{
    public void testBasics()
    {
        ArrayList coll1 = new ArrayList();
        ArrayList coll2 = new ArrayList();

        coll1.add("delta");
        coll1.add("beta");
        coll1.add("echo");
        coll1.add("foxtrot");

        coll2.add("delta");
        coll2.add("beta");
        coll2.add("foxtrot");
        coll2.add("alpha");
        coll2.add("golf");
        coll2.add("hotel");

        CoIterator iter = new CoIterator(null, coll1, coll2);

        ArrayList lefts = new ArrayList();
        ArrayList rights = new ArrayList();

        while (iter.hasNext())
        {
            iter.next();
            lefts.add(iter.getLeft());
            rights.add(iter.getRight());
        }

        int coll1Size = coll1.size();
        int coll2Size = coll2.size();
        assertEquals(7, lefts.size());
        assertEquals(7, rights.size());

        assertEquals("lefts should not have alpha", null, lefts.get(0));
        assertEquals("rights should have alpha", "alpha", rights.get(0));

        assertEquals("lefts should have beta", "beta", lefts.get(1));
        assertEquals("rights should have beta", "beta", rights.get(1));

        assertEquals("delta", lefts.get(2));
        assertEquals("delta", rights.get(2));

        assertEquals("echo", lefts.get(3));
        assertEquals(null, rights.get(3));

        assertEquals("foxtrot", lefts.get(4));
        assertEquals("foxtrot", rights.get(4));

        assertEquals(null, lefts.get(5));
        assertEquals("golf", rights.get(5));

        assertEquals(null, lefts.get(6));
        assertEquals("hotel", rights.get(6));
    }
}
