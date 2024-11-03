package nl.tudelft.simulation.dsol.naming.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.reference.ReferenceType;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.FileContext;
import nl.tudelft.simulation.naming.context.JvmContext;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;

/**
 * Tests the context's publish / subscribe mechanism.
 * <p>
 * Copyright (c) 2004-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContextTestPubSub
{
    /**
     * test for different types of context.
     * @throws NamingException on error
     * @throws RemoteException on RMI error
     * @throws IOException on creation of temporary file
     * @throws AlreadyBoundException on RMI error
     */
    @Test
    public void testContextPubSub() throws NamingException, RemoteException, IOException, AlreadyBoundException
    {
        // test JvmContext
        ContextInterface jvmContext = new JvmContext(null, "root");
        assertNull(jvmContext.getParent());
        assertEquals("root", jvmContext.getAtomicName());
        assertEquals(jvmContext, jvmContext.getRootContext());
        assertEquals("", jvmContext.getAbsolutePath());
        testContextEvents(jvmContext);

        // test FileContext directly
        Path path = Files.createTempFile("context-file", ".jpo");
        File file = path.toFile();
        file.deleteOnExit();
        ContextInterface fileContext = new FileContext(file, "root");
        testContextEvents(fileContext);

        // test InitialEventContext
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.JvmContextFactory");
        InitialEventContext eventContext = InitialEventContext.instantiate(properties, "root");
        assertNull(eventContext.getParent());
        assertEquals("root", eventContext.getAtomicName());
        testContextEvents(eventContext);
        eventContext.close();
        ContextTestUtil.destroyInitialEventContext(eventContext);

        // test FileContext via FileContextFactory
        Path fcPath = Files.createTempFile("factory-context-file", ".jpo");
        File fcFile = fcPath.toFile();
        fcFile.delete(); // should not exist yet -- only the name and handle.
        fcFile.deleteOnExit();
        String fcName = fcPath.toUri().toURL().toString();
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.FileContextFactory");
        properties.put("java.naming.provider.url", fcName);
        InitialEventContext factoryFileContext = InitialEventContext.instantiate(properties, "root");
        testContextEvents(factoryFileContext);
        ContextTestUtil.destroyInitialEventContext(factoryFileContext);
    }

    /**
     * test basics for a Context.
     * @param context the context to test
     * @throws RemoteException on RMI error
     * @throws NamingException on error
     */
    public void testContextEvents(final ContextInterface context) throws NamingException, RemoteException
    {
        TestEventListener listener = new TestEventListener();
        context.addListener(listener, ContextInterface.OBJECT_ADDED_EVENT);
        context.addListener(listener, ContextInterface.OBJECT_REMOVED_EVENT, ReferenceType.WEAK);
        context.addListener(listener, ContextInterface.OBJECT_CHANGED_EVENT, LocalEventProducer.LAST_POSITION);

        listener.setExpectingNotification(true);
        TestObject object1 = new TestObject("object1");
        context.bind("o1", object1);
        Event event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_ADDED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        Object[] content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals(context.getAbsolutePath(), content[0]);
        assertEquals("o1", content[1]);
        assertEquals(object1, content[2]);

        listener.setExpectingNotification(true);
        ContextInterface sub1 = context.createSubcontext("sub1");
        event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_ADDED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals("", content[0]);
        assertEquals("sub1", content[1]);
        assertEquals(sub1, content[2]);

        listener.setExpectingNotification(false);
        TestObject object12 = new TestObject("object12");
        sub1.bind("o12", object12);

        listener.setExpectingNotification(true);
        object1.setField("CHANGED");
        context.fireObjectChangedEventKey("o1");
        event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_CHANGED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals(context.getAbsolutePath(), content[0]);
        assertEquals("o1", content[1]);
        assertEquals(object1, content[2]);
        assertEquals("CHANGED", object1.getField());

        listener.setExpectingNotification(true);
        object1.setField("o1"); // fireObjectChangedEventValue uses the toString of object to look up
        context.fireObjectChangedEventValue(object1);
        event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_CHANGED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals(context.getAbsolutePath(), content[0]);
        assertEquals("o1", content[1]);
        assertEquals(object1, content[2]);
        assertEquals("o1", object1.getField());

        try
        {
            context.fireObjectChangedEventKey("xyz");
            fail("fireObjectChangedEventKey of non-existing key should have caused exception");
        }
        catch (NameNotFoundException e)
        {
            // ok
        }

        try
        {
            context.fireObjectChangedEventKey("a/b/c");
            fail("fireObjectChangedEventKey of subtree should have caused exception");
        }
        catch (NamingException e)
        {
            // ok
        }

        try
        {
            context.fireObjectChangedEventKey("");
            fail("fireObjectChangedEventKey of empty key should have caused exception");
        }
        catch (NamingException e)
        {
            // ok
        }

        try
        {
            context.fireObjectChangedEventKey(null);
            fail("fireObjectChangedEventKey of null key should have caused exception");
        }
        catch (NullPointerException e)
        {
            // ok
        }

        try
        {
            context.fireObjectChangedEventValue(null);
            fail("fireObjectChangedEventKey of null value should have caused exception");
        }
        catch (NullPointerException e)
        {
            // ok
        }

        listener.setExpectingNotification(true);
        context.unbindObject("o1");
        event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals(context.getAbsolutePath(), content[0]);
        assertEquals("o1", content[1]);
        assertEquals(object1, content[2]);

        listener.setExpectingNotification(false);
        sub1.unbindObject("o12");

        listener.setExpectingNotification(true);
        context.unbindObject("sub1");
        event = listener.getReceivedEvent();
        assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, event.getType());
        assertTrue(event.getContent() instanceof Object[]);
        content = (Object[]) event.getContent();
        assertEquals(3, content.length);
        assertEquals(context.getAbsolutePath(), content[0]);
        assertEquals("sub1", content[1]);
        assertEquals(sub1, content[2]);

        context.removeListener(listener, ContextInterface.OBJECT_ADDED_EVENT);
        context.removeListener(listener, ContextInterface.OBJECT_REMOVED_EVENT);
        context.removeListener(listener, ContextInterface.OBJECT_CHANGED_EVENT);
    }

    /** EventListener to test received events. */
    protected static class TestEventListener implements EventListener
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** expect notification or not. */
        private boolean expectingNotification = true;

        /** received event. */
        private Event receivedEvent;

        /**
         * @param expectingNotification set expectingNotification
         */
        public void setExpectingNotification(final boolean expectingNotification)
        {
            this.expectingNotification = expectingNotification;
            this.receivedEvent = null;
        }

        /**
         * @return receivedEvent
         */
        public Event getReceivedEvent()
        {
            return this.receivedEvent;
        }

        @Override
        public void notify(final Event event) throws RemoteException
        {
            if (!this.expectingNotification)
            {
                fail("Received event " + event + " unexpectedly");
            }
            this.receivedEvent = event;
        }
    }

    /** Test object with ToString() method. */
    protected static class TestObject implements Serializable
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** */
        private String field;

        /**
         * @param field the field
         */
        public TestObject(final String field)
        {
            this.field = field;
        }

        /**
         * @return field
         */
        public String getField()
        {
            return this.field;
        }

        /**
         * @param field set field
         */
        public void setField(final String field)
        {
            this.field = field;
        }

        @Override
        public String toString()
        {
            return this.field;
        }
    }

}
