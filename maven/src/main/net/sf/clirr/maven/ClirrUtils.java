//////////////////////////////////////////////////////////////////////////////
//Clirr: compares two versions of a java library for binary compatibility
//Copyright (C) 2004  Lars Kühne
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//////////////////////////////////////////////////////////////////////////////

package net.sf.clirr.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.jelly.MavenJellyContext;
import org.apache.maven.project.Version;
import org.apache.maven.util.HttpUtils;


/**
 * Utility class to manipulate POM version information.
 * 
 * @author Vincent Massol
 */
public class ClirrUtils
{
    /**
     * @return the latest released version, which means the latest version
     *         listed in the POM &lt;version&gt; elements with a 
     *         <code>tag</code> different from <code>HEAD</code>. Returns
     *         null if no latest released version is found
     * @param versions the list of {@link Version} objects from the POM
     */
    public static String getLatestVersion(List versions)
    {
        String result = null;
        
        if (!versions.isEmpty())
        {
            int pos = versions.size();
            while (pos > 0)
            {
                Version latestVersion = 
                    (Version) versions.get(pos - 1);
                
                // Is it a released version?
                if (!latestVersion.getTag().equalsIgnoreCase("HEAD"))
                {
                    result = latestVersion.getId();
                    break;
                }
                else
                {
                    pos = pos - 1;
                }
            }
        }
        
        return result;
    }

    /**
     * TODO: Add support for proxies  
     */
    public static void getBaselineJar(MavenJellyContext context)
        throws Exception
    {
        HttpUtils.getFile(
            (String) context.getVariable("clirr.baseline.url"),
            new File((String) context.getVariable("clirr.baseline.destination")),
            false,
            false,
            null,
            null,
            null,
            null);
    }
    
}
