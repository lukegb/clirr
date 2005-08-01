package testlib;

import java.io.IOException;

public class MethodsChange
{
    private int priv;
    
    public MethodsChange()
    {
	priv = 2;
    }

    protected MethodsChange(int initialpriv, boolean newArg)
    {
	priv = initialpriv;
    }

    protected MethodsChange(Integer initialpriv)
    {
	priv = initialpriv.intValue();
    }

    public int getPriv()
    {
	return priv;
    }

    private int getPriv2()
    {
	return priv;
    }

    public Integer getPrivAsNumber()
    {
	return new Integer(priv);
    }

    public Number getPrivAsInteger()
    {
	return new Integer(priv);
    }

    public Long getPrivSquare()
    {
	return new Long(priv * priv);
    }

    public void printPriv(String prefix)
    {
	System.out.println(prefix + priv);
    }

    public void weakenParamType(Object s)
    {
    }

    public void strengthenParamType(String s)
    {
    }

    public void changeParamType(Integer x)
    {
    }

    public void throwIOException() throws Exception
    {
	throw new java.io.IOException();
    }

    public void throwException() throws IOException
    {
	throw new java.io.IOException();
    }

    public void throwException2()
    {
    }

    public void throwRuntimeException()
    {
	throw new RuntimeException();
    }


    public void throwNoRuntimeException() throws RuntimeException
    {
	throw new RuntimeException();
    }

    public void throwNoException() throws Exception
    {
	throw new Exception();
    }

    /**
     * @deprecated this is a bad method.
     */
    public void becomesDeprecated()
    {
    }

    /** 
     * This method was previously deprecated.
     */
    public void becomesUndeprecated()
    {
    }
    
    
    public void becomesNonFinal()
    {
    }
    
    public final void becomesFinal()
    {
    }

}
