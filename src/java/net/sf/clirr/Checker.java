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

package net.sf.clirr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

import net.sf.clirr.checks.AddedClassCheck;
import net.sf.clirr.checks.ClassHierarchyCheck;
import net.sf.clirr.checks.ClassModifierCheck;
import net.sf.clirr.checks.GenderChangeCheck;
import net.sf.clirr.checks.InterfaceSetCheck;
import net.sf.clirr.checks.RemovedClassCheck;
import net.sf.clirr.checks.FieldSetCheck;
import net.sf.clirr.checks.MethodSetCheck;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.DiffListener;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.framework.ClassSetChangeCheck;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.util.ClassSet;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.tools.ant.BuildException;


/**
 * This is the main class to be used by Clirr frontends,
 * it implements the checking functionality of Clirr.
 * Frontends can create an instance of this class
 * and register themselves as DiffListeners, they are then
 * informed whenever an API change is detected by the
 * reportDiffs method.
 *
 * @author lkuehne
 */
public final class Checker implements ApiDiffDispatcher
{

    private List listeners = new ArrayList();

    private List classSetChecks = new ArrayList();
    private List classChecks = new ArrayList();

    /**
     * Package visible constructor for unit testing.
     */
    Checker(ClassSetChangeCheck cscc)
    {
        classSetChecks.add(cscc);
    }

    /**
     * Package visible constructor for unit testing.
     */
    Checker(ClassChangeCheck ccc)
    {
        classChecks.add(ccc);
    }

    /**
     * Creates a new Checker.
     */
    public Checker()
    {
        classSetChecks.add(new RemovedClassCheck(this));
        classSetChecks.add(new AddedClassCheck(this));

        classChecks.add(new GenderChangeCheck(this));
        classChecks.add(new ClassModifierCheck(this));
        classChecks.add(new InterfaceSetCheck(this));
        classChecks.add(new ClassHierarchyCheck(this));
        classChecks.add(new FieldSetCheck(this));
        classChecks.add(new MethodSetCheck(this));
    }

    public void addDiffListener(DiffListener listener)
    {
        listeners.add(listener);
    }

    private void fireStart()
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.start();
        }
    }

    private void fireStop()
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.stop();
        }
    }

    public void fireDiff(ApiDifference diff)
    {
        for (Iterator it = listeners.iterator(); it.hasNext();)
        {
            DiffListener diffListener = (DiffListener) it.next();
            diffListener.reportDiff(diff);
        }
    }

    public void reportDiffs(
            File[] origJars, File[] newJars,
            ClassLoader origThirdPartyLoader, ClassLoader newThirdPartyLoader)
    {
        final ClassSet origClasses = createClassSet(origJars, origThirdPartyLoader);
        final ClassSet newClasses = createClassSet(newJars, newThirdPartyLoader);
        reportDiffs(origClasses, newClasses);
    }

    private ClassSet createClassSet(File[] jarFiles, ClassLoader thirdPartyClasses)
    {
        ClassLoader classLoader = createClassLoader(jarFiles, thirdPartyClasses);

        Repository repository = new ClassLoaderRepository(classLoader);

        ClassSet ret = new ClassSet();

        for (int i = 0; i < jarFiles.length; i++)
        {
            File jarFile = jarFiles[i];
            ZipFile zip = null;
            try
            {
                zip = new ZipFile(jarFile, ZipFile.OPEN_READ);
            }
            catch (IOException ex)
            {
                throw new BuildException("Cannot open " + jarFile + " for reading", ex);
            }
            Enumeration enum = zip.entries();
            while (enum.hasMoreElements())
            {
                ZipEntry zipEntry = (ZipEntry) enum.nextElement();
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".class"))
                {
                    JavaClass clazz = extractClass(zipEntry, zip, repository);
                    if (clazz.isPublic() || clazz.isProtected())
                    {
                        ret.add(clazz);
                    }
                }
            }
        }

        return ret;
    }

    private JavaClass extractClass(ZipEntry zipEntry, ZipFile zip, Repository repository)
    {
        String name = zipEntry.getName();
        InputStream is = null;
        try
        {
            is = zip.getInputStream(zipEntry);

            ClassParser parser = new ClassParser(is, name);
            JavaClass clazz = parser.parse();
            clazz.setRepository(repository);
            return clazz;
        }
        catch (IOException ex)
        {
            throw new BuildException("Cannot read " + zipEntry.getName() + " from " + zip.getName(), ex);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ex)
                {
                    throw new BuildException("Cannot close " + zip.getName(), ex);
                }
            }
        }
    }

    private ClassLoader createClassLoader(File[] jarFiles, ClassLoader thirdPartyClasses)
    {
        final URL[] jarUrls = new URL[jarFiles.length];
        for (int i = 0; i < jarFiles.length; i++)
        {
            File jarFile = jarFiles[i];
            try
            {
                URL url = jarFile.toURL();
                jarUrls[i] = url;
            }
            catch (MalformedURLException ex)
            {
                final IllegalArgumentException illegalArgumentException =
                        new IllegalArgumentException("Cannot create classloader with jar file " + jarFile);
                illegalArgumentException.initCause(ex);
                throw illegalArgumentException;
            }
        }
        final URLClassLoader jarsLoader = new URLClassLoader(jarUrls, thirdPartyClasses);

        return jarsLoader;
    }


    /**
     * Checks two sets of classes for api changes and reports
     * them to the DiffListeners.
     * @param compatibilityBaseline the classes that form the
     *        compatibility baseline to check against
     * @param currentVersion the classes that are checked for
     *        compatibility with compatibilityBaseline
     */
    private void reportDiffs(ClassSet compatibilityBaseline, ClassSet currentVersion)
    {
        fireStart();
        for (Iterator it = classSetChecks.iterator(); it.hasNext();)
        {
            ClassSetChangeCheck check = (ClassSetChangeCheck) it.next();
            check.check(compatibilityBaseline, currentVersion);
        }
        runClassChecks(compatibilityBaseline, currentVersion);
        fireStop();
    }

    private void runClassChecks(ClassSet compatBaseline, ClassSet currentVersion)
    {
        JavaClass[] compat = compatBaseline.toArray();
        JavaClass[] current = currentVersion.toArray();

        for (int i = 0; i < compat.length; i++)
        {
            JavaClass compatBaselineClass = compat[i];
            JavaClass currentClass = findClass(compatBaselineClass.getClassName(), current);
            if (currentClass != null)
            {
                // class still available in current release
                for (Iterator it = classChecks.iterator(); it.hasNext();)
                {
                    ClassChangeCheck classChangeCheck = (ClassChangeCheck) it.next();
                    classChangeCheck.check(compatBaselineClass, currentClass);
                }
            }
        }
    }

    private JavaClass findClass(String className, JavaClass[] javaClasses)
    {
        for (int i = 0; i < javaClasses.length; i++)
        {
            JavaClass javaClass = javaClasses[i];
            if (javaClass.getClassName().equals(className))
            {
                return javaClass;
            }
        }
        return null;
    }

}
