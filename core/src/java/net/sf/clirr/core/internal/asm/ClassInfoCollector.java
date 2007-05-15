package net.sf.clirr.core.internal.asm;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * An ASM class visitor that collects the information clirr needs in a JavaType.
 * @author lk
 *
 */
class ClassInfoCollector extends ClassAdapter
{
    private AsmJavaType javaType;
    private final Repository repository;
    
    ClassInfoCollector(Repository repository)
    {
        super(new EmptyVisitor());
        this.repository = repository;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        final String className = prettyprintClassName(name);
        final String superClassName = prettyprintClassName(superName);
        final String[] interfaceNames = prettyprintClassNames(interfaces);
        javaType = new AsmJavaType(version, repository, access, className, superClassName, interfaceNames);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
    {
        Type type = Type.getType(desc);
        final AsmField asmField = new AsmField(javaType, access, name, value, type);
        javaType.addField(asmField);
        
        // currently no need for visiting annotations 
        return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        final Type[] argumentTypes = Type.getArgumentTypes(desc);
        final Type returnType = Type.getReturnType(desc);
        final AsmMethod asmMethod = 
            new AsmMethod(javaType, access, returnType, name, argumentTypes, exceptions);
        javaType.addMethod(asmMethod);

        // currently no need for visiting annotations 
        return null;
    }
    
    
    public void visitInnerClass(String name, String outerName, String innerName, int access)
    {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    private static String prettyprintClassName(final String internal)
    {
        if (internal == null)
        {
            return null;
        }
        return internal.replaceAll("/", ".");
    }
    
    private static String[] prettyprintClassNames(String[] internal)
    {
        String[] ret = new String[internal.length];
        for (int i = 0; i < internal.length; i++)
        {
            ret[i] = prettyprintClassName(internal[i]);
        }
        return ret;
    }

    public AsmJavaType getJavaType()
    {
        return javaType;
    }

}
