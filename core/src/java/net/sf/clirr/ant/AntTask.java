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

package net.sf.clirr.ant;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.clirr.core.Checker;
import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.ClassFilter;
import net.sf.clirr.core.ClassSelector;
import net.sf.clirr.core.PlainDiffListener;
import net.sf.clirr.core.XmlDiffListener;
import net.sf.clirr.core.internal.ClassLoaderUtil;
import net.sf.clirr.core.internal.bcel.BcelTypeArrayBuilder;
import net.sf.clirr.core.spi.JavaType;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;


/**
 * Implements the Clirr ant task.
 * @author lkuehne
 */
public final class AntTask extends Task
{
    private static final String FORMATTER_TYPE_PLAIN = "plain";
    private static final String FORMATTER_TYPE_XML = "xml";

    /**
     * Output formater.
     */
    public static final class Formatter
    {
        private String type = null;
        private String outFile = null;

        public String getOutFile()
        {
            return outFile;
        }

        public void setOutFile(String outFile)
        {
            this.outFile = outFile;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            String lowerCase = type.toLowerCase();
            if (!lowerCase.equals(FORMATTER_TYPE_XML)
                && !lowerCase.equals(FORMATTER_TYPE_PLAIN))
            {
                throw new BuildException(
                    "Illegal formatter type, only plain and xml are supported");
            }

            this.type = type;
        }
    }

    /**
     * Class Filter that returns the logical "and" of two underlying class filters.
     */
    private static class CompoundClassFilter implements ClassFilter
    {
        private final ClassFilter patternSetFilter;
        private final ClassFilter scopeSelector;

        public CompoundClassFilter(ClassFilter patternSetFilter, ClassFilter scopeSelector)
        {
            this.patternSetFilter = patternSetFilter;
            this.scopeSelector = scopeSelector;
        }

        public boolean isSelected(JavaType clazz)
        {
            return patternSetFilter.isSelected(clazz) && scopeSelector.isSelected(clazz);
        }
    }

    private FileSet origFiles = null;
    private FileSet newFiles = null;
    private Path newClassPath = null;
    private Path origClassPath = null;

    private boolean failOnBinError = true;
    private boolean failOnBinWarning = false;
    private boolean failOnSrcError = true;
    private boolean failOnSrcWarning = false;
    private List formatters = new LinkedList();
    private List patternSets = new LinkedList();


    public Path createNewClassPath()
    {
        if (newClassPath == null)
        {
            newClassPath = new Path(getProject());
        }
        return newClassPath.createPath();
    }

    public void setNewClassPath(Path path)
    {
        if (newClassPath == null)
        {
            newClassPath = path;
        }
        else
        {
            newClassPath.append(path);
        }
    }

    public Path createOrigClassPath()
    {
        if (origClassPath == null)
        {
            origClassPath = new Path(getProject());
        }
        return origClassPath.createPath();
    }

    public void setOrigClassPath(Path path)
    {
        if (origClassPath == null)
        {
            origClassPath = path;
        }
        else
        {
            origClassPath.append(path);
        }
    }

    public void addOrigFiles(FileSet origFiles)
    {
        if (this.origFiles != null)
        {
            throw new BuildException();
        }
        this.origFiles = origFiles;
    }

    public void addNewFiles(FileSet newFiles)
    {
        if (this.newFiles != null)
        {
            throw new BuildException();
        }
        this.newFiles = newFiles;
    }

    public void setFailOnBinError(boolean failOnBinError)
    {
        this.failOnBinError = failOnBinError;
    }

    public void setFailOnBinWarning(boolean failOnBinWarning)
    {
        this.failOnBinWarning = failOnBinWarning;
    }

    public void setFailOnSrcError(boolean failOnSrcError)
    {
        this.failOnSrcError = failOnSrcError;
    }

    public void setFailOnSrcWarning(boolean failOnSrcWarning)
    {
        this.failOnSrcWarning = failOnSrcWarning;
    }

    public void addFormatter(Formatter formatter)
    {
        formatters.add(formatter);
    }

    public void addApiClasses(PatternSet set)
    {
        patternSets.add(set);
    }

    public void execute()
    {
        log("Running Clirr, built from tag $Name$", Project.MSG_VERBOSE);

        if (origFiles == null || newFiles == null)
        {
            throw new BuildException(
                "Missing nested filesets origFiles and newFiles.", getLocation());
        }

        if (newClassPath == null)
        {
            newClassPath = new Path(getProject());
        }

        if (origClassPath == null)
        {
            origClassPath = new Path(getProject());
        }

        final File[] origJars = scanFileSet(origFiles);
        final File[] newJars = scanFileSet(newFiles);

        if (origJars.length == 0)
        {
            throw new BuildException(
                "No files in nested fileset origFiles - nothing to check!"
                + " Please check your fileset specification.");
        }

        if (newJars.length == 0)
        {
            throw new BuildException(
                "No files in nested fileset newFiles - nothing to check!"
                + " Please check your fileset specification.");
        }

        final ClassLoader origThirdPartyLoader = createClasspathLoader(origClassPath);
        final ClassLoader newThirdPartyLoader = createClasspathLoader(newClassPath);

        final Checker checker = new Checker();
        final ChangeCounter counter = new ChangeCounter();

        boolean formattersWriteToStdOut = false;

        for (Iterator it = formatters.iterator(); it.hasNext();)
        {
            Formatter formatter = (Formatter) it.next();
            final String type = formatter.getType();
            final String outFile = formatter.getOutFile();

            formattersWriteToStdOut = formattersWriteToStdOut || outFile == null;

            try
            {
                if (FORMATTER_TYPE_PLAIN.equals(type))
                {
                    checker.addDiffListener(new PlainDiffListener(outFile));
                }
                else if (FORMATTER_TYPE_XML.equals(type))
                {
                    checker.addDiffListener(new XmlDiffListener(outFile));
                }
            }
            catch (IOException ex)
            {
                log("unable to initialize formatter: " + ex.getMessage(),
                    Project.MSG_WARN);
            }
        }

        if (!formattersWriteToStdOut)
        {
            checker.addDiffListener(new AntLogger(this));
        }

        checker.addDiffListener(counter);
        try
        {
            ClassFilter classSelector = buildClassFilter();
            final JavaType[] origClasses =
                BcelTypeArrayBuilder.createClassSet(origJars, origThirdPartyLoader, classSelector);
            
            final JavaType[] newClasses =
                BcelTypeArrayBuilder.createClassSet(newJars, newThirdPartyLoader, classSelector);
            
            checker.reportDiffs(origClasses, newClasses);
        }
        catch (CheckerException ex)
        {
            throw new BuildException(ex.getMessage());
        }

        if ((counter.getBinWarnings() > 0 && failOnBinWarning)
            || (counter.getBinErrors() > 0 && failOnBinError))
        {
            throw new BuildException("detected binary incompatible API changes");
        }

        if ((counter.getSrcWarnings() > 0 && failOnSrcWarning)
            || (counter.getSrcErrors() > 0 && failOnSrcError))
        {
            throw new BuildException("detected source incompatible API changes");
        }
    }

    private ClassFilter buildClassFilter()
    {
        final PatternSetFilter patternSetFilter = new PatternSetFilter(getProject(), patternSets);
        final ClassFilter scopeSelector = new ClassSelector(ClassSelector.MODE_UNLESS);
        return new CompoundClassFilter(patternSetFilter, scopeSelector);
    }


    private ClassLoader createClasspathLoader(Path classpath)
    {
        final String[] cpEntries = classpath.list();
        return ClassLoaderUtil.createClassLoader(cpEntries);
    }

    private File[] scanFileSet(FileSet fs)
    {
        Project prj = getProject();
        DirectoryScanner scanner = fs.getDirectoryScanner(prj);
        scanner.scan();
        File basedir = scanner.getBasedir();
        String[] fileNames = scanner.getIncludedFiles();
        File[] ret = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++)
        {
            String fileName = fileNames[i];
            ret[i] = new File(basedir, fileName);
        }
        return ret;
    }

}
