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
 * Created by IntelliJ IDEA.
 * User: lk
 * Date: Sep 7, 2003
 * Time: 1:58:20 PM
 * To change this template use Options | File Templates.
 */
public class FileDiffListener extends DiffListenerAdapter
{
    private PrintStream outputStream;

    FileDiffListener(String outFile) throws FileNotFoundException
    {
        if (outFile == null)
        {
            this.outputStream = System.out;
        }
        else
        {
            final OutputStream out = new FileOutputStream(outFile);
            this.outputStream = new PrintStream(out);
        }

    }

    protected PrintStream getOutputStream()
    {
        return outputStream;
    }

    public final void stop()
    {

        // TODO: duplicate code in XML
        if (outputStream != System.out)
        {
            outputStream.close();
        }
        super.stop();
    }
}
