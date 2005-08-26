package net.sf.clirr.core.internal.bcel;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Scope;

final class BcelField implements net.sf.clirr.core.spi.Field
{
    private final Field field;
    private final JavaClass owningClass;
    
    BcelField(JavaClass owningClass, Field field)
    {
        this.owningClass = owningClass;
        this.field = field;
    }
    
    public String getName() {
        return field.getName();
    }

    public JavaType getType() {
        return new BcelJavaType(field.getType(), owningClass.getRepository());
    }

    public boolean isFinal() {
        return field.isFinal();
    }

    public boolean isStatic() {
        return field.isStatic();
    }

    public boolean isDeprecated() 
    {
        Attribute[] attrs = field.getAttributes();
        for (int i = 0; i < attrs.length; ++i)
        {
            if (attrs[i] instanceof org.apache.bcel.classfile.Deprecated)
            {
                return true;
            }
        }
        
        return false;
    }

    public Object getConstantValue() {
        return field.getConstantValue();
    }

    public Scope getDeclaredScope() {
        return BcelScopeHelper.getScope(field.getAccessFlags());
    }

    public Scope getEffectiveScope() {
        return getDeclaredScope(); // FIXME
    }
    
    public String toString()
    {
        return field.toString();
    }
}
