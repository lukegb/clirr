package net.sf.clirr.core.spi;

/**
 * Enumeration type that represents an "accessibility" level for
 * a java class, field or method.
 * <p>
 * Change of access rights from lower to higher visibility rating is a
 * binary-compatible change. Change of access rights from higher to
 * lower is a binary-incompatible change.
 * <p>
 * Public &gt; Protected &gt; Package &gt; Private
 *
 * @author Simon Kitching
 */
public final class Scope
{
    private int vis;
    private String desc;
    private String decl;

    /** Object representing private scoped objects. */
    public static final Scope PRIVATE = new Scope(0, "private", "private");

    /** Object representing package scoped objects. */
    public static final Scope PACKAGE = new Scope(1, "package", "");

    /** Object representing protected scoped objects. */
    public static final Scope PROTECTED = new Scope(2, "protected", "protected");

    /** Object representing public scoped objects. */
    public static final Scope PUBLIC = new Scope(3, "public", "public");

    private Scope(int vis, String desc, String decl)
    {
        this.vis = vis;
        this.desc = desc;
        this.decl = decl;
    }

    public boolean isMoreVisibleThan(Scope v)
    {
        return this.vis > v.vis;
    }

    public boolean isLessVisibleThan(Scope v)
    {
        return this.vis < v.vis;
    }

    public String getDesc()
    {
        return desc;
    }

    /** the Java visibility modifier. **/
    public String getDecl()
    {
        return decl;
    }
}
