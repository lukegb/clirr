package net.sf.clirr.core.internal.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

public class AsmJavaType extends AbstractAsmScoped implements JavaType
{
    private final Repository repository;
    
    private final String name;

    private String superClassName;
    
    private final List fields = new ArrayList();
    
    private final List methods = new ArrayList();

    private final String[] interfaceNames;

    public AsmJavaType(Repository repository, int access, String name, String superClassName, String[] interfaceNames)
    {
        super(access);
        this.repository = repository;
        this.name = name;
        this.superClassName = superClassName;
        this.interfaceNames = interfaceNames;
    }

    
    public String getBasicName()
    {
        // TODO handle array types correctly
        return name;
    }
    
    public String getName()
    {
        return name;
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
        JavaType[] ret = new JavaType[interfaceNames.length];
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = repository.findTypeByName(interfaceNames[i]);
        }
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
        return "AsmJavaType[" + name + "]";
    }
}
