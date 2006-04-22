package net.sf.clirr.core.spi;

/**
 * A Java Type (Object, Interface, primitive type or void).
 * 
 * @author lkuehne
 */
public interface JavaType extends Named, Scoped
{
    /**
     * The type's fully qualified class name.
     * In case of array types, this is the name without the array brackets
     * 
     * @return a fully qualified class name,
     * like <code>"my.company.procuct.SampleClass"</code>.
     */
    String getBasicName();

    /**
     * The type's fully qualified class name.
     * In case of array types, this is the name with the array brackets.
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

    /**
     * The number of array dimensions this type has.
     * @return 0 if this type does not represent an array.
     */
    int getArrayDimension();
    
    /**
     * Whether this type represents a primitive type like <code>int</code>.
     * @return true iff this type represents a primitive type.
     */
    boolean isPrimitive();
    
    /**
     * Whether this class is declared as final.
     * @return true iff this type represents a final class or a {@link #isPrimitive() primitive} type.
     */
    boolean isFinal();

    /**
     * Whether this type represents a class that is declared as abstract. 
     * Note that interfaces are not abstract.
     * 
     * @return true iff this type represents an abstract class.
     */
    boolean isAbstract();

    /**
     * Whether this type represents an interface.
     * 
     * @return true iff this type represents an interface. 
     */
    boolean isInterface();
}
