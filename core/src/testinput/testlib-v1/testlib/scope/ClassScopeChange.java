package testlib.scope;

public class ClassScopeChange
{
    // public class is unchanged
    public static class A1 {}
    
    // public class becomes protected
    public static class A2 {}

    // public class becomes package
    public static class A3 {}

    // public class becomes private
    public static class A4 {}
    
    // public class is removed
    public static class A5 {}

    // protected class is unchanged
    protected static class B1 {}
    
    // protected class becomes public
    protected static class B2 {}

    // protected class becomes package
    protected static class B3 {}

    // protected class becomes private
    protected static class B4 {}
    
    // package class is unchanged
    static class C1 {}

    // package class becomes public
    static class C2 {}

    // package class becomes protected
    static class C3 {}

    // package class becomes private
    static class C4 {}

    // package class is removed
    static class C5 {}
    
    // private class is unchanged
    private static class D1 {}
    
    // private class becomes public
    private static class D2 {}

    // private class becomes protected
    private static class D3 {}

    // private class becomes package
    private static class D4 {}

    // unchanged scope of class defined inside method body
    private void method1()
    {
        class E1 {};
        E1 e1 = new E1();
    }
}
