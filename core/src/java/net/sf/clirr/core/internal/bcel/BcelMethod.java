package net.sf.clirr.core.internal.bcel;

import java.util.Arrays;

import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Scope;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

final class BcelMethod implements net.sf.clirr.core.spi.Method
{

    private Method method;
    private JavaClass owningClass;     
    
    public BcelMethod(JavaClass owningClass, Method method) 
    {
        this.owningClass = owningClass;
        this.method = method;
    }

    public JavaType getReturnType() {
        return convertType(method.getReturnType());
    }

    public String getName() {
        return method.getName();
    }

    public boolean isFinal() {
        return method.isFinal();
    }

    public boolean isStatic() {
        return method.isStatic();
    }

    public boolean isAbstract() {
        return method.isAbstract();
    }

    public boolean isDeprecated() {
        Attribute[] attrs = method.getAttributes();
        for (int i = 0; i < attrs.length; ++i)
        {
            if (attrs[i] instanceof org.apache.bcel.classfile.Deprecated)
            {
                return true;
            }
        }

        return false;
    }

    public Scope getDeclaredScope() {
        
        return BcelScopeHelper.getScope(method.getAccessFlags());
    }

    public Scope getEffectiveScope() {
        // TODO: real impl
        return getDeclaredScope();
    }

    public JavaType[] getArgumentTypes() {
        final Type[] types = method.getArgumentTypes();
        return convertTypes(types);
    }

    private JavaType convertType(Type bcelType) {
        return new BcelJavaType(bcelType, owningClass.getRepository());
    }

    /**
     * @param types
     * @return
     */
    private JavaType[] convertTypes(final Type[] types) {
        JavaType[] retval = new JavaType[types.length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = convertType(types[i]);
        }
        return retval;
    }

    public String toString() {
        return owningClass.getClassName() 
        + "#" + getName() 
        + Arrays.asList(getArgumentTypes()); 
    }
    
    
}
