package testlib.modifiers;

/**
 * Because this class has a package-scope constructor, no subclass of this
 * class can ever be created. And because of that, it is not a problem if
 * this class is declared "final" in a later version.
 * <p>
 * Classes with only private constructors are commonly used to implement
 * an "enumerated type" in java, as is done here.
 */

public class EffectivelyFinal
{
    int val;
    
    public static final EffectivelyFinal ZERO = new EffectivelyFinal(0);
    public static final EffectivelyFinal ONE = new EffectivelyFinal(1);
    
    private EffectivelyFinal(int i) 
    {
        val = i;
    }
    
    public int getValue() 
    {
        return val;
    }
}
