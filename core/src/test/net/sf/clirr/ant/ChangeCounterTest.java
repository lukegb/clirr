package net.sf.clirr.ant;

import junit.framework.TestCase;
import net.sf.clirr.core.ApiDifference;
import net.sf.clirr.core.Severity;
import net.sf.clirr.core.Message;

public class ChangeCounterTest extends TestCase
{
  public void testCorrectCounting()
  {
      // a dummy message object
      Message msg = new Message(0, false);

      ChangeCounter counter = new ChangeCounter();
      counter.reportDiff(new ApiDifference(msg, Severity.WARNING, "Test", null, null, null));
      counter.reportDiff(new ApiDifference(msg, Severity.ERROR, "Test", null, null, null));
      counter.reportDiff(new ApiDifference(msg, Severity.INFO, "Test", null, null, null));
      counter.reportDiff(new ApiDifference(msg, Severity.ERROR, "Test", null, null, null));
      counter.reportDiff(new ApiDifference(msg, Severity.ERROR, "Test", null, null, null));
      counter.reportDiff(new ApiDifference(msg, Severity.WARNING, "Test", null, null, null));
      assertEquals("number of expected errors", 3, counter.getBinErrors());
      assertEquals("number of expected warnings", 2, counter.getBinWarnings());
      assertEquals("number of expected infos", 1, counter.getBinInfos());
  }
}