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

import net.sf.clirr.checks.AddedClassCheck;
import net.sf.clirr.checks.ClassHierarchyCheck;
import net.sf.clirr.checks.ClassModifierCheck;
import net.sf.clirr.checks.GenderChangeCheck;
import net.sf.clirr.checks.InterfaceSetCheck;
import net.sf.clirr.checks.RemovedClassCheck;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.DiffListener;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassSet;


public final class Checker implements ApiDiffDispatcher
{

    private List listeners = new ArrayList();

    private List classSetChecks = new ArrayList();
    private List classChecks = new ArrayList();

    public Checker()
    {
        classSetChecks.add(new RemovedClassCheck(this));
        classSetChecks.add(new AddedClassCheck(this));

        classChecks.add(new GenderChangeCheck(this));
        classChecks.add(new ClassModifierCheck(this));
        classChecks.add(new InterfaceSetCheck(this));
        classChecks.add(new ClassHierarchyCheck(this));
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

    public void diffs(ClassSet oldClasses, ClassSet newClasses)
    {
        fireStart();
        for (Iterator it = classSetChecks.iterator(); it.hasNext();)
        {
            ClassSetChangeCheck check = (ClassSetChangeCheck) it.next();
            check.check(oldClasses, newClasses);
        }
        runClassChecks(oldClasses, newClasses);
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
