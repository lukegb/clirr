//////////////////////////////////////////////////////////////////////////////
// Clirr: compares two versions of a java library for binary compatibility
// Copyright (C) 2003  Lars Kühne
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Abstract DiffListener that writes output to some textual
 * output stream. That stream can either be System.out or a textfile.
 *
 * @author lkuehne
 */
abstract class FileDiffListener extends DiffListenerAdapter
{
    private PrintStream outputStream;

    /**
     * Initializes the outputstream.
     * @param outFile the filename (System.out is used if null is provided here)
     * @throws FileNotFoundException if there are problems with
     */
    FileDiffListener(String outFile) throws FileNotFoundException
    {
        if (outFile == null)
        {
            outputStream = System.out;
        }
        else
        {
            final OutputStream out = new FileOutputStream(outFile);
            outputStream = new PrintStream(out);
        }

    }

    /**
     * Returns the output stream so subclasses can write data.
     * @return the output stream
     */
    protected final PrintStream getOutputStream()
    {
        return outputStream;
    }


    /**
     * Writes a footer and closes the
     * output stream if necessary.
     *
     * @see #writeFooter()
     */
    public final void stop()
    {
        writeFooter();

        if (outputStream != System.out)
        {
            outputStream.close();
        }
        super.stop();
    }

    /**
     * A hook to write footer info to the output stream.
     * This implementation does nothing, subclasses can override
     * this method if necessary.
     *
     * @see #stop()
     */
    protected void writeFooter()
    {
    }
}
