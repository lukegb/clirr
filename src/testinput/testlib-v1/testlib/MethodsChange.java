package testlib;

import java.io.IOException;

public class MethodsChange
{
    private int priv;
    
    static
    {
	System.out.println("static initializer");
    }

    {
	System.out.println("non-static initializer");
    }

    public MethodsChange()
    {
	priv = 2;
    }

    protected MethodsChange(int initialpriv)
    {
	priv = initialpriv;
    }

    public int getPriv()
    {
	return priv;
    }

    public int getPriv2()
    {
	return priv;
    }

    public Integer getPrivAsInteger()
    {
	return new Integer(priv);
    }

    public Number getPrivAsNumber()
    {
	return new Integer(priv);
    }

    public void printPriv()
    {
	System.out.println(priv);
    }

    public void removedMethod(String x)
    {
    }

    public void weakenParamType(String s)
    {
    }

    public void strengthenParamType(Object s)
    {
    }

    public void changeParamType(String s)
    {
    }

    public void throwIOException() throws IOException
    {
	throw new java.io.IOException();
    }

    public void throwException() throws Exception
    {
	throw new java.io.IOException();
    }

    public void throwException2() throws Exception
    {
	throw new Exception();
    }

    public void throwRuntimeException() throws RuntimeException
    {
	throw new RuntimeException();
    }

    public void throwNoRuntimeException()
    {
    }

    public void throwNoException()
    {
    }
}
