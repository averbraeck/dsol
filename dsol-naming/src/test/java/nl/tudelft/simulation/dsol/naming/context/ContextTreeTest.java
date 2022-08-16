package nl.tudelft.simulation.dsol.naming.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.djutils.rmi.RMIUtils;
import org.junit.Test;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.FileContext;
import nl.tudelft.simulation.naming.context.JVMContext;
import nl.tudelft.simulation.naming.context.RemoteContext;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * Tests the context where larger parts of the tree are evaluated.
 * <p>
 * Copyright (c) 2004-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContextTreeTest
{
    /**
     * test for different types of context.
     * @throws NamingException on error
     * @throws RemoteException on RMI error
     * @throws IOException on creation of temporary file
     * @throws AlreadyBoundException on RMI error
     */
    @Test
    public void testContextTree() throws NamingException, RemoteException, IOException, AlreadyBoundException
    {
        // test JVMContext
        ContextInterface jvmContext = new JVMContext(null, "root");
        testContextTree(jvmContext);
        jvmContext = new JVMContext(null, "root");
        testContextUtil(jvmContext);

        // test FileContext directly
        Path path = Files.createTempFile("context-file", ".jpo");
        File file = path.toFile();
        file.deleteOnExit();
        ContextInterface fileContext = new FileContext(file, "root");
        testContextTree(fileContext);
        path = Files.createTempFile("context-file", ".jpo");
        file = path.toFile();
        file.deleteOnExit();
        fileContext = new FileContext(file, "root");
        testContextUtil(fileContext);

        // test RemoteContext directly
        RemoteContext remoteContext = new RemoteContext("127.0.0.1", 1099, "remoteContextKey", new JVMContext(null, "root"),
                "remoteEventProducerKey");
        testContextTree(remoteContext);
        try
        {
            RMIUtils.closeRegistry(remoteContext.getRegistry());
        }
        catch (NoSuchObjectException e)
        {
            // TODO: research why RMIUtils.closeRegistry(remoteContext.getRegistry()) gives an error
            System.err.println(e.getMessage()); 
        }
        remoteContext = new RemoteContext("127.0.0.1", 1099, "remoteContextKey", new JVMContext(null, "root"),
                "remoteEventProducerKey");
        testContextUtil(remoteContext);
        try
        {
            RMIUtils.closeRegistry(remoteContext.getRegistry());
        }
        catch (NoSuchObjectException e)
        {
            // TODO: research why RMIUtils.closeRegistry(remoteContext.getRegistry()) gives an error
            System.err.println(e.getMessage());
        }

        // test InitialEventContext
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.JVMContextFactory");
        InitialEventContext eventContext = InitialEventContext.instantiate(properties, "root");
        testContextTree(eventContext);
        eventContext.close();
        ContextTestUtil.destroyInitialEventContext(eventContext);
        eventContext = InitialEventContext.instantiate(properties, "root");
        testContextUtil(eventContext);
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
        testContextTree(factoryFileContext);
        ContextTestUtil.destroyInitialEventContext(eventContext);

        // test RemoteContext
        properties.put("java.naming.factory.initial", "nl.tudelft.simulation.naming.context.RemoteContextFactory");
        properties.put("java.naming.provider.url", "http://localhost:1099/remoteContext");
        properties.put("wrapped.naming.factory.initial", "nl.tudelft.simulation.naming.context.JVMContextFactory");
        InitialEventContext remoteEventContext = InitialEventContext.instantiate(properties, "root");
        testContextTree(remoteEventContext);
        ContextTestUtil.destroyInitialEventContext(eventContext);
    }

    /**
     * test basics for a Context Tree.
     * @param root the context to test
     * @throws RemoteException on RMI error
     * @throws NamingException on error
     */
    public void testContextTree(final ContextInterface root) throws NamingException, RemoteException
    {
        assertEquals("", root.getSourceId());
        assertEquals("", root.getAbsolutePath());

        // fill
        root.createSubcontext("/1/11/111/1111/11111");
        root.createSubcontext("/1/11/111/1112/11121");
        root.createSubcontext("/2/21/211/2111/21111");
        ContextInterface c11 = ContextUtil.lookupSubContext(root, "/1/11");
        ContextInterface c11b = (ContextInterface) ((ContextInterface) root.get("1")).get("11");
        assertNotNull(c11);
        assertEquals(c11, c11b);
        ContextInterface c1112 = ContextUtil.lookupSubContext(c11, "111/1112");
        ContextInterface c1112b = ContextUtil.lookupSubContext(root, "/1/11/111/1112");
        assertNotNull(c1112);
        assertEquals("/1/11/111/1112", c1112.getSourceId());
        assertEquals("/1/11/111/1112", c1112.getAbsolutePath());
        assertEquals(c1112, c1112b);
        c11.bindObject("o11", new Object());
        c11.bindObject("o12", new Object());
        c11.bindObject("o13", new Object());
        assertEquals(4, c11.keySet().size());
        ContextUtil.lookupSubContext(c11, "111").bindObject("o111", new Object());
        c1112.bind("a1112", "a-object");
        c1112.bind("b1112", "b-object");
        c1112.bind("c1112", "c-object");
        c1112.bind("d1112", "d-object");
        System.out.println(root.toString(true));
        assertEquals(5, c1112.bindings().size());
        ContextInterface c21111 = ContextUtil.lookupOrCreateSubContext(root, "/2/21/211/2111/21111");
        c21111.bindObject("o21111", "deep");
        assertEquals(1, c21111.values().size());

        // test circular error
        try
        {
            c1112.bindObject(c11);
            fail("circular reference should have given error");
        }
        catch (NamingException ne)
        {
            // ignore: ok
        }

        // empty
        c1112.destroySubcontext("11121");
        assertEquals(4, c1112.bindings().size());
        c11.destroySubcontext("111");
        assertEquals(2, root.keySet().size());
        assertEquals(3, c11.keySet().size());
        root.destroySubcontext("1");
        assertEquals(1, root.keySet().size());
        root.destroySubcontext("2");
        assertEquals(0, root.keySet().size());
    }

    /**
     * test ContextUtil methods for a Context Tree.
     * @param root the context to test
     * @throws RemoteException on RMI error
     * @throws NamingException on error
     */
    public void testContextUtil(final ContextInterface root) throws NamingException, RemoteException
    {
        // fill
        ContextInterface c1 = ContextUtil.lookupOrCreateSubContext(root, "1");
        ContextInterface c11 = ContextUtil.lookupOrCreateSubContext(c1, "11");
        ContextInterface c111 = ContextUtil.lookupOrCreateSubContext(c11, "111");
        ContextUtil.lookupOrCreateSubContext(c111, "1111");
        root.createSubcontext("/1/11/111/1111/11111");
        root.createSubcontext("/1/11/111/1112/11121");
        root.createSubcontext("/2/21/211/2111/21111");

        ContextInterface c11b = (ContextInterface) ((ContextInterface) root.get("1")).get("11");
        assertNotNull(c11);
        assertEquals(c11, c11b);
        ContextInterface c1112 = ContextUtil.lookupSubContext(c11, "111/1112");
        ContextInterface c1112b = ContextUtil.lookupSubContext(root, "/1/11/111/1112");
        assertNotNull(c1112);
        assertEquals(c1112, c1112b);
        c11.bindObject("o11", new Object());
        c11.bindObject("o12", new Object());
        c11.bindObject("o13", new Object());
        assertEquals(4, c11.keySet().size());
        ContextUtil.lookupSubContext(c11, "111").bindObject("o111", new Object());
        c1112.bind("a1112", "a-object");
        c1112.bind("b1112", "b-object");
        c1112.bind("c1112", "c-object");
        c1112.bind("d1112", "d-object");
        assertEquals(5, c1112.bindings().size());
        ContextInterface c21111 = ContextUtil.lookupOrCreateSubContext(root, "/2/21/211/2111/21111");
        c21111.bindObject("o21111", "deep");
        assertEquals(1, c21111.values().size());

        // error in lookup
        try
        {
            ContextUtil.lookupOrCreateSubContext(c11, "o11");
            fail("lookup of object instead of context should have thrown error");
        }
        catch (Exception e)
        {
            // ignore
        }
        try
        {
            ContextUtil.lookupSubContext(c11, "o11");
            fail("lookup of object instead of context should have thrown error");
        }
        catch (Exception e)
        {
            // ignore
        }
        try
        {
            ContextUtil.lookupSubContext(c11, "xyz");
            fail("lookup of object instead of context should have thrown error");
        }
        catch (Exception e)
        {
            // ignore
        }
        try
        {
            ContextUtil.destroySubContext(c11, "xyz");
            fail("lookup of object instead of context should have thrown error");
        }
        catch (Exception e)
        {
            // ignore
        }

        List<String> keys = ContextUtil.resolveKeys(root, c1112);
        assertEquals(1, keys.size());
        assertEquals("/1/11/111/1112", keys.get(0));
        keys = ContextUtil.resolveKeys(root, "xyz");
        assertEquals(0, keys.size());
        c21111.bindObject("extra", "a-object");
        keys = ContextUtil.resolveKeys(root, "a-object");
        assertEquals(2, keys.size());
        c21111.bindObject("null", null);
        keys = ContextUtil.resolveKeys(c11, null);
        assertEquals(0, keys.size());
        keys = ContextUtil.resolveKeys(root, null);
        assertEquals(1, keys.size());

        // empty
        ContextUtil.destroySubContext(c1112, "11121");
        assertEquals(4, c1112.bindings().size());
        ContextUtil.destroySubContext(c11, "111");
        assertEquals(2, root.keySet().size());
        assertEquals(3, c11.keySet().size());
        root.destroySubcontext("1");
        assertEquals(1, root.keySet().size());
        root.destroySubcontext("2");
        assertEquals(0, root.keySet().size());
    }
}
