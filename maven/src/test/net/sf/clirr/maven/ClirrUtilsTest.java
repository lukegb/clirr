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

import java.util.ArrayList;

import org.apache.maven.project.Version;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ClirrUtils}.
 *
 * @author Vincent Massol
 */
public class ClirrUtilsTest extends TestCase
{
    public void testGetLatestVersionWhenNoVersionElementsDefined()
    {
        String result = ClirrUtils.getLatestVersion(new ArrayList());
        assertNull(result);
    }

    public void testGetLatestVersionWhenNoReleasedVersionElementDefined()
    {
        Version unreleasedVersion = new Version();
        unreleasedVersion.setId("0.1");
        unreleasedVersion.setTag("HEAD");
        
        ArrayList versions = new ArrayList();
        versions.add(unreleasedVersion);
        
        String result = ClirrUtils.getLatestVersion(versions);
        assertNull(result);
    }

    public void testGetLatestVersionWhenReleasedVersionElementsDefined()
    {
        Version releasedVersion = new Version();
        releasedVersion.setId("0.1");
        releasedVersion.setTag("RELEASE_DUMMY_0_1");

        Version unreleasedVersion = new Version();
        unreleasedVersion.setId("0.2");
        unreleasedVersion.setTag("HEAD");

        ArrayList versions = new ArrayList();
        versions.add(releasedVersion);
        versions.add(unreleasedVersion);
        
        String result = ClirrUtils.getLatestVersion(versions);
        assertEquals("0.1", result);
    }
}
