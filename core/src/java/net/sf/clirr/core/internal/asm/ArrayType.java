package net.sf.clirr.core.internal.asm;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

class ArrayType implements JavaType
{
    private final JavaType basicType;
    private final int dimension;

    ArrayType(JavaType basicType, int dimension)
    {
        this.basicType = basicType;
        this.dimension = dimension;
    }
    
    public String getBasicName()
    {
        return basicType.getBasicName();
    }

    public String getName()
    {
        StringBuffer arrayDimIndicator = new StringBuffer();
        final int arrayDimension = getArrayDimension();
        for (int i = 0; i < arrayDimension; i++)
        {
            arrayDimIndicator.append("[]");
        }

        return basicType.getBasicName() + arrayDimIndicator;
    }

    public JavaType getContainingClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaType[] getSuperClasses()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaType[] getAllInterfaces()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Method[] getMethods()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Field[] getFields()
    {
        return new Field[0];
    }

    public int getArrayDimension()
    {
        return dimension;
    }

    public boolean isPrimitive()
    {
        return basicType.isPrimitive();
    }

    public boolean isFinal()
    {
        return false;
    }

    public boolean isAbstract()
    {
        return false;
    }

    public boolean isInterface()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Scope getDeclaredScope()
    {
        return basicType.getDeclaredScope();
    }

    public Scope getEffectiveScope()
    {
        return basicType.getEffectiveScope();
    }

    public int getClassFormatVersion()
    {
        return basicType.getClassFormatVersion();
    }

    public String toString()
    {
        
        return "ArrayType[" + basicType.toString() + " ^ " + dimension + "]";
    }
}
