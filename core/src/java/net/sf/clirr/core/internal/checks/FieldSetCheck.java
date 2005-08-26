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

package net.sf.clirr.core.internal.checks;

import java.util.Comparator;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.internal.AbstractDiffReporter;
import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.CoIterator;
import net.sf.clirr.core.internal.NameComparator;
import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Scope;
import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.ScopeSelector;
import net.sf.clirr.core.Message;

/**
 * Checks the fields of a class.
 *
 * @author lkuehne
 */
public class FieldSetCheck
    extends AbstractDiffReporter
    implements ClassChangeCheck
{
    private static final Message MSG_FIELD_ADDED = new Message(6000);
    private static final Message MSG_FIELD_REMOVED = new Message(6001);
    private static final Message MSG_FIELD_NOT_CONSTANT = new Message(6002);
    private static final Message MSG_FIELD_CONSTANT_CHANGED = new Message(6003);
    private static final Message MSG_FIELD_TYPE_CHANGED = new Message(6004);
    private static final Message MSG_FIELD_NOW_NON_FINAL = new Message(6005);
    private static final Message MSG_FIELD_NOW_FINAL = new Message(6006);
    private static final Message MSG_FIELD_NOW_NON_STATIC = new Message(6007);
    private static final Message MSG_FIELD_NOW_STATIC = new Message(6008);
    private static final Message MSG_FIELD_MORE_ACCESSIBLE = new Message(6009);
    private static final Message MSG_FIELD_LESS_ACCESSIBLE = new Message(6010);
    private static final Message MSG_CONSTANT_FIELD_REMOVED = new Message(6011);

    private static final Comparator COMPARATOR = new NameComparator();
    private ScopeSelector scopeSelector;

    public FieldSetCheck(ApiDiffDispatcher dispatcher, ScopeSelector scopeSelector)
    {
        super(dispatcher);
        this.scopeSelector = scopeSelector;
    }

    public final boolean check(JavaType baselineClass, JavaType currentClass)
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
                    String scope = cField.getDeclaredScope().getDesc();
                    fireDiff(MSG_FIELD_ADDED,
                        Severity.INFO, currentClass, cField,
                        new String[]{scope});
                }
            }
            else if (cField == null)
            {
                if (scopeSelector.isSelected(bField))
                {
                    if ((bField.getConstantValue() != null) && bField.isFinal())
                    {
                        // Fields which are compile-time constants will have
                        // been inlined into callers; even though the field
                        // has been deleted, the caller will continue to use
                        // the old value. The result is therefore not
                        // technically a binary incompatibility, though it is
                        // a source-code incompatibility.
                        // See bugtracker #961222
                        fireDiff(MSG_CONSTANT_FIELD_REMOVED,
                            getSeverity(baselineClass, bField, Severity.WARNING),
                            getSeverity(baselineClass, bField, Severity.ERROR),
                            baselineClass, bField, null);
                    }
                    else
                    {
                        fireDiff(MSG_FIELD_REMOVED,
                            getSeverity(baselineClass, bField, Severity.ERROR),
                            baselineClass, bField, null);
                    }
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

    private void checkForConstantValueChange(Field bField, Field cField, JavaType currentClass)
    {
        if (!(bField.isStatic() && bField.isFinal() && cField.isStatic() && cField.isFinal()))
        {
            return;
        }

        final Object bVal = bField.getConstantValue();

        if (bVal != null)
        {
            final String bValRep = bVal.toString();
            final Object cVal = cField.getConstantValue();
            if (cVal == null)
            {
                // TODO: also check whether old field is final. If it's not
                // final, then external code cannot have inlined the
                // constant, and therefore we can issue an INFO instead
                // of a warning. Actually, may be better to introduce a
                // different message code rather than issue this code with
                // two different severity levels..
                fireDiff(MSG_FIELD_NOT_CONSTANT,
                        getSeverity(currentClass, bField, Severity.WARNING),
                        currentClass, cField, null);
                return;
            }

            final String cValRep = String.valueOf(cVal);
            if (!bValRep.equals(cValRep))
            {
                // TODO: print out old and new value
                // How can that be done with BCEL, esp. for boolean values?
                //
                // TODO: also check whether field is final (see above).
                fireDiff(MSG_FIELD_CONSTANT_CHANGED,
                        getSeverity(currentClass, bField, Severity.WARNING),
                        currentClass, cField, null);
            }
        }
    }

    private void checkForTypeChange(Field bField, Field cField, JavaType currentClass)
    {
        final String bSig = bField.getType().toString();
        final String cSig = cField.getType().toString();
        if (!bSig.equals(cSig))
        {
            fireDiff(MSG_FIELD_TYPE_CHANGED,
                    getSeverity(currentClass, bField, Severity.ERROR),
                    currentClass, bField,
                    new String[] {bSig, cSig});
        }
    }

    private void checkForModifierChange(Field bField, Field cField, JavaType clazz)
    {
        if (bField.isFinal() && !cField.isFinal())
        {
            fireDiff(MSG_FIELD_NOW_NON_FINAL,
                Severity.INFO, clazz, cField, null);
        }

        if (!bField.isFinal() && cField.isFinal())
        {
            fireDiff(MSG_FIELD_NOW_FINAL, Severity.ERROR, clazz, cField, null);
        }

        if (bField.isStatic() && !cField.isStatic())
        {
            fireDiff(MSG_FIELD_NOW_NON_STATIC,
                getSeverity(clazz, bField, Severity.ERROR),
                clazz, cField, null);
        }

        if (!bField.isStatic() && cField.isStatic())
        {
            fireDiff(MSG_FIELD_NOW_STATIC,
                getSeverity(clazz, bField, Severity.ERROR),
                clazz, cField, null);
        }

        // JLS, 13.4.10: Adding or deleting a transient modifier of a field
        // does not break compatibility with pre-existing binaries

        // TODO: What about volatile?
    }

    private void checkForVisibilityChange(Field bField, Field cField, JavaType clazz)
    {
        Scope bScope = bField.getEffectiveScope();
        Scope cScope = cField.getEffectiveScope();

        if (cScope.isMoreVisibleThan(bScope))
        {
            fireDiff(MSG_FIELD_MORE_ACCESSIBLE,
                Severity.INFO, clazz, cField,
                new String[] {bScope.getDesc(), cScope.getDesc()});
        }
        else if (cScope.isLessVisibleThan(bScope))
        {
            fireDiff(MSG_FIELD_LESS_ACCESSIBLE,
                getSeverity(clazz, bField, Severity.ERROR),
                clazz, cField,
                new String[] {bScope.getDesc(), cScope.getDesc()});
        }
    }

    private void fireDiff(
        Message msg,
        Severity severity,
        JavaType clazz,
        Field field,
        String[] args)
    {
        fireDiff(msg, severity, severity, clazz, field, args);
    }

    private void fireDiff(
        Message msg,
        Severity binarySeverity,
        Severity sourceSeverity,
        JavaType clazz,
        Field field,
        String[] args)
    {
        final String className = clazz.getName();
        final ApiDifference diff =
            new ApiDifference(
                msg,
                binarySeverity, sourceSeverity,
                className, null, field.getName(), args);
        getApiDiffDispatcher().fireDiff(diff);
    }
}
