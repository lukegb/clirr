package net.sf.clirr.core.internal.checks;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.clirr.core.internal.ApiDiffDispatcher;
import net.sf.clirr.core.internal.checks.ExpectedDiff;
import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.DiffListener;
import net.sf.clirr.core.MessageTranslator;
import net.sf.clirr.core.Severity;
import junit.framework.TestCase;

class TestDiffListener implements ApiDiffDispatcher, DiffListener
{
    private MessageTranslator translator = new MessageTranslator();
    
    private Set diffs = new HashSet();
    
    public void fireDiff(ApiDifference difference)
    {
        diffs.add(difference);
    }
    
    public void checkExpected(ExpectedDiff[] expectedDiffs)
    {
        for (int i=0; i<expectedDiffs.length; ++i)
        {
            ExpectedDiff expected = expectedDiffs[i];

            // now see if the expected diff is in fact in the set of
            // diffs that occurred during the test comparison
            boolean found = false;
            for(Iterator j = diffs.iterator(); j.hasNext() && !found;)
            {
                ApiDifference actual = (ApiDifference) j.next();
                found = expected.matches(actual);
            }

            if (!found)
            {
                // build a useful failure message
                MessageTranslator translator = new MessageTranslator();
                
                StringBuffer buf = new StringBuffer();
                buf.append("Expected diff " + i + " '" + expected + "' was not generated.\n");
                if (diffs.size() == 0)
                {
                    buf.append("No diffs were generated.");
                }
                else
                {
                    buf.append("Actual diffs generated were: \n");
                    for(Iterator diffIter = diffs.iterator(); diffIter.hasNext();)
                    {
                        ApiDifference diff = (ApiDifference) diffIter.next();
                        
                        buf.append(" * ");
                        buf.append(diff.toString(translator));
                        buf.append("\n");
                    }
                }
                
                TestCase.fail(buf.toString());
            }
        }
        
        StringBuffer buf = null;
        for (Iterator it = diffs.iterator(); it.hasNext();)
        {
            ApiDifference actual = (ApiDifference) it.next();
            
            // see if the actual (generated) diff is in the expected set
            boolean found = false;
            for(int i=0; i<expectedDiffs.length && !found; ++i)
            {
                found = expectedDiffs[i].matches(actual);
            }
            
            if (!found)
            {
                if (buf == null)
                {
                    buf = new StringBuffer();
                    buf.append("Unexpected diffs:\n");
                }
                buf.append(" * ");
                buf.append(actual.toString(translator));
                buf.append("\n");
            }
        }
        
        if (buf != null)
        {
            // we must have found at least one unexpected diff
            TestCase.fail(buf.toString());
        }
    }

    public int countBinaryCompatibilityDiffs(Severity severity)
    {
        int ret = 0;
        for (Iterator it = diffs.iterator(); it.hasNext();)
        {
            ApiDifference diff = (ApiDifference) it.next();
            if (diff.getBinaryCompatibilitySeverity().equals(severity))
            {
                ret += 1;
            }
        }
        return ret;
    }
    
    public int countSourceCompatibilityDiffs(Severity severity)
    {
        int ret = 0;
        for (Iterator it = diffs.iterator(); it.hasNext();)
        {
            ApiDifference diff = (ApiDifference) it.next();
            if (diff.getSourceCompatibilitySeverity().equals(severity))
            {
                ret += 1;
            }
        }
        return ret;
    }
    
    public void start() 
    {
    }
    
    public void reportDiff(ApiDifference difference) {
        diffs.add(difference);
    }
    
    public void stop() 
    {
    }
}
