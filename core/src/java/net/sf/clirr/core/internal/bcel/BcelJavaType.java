package net.sf.clirr.core.internal.bcel;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.Repository;

/**
 *
 */
public final class BcelJavaType implements JavaType
{
    private Type type;
    private JavaClass clazz;
    private Repository repository;
    
    
    public BcelJavaType(Type bcelType, Repository repository)
    {
        this.type = bcelType;
        this.repository = repository;
        this.clazz = findJavaClass(type);
    }

    public BcelJavaType(JavaClass clazz)
    {
        this.type = null; // TODO: how can I convert a JavaClass to the corresponding Type?
        this.repository = clazz.getRepository();
        this.clazz = clazz;
    }
    
    public String getName() {
        if (clazz != null)
        {
            return clazz.getClassName();
        }
        else
        {
            return type.toString();
        }
    }

    public JavaType getContainingClass() {

        // TODO: move code from ScopeHelper here
        
        return null;
    }

    private JavaClass findJavaClass(Type type) {
        if (!(type instanceof ObjectType))
        {
            return null;
        }
        ObjectType ot = (ObjectType) type;
        return repository.findClass(ot.getClassName());
    }

    public JavaType[] getSuperClasses() {
        if (clazz == null)
        {
            return new JavaType[0];
        }
        final JavaClass[] superClasses = clazz.getSuperClasses();
        return convertToJavaTypeArray(superClasses);
    }

    public JavaType[] getAllInterfaces() {
        if (clazz == null)
        {
            return new JavaType[0];
        }
        final JavaClass[] interfaces = clazz.getAllInterfaces();
        return convertToJavaTypeArray(interfaces);
    }

    public JavaType[] getInnerClasses() {
        return new JavaType[0];
    }

    public Method[] getMethods() {
        if (clazz == null)
        {
            return new Method[0];
        }
        final org.apache.bcel.classfile.Method[] methods = clazz.getMethods();
        Method[] ret = new Method[methods.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new BcelMethod(clazz, methods[i]);
        }
        return ret;
    }

    public Field[] getFields() {
        if (clazz == null)
        {
            return new Field[0];
        }
        final org.apache.bcel.classfile.Field[] fields = clazz.getFields();
        Field[] ret = new Field[fields.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new BcelField(clazz, fields[i]);
        }
        return ret;
    }

    public boolean isPrimitive() {
        return clazz == null;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isFinal() {
        if (clazz == null)
        {
            return false;
        }
        return clazz.isFinal();
    }

    public boolean isAbstract() {
        if (clazz == null)
        {
            return false;
        }
        return clazz.isAbstract();
    }

    public boolean isInterface() {
        return clazz.isInterface();
    }

    public Scope getDeclaredScope() {
        return BcelScopeHelper.getClassScope(clazz);
    }

    public Scope getEffectiveScope() {
         return getDeclaredScope(); // FIXME
    }
    
    public String toString()
    {
        return getName();
    }
    
    /**
     * @param bcelClasses
     * @return
     */
    private JavaType[] convertToJavaTypeArray(final JavaClass[] bcelClasses) {
        JavaType[] ret = new JavaType[bcelClasses.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new BcelJavaType(bcelClasses[i]);
        }
        return ret;
    }


}
