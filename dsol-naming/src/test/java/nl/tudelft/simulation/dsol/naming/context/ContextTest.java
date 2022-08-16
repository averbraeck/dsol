package nl.tudelft.simulation.dsol.naming.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;

import org.djutils.event.ref.ReferenceType;
import org.djutils.exceptions.Try;
import org.djutils.rmi.RMIUtils;
import org.junit.Test;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.FileContext;
import nl.tudelft.simulation.naming.context.JVMContext;
import nl.tudelft.simulation.naming.context.RemoteContext;
import nl.tudelft.simulation.naming.context.RemoteContextInterface;
import nl.tudelft.simulation.naming.context.event.ContextScope;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;

/**
 * Tests the context.
 * <p>
 * Copyright (c) 2004-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContextTest
{
    /**
     * test for different types of context.
     * @throws NamingException on error
     * @throws RemoteException on RMI error
     * @throws IOException on creation of temporary file
     * @throws AlreadyBoundException on RMI error
     */
    @Test
    public void testContext() throws NamingException, RemoteException, IOException, AlreadyBoundException
    {
        // test the ContextName object
        testContextName();

        // test JVMContext
        ContextInterface jvmContext = new JVMContext(null, "root");
        assertNull(jvmContext.getParent());
        assertEquals("root", jvmContext.getAtomicName());
        assertEquals(jvmContext, jvmContext.getRootContext());
        testContext(jvmContext, jvmContext, true);

        // test FileContext directly
        Path path = Files.createTempFile("context-file", ".jpo");
        File file = path.toFile();
        file.deleteOnExit();
        ContextInterface fileContext = new FileContext(file, "root");
        testContext(fileContext, fileContext, true);

        // test RemoteContext directly
        ContextInterface embeddedContext = new JVMContext(null, "root");
        RemoteContext remoteContext =
                new RemoteContext("127.0.0.1", 1099, "remoteContextKey", embeddedContext, "remoteEventProducerKey");
        testContext(remoteContext, embeddedContext, true);
        try
        {
            RMIUtils.closeRegistry(remoteContext.getRegistry());
        }
        catch (NoSuchObjectException e)
        {
            System.err.println(e.getMessage());
        }

        // test InitialEventContext
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.JVMContextFactory");
        InitialEventContext eventContext = InitialEventContext.instantiate(properties, "root");
        assertNull(eventContext.getParent());
        assertEquals("root", eventContext.getAtomicName());
        testContext(eventContext, eventContext.getRootContext(), true);
        InitialEventContext eventContext2 = InitialEventContext.instantiate(properties, "root");
        assertEquals(eventContext, eventContext2);
        assertEquals(eventContext.getRootContext(), eventContext2.getRootContext());
        testContext(eventContext2, eventContext2.getRootContext(), true);
        eventContext2.close();
        testNullEventContext(eventContext);
        ContextTestUtil.destroyInitialEventContext(eventContext);
        // use jndi.properties
        InitialEventContext eventContext3 = InitialEventContext.instantiate(new Properties(), "root");
        assertNull(eventContext3.getParent());
        assertEquals("root", eventContext3.getAtomicName());
        testContext(eventContext3, eventContext3.getRootContext(), true);
        ContextTestUtil.destroyInitialEventContext(eventContext3);

        // test FileContext via FileContextFactory
        Path fcPath = Files.createTempFile("factory-context-file", ".jpo");
        File fcFile = fcPath.toFile();
        fcFile.delete(); // should not exist yet -- only the name and handle.
        fcFile.deleteOnExit();
        String fcName = fcPath.toUri().toURL().toString();
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.FileContextFactory");
        properties.put("java.naming.provider.url", fcName);
        InitialEventContext factoryFileContext = InitialEventContext.instantiate(properties, "root");
        testContext(factoryFileContext, factoryFileContext.getRootContext(), true);
        ContextTestUtil.destroyInitialEventContext(factoryFileContext);

        // test RemoteEventContext
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.RemoteContextFactory");
        properties.put("java.naming.provider.url", "http://localhost:1099/remoteContext");
        properties.put("wrapped.naming.factory.initial", "nl.tudelft.simulation.naming.context.JVMContextFactory");
        InitialEventContext remoteEventContext = InitialEventContext.instantiate(properties, "root");
        testContext(remoteEventContext, remoteEventContext.getRootContext(), false);
        ContextTestUtil.destroyInitialEventContext(remoteEventContext);
    }

    /**
     * test basics for a Context.
     * @param context the context to test
     * @param expectedRootContext the expected root context (different from context in case of wrapping)
     * @param testSub boolean; temporary switch to not test Subtrees for Remote
     * @throws RemoteException on RMI error
     * @throws NamingException on error
     */
    public void testContext(final ContextInterface context, final ContextInterface expectedRootContext, boolean testSub)
            throws NamingException, RemoteException
    {
        testContextBindUnbind(context, context, "", 0, expectedRootContext);
        testContextBindUnbind(context, context, "/", 0, expectedRootContext);
        ContextInterface subContext = context.createSubcontext("level1");
        testContextBindUnbind(context, subContext, "level1/", 0, expectedRootContext);
        testContextBindUnbind(context, subContext, "/level1/", 0, expectedRootContext);
        testContextBindUnbind(subContext, subContext, "", 0, subContext);
        testContextBindUnbind(subContext, context, "/", 1, subContext);
        ContextInterface level21 = subContext.createSubcontext("/level1/level21");
        ContextInterface level22 = subContext.createSubcontext("level22");
        testContextBindUnbind(subContext, level21, "level21/", 0, subContext);
        testContextBindUnbind(context, level22, "/level1/level22/", 0, expectedRootContext);
        context.destroySubcontext("/level1/level21");
        subContext.destroySubcontext("level22");
        context.destroySubcontext("level1");
        assertTrue(context.toString(false).contains(context.getAtomicName()));

        // TODO: make sure testSubContext also runs for RemoteContext
        if (testSub)
            testSubContext(context, expectedRootContext);
    }

    /**
     * test bind, rebind and unbind, get, exists, keySet, values, bindings for a Context.
     * @param context the context to test
     * @param subContext the subcontext in which objects will be inserted
     * @param absrel the name for absolute or relative reference
     * @param initialSize the expected initial size of the subContext
     * @param expectedRootContext the expected root context (different from context in case of wrapping)
     * @throws NamingException on error
     * @throws RemoteException on RMI error
     */
    public void testContextBindUnbind(final ContextInterface context, final ContextInterface subContext, final String absrel,
            final int initialSize, final ContextInterface expectedRootContext) throws NamingException, RemoteException
    {
        // test empty root context
        assertEquals(initialSize == 0, subContext.isEmpty());
        assertEquals(initialSize, subContext.keySet().size());
        assertEquals(initialSize, subContext.values().size());
        assertEquals(initialSize, subContext.bindings().size());
        assertFalse(context.exists(absrel + "abc"));
        assertFalse(subContext.hasKey("abc"));
        assertFalse(subContext.hasObject("abc"));

        // bind an object
        context.bind(absrel + "key1", "value1");
        assertFalse(subContext.isEmpty());
        assertEquals(1 + initialSize, subContext.keySet().size());
        assertEquals(1 + initialSize, subContext.values().size());
        assertEquals(1 + initialSize, subContext.bindings().size());
        assertTrue(subContext.hasKey("key1"));
        assertTrue(subContext.hasObject("value1"));
        assertEquals("value1", subContext.getObject("key1"));
        assertTrue(context.exists(absrel + "key1"));
        assertEquals("value1", context.get(absrel + "key1"));
        subContext.bindObject("key3", "value3");
        assertTrue(subContext.hasKey("key3"));
        assertTrue(subContext.hasObject("value3"));

        // rebind an object
        context.rebind(absrel + "key1", "value1new");
        assertFalse(subContext.isEmpty());
        assertEquals(2 + initialSize, subContext.keySet().size());
        assertEquals(2 + initialSize, subContext.values().size());
        assertEquals(2 + initialSize, subContext.bindings().size());
        assertTrue(subContext.hasKey("key1"));
        assertFalse(subContext.hasObject("value1"));
        assertTrue(subContext.hasObject("value1new"));
        assertTrue(context.exists(absrel + "key1"));
        assertEquals("value1new", context.get(absrel + "key1"));
        subContext.rebindObject("key3", "value3new");
        assertTrue(subContext.hasKey("key3"));
        assertFalse(subContext.hasObject("value3"));
        assertTrue(subContext.hasObject("value3new"));

        // rename an object
        context.rename(absrel + "key1", absrel + "key1new");
        assertFalse(subContext.isEmpty());
        assertEquals(2 + initialSize, subContext.keySet().size());
        assertEquals(2 + initialSize, subContext.values().size());
        assertEquals(2 + initialSize, subContext.bindings().size());
        assertFalse(subContext.hasKey("key1"));
        assertTrue(subContext.hasKey("key1new"));
        assertFalse(subContext.hasObject("value1"));
        assertTrue(subContext.hasObject("value1new"));
        assertFalse(context.exists(absrel + "key1"));
        assertTrue(context.exists(absrel + "key1new"));
        assertEquals("value1new", context.get(absrel + "key1new"));

        // unbind an object
        context.unbind(absrel + "key1new");
        assertEquals(1 + initialSize == 0, subContext.isEmpty());
        assertEquals(1 + initialSize, subContext.keySet().size());
        assertEquals(1 + initialSize, subContext.values().size());
        assertEquals(1 + initialSize, subContext.bindings().size());
        assertFalse(subContext.hasKey("key1"));
        assertFalse(subContext.hasKey("key1new"));
        assertFalse(subContext.hasObject("value1"));
        assertFalse(subContext.hasObject("value1new"));
        assertFalse(context.exists(absrel + "key1"));
        assertFalse(context.exists(absrel + "key1new"));
        subContext.unbindObject("key3");
        assertFalse(subContext.hasKey("key3"));
        assertFalse(subContext.hasObject("value3"));
        assertFalse(subContext.hasObject("value3new"));

        // unbind a non-existing object should not give a problem
        context.unbind(absrel + "key2");

        // get an non-existing object
        Try.testFail(() -> { context.get(absrel + "key2"); });

        // test the current context and root context
        if (!(expectedRootContext instanceof RemoteContextInterface))
        {
            assertEquals(expectedRootContext, context.get(""));
            if (context.getParent() == null)
            {
                assertEquals(expectedRootContext, context.get("/"));
            }
        }
        assertTrue(context.exists(""));
        assertTrue(context.exists("/"));

        // rebind a non-existing object
        context.rebind(absrel + "key2", "value2");
        assertFalse(subContext.isEmpty());
        assertEquals(1 + initialSize, subContext.keySet().size());
        assertEquals(1 + initialSize, subContext.values().size());
        assertEquals(1 + initialSize, subContext.bindings().size());
        assertTrue(subContext.hasKey("key2"));
        assertTrue(subContext.hasObject("value2"));
        assertTrue(context.exists(absrel + "key2"));
        assertEquals("value2", context.get(absrel + "key2"));

        // test already bound exception
        Try.testFail(() -> { context.bind(absrel + "key2", "newobject"); }, NameAlreadyBoundException.class);

        // create a name clash for rename
        context.bind(absrel + "key1", "value1");
        Try.testFail(() -> { context.rename(absrel + "key1", absrel + "key2"); }, NameAlreadyBoundException.class);

        // clean up
        context.unbind(absrel + "key1");
        context.unbind(absrel + "key2");

        // test binding of object with toString()
        TestObject val1 = new TestObject("val1");
        subContext.bindObject(val1);
        assertFalse(subContext.isEmpty());
        assertEquals(1 + initialSize, subContext.keySet().size());
        assertEquals(1 + initialSize, subContext.values().size());
        assertEquals(1 + initialSize, subContext.bindings().size());
        assertTrue(subContext.hasKey("val1"));
        assertTrue(subContext.hasObject(val1));
        assertTrue(subContext.exists("val1"));
        assertEquals(val1, subContext.get("val1"));
        subContext.unbind("val1");

        // test object with forward slash in toString()
        TestObject val2 = new TestObject("a/b/c//");
        subContext.bindObject(val2);
        assertFalse(subContext.isEmpty());
        assertEquals(1 + initialSize, subContext.keySet().size());
        assertEquals(1 + initialSize, subContext.values().size());
        assertEquals(1 + initialSize, subContext.bindings().size());
        assertTrue(subContext.hasKey("a#b#c##"));
        assertTrue(subContext.hasObject(val2));
        assertTrue(subContext.exists("a#b#c##"));
        assertEquals(val2, subContext.get("a#b#c##"));
        subContext.unbind("a#b#c##");
        assertEquals(initialSize == 0, subContext.isEmpty());

        // Checking empty name, null name, slash for get
        for (String wrong : new String[] {"//", null})
        {
            Try.testFail(() -> { subContext.get(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for exists
        for (String wrong : new String[] {"//", null})
        {
            Try.testFail(() -> { subContext.exists(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for hasKey
        for (String wrong : new String[] {"", "//", "/", "/xyz/abc", null})
        {
            Try.testFail(() -> { subContext.hasKey(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for getObject
        for (String wrong : new String[] {"", "//", "/", null})
        {
            Try.testFail(() -> { subContext.getObject(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for bindObject
        for (String wrong : new String[] {"", "//", "/", "/xyz/abc", null})
        {
            Try.testFail(() -> { subContext.bindObject(wrong, "newobject"); },
                    "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for rebindObject
        for (String wrong : new String[] {"", "//", "/", "/xyz/abc", null})
        {
            Try.testFail(() -> { subContext.rebindObject(wrong, "newobject"); },
                    "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for unbindObject
        for (String wrong : new String[] {"", "//", "/", "/xyz/abc", null})
        {
            Try.testFail(() -> { subContext.unbindObject(wrong); }, "test should have failed for name [" + wrong + "]");
        }
    }

    /**
     * test create and destroy for a subContext.
     * @param context the context to test
     * @param expectedRootContext the expected root context (different from context in case of wrapping)
     * @throws NamingException on error
     * @throws RemoteException on RMI error
     */
    public void testSubContext(final ContextInterface context, final ContextInterface expectedRootContext)
            throws NamingException, RemoteException
    {
        // test empty root context
        assertTrue(context.isEmpty());

        // test toString()
        assertTrue(context.toString().contains("parent=null"));

        // build a normal tree
        ContextInterface level1 = context.createSubcontext("level1");
        assertFalse(context.isEmpty());
        assertEquals(new LinkedHashSet<String>(Arrays.asList("level1")), context.keySet());
        assertEquals(level1, context.get("level1"));
        assertTrue(level1.isEmpty());

        ContextInterface level2 = level1.createSubcontext("level2");
        assertEquals(level2, context.get("level1/level2"));
        assertTrue(level2.isEmpty());
        assertEquals(expectedRootContext, level2.getRootContext());

        // test toString()
        assertTrue(context.toString().contains("parent=null"));
        assertTrue(level2.toString().contains("parent=level1"));
        assertTrue(level1.toString().contains("atomicName=level1"));

        // name clash with existing subcontext
        Try.testFail(() -> { context.createSubcontext("/level1"); });

        // try to remove non-existing subcontext
        Try.testFail(() -> { context.destroySubcontext("/level3"); });

        // name clash with existing key
        context.bind("key1", "value1");
        Try.testFail(() -> { context.createSubcontext("/key1"); }, NameAlreadyBoundException.class);

        // try to remove subcontext that points to key
        Try.testFail(() -> { context.destroySubcontext("/key1"); }, NotContextException.class);

        // Checking empty name, null name, slash for createSubcontext
        for (String wrong : new String[] {"", "//", "/", null})
        {
            Try.testFail(() -> { context.createSubcontext(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // Checking empty name, null name, slash for destroySubcontext
        for (String wrong : new String[] {"", "//", "/", null})
        {
            Try.testFail(() -> { context.destroySubcontext(wrong); }, "test should have failed for name [" + wrong + "]");
        }

        // use a key as part of the path
        Try.testFail(() -> { context.get("/key1/level2"); }, NameNotFoundException.class);

        // TODO: test circular references when check has been implemented

        // clean up
        context.unbind("key1");
        context.destroySubcontext("/level1/level2");
        context.destroySubcontext("/level1");
        assertTrue(context.isEmpty());
    }

    /**
     * test the ContextName object
     */
    public void testContextName()
    {
        ContextInterface c1 = new JVMContext(null, "c1");
        ContextInterface c2 = new JVMContext(null, "c2");
        String name1 = "name1";
        String name2 = "name2";
        JVMContext.ContextName cn1 = new JVMContext.ContextName(c1, name1);
        JVMContext.ContextName cn2 = new JVMContext.ContextName(c2, name2);

        assertEquals(cn1, cn1);
        assertEquals(cn1, new JVMContext.ContextName(c1, name1));
        assertNotEquals(cn1, cn2);
        assertNotEquals(cn1, null);
        assertNotEquals(cn1, new Object());
        assertNotEquals(cn1, new JVMContext.ContextName(null, name1));
        assertNotEquals(cn1, new JVMContext.ContextName(c1, null));
        assertNotEquals(new JVMContext.ContextName(null, name1), cn1);
        assertNotEquals(new JVMContext.ContextName(c1, null), cn1);
        assertNotEquals(new JVMContext.ContextName(null, name1), new JVMContext.ContextName(null, name2));
        assertEquals(new JVMContext.ContextName(null, name1), new JVMContext.ContextName(null, name1));
        assertNotEquals(new JVMContext.ContextName(c1, null), new JVMContext.ContextName(c2, null));
        assertEquals(new JVMContext.ContextName(c1, null), new JVMContext.ContextName(c1, null));

        assertEquals(cn1.hashCode(), cn1.hashCode());
        assertNotEquals(cn1.hashCode(), cn2.hashCode());
        assertNotEquals(cn1.hashCode(), new JVMContext.ContextName(c1, null).hashCode());
        assertNotEquals(cn1.hashCode(), new JVMContext.ContextName(null, name1).hashCode());

        assertTrue(cn1.toString().contains("name=name1"));
        assertTrue(cn1.toString().contains("atomicName=c1"));
    }

    /** Test object with ToString() method. */
    protected class TestObject implements Serializable
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** */
        private final String field;

        /**
         * @param field the field
         */
        public TestObject(String field)
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

    /**
     * test the non-initialized InitialEventContext.
     * @param ctx the uninitialized (closed) InitialEventContext
     * @throws NamingException on failed test
     * @throws RemoteException on RMI error
     */
    public void testNullEventContext(final InitialEventContext ctx) throws NamingException, RemoteException
    {
        try
        {
            ctx.getAtomicName();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.getParent();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.getRootContext();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.get("abc");
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.getObject("abc");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.exists("abc");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.hasKey("abc");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.hasObject("def");
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.isEmpty();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.bind("abc", "def");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.bindObject("abc", "def");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.bindObject("def");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.unbind("abc");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.unbindObject("abc");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.rebind("abc", "def");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.rebindObject("abc", "def");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.rename("abc", "abc2");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.createSubcontext("sub");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.destroySubcontext("sub");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.checkCircular("sub");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException nce)
        {
            // ok
        }

        try
        {
            ctx.keySet();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.values();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.bindings();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.close();
        }
        catch (RuntimeException rte)
        {
            fail("should not have thrown exception");
        }

        try
        {
            ctx.getAbsolutePath();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.fireObjectChangedEventKey("key");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException rte)
        {
            // ok
        }

        try
        {
            ctx.fireObjectChangedEventValue("value");
            fail("should have thrown exception");
        }
        catch (NoInitialContextException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, ContextInterface.OBJECT_ADDED_EVENT);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, ContextInterface.OBJECT_ADDED_EVENT, ReferenceType.STRONG);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, ContextInterface.OBJECT_ADDED_EVENT, 0);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, ContextInterface.OBJECT_ADDED_EVENT, 0, ReferenceType.STRONG);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.removeListener(null, ContextInterface.OBJECT_ADDED_EVENT);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.hasListeners();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.numberOfListeners(ContextInterface.OBJECT_ADDED_EVENT);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.getEventTypesWithListeners();
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, "", ContextScope.SUBTREE_SCOPE);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, "", ContextScope.SUBTREE_SCOPE, ReferenceType.STRONG);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, "", ContextScope.SUBTREE_SCOPE, 0);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.addListener(null, "", ContextScope.SUBTREE_SCOPE, 0, ReferenceType.STRONG);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        try
        {
            ctx.removeListener(null, "", ContextScope.SUBTREE_SCOPE);
            fail("should have thrown exception");
        }
        catch (RuntimeException rte)
        {
            // ok
        }

        assertTrue(ctx.toString().startsWith("InitialEventContext"));
        assertTrue(ctx.toString().contains("null"));
    }
}
