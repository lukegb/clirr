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

package net.sf.clirr.checks;

import java.util.Arrays;
import java.util.Comparator;

import net.sf.clirr.framework.ClassChangeCheck;
import net.sf.clirr.framework.AbstractDiffReporter;
import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.framework.CoIterator;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;
import net.sf.clirr.event.ScopeSelector;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.ConstantValue;

/**
 * Checks the fields of a class.
 *
 * @author lkuehne
 */
public class FieldSetCheck
        extends AbstractDiffReporter
        implements ClassChangeCheck
{
    private static final class FieldNameComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            Field f1 = (Field) o1;
            Field f2 = (Field) o2;

            final String name1 = f1.getName();
            final String name2 = f2.getName();

            return name1.compareTo(name2);
        }
    }

    private static final Comparator COMPARATOR = new FieldNameComparator();
    private ScopeSelector scopeSelector;

    public FieldSetCheck(ApiDiffDispatcher dispatcher, ScopeSelector scopeSelector)
    {
        super(dispatcher);
        this.scopeSelector = scopeSelector;
    }

    public final boolean check(JavaClass baselineClass, JavaClass currentClass)
    {
        final Field[] baselineFields = baselineClass.getFields();
        final Field[] currentFields = currentClass.getFields();

        CoIterator iter = new CoIterator(
            COMPARATOR, baselineFields, currentFields);

        while (iter.hasNext())
        {
            iter.next();

            Field bField = (Field) iter.getLeft();
            Field cField = (Field) iter.getRight();
            
            if (bField == null)
            {
                if (scopeSelector.isSelected(cField)) 
                {
                    final String name = cField.getName();
                    String scope = ScopeSelector.getScopeDesc(cField);
                    fireDiff("Added " + scope + " field " + name, Severity.INFO, currentClass, cField);
                }
            }
            else if (cField == null)
            {
                if (scopeSelector.isSelected(bField)) 
                {
                    final String name = bField.getName();
                    fireDiff("Field " + name + " has been removed", Severity.ERROR, baselineClass, bField);
                }
            }
            else if (scopeSelector.isSelected(bField) || scopeSelector.isSelected(cField))
            {
                checkForModifierChange(bField, cField, currentClass);
                checkForVisibilityChange(bField, cField, currentClass);
                checkForTypeChange(bField, cField,  currentClass);
                checkForConstantValueChange(bField, cField,  currentClass);
            }
        }
        
        return true;
    }

    private void checkForConstantValueChange(Field bField, Field cField, JavaClass currentClass)
    {
        if (!(bField.isStatic() && bField.isFinal() && cField.isStatic() && cField.isFinal()))
        {
            return;
        }

        final ConstantValue bVal = bField.getConstantValue();

        if (bVal != null)
        {
            final String bValRep = bVal.toString();
            final ConstantValue cVal = cField.getConstantValue();
            if (cVal == null)
            {
                fireDiff("Value of " + bField.getName()
                        + " is no longer a compile time constant",
                        Severity.WARNING, currentClass, cField);
                return;
            }

            final String cValRep = String.valueOf(cVal);
            if (!bValRep.equals(cValRep))
            {
                // TODO: print out old and new value
                // How can that be done with BCEL, esp. for boolean values?
                fireDiff("Value of compile time constant " + bField.getName()
                        + " has been changed",
                        Severity.WARNING, currentClass, cField);
            }
        }
    }

    private void checkForTypeChange(Field bField, Field cField, JavaClass currentClass)
    {
        final String bSig = bField.getType().toString();
        final String cSig = cField.getType().toString();
        if (!bSig.equals(cSig))
        {
            fireDiff("Changed type of field " + bField.getName() + " from " + bSig + " to " + cSig,
                    Severity.ERROR, currentClass, bField);
        }
    }

    private void checkForModifierChange(Field bField, Field cField, JavaClass clazz)
    {
        if (bField.isFinal() && !cField.isFinal())
        {
            fireDiff("Field " + bField.getName() + " is now non-final", Severity.INFO, clazz, cField);
        }

        if (!bField.isFinal() && cField.isFinal())
        {
            fireDiff("Field " + bField.getName() + " is now final", Severity.ERROR, clazz, cField);
        }

        if (bField.isStatic() && !cField.isStatic())
        {
            fireDiff("Field " + bField.getName() + " is now non-static", Severity.ERROR, clazz, cField);
        }

        if (!bField.isStatic() && cField.isStatic())
        {
            fireDiff("Field " + bField.getName() + " is now static", Severity.ERROR, clazz, cField);
        }

        // JLS, 13.4.10: Adding or deleting a transient modifier of a field
        // does not break compatibility with pre-existing binaries

        // TODO: What about volatile?
    }

    private void checkForVisibilityChange(Field bField, Field cField, JavaClass clazz)
    {
        ScopeSelector.Scope bScope = ScopeSelector.getScope(bField);
        ScopeSelector.Scope cScope = ScopeSelector.getScope(cField);

        if (cScope.isMoreVisibleThan(bScope))
        {
            fireDiff(
                "Accessability of field " + bField.getName()
                + " has been increased"
                + " from " + bScope.getDesc()
                + " to " + cScope.getDesc(),
                Severity.INFO, clazz, cField);
        }
        else if (cScope.isLessVisibleThan(bScope))
        {
            fireDiff(
                "Accessibility of field " + bField.getName()
                + " has been weakened"
                + " from " + bScope.getDesc()
                + " to " + cScope.getDesc(),
                Severity.ERROR, clazz, cField);
        }
    }

    private void fireDiff(String report, Severity severity, JavaClass clazz, Field field)
    {
        final String className = clazz.getClassName();
        final ApiDifference diff =
                new ApiDifference(report + " in " + className,
                        severity, className, null, field.getName());
        getApiDiffDispatcher().fireDiff(diff);

    }
}
