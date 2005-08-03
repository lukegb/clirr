//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003 - 2005  Lars Kühne
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//////////////////////////////////////////////////////////////////////////////

package net.sf.clirr.core.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This is an iterator that walks a pair of collections, returning
 * matching pairs from the set.
 * <p>
 * When an element is present in the left set but there is no equal object
 * in the right set, the pair (leftobj, null) is returned.
 * <p>
 * When an element is present in the right set but there is no equal object
 * in the left set, the pair (null, rightobj) is returned.
 * <p>
 * When an element in one set has an equal element in the other set, the
 * pair (leftobj, rightobj) is returned.
 * <p>
 * Note that the phrase "pair is returned" above actually means that the
 * getLeft and getRight methods on the iterator return those objects; the
 * pair is "conceptual" rather than a physical Pair instance. This avoids
 * instantiating an object to represent the pair for each step of the
 * iterator which would not be efficient.
 * <p>
 * Note also that elements from the sets are always returned in the order
 * defined by the provided comparator.
 *
 * @author Simon Kitching.
 */

public final class CoIterator
{
    private Object[] left;
    private Object[] right;

    private int leftIndex;
    private int rightIndex;

    private Object currLeft;
    private Object currRight;

    private Comparator comparator;

    /**
     * Iterate over the two collections, using the provided comparator.
     * <p>
     * The collections are not modified by this iterator.
     *
     * @param comparator is used to compare elements from the two collections.
     * If null, then the objects in the collections are expected to implement
     * the Comparable interface.
     */
    public CoIterator(Comparator comparator, Collection left, Collection right)
    {
        this.comparator = comparator;
        this.left = left.toArray();
        this.right = right.toArray();

        Arrays.sort(this.left, comparator);
        Arrays.sort(this.right, comparator);
    }

    /**
     * Iterate over the objects in the two arrays, using the provided comparator.
     * <p>
     * The arrays are not modified by this iterator. In particular, the
     * iterator returns the elements in ascending order, but the actual
     * arrays passed in here are cloned so that they are not modified.
     *
     * @param comparator is used to compare elements from the two collections.
     * If null, then the objects in the collections are expected to implement
     * the Comparable interface.
     */
    public CoIterator(Comparator comparator, Object[] left, Object[] right)
    {
        this.comparator = comparator;
        this.left = (Object[]) left.clone();
        this.right = (Object[]) right.clone();

        Arrays.sort(this.left, comparator);
        Arrays.sort(this.right, comparator);
    }

    /**
     * Indicates whether there are any more elements to be returned.
     */
    public boolean hasNext()
    {
        return (leftIndex < left.length) || (rightIndex < right.length);
    }

    /**
     * Moves this iterator object to refer to the next "pair" of objects.
     * <p>
     * Note that unlike the standard java.util.Iterator, this method does
     * not return anything; it simply modifies which objects will be
     * returned by the getLeft and getRight methods.
     *
     * @throws java.util.NoSuchElementException if this is called when hasNext would
     * report false (this is standard iterator behaviour).
     */
    public void next()
    {
        boolean haveLeft = leftIndex < left.length;
        boolean haveRight = rightIndex < right.length;

        if (!haveLeft && !haveRight)
        {
            currLeft = null;
            currRight = null;
            throw new NoSuchElementException();
        }

        int order;

        if (haveLeft && !haveRight)
        {
            order = -1;
        }
        else if (!haveLeft && haveRight)
        {
            order = +1;
        }
        else if (comparator != null)
        {
            order = comparator.compare(left[leftIndex], right[rightIndex]);
        }
        else
        {
            Comparable c1 = (Comparable) left[leftIndex];
            order = c1.compareTo(right[rightIndex]);
        }

        if (order < 0)
        {
            currLeft = left[leftIndex];
            currRight = null;
            ++leftIndex;
        }
        else if (order > 0)
        {
            currLeft = null;
            currRight = right[rightIndex];
            ++rightIndex;
        }
        else
        {
            currLeft = left[leftIndex];
            currRight = right[rightIndex];
            ++leftIndex;
            ++rightIndex;
        }
    }

    /**
     * Return an object from the "left" collection specified to this object's
     * constructor. When the iterator has selected an element in the "right"
     * collection for which there is no corresponding element in the left
     * collection, then this will return null.
     */
    public Object getLeft()
    {
        return currLeft;
    }

    /**
     * See getLeft.
     */
    public Object getRight()
    {
        return currRight;
    }
}
