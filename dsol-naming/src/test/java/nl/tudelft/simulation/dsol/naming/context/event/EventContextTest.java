package nl.tudelft.simulation.dsol.naming.context.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducerInterface;
import org.djutils.event.ref.ReferenceType;
import org.junit.Test;

import nl.tudelft.simulation.dsol.naming.context.ContextTestUtil;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.event.ContextEventProducerImpl;
import nl.tudelft.simulation.naming.context.event.ContextScope;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * EventContextTest.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class EventContextTest
{
    /**
     * Test the ContextEventProducerImpl class.
     * @throws Exception on error when executing test
     */
    @Test
    public void testEventContextImplKeys() throws Exception
    {
        InitialEventContext ctx = null;
        try
        {
            Hashtable<String, String> environment = new Hashtable<>();
            environment.put(InitialEventContext.INITIAL_CONTEXT_FACTORY,
                    "nl.tudelft.simulation.naming.context.JVMContextFactory");
            ctx = InitialEventContext.instantiate(environment, "root");

            // get the embedded contextEventProducer
            Field cepiField = InitialEventContext.class.getDeclaredField("contextEventProducerImpl");
            cepiField.setAccessible(true);
            ContextEventProducerImpl contextEventProducerImpl = (ContextEventProducerImpl) cepiField.get(ctx);

            // get the private makeRegistryKey and makeRegExpKey methods
            Method makeRegistryKey =
                    ContextEventProducerImpl.class.getDeclaredMethod("makeRegistryKey", String.class, ContextScope.class);
            makeRegistryKey.setAccessible(true);
            Method makeRegExpKey =
                    ContextEventProducerImpl.class.getDeclaredMethod("makeRegex", String.class, ContextScope.class);
            makeRegExpKey.setAccessible(true);

            // OBJECT_SCOPE
            String registryKeyObject = (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1/object",
                    ContextScope.OBJECT_SCOPE);
            assertEquals("/simulation1/sub1/object#OBJECT_SCOPE", registryKeyObject);
            String regExpObject = (String) makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1/object",
                    ContextScope.OBJECT_SCOPE);
            assertEquals("/simulation1/sub1/object", regExpObject);
            assertTrue("/simulation1/sub1/object".matches(regExpObject));
            assertFalse("/simulation1/sub1".matches(regExpObject));
            assertFalse("/simulation1/sub1/object1".matches(regExpObject));
            assertFalse("/simulation1/sub1/object/".matches(regExpObject));
            assertFalse("root/simulation1/sub1/object".matches(regExpObject));
            assertFalse("sub1/object".matches(regExpObject));

            // LEVEL_SCOPE
            registryKeyObject =
                    (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1", ContextScope.LEVEL_SCOPE);
            assertEquals("/simulation1/sub1#LEVEL_SCOPE", registryKeyObject);
            registryKeyObject =
                    (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1/", ContextScope.LEVEL_SCOPE);
            assertEquals("/simulation1/sub1#LEVEL_SCOPE", registryKeyObject);
            regExpObject =
                    (String) makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1", ContextScope.LEVEL_SCOPE);
            assertEquals(regExpObject,
                    makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1/", ContextScope.LEVEL_SCOPE));

            assertFalse("/simulation1/sub1".matches(regExpObject));
            assertTrue("/simulation1/sub1/object".matches(regExpObject));
            assertTrue("/simulation1/sub1/abc".matches(regExpObject));
            assertFalse("/simulation1/sub1/abc/def".matches(regExpObject));
            assertFalse("/simulation1/sub1/abc/".matches(regExpObject));
            assertFalse("/simulation1".matches(regExpObject));
            assertFalse("/simulation1/sub12".matches(regExpObject));
            assertFalse("/simulation1/sub2".matches(regExpObject));
            assertFalse("root/simulation1/sub1/object".matches(regExpObject));
            assertFalse("sub1/object".matches(regExpObject));

            // LEVEL_OBJECT_SCOPE
            registryKeyObject = (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1",
                    ContextScope.LEVEL_OBJECT_SCOPE);
            assertEquals("/simulation1/sub1#LEVEL_OBJECT_SCOPE", registryKeyObject);
            registryKeyObject = (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1/",
                    ContextScope.LEVEL_OBJECT_SCOPE);
            assertEquals("/simulation1/sub1#LEVEL_OBJECT_SCOPE", registryKeyObject);
            regExpObject = (String) makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1",
                    ContextScope.LEVEL_OBJECT_SCOPE);
            assertEquals(regExpObject,
                    makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1/", ContextScope.LEVEL_OBJECT_SCOPE));

            assertTrue("/simulation1/sub1".matches(regExpObject));
            assertTrue("/simulation1/sub1/".matches(regExpObject));
            assertTrue("/simulation1/sub1/object".matches(regExpObject));
            assertTrue("/simulation1/sub1/abc".matches(regExpObject));
            assertFalse("/simulation1/sub1/abc/def".matches(regExpObject));
            assertFalse("/simulation1/sub1/abc/".matches(regExpObject));
            assertFalse("/simulation1".matches(regExpObject));
            assertFalse("/simulation1/sub12".matches(regExpObject));
            assertFalse("/simulation1/sub2".matches(regExpObject));
            assertFalse("root/simulation1/sub1/object".matches(regExpObject));
            assertFalse("sub1/object".matches(regExpObject));

            // SUBTREE_SCOPE
            registryKeyObject =
                    (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1", ContextScope.SUBTREE_SCOPE);
            assertEquals("/simulation1/sub1#SUBTREE_SCOPE", registryKeyObject);
            registryKeyObject =
                    (String) makeRegistryKey.invoke(contextEventProducerImpl, "/simulation1/sub1/", ContextScope.SUBTREE_SCOPE);
            assertEquals("/simulation1/sub1#SUBTREE_SCOPE", registryKeyObject);
            regExpObject =
                    (String) makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1", ContextScope.SUBTREE_SCOPE);
            assertEquals(regExpObject,
                    makeRegExpKey.invoke(contextEventProducerImpl, "/simulation1/sub1/", ContextScope.SUBTREE_SCOPE));

            assertTrue("/simulation1/sub1".matches(regExpObject));
            assertTrue("/simulation1/sub1/".matches(regExpObject));
            assertTrue("/simulation1/sub1/object".matches(regExpObject));
            assertTrue("/simulation1/sub1/abc".matches(regExpObject));
            assertTrue("/simulation1/sub1/abc/def".matches(regExpObject));
            assertTrue("/simulation1/sub1/abc/".matches(regExpObject));
            assertFalse("/simulation1".matches(regExpObject));
            assertFalse("/simulation1/sub12".matches(regExpObject));
            assertFalse("/simulation1/sub2/sub1".matches(regExpObject));
            assertFalse("root/simulation1/sub1/object".matches(regExpObject));
            assertFalse("sub1/object".matches(regExpObject));
        }
        catch (NamingException | RemoteException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException exception)
        {
            throw exception;
        }
        finally
        {
            ContextTestUtil.destroyInitialEventContext(ctx);
        }
    }

    /**
     * Test the context scope pub/sub mechanism of the EventContext.
     * @throws Exception on error when executing test
     */
    @Test
    public void testEventContextPubSub() throws Exception
    {
        InitialEventContext context = null;
        try
        {
            Hashtable<String, String> environment = new Hashtable<>();
            environment.put(InitialEventContext.INITIAL_CONTEXT_FACTORY,
                    "nl.tudelft.simulation.naming.context.JVMContextFactory");
            context = InitialEventContext.instantiate(environment, "root");

            // make the start of a tree
            ContextInterface c11 = context.createSubcontext("/1/11");
            ContextInterface c12 = context.createSubcontext("/1/12");
            context.createSubcontext("/2/21/211/2111/21111");
            TestObject object111 = new TestObject("object111");
            c11.bind("o111", object111);
            ContextInterface c1 = ContextUtil.lookupSubContext(context, "/1");

            TestEventListener listenerObject = new TestEventListener(ContextScope.OBJECT_SCOPE);
            TestEventListener listenerLevel = new TestEventListener(ContextScope.LEVEL_SCOPE);
            TestEventListener listenerLevelObject = new TestEventListener(ContextScope.LEVEL_OBJECT_SCOPE);
            TestEventListener listenerSubTree = new TestEventListener(ContextScope.SUBTREE_SCOPE);
            context.addListener(listenerObject, "/1/11/o111", ContextScope.OBJECT_SCOPE);
            context.addListener(listenerLevel, "/1/11", ContextScope.LEVEL_SCOPE, ReferenceType.WEAK);
            context.addListener(listenerLevelObject, "/1/11", ContextScope.LEVEL_OBJECT_SCOPE,
                    EventProducerInterface.LAST_POSITION);
            context.addListener(listenerSubTree, "/1/11", ContextScope.SUBTREE_SCOPE, EventProducerInterface.FIRST_POSITION,
                    ReferenceType.STRONG);

            // add an object to c11. Expected behavior: O: no, L: yes, LO: yes, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(true);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            TestObject object112 = new TestObject("object112");
            c11.bind("o112", object112);
            EventInterface eventLevel = listenerLevel.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_ADDED_EVENT, eventLevel.getType());
            assertTrue(eventLevel.getContent() instanceof Object[]);
            Object[] content = (Object[]) eventLevel.getContent();
            assertEquals(3, content.length);
            assertEquals(c11.getAbsolutePath(), content[0]);
            assertEquals("o112", content[1]);
            assertEquals(object112, content[2]);
            assertEquals(c11.getSourceId(), eventLevel.getSourceId());
            EventInterface eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventLevelObject);
            EventInterface eventSubTree = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventSubTree);

            // add an object to c12. Expected behavior: O: no, L: no, LO: no, ST: no
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(false);
            TestObject object121 = new TestObject("object121");
            c12.bind("o121", object121);

            // add a context to c11. Expected behavior: O: no, L: yes, LO: yes, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(true);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            ContextInterface c111 = context.createSubcontext("/1/11/111");
            eventLevel = listenerLevel.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_ADDED_EVENT, eventLevel.getType());
            assertTrue(eventLevel.getContent() instanceof Object[]);
            content = (Object[]) eventLevel.getContent();
            assertEquals(3, content.length);
            assertEquals(c11.getAbsolutePath(), content[0]);
            assertEquals("111", content[1]);
            assertEquals(c111, content[2]);
            assertEquals(c11.getSourceId(), eventLevel.getSourceId());
            eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventLevelObject);
            eventSubTree = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventSubTree);

            // add an object to c111. Expected behavior: O: no, L: no, LO: no, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(true);
            TestObject object111x = new TestObject("object111x");
            c111.bind("o111x", object111x);
            eventSubTree = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_ADDED_EVENT, eventSubTree.getType());
            assertTrue(eventSubTree.getContent() instanceof Object[]);
            content = (Object[]) eventSubTree.getContent();
            assertEquals(3, content.length);
            assertEquals(c111.getAbsolutePath(), content[0]);
            assertEquals("o111x", content[1]);
            assertEquals(object111x, content[2]);
            assertEquals(c111.getSourceId(), eventSubTree.getSourceId());

            // add an subcontext to c111. Expected behavior: O: no, L: no, LO: no, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(true);
            ContextInterface c1111 = context.createSubcontext("/1/11/111/1111");
            eventSubTree = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_ADDED_EVENT, eventSubTree.getType());
            assertTrue(eventSubTree.getContent() instanceof Object[]);
            content = (Object[]) eventSubTree.getContent();
            assertEquals(3, content.length);
            assertEquals(c111.getAbsolutePath(), content[0]);
            assertEquals("1111", content[1]);
            assertEquals(c1111, content[2]);
            assertEquals(c111.getSourceId(), eventSubTree.getSourceId());

            // change an object in c11. Expected behavior: O: no, L: yes, LO: yes, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(true);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            c11.fireObjectChangedEventKey("o112");
            eventLevel = listenerLevel.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_CHANGED_EVENT, eventLevel.getType());
            assertTrue(eventLevel.getContent() instanceof Object[]);
            content = (Object[]) eventLevel.getContent();
            assertEquals(3, content.length);
            assertEquals(c11.getAbsolutePath(), content[0]);
            assertEquals("o112", content[1]);
            assertEquals(object112, content[2]);
            assertEquals(c11.getSourceId(), eventLevel.getSourceId());
            eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventLevelObject);
            eventSubTree = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventSubTree);

            // change an object in c12. Expected behavior: O: no, L: no, LO: no, ST: no
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(false);
            c12.fireObjectChangedEventKey("o121");

            // change an object in c111. Expected behavior: O: no, L: no, LO: no, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(true);
            c111.fireObjectChangedEventKey("o111x");
            eventSubTree = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_CHANGED_EVENT, eventSubTree.getType());
            assertTrue(eventSubTree.getContent() instanceof Object[]);
            content = (Object[]) eventSubTree.getContent();
            assertEquals(3, content.length);
            assertEquals(c111.getAbsolutePath(), content[0]);
            assertEquals("o111x", content[1]);
            assertEquals(object111x, content[2]);
            assertEquals(c111.getSourceId(), eventSubTree.getSourceId());

            // remove an object from c12. Expected behavior: O: no, L: no, LO: no, ST: no
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(false);
            c12.unbindObject("o121");

            // remove an object from c111. Expected behavior: O: no, L: no, LO: no, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(true);
            c111.unbindObject("o111x");
            eventSubTree = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, eventSubTree.getType());
            assertTrue(eventSubTree.getContent() instanceof Object[]);
            content = (Object[]) eventSubTree.getContent();
            assertEquals(3, content.length);
            assertEquals(c111.getAbsolutePath(), content[0]);
            assertEquals("o111x", content[1]);
            assertEquals(object111x, content[2]);
            assertEquals(c111.getSourceId(), eventSubTree.getSourceId());

            // remove object o111. Expected behavior: O: yes, L: yes, LO: yes, ST: yes
            listenerObject.setExpectingNotification(true);
            listenerLevel.setExpectingNotification(true);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            c11.unbindObject("o111");
            eventLevel = listenerLevel.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, eventLevel.getType());
            assertTrue(eventLevel.getContent() instanceof Object[]);
            content = (Object[]) eventLevel.getContent();
            assertEquals(3, content.length);
            assertEquals(c11.getAbsolutePath(), content[0]);
            assertEquals("o111", content[1]);
            assertEquals(object111, content[2]);
            assertEquals(c11.getSourceId(), eventLevel.getSourceId());
            eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventLevelObject);
            eventSubTree = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventSubTree);
            EventInterface eventObject = listenerObject.getReceivedEvent();
            assertEquals(eventLevel, eventObject);
            
            // remove context 111. Expected behavior: O: no, L: yes, LO: yes, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(true);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            ContextUtil.destroySubContext(c11, "111");
            eventLevel = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, eventLevel.getType());
            assertTrue(eventLevel.getContent() instanceof Object[]);
            content = (Object[]) eventLevel.getContent();
            assertEquals(3, content.length);
            assertEquals(c11.getAbsolutePath(), content[0]);
            assertEquals("111", content[1]);
            assertEquals(c111, content[2]);
            assertEquals(c11.getSourceId(), eventLevel.getSourceId());
            eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventLevelObject);
            eventSubTree = listenerLevelObject.getReceivedEvent();
            assertEquals(eventLevel, eventSubTree);

            // empty c11
            c11.unbindObject("o112");
            assertEquals(0, c11.bindings().size());

            // remove context 11. Expected behavior: O: no, L: no, LO: yes, ST: yes
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(true);
            listenerSubTree.setExpectingNotification(true);
            ContextUtil.destroySubContext(c1, "11");
            eventSubTree = listenerSubTree.getReceivedEvent();
            assertEquals(ContextInterface.OBJECT_REMOVED_EVENT, eventSubTree.getType());
            assertTrue(eventSubTree.getContent() instanceof Object[]);
            content = (Object[]) eventSubTree.getContent();
            assertEquals(3, content.length);
            assertEquals(c1.getAbsolutePath(), content[0]);
            assertEquals("11", content[1]);
            assertEquals(c11, content[2]);
            assertEquals(c1.getSourceId(), eventSubTree.getSourceId());
            eventLevelObject = listenerLevelObject.getReceivedEvent();
            assertEquals(eventSubTree, eventLevelObject);

            // remove context /1. There is nothing left to report. Expected behavior: O: no, L: no, LO: no, ST: no
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(false);
            System.out.println("\nSHOULD HAVE 1 and 2");
            System.out.println(context.toString(true));
            context.destroySubcontext("1");

            // remove context /2. This should not report anything. Expected behavior: O: no, L: no, LO: no, ST: no
            listenerObject.setExpectingNotification(false);
            listenerLevel.setExpectingNotification(false);
            listenerLevelObject.setExpectingNotification(false);
            listenerSubTree.setExpectingNotification(false);
            context.destroySubcontext("/2");

            context.removeListener(listenerObject, "/1/11/o111", ContextScope.OBJECT_SCOPE);
            context.removeListener(listenerLevel, "/1/11", ContextScope.LEVEL_SCOPE);
            context.removeListener(listenerLevelObject, "/1/11", ContextScope.LEVEL_OBJECT_SCOPE);
            context.removeListener(listenerSubTree, "/1/11", ContextScope.SUBTREE_SCOPE);

        }
        catch (NamingException | RemoteException | SecurityException | IllegalArgumentException exception)
        {
            throw exception;
        }
        finally
        {
            ContextTestUtil.destroyInitialEventContext(context);
        }
    }

    /** EventListener to test received events. */
    protected static class TestEventListener implements EventListenerInterface
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** expect notification or not. */
        private boolean expectingNotification = true;

        /** received event. */
        private EventInterface receivedEvent;
        
        /** the scope for reporting. */
        private final ContextScope contextScope;

        /**
         * @param contextScope the context scope for reporting
         */
        public TestEventListener(final ContextScope contextScope)
        {
            this.contextScope = contextScope;
        }

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
        public EventInterface getReceivedEvent()
        {
            return this.receivedEvent;
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final EventInterface event) throws RemoteException
        {
            if (!this.expectingNotification)
            {
                fail("Received event " + event + " unexpectedly for scope " + this.contextScope);
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
        public TestObject(String field)
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

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return this.field;
        }
    }

}
