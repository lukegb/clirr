package testlib.modifiers;

/**
 * It is a binary compatibility error for a non-final class to become
 * final in a later version, because users may have created classes which
 * are derived from it. Such classes will fail to load with the new version
 * of the class present.
 */

public class NonFinalBecomesFinal
{
}
