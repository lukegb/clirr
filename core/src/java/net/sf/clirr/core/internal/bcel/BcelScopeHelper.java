package net.sf.clirr.core.internal.bcel;

import net.sf.clirr.core.CheckerException;
import net.sf.clirr.core.spi.Scope;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;

final class BcelScopeHelper 
{
    
    /**
     * 
     *
     */
    private BcelScopeHelper()
    {
    }

    /**
     * Get a Scope object representing the accessibility of the specified
     * object.
     * <p>
     * Note that this method gives the wrong results for JavaClass objects
     * which are nested classes. Use getClassScope(jclass) instead.
     */
    public static Scope getScope(int accessFlags)
    {
        if ((accessFlags & Constants.ACC_PUBLIC) > 0)
        {
            return Scope.PUBLIC;
        }

        if ((accessFlags & Constants.ACC_PROTECTED) > 0)
        {
            return Scope.PROTECTED;
        }

        if ((accessFlags & Constants.ACC_PRIVATE) > 0)
        {
            return Scope.PRIVATE;
        }

        return Scope.PACKAGE;
    }

    /**
     * Java class files only ever contain scope specifiers of "public" or
     * "package". For top-level classes, this is expected: it is not possible
     * to have a top-level protected or private class.
     * <p>
     * However nested classes <i>can</i> be declared as protected or private. The
     * way to tell the real scope of a nested class is to ignore the scope in
     * the actual class file itself, and instead look in the "InnerClasses"
     * attribute stored on the enclosing class. This is exactly what the java
     * compiler does when compiling, and what the jvm does when verifying class
     * linkage at runtime.
     * <p>
     * For a "top-level" class, this method just returns the access scope for
     * the class itself. For nested classes, the enclosing class of the
     * specified class is retrieved and its InnerClasses attribute checked to
     * find the true scope for the specified class.
     * <p>
     * @throws CheckerException if the specified class is a nested class and
     * the enclosing class could not be found, or if the supposedly enclosing
     * class has no reference to the nested class. This exception is not
     * expected to occur in practice, unless a truly screwed-up jar file is
     * passed to clirr for inspection.
     */
    public static Scope getClassScope(JavaClass jclass) throws CheckerException
    {
        int dollarPos = jclass.getClassName().lastIndexOf('$');
        if (dollarPos == -1)
        {
            // not a nested class
            return getScope(jclass.getAccessFlags());
        }

        // ok this is a nested class
        String jclassName = jclass.getClassName();
        String enclosingClassName = jclassName.substring(0, dollarPos);
        Repository repo = jclass.getRepository();
        JavaClass enclosingClass = repo.findClass(enclosingClassName);

        if (enclosingClass == null)
        {
            throw new CheckerException(
                "Unable to locate enclosing class " + enclosingClassName
                + " for nested class " + jclassName);
        }

        ConstantPool pool = enclosingClass.getConstantPool();
        Attribute[] attrs = enclosingClass.getAttributes();
        for (int i = 0; i < attrs.length; ++i)
        {
            if (attrs[i] instanceof InnerClasses)
            {
                InnerClasses ics = (InnerClasses) attrs[i];
                InnerClass[] icarray = ics.getInnerClasses();
                for (int j = 0; j < icarray.length; ++j)
                {
                    // in the code below, instanceof checks should not be necessary
                    // before casting Constants because the classfile format ensures
                    // that instanceof would always be true
                    InnerClass ic = icarray[j];
                    int classIndex = ic.getInnerClassIndex();
                    ConstantClass constClass = (ConstantClass) pool.getConstant(classIndex);
                    int nameIndex = constClass.getNameIndex();
                    ConstantUtf8 nameconst = (ConstantUtf8) pool.getConstant(nameIndex);
                    String classname = nameconst.getBytes().replace('/', '.');
                    if (jclassName.equals(classname))
                    {
                        return getScope(ic.getInnerAccessFlags());
                    }
                }
            }
        }

        // weird; no nested class info found
        throw new CheckerException(
            "Unable to find information in class " + enclosingClass.getClassName()
            + " referring back to nested class " + jclassName);

    }

}
