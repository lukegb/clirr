package testlib.scope;

public class ClassScopeChange
{
    // public class is unchanged
    public static class A1 {}
    
    // public class becomes protected
    protected static class A2 {}

    // public class becomes package
    static class A3 {}

    // public class becomes private
    private static class A4 {}
    
    // protected class is unchanged
    protected static class B1 {}
    
    // protected class becomes public
    public static class B2 {}

    // protected class becomes package
    static class B3 {}

    // protected class becomes private
    private static class B4 {}
    
    // package class is unchanged
    static class C1 {}

    // package class becomes public
    public static class C2 {}

    // package class becomes protected
    protected static class C3 {}

    // package class becomes private
    private static class C4 {}
    
    // private class is unchanged
    private static class D1 {}
    
    // private class becomes public
    public static class D2 {}

    // private class becomes protected
    protected static class D3 {}

    // private class becomes package
    static class D4 {}

    // unchanged scope of class defined inside method body
    private void method1()
    {
        class E1 {};
        E1 e1 = new E1();
    }
}
