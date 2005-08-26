package net.sf.clirr.core.spi;

/**
 * Describes a Java method.
 */
public interface Method extends Named, Scoped
{
    /**
     * 
     * @return the return type of this method, or null if the method return type is <code>void</code>
     */
    JavaType getReturnType();

    /**
     * 
     * @return the argument types of this method, never null.
     */
    JavaType[] getArgumentTypes();

//    JavaType[] getDeclaredExceptions();

    boolean isFinal();
    
    boolean isStatic();

    boolean isAbstract();

    boolean isDeprecated();
}
