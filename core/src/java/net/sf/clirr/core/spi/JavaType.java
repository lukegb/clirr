package net.sf.clirr.core.spi;

/**
 * A Java Type (Object, Interface, primitive type or void).
 * 
 * @author lkuehne
 */
public interface JavaType extends Named, Scoped
{
    /**
     * Type fully qualified class name.
     * 
     * @return a fully qualified class name,
     * like <code>"my.company.procuct.SampleClass"</code>.
     */
    String getName();

    /**
     * The containing class if this is an inner class.
     * 
     * @return the containing class or <code>null</code>
     * if this JavaType does not represent an inner class.
     */
    JavaType getContainingClass();


    /**
     * Return the superclasses of this class.
     * 
     * @return the chain of superclasses of this type, starting from 
     * the direct superclass and ending with <code>java.lang.Object</code>.
     */
    JavaType[] getSuperClasses();

    /**
     * Return the list of all interfaces this class implements.
     * 
     * @return the list of all interfaces this class implements/extends, 
     * excluding <code>this</code> if this JavaType represents an interface itself.
     */
    JavaType[] getAllInterfaces();

    JavaType[] getInnerClasses();

    /**
     * All methods that are declared by this class.
     * Methods of superclasses/interfaces are not returned 
     * if they are not overridden/redeclared here.
     * 
     * @return all methods that are declared by this class.
     */
    Method[] getMethods();

    /**
     * All fields that are declared by this class.
     * Fields of superclasses/interfaces are not returned.
     *  
     * @return all fields that are declared by this class.
     */
    Field[] getFields();

    boolean isPrimitive();
    
    boolean isArray();
    
    boolean isFinal();

    boolean isAbstract();

    boolean isInterface();
}
