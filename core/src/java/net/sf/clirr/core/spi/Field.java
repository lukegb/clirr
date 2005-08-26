package net.sf.clirr.core.spi;

/**
 * Describes a field of a class.
 */
public interface Field extends Named, Scoped
{
    /**
     * The type of this field.
     */
    JavaType getType();
    
    /**
     * Whether the field is declared as final.
     */
    boolean isFinal();
    
    /**
     * Whether the field is declared as static.
     */
    boolean isStatic();

    /**
     * Whether the field is deprecated.
     */
    boolean isDeprecated();
    
    /**
     * Returns the constant value of this field.
     * The constant value is an Object if the field is static and final and the java compiler 
     * could calculate the value at compilation time.
     * 
     * @return the constant value or <code>null</code> if the compiler could 
     * not calculate the value at compilation time  
     */
    Object getConstantValue();
}
