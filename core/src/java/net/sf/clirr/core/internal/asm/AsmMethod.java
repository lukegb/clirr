package net.sf.clirr.core.internal.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

public class AsmMethod extends AbstractAsmScoped implements Method
{
    private final Repository repository;

    private final Type returnType;

    private final String name;

    private final Type[] argumentTypes;

    private final String[] exceptions;
    
    AsmMethod(Repository repository, int access, Type returnType,
            String name, Type[] argumentTypes, String[] exceptions)
    {
        super(access);
        this.repository = repository;
        this.returnType = returnType;
        this.name = name;
        this.argumentTypes = argumentTypes;
        this.exceptions = exceptions;
    }
    
    public JavaType getReturnType()
    {
        if (Type.VOID_TYPE.equals(returnType))
        {
            return null;
        }
        return repository.findTypeByName(returnType.getClassName());
    }

    public JavaType[] getArgumentTypes()
    {
        // TODO support primitive types
        JavaType[] ret = new JavaType[argumentTypes.length];
        for (int i = 0; i < ret.length; i++)
        {
            final String className = argumentTypes[i].getClassName();
            ret[i] = repository.findTypeByName(className);
        }
        return ret;
    }

    public JavaType[] getDeclaredExceptions()
    {
        JavaType[] ret = new JavaType[exceptions.length];
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = repository.findTypeByName(exceptions[i]);
        }
        return ret;
    }

    public boolean isFinal()
    {
        return checkFlag(Opcodes.ACC_FINAL);
    }

    public boolean isStatic()
    {
        return checkFlag(Opcodes.ACC_STATIC);
    }

    public boolean isAbstract()
    {
        return checkFlag(Opcodes.ACC_ABSTRACT);
    }

    public boolean isDeprecated()
    {
        return checkFlag(Opcodes.ACC_DEPRECATED);
    }

    public String getName()
    {
        return name;
    }

    public Scope getEffectiveScope()
    {
        // TODO Auto-generated method stub
        return getDeclaredScope();
    }

}
