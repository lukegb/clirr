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

package net.sf.clirr.event;


/**
 * Describes an API change.
 *
 * @author Lars
 */
public final class ApiDifference
{
    private static final int HASHCODE_MAGIC = 29;

    /** human readable change report. */
    private String report;

    /**
     * severity of the change in terms of binary compatibility,
     * as determined by clirr.
     */
    private Severity binaryCompatibilitySeverity;

    /**
     * severity of the change in terms of source compatibility,
     * as determined by clirr.
     */
    private Severity sourceCompatibilitySeverity;

    /** The fully qualified class name that is affected by the API change. */
    private String affectedClass;

    /**
     * The method that is affected, if any.
     * <p/>
     * The content is the method name plus the fully qualified
     * parameter types separated by comma and space and enclosed in
     * brackets, e.g. "doStuff(java.lang.String, int)".
     * <p/>
     * This value is <code>null</code> if no single method is
     * affected, i.e. if the
     * api change affects a field or is global
     * (like "class is now final").
     */
    private String affectedMethod;

    /**
     * The field that is affected, if any.
     * <p/>
     * The content is the field name, e.g. "someValue".
     * Type information for the field is not available.
     * <p/>
     * This value is <code>null</code> if no single field is
     * affected, i.e. if the
     * api change affects a method or is global
     * (like "class is now final").
     */
    private String affectedField;

    /**
     * Create a new API differnce representation.
     *
     * @param report a human readable string describing the change that was made, must be non-null.
     * @param severity the severity in terms of binary and source code compatibility, must be non-null.
     * @param clazz the fully qualified class name where the change occured, must be non-null.
     * @param method the method signature of the method that changed, <code>null</code>
     *   if no method was affected.
     * @param field the field name where the change occured, <code>null</code>
     *   if no field was affected.
     */
    public ApiDifference(String report, Severity severity, String clazz, String method, String field)
    {
        this(report, severity, severity, clazz, method, field);
    }

    /**
     * Create a new API differnce representation.
     *
     * @param report a human readable string describing the change that was made, must be non-null.
     * @param binarySeverity the severity in terms of binary compatibility, must be non-null.
     * @param sourceSeverity the severity in terms of source code compatibility, must be non-null.
     * @param clazz the fully qualified class name where the change occured, must be non-null.
     * @param method the method signature of the method that changed, <code>null</code>
     *   if no method was affected.
     * @param field the field name where the change occured, <code>null</code>
     *   if no field was affected.
     */
    public ApiDifference(String report, Severity binarySeverity, Severity sourceSeverity,
                         String clazz, String method, String field)
    {
        checkNonNull(report);
        checkNonNull(binarySeverity);
        checkNonNull(sourceSeverity);
        checkNonNull(clazz);

        this.report = report;
        this.binaryCompatibilitySeverity = binarySeverity;
        this.sourceCompatibilitySeverity = sourceSeverity;
        this.affectedClass = clazz;
        this.affectedField = field;
        this.affectedMethod = method;
    }

    private void checkNonNull(Object o)
    {
        if (o == null)
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * The Severity of the API difference in terms of binary compatibility.
     * ERROR means that clients will definitely break, WARNING means that
     * clients may break, depending on how they use the library.
     * See the eclipse paper for further explanation.
     *
     * @return the severity of the API difference in terms of binary compatibility.
     */
    public Severity getBinaryCompatibilitySeverity()
    {
        return binaryCompatibilitySeverity;
    }

    /**
     * The Severity of the API difference in terms of source compatibility.
     * Sometimes this is different than {@link #getBinaryCompatibilitySeverity
     * binary compatibility severity}, for example adding a checked exception
     * to a method signature is binary compatible but not source compatible.
     * ERROR means that clients will definitely break, WARNING means that
     * clients may break, depending on how they use the library.
     * See the eclipse paper for further explanation.
     *
     * @return the severity of the API difference in terms of source code
     * compatibility.
     */
    public Severity getSourceCompatibilitySeverity()
    {
        return sourceCompatibilitySeverity;
    }

    public Severity getMaximumSeverity()
    {
        final Severity src = getSourceCompatibilitySeverity();
        final Severity bin = getBinaryCompatibilitySeverity();
        return src.compareTo(bin) < 0 ? bin : src;
    }

    /**
     * Human readable api change description.
     *
     * @return a human readable description of this API difference.
     */
    public String getReport()
    {
        return report;
    }

    /**
     * The fully qualified class name of the class that has changed.
     * @return fully qualified class name of the class that has changed.
     */
    public String getAffectedClass()
    {
        return affectedClass;
    }

    /**
     * Method signature of the method that has changed, if any.
     * @return method signature or <code>null</code> if no method is affected.
     */
    public String getAffectedMethod()
    {
        return affectedMethod;
    }

    /**
     * Field name of the field that has changed, if any.
     * @return field name or <code>null</code> if no field is affected.
     */
    public String getAffectedField()
    {
        return affectedField;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return report + " (" + binaryCompatibilitySeverity + ") - "
                + getAffectedClass() + '[' + getAffectedField() + '/' + getAffectedMethod() + ']';
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof ApiDifference))
        {
            return false;
        }

        final ApiDifference other = (ApiDifference) o;

        if (!report.equals(other.report))
        {
            return false;
        }

        if (!binaryCompatibilitySeverity.equals(other.binaryCompatibilitySeverity))
        {
            return false;
        }

        if (!sourceCompatibilitySeverity.equals(other.sourceCompatibilitySeverity))
        {
            return false;
        }


        final String otherClass = other.affectedClass;
        if (!affectedClass.equals(otherClass))
        {
            return false;
        }

        final String otherMethod = other.affectedMethod;
        if (affectedMethod != null ? !affectedMethod.equals(otherMethod) : otherMethod != null)
        {
            return false;
        }

        final String otherField = other.affectedField;
        if (affectedField != null ? !affectedField.equals(otherField) : otherField != null)
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        int result;
        result = report != null ? report.hashCode() : 0;
        result = HASHCODE_MAGIC * result + binaryCompatibilitySeverity.hashCode();
        result = HASHCODE_MAGIC * result + sourceCompatibilitySeverity.hashCode();
        result = HASHCODE_MAGIC * result + affectedClass.hashCode();
        result = HASHCODE_MAGIC * result + (affectedMethod != null ? affectedMethod.hashCode() : 0);
        result = HASHCODE_MAGIC * result + (affectedField != null ? affectedField.hashCode() : 0);
        return result;
    }


}
