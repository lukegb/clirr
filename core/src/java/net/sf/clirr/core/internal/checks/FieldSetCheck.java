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

package net.sf.clirr.core.internal.checks;

import java.util.Comparator;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.AbstractDiffReporter;
import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.CoIterator;
import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.Message;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.ConstantValue;

/**
 * Checks the fields of a class.
 *
 * @author lkuehne
 */
public class FieldSetCheck extends AbstractDiffReporter implements ClassChangeCheck
{
    private static final net.sf.clirr.core.Message MSG_FIELD_ADDED = new Message(6000);
    private static final net.sf.clirr.core.Message MSG_FIELD_REMOVED = new Message(6001);
    private static final net.sf.clirr.core.Message MSG_FIELD_NOT_CONSTANT = new Message(6002);
    private static final net.sf.clirr.core.Message MSG_FIELD_CONSTANT_CHANGED = new Message(6003);
    private static final net.sf.clirr.core.Message MSG_FIELD_TYPE_CHANGED = new Message(6004);
    private static final net.sf.clirr.core.Message MSG_FIELD_NOW_NON_FINAL = new Message(6005);
    private static final net.sf.clirr.core.Message MSG_FIELD_NOW_FINAL = new Message(6006);
    private static final net.sf.clirr.core.Message MSG_FIELD_NOW_NON_STATIC = new Message(6007);
    private static final net.sf.clirr.core.Message MSG_FIELD_NOW_STATIC = new Message(6008);
    private static final net.sf.clirr.core.Message MSG_FIELD_MORE_ACCESSABLE = new Message(6009);
    private static final net.sf.clirr.core.Message MSG_FIELD_LESS_ACCESSABLE = new Message(6010);

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
    private net.sf.clirr.core.ScopeSelector scopeSelector;

    public FieldSetCheck(ApiDiffDispatcher dispatcher, net.sf.clirr.core.ScopeSelector scopeSelector)
    {
        super(dispatcher);
        this.scopeSelector = scopeSelector;
    }

    public final boolean check(JavaClass baselineClass, JavaClass currentClass)
    {
        final Field[] baselineFields = baselineClass.getFields();
        final Field[] currentFields = currentClass.getFields();

        CoIterator iter = new CoIterator(COMPARATOR, baselineFields, currentFields);

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
                    String scope = net.sf.clirr.core.ScopeSelector.getScopeDesc(cField);
                    fireDiff(MSG_FIELD_ADDED, net.sf.clirr.core.Severity.INFO, currentClass, cField, new String[]{scope});
                }
            }
            else if (cField == null)
            {
                if (scopeSelector.isSelected(bField))
                {
                    final String name = bField.getName();
                    fireDiff(MSG_FIELD_REMOVED, net.sf.clirr.core.Severity.ERROR, baselineClass, bField, null);
                }
            }
            else if (scopeSelector.isSelected(bField) || scopeSelector.isSelected(cField))
            {
                checkForModifierChange(bField, cField, currentClass);
                checkForVisibilityChange(bField, cField, currentClass);
                checkForTypeChange(bField, cField, currentClass);
                checkForConstantValueChange(bField, cField, currentClass);
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
                fireDiff(MSG_FIELD_NOT_CONSTANT, net.sf.clirr.core.Severity.WARNING, currentClass, cField, null);
                return;
            }

            final String cValRep = String.valueOf(cVal);
            if (!bValRep.equals(cValRep))
            {
                // TODO: print out old and new value
                // How can that be done with BCEL, esp. for boolean values?
                fireDiff(MSG_FIELD_CONSTANT_CHANGED, net.sf.clirr.core.Severity.WARNING, currentClass, cField, null);
            }
        }
    }

    private void checkForTypeChange(Field bField, Field cField, JavaClass currentClass)
    {
        final String bSig = bField.getType().toString();
        final String cSig = cField.getType().toString();
        if (!bSig.equals(cSig))
        {
            fireDiff(MSG_FIELD_TYPE_CHANGED, net.sf.clirr.core.Severity.ERROR, currentClass, bField, new String[]{bSig, cSig});
        }
    }

    private void checkForModifierChange(Field bField, Field cField, JavaClass clazz)
    {
        if (bField.isFinal() && !cField.isFinal())
        {
            fireDiff(MSG_FIELD_NOW_NON_FINAL, net.sf.clirr.core.Severity.INFO, clazz, cField, null);
        }

        if (!bField.isFinal() && cField.isFinal())
        {
            fireDiff(MSG_FIELD_NOW_FINAL, net.sf.clirr.core.Severity.ERROR, clazz, cField, null);
        }

        if (bField.isStatic() && !cField.isStatic())
        {
            fireDiff(MSG_FIELD_NOW_NON_STATIC, net.sf.clirr.core.Severity.ERROR, clazz, cField, null);
        }

        if (!bField.isStatic() && cField.isStatic())
        {
            fireDiff(MSG_FIELD_NOW_STATIC, net.sf.clirr.core.Severity.ERROR, clazz, cField, null);
        }

        // JLS, 13.4.10: Adding or deleting a transient modifier of a field
        // does not break compatibility with pre-existing binaries

        // TODO: What about volatile?
    }

    private void checkForVisibilityChange(Field bField, Field cField, JavaClass clazz)
    {
        net.sf.clirr.core.ScopeSelector.Scope bScope = net.sf.clirr.core.ScopeSelector.getScope(bField);
        net.sf.clirr.core.ScopeSelector.Scope cScope = net.sf.clirr.core.ScopeSelector.getScope(cField);

        if (cScope.isMoreVisibleThan(bScope))
        {
            fireDiff(MSG_FIELD_MORE_ACCESSABLE, net.sf.clirr.core.Severity.INFO, clazz, cField, new String[]{bScope.getDesc(), cScope.getDesc()});
        }
        else if (cScope.isLessVisibleThan(bScope))
        {
            fireDiff(MSG_FIELD_LESS_ACCESSABLE, net.sf.clirr.core.Severity.ERROR, clazz, cField, new String[]{bScope.getDesc(), cScope.getDesc()});
        }
    }

    private void fireDiff(net.sf.clirr.core.Message msg, net.sf.clirr.core.Severity severity, JavaClass clazz, Field field, String[] args)
    {
        final String className = clazz.getClassName();
        final net.sf.clirr.core.ApiDifference diff = new ApiDifference(msg, severity, className, null, field.getName(), args);
        getApiDiffDispatcher().fireDiff(diff);

    }
}
