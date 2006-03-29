package net.sf.clirr.core.internal.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

class AsmJavaType extends AbstractAsmScoped implements JavaType
{
    private final Repository repository;
    
    private final String basicName;

    private String superClassName;
    
    private final List fields = new ArrayList();
    
    private final List methods = new ArrayList();

    private final String[] interfaceNames;

    public AsmJavaType(Repository repository, int access, String basicName, String superClassName, String[] interfaceNames)
    {
        super(access);
        this.repository = repository;
        this.basicName = basicName;
        this.superClassName = superClassName;
        this.interfaceNames = interfaceNames;
    }

    
    public String getBasicName()
    {
        return basicName;
    }
    
    public String getName()
    {
        // arrays are always represented by ArrayType instances, so name == basicName
        return basicName;
    }

    public JavaType getContainingClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JavaType[] getSuperClasses()
    {
        if (superClassName == null)
        {
            return new JavaType[0];
        }
        JavaType superType = repository.findTypeByName(superClassName);
        final JavaType[] superSuper = superType.getSuperClasses();
        JavaType[] ret = new JavaType[superSuper.length + 1];
        System.arraycopy(superSuper, 0, ret, 0, superSuper.length);
        ret[superSuper.length] = superType;
        return ret;
    }

    public JavaType[] getAllInterfaces()
    {
        Set interfaceSet = new HashSet(interfaceNames.length);
        for (int i = 0; i < interfaceNames.length; i++)
        {
            JavaType type = repository.findTypeByName(interfaceNames[i]);
            interfaceSet.add(type);
        }
        JavaType[] superClasses = getSuperClasses();
        for (int i = 0; i < superClasses.length; i++)
        {
            JavaType superClass = superClasses[i];
            final JavaType[] superInterfaces = superClass.getAllInterfaces();
            interfaceSet.addAll(Arrays.asList(superInterfaces));
        }
        final JavaType[] ret = new JavaType[interfaceSet.size()];
        interfaceSet.toArray(ret);
        return ret;
    }

    public JavaType[] getInnerClasses()
    {
        // TODO Auto-generated method stub
        return null;
    }

    void addMethod(Method method)
    {
        methods.add(method);
    }

    public Method[] getMethods()
    {
        Method[] ret = new Method[methods.size()];
        methods.toArray(ret);
        return ret;
    }

    void addField(Field field)
    {
        fields.add(field);
    }
    
    public Field[] getFields()
    {
        Field[] ret = new Field[fields.size()];
        fields.toArray(ret);
        return ret;
    }

    public boolean isPrimitive()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public int getArrayDimension()
    {
        // TODO: handle correctly for method argument and return types
        return 0;
    }

    public boolean isFinal()
    {
        return checkFlag(Opcodes.ACC_FINAL);
    }

    public boolean isAbstract()
    {
        return checkFlag(Opcodes.ACC_ABSTRACT);
    }

    public boolean isInterface()
    {
        return checkFlag(Opcodes.ACC_INTERFACE);
    }

    public Scope getEffectiveScope()
    {
        // TODO: replace with real impl
        return getDeclaredScope();
    }


    public String toString()
    {
        return "AsmJavaType[" + getName() + "]";
    }


    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!AsmJavaType.class.equals(obj.getClass()))
        {
            return false;
        }
        AsmJavaType other = (AsmJavaType) obj;
        if (other.repository != this.repository)
        {
            return false;
        }
        return (other.getName().equals(this.getName()));
    }


    public int hashCode()
    {
        return getName().hashCode();
    }
    
    
}
