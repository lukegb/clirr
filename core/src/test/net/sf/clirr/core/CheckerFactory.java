package net.sf.clirr.core;

import net.sf.clirr.core.internal.ClassChangeCheck;
import net.sf.clirr.core.Checker;

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

    public static Checker createChecker()
    {
        return new Checker();
    }
}
