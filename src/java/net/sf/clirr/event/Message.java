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

package net.sf.clirr.event;

/**
 * Class which manages API Difference messages, including expanding message
 * codes into strings and descriptions.
 */
public final class Message
{
    private int id;

    /**
     * This constructor is equivalent to new Message(id, true).
     */
    public Message(int id)
    {
        this(id, true);
    }

    /**
     * Create an instance of this object with the specified message id
     *
     * @param id is an integer which is used to look up the appropriate
     * text string for this message from a resource file. The id of a
     * message should be unique.
     *
     * @param register determines whether the new Message object should be
     * registered with the central MessageManager object. This is normally
     * desirable, as this allows the unit tests associated with clirr to
     * verify that message ids are unique and that translations exist for
     * all registered messages. However false can be useful in some
     * circumstances, eg when creating Message objects for the purposes
     * of unit tests.
     */
    public Message(int id, boolean register)
    {
        this.id = id;
        if (register)
        {
            MessageManager.getInstance().addMessage(this);
        }
    }

    public int getId()
    {
        return id;
    }
}
