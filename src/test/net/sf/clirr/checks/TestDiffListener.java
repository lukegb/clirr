package net.sf.clirr.checks;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.clirr.framework.ApiDiffDispatcher;
import net.sf.clirr.event.ApiDifference;
import junit.framework.TestCase;

class TestDiffListener implements ApiDiffDispatcher
{
        private Set diffs = new HashSet();

        public void fireDiff(ApiDifference difference)
        {
            diffs.add(difference);
        }

        public void checkExpected(ApiDifference[] expected)
        {
            HashSet expectedDiffs = new HashSet(expected.length);
            for (int i = 0; i < expected.length; i++) {
                ApiDifference apiDifference = expected[i];
                expectedDiffs.add(apiDifference);
            }

            for (Iterator it = expectedDiffs.iterator(); it.hasNext();) {
                ApiDifference apiDifference = (ApiDifference) it.next();
                TestCase.assertTrue("expected diff " + apiDifference + " was not generated: " + diffs,
                        diffs.contains(apiDifference));
            }

            for (Iterator it = diffs.iterator(); it.hasNext();) {
                ApiDifference apiDifference = (ApiDifference) it.next();
                TestCase.assertTrue("unexpected diff " + apiDifference,
                        expectedDiffs.contains(apiDifference));
            }
        }
}
