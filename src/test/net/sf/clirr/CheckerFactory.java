package net.sf.clirr;

import net.sf.clirr.framework.ClassChangeCheck;

/**
 * Provides a way for check tests to create a checker via the package private
 * Checker constructor, even if the tests are not in the same package.
 */
public class CheckerFactory
{
    public static Checker createChecker(ClassChangeCheck check)
    {
        return new Checker(check);
    }
}
