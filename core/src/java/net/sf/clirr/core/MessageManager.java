//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003 - 2004  Lars Kühne
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

package net.sf.clirr.core;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;

/**
 * Class which manages API Difference messages, including expanding message
 * codes into strings and descriptions.
 */
public final class MessageManager
{
    private static MessageManager instance;
    private ArrayList messages = new ArrayList();

    /**
     * Utility class to sort messages by their numeric ids.
     */
    private static class MessageComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            Message m1 = (Message) o1;
            Message m2 = (Message) o2;
            return m1.getId() - m2.getId();
        }
    }

    /**
     * This is a singleton class; to get an instance of this class, use
     * the getInstance method.
     */
    private MessageManager()
    {
    }

    /**
     * Return the singleton instance of this class.
     */
    public static MessageManager getInstance()
    {
        if (instance == null)
        {
            instance = new MessageManager();
        }
        return instance;
    }

    /**
     * Add a message to the list of known messages.
     */
    public void addMessage(Message msg)
    {
        messages.add(msg);
    }

    /**
     * Verify that the list of known messages contains no two objects
     * with the same numeric message id. This method is expected to be
     * called from the unit tests, so that if a developer adds a new
     * message and accidentally uses the message id of an existing
     * message object, then this will be reported as an error.
     * <p>
     * @throws java.lang.IllegalArgumentException if any duplicate id is found.
     */
    public void checkUnique()
    {
        java.util.Collections.sort(messages, new MessageComparator());
        int lastId = -1;
        for (Iterator i = messages.iterator(); i.hasNext();)
        {
            // check for any duplicates
            Message m = (Message) i.next();
            int currId = m.getId();
            if (currId <= lastId)
            {
                throw new IllegalArgumentException("Message id [" + currId + "] is not unique.");
            }
        }
    }

    /**
     * Return the complete set of registered messages.
     */
    public Collection getMessages()
    {
        return messages;
    }
}
