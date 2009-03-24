package net.sf.clirr.core;

import java.util.Collection;
import java.util.Locale;
import junit.framework.TestCase;
import net.sf.clirr.core.MessageManager;
import net.sf.clirr.core.MessageTranslator;

/**
 * Tests for the Message and MessageManager classes.
 * <p>
 * It is assumed here that the other unit tests have forced every Check
 * class to be loaded into memory, hence all the static Message objects
 * have been created and registered with the MessageManager.
 */
public class MessageTest extends TestCase
{
    /**
     * This test verifies that none of the check classes has used
     * a message-id which is already in use elsewhere.
     * <p>
     * It is assumed that instantiating the Checker class causes every
     * check class to be loaded, which in turn causes every Message
     * object (which are expected to be static members of checks) to be created.
     */
    public void testUnique()
    {
        Checker checker = CheckerFactory.createChecker();
        MessageManager.getInstance().checkUnique();
    }

    /**
     * This test verifies that the default resource bundle contains an
     * entry for every known message.
     * <p>
     * Unfortunately, it is not possible to check whether, for example,
     * the "de" locale has a complete set of translations. This is because
     * the ResourceBundle implementation simply returns a string from an
     * inherited "parent" resource bundle if the key is not found in a
     * locale-specific bundle, and there is no way of telling which
     * bundle the message was retrieved from.
     */
    public void testComplete()
    {
        Checker checker = CheckerFactory.createChecker();
        Collection messages = MessageManager.getInstance().getMessages();

        // there are at least 10 messages in the system
        assertTrue(messages.size() > 10);

        // check the english locale
        MessageTranslator translator = new MessageTranslator();
        translator.setLocale(Locale.ENGLISH);
        translator.checkComplete(messages);
    }
}
