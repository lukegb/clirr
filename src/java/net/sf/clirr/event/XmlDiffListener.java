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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class XmlDiffListener extends DiffListenerAdapter
{
    private static final String DIFFREPORT = "diffreport";
    private static final String DIFFERENCE = "difference";

    // TODO: ---- duplicate code in PlainDiffListener
    private PrintStream out;

    public XmlDiffListener(String outFile) throws IOException
    {
        if (outFile == null)
        {
            this.out = System.out;
        }
        else
        {
            final OutputStream out = new FileOutputStream(outFile);
            this.out = new PrintStream(out);
        }
    }

    // TODO: ---- end of duplicate code in PlainDiffListener


    public void reportDiff(ApiDifference difference)
    {
        out.print("  <" + DIFFERENCE);
        out.print(" severity=\"" + difference.getSeverity() + "\">");
        out.print(difference.getReport()); // TODO: XML escapes??
        out.println("</" + DIFFERENCE + ">");
    }

    public void start()
    {
        out.println("<?xml version=\"1.0\"?>");
        out.println("<" + DIFFREPORT + ">");
        out.println("<!--");
        out.println("  In future versions the differences will have more attributes, e.g.");
        out.println("  - affected package");
        out.println("  - affected class/interface");
        out.println("  - affected method, if any");
        out.println("  - change type (ADD/REMOVE/CHANGE)");
        out.println("  - ... anything else you need?");
        out.println("-->");
    }

    public void stop()
    {
        out.println("</" + DIFFREPORT + ">");

        if (out != System.out)
        {
            out.close();
        }
    }
}
