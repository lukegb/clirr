package net.sf.clirr.core.internal.asm;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

class PrimitiveType implements JavaType
{
    private final String basicName;

    PrimitiveType(String name)
    {
        this.basicName = name;
    }
    
    public String getBasicName()
    {
        return basicName;
    }

    public String getName()
    {
        return basicName;
    }

    public JavaType getContainingClass()
    {
        return null;
    }

    public JavaType[] getSuperClasses()
    {
        return new JavaType[0];
    }

    public JavaType[] getAllInterfaces()
    {
        return new JavaType[0];
    }

    public JavaType[] getInnerClasses()
    {
        return new JavaType[0];
    }

    public Method[] getMethods()
    {
        return new Method[0];
    }

    public Field[] getFields()
    {
        return new Field[0];
    }

    public int getArrayDimension()
    {
        return 0;
    }

    public boolean isPrimitive()
    {
        return true;
    }

    public boolean isFinal()
    {
        return true;
    }

    public boolean isAbstract()
    {
        return false;
    }

    public boolean isInterface()
    {
        return false;
    }

    public Scope getDeclaredScope()
    {
        return Scope.PUBLIC;
    }

    public Scope getEffectiveScope()
    {
        return Scope.PUBLIC;
    }
    
    public String toString()
    {
        return getName();
    }
}