package net.sf.clirr.ant;

import junit.framework.TestCase;
import net.sf.clirr.event.ApiDifference;
import net.sf.clirr.event.Severity;

public class ChangeCounterTest extends TestCase
{
  public void testCorrectCounting()
  {
      ChangeCounter counter = new ChangeCounter();
      counter.reportDiff(new ApiDifference("blah", Severity.WARNING));
      counter.reportDiff(new ApiDifference("blah", Severity.ERROR));
      counter.reportDiff(new ApiDifference("blah", Severity.INFO));
      counter.reportDiff(new ApiDifference("blah", Severity.ERROR));
      counter.reportDiff(new ApiDifference("blah", Severity.ERROR));
      counter.reportDiff(new ApiDifference("blah", Severity.WARNING));
      assertEquals("number of expected errors", 3, counter.getErrors());
      assertEquals("number of expected warnings", 2, counter.getWarnings());
      assertEquals("number of expected infos", 1, counter.getInfos());
  }
}