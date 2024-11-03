package nl.tudelft.simulation.dsol.naming.demo;

import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Scanner;

import javax.naming.NamingException;

import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.rmi.RmiObject;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.JvmContext;
import nl.tudelft.simulation.naming.context.event.RemoteEventContext;
import nl.tudelft.simulation.naming.context.event.RemoteEventContextInterface;

/**
 * DemoServer sets up a context with a few items and subcontexts to which a DemoClient can subscribe.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemoServer extends RmiObject implements EventProducer
{
    /** */
    private static final long serialVersionUID = 20200210L;

    /** */
    private final EventListenerMap eventListenerMap;
    
    /**
     * @throws RemoteException on network error
     * @throws AlreadyBoundException on two demo servers being started
     * @throws NamingException on context error
     * @throws IOException on error reading from stdin
     * @throws InterruptedException on sleep
     */
    public DemoServer() throws RemoteException, AlreadyBoundException, NamingException, IOException, InterruptedException
    {
        super("127.0.0.1", 1099, "demoserver");
        this.eventListenerMap = new EventListenerMap();
        URL url = new URL("http://127.0.0.1:1099/remoteContext");
        RemoteEventContextInterface remoteContext =
                new RemoteEventContext(url, new JvmContext("root"), "remoteContext.producer");

        remoteContext.bind("key1", "string1");
        remoteContext.bind("key2", "string2");
        remoteContext.bind("key3", "string3");
        ContextInterface level1 = remoteContext.createSubcontext("level1");
        level1.bind("key11", "string11");
        level1.bind("key12", "string12");
        level1.bind("key13", "string13");

        // create a small interactive application
        char c = 'n';
        Scanner s = new Scanner(System.in);
        while (c != 'x')
        {
            System.out.println(
                    "\nCommands: 'x' = exit; 'l' - list; 'a key value' = add key; 'm name' = make ctx; 'd key' = delete key");
            String str = s.nextLine();
            c = str.charAt(0);

            if (c == 'a')
            {
                String[] cmd = str.split(" ");
                if (cmd.length != 3)
                {
                    System.err.println("not a legal command");
                    continue;
                }
                try
                {
                    remoteContext.rebind(cmd[1], cmd[2]);
                    continue;
                }
                catch (NamingException e)
                {
                    System.err.println(e.getMessage());
                    continue;
                }
            }

            if (c == 'x')
            {
                continue;
            }

            if (c == 'd')
            {
                String[] cmd = str.split(" ");
                if (cmd.length != 2)
                {
                    System.err.println("not a legal command");
                    continue;
                }
                try
                {
                    remoteContext.unbind(cmd[1]);
                    continue;
                }
                catch (NamingException e)
                {
                    System.err.println(e.getMessage());
                    continue;
                }
            }

            if (c == 'm')
            {
                String[] cmd = str.split(" ");
                if (cmd.length != 2)
                {
                    System.err.println("not a legal command");
                    continue;
                }
                try
                {
                    remoteContext.createSubcontext(cmd[1]);
                    continue;
                }
                catch (NamingException e)
                {
                    System.err.println(e.getMessage());
                    continue;
                }
            }

            if (c == 'l')
            {
                print(remoteContext, 0);
            }
        }
        fireEvent(DemoServerInterface.EXIT_EVENT);
        s.close();
        Thread.sleep(1000);
        System.exit(0);
    }

    /**
     * @param args empty
     * @throws NamingException on context error
     * @throws IOException on error reading from stdin
     * @throws AlreadyBoundException on two demo servers being started
     * @throws InterruptedException on sleep
     */
    public static void main(final String[] args)
            throws NamingException, IOException, AlreadyBoundException, InterruptedException
    {
        new DemoServer();
    }

    /**
     * recursively print the context to stdout.
     * @param ctx the context to print
     * @param indent the indentation on the screen
     * @throws RemoteException on network error
     */
    public static void print(final ContextInterface ctx, final int indent) throws RemoteException
    {
        try
        {
            System.out.println(String.join("", Collections.nCopies(indent, " ")) + "CTX " + ctx.getAtomicName());
            for (String key : ctx.keySet())
            {
                Object obj = ctx.getObject(key);
                if (obj instanceof ContextInterface)
                {
                    print((ContextInterface) obj, indent + 2);
                }
                else
                {
                    System.out.println(String.join("", Collections.nCopies(indent + 2, " ")) + key + "=" + obj);
                }
            }
        }
        catch (NamingException | RemoteException exception)
        {
            System.err.println("ERR " + exception.getMessage());
        }
    }

    @Override
    public String toString()
    {
        try
        {
            return "DemoServer [" + getRegistry().toString() + "]";
        }
        catch (RemoteException exception)
        {
            return "DemoServer [ERROR = " + exception.getMessage() + "]";
        }
    }

    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.eventListenerMap;
    }

}
