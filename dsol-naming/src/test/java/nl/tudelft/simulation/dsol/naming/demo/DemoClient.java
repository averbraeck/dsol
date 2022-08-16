package nl.tudelft.simulation.dsol.naming.demo;

import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.remote.RemoteEventListenerInterface;
import org.djutils.event.remote.RemoteEventProducerInterface;
import org.djutils.rmi.RMIObject;
import org.djutils.rmi.RMIUtils;

import nl.tudelft.simulation.naming.context.event.ContextScope;
import nl.tudelft.simulation.naming.context.event.RemoteEventContextInterface;

/**
 * DemoClient sets up a connection to the remote context at DemoServer and periodically prints the results.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemoClient extends RMIObject implements RemoteEventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20200210L;

    /** exit the program? */
    private boolean exit = false;

    /** the remote context. */
    private RemoteEventContextInterface remoteContext;

    /**
     * @throws RemoteException on network error
     * @throws AlreadyBoundException when object was aleady registered in RMI
     * @throws NamingException on context error
     * @throws IOException on error reading from stdin
     * @throws NotBoundException when server is not running
     * @throws InterruptedException for sleep
     */
    public DemoClient()
            throws RemoteException, AlreadyBoundException, NamingException, IOException, NotBoundException, InterruptedException
    {
        super("127.0.0.1", 1099, "democlient");

        RemoteEventProducerInterface demoServer = (RemoteEventProducerInterface) RMIUtils.lookup(getRegistry(), "demoserver");
        demoServer.addListener(this, DemoServerInterface.EXIT_EVENT);

        URL url = new URL("http://127.0.0.1:1099/remoteContext");
        Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
        this.remoteContext = (RemoteEventContextInterface) registry.lookup(url.getFile());

        this.remoteContext.addListener(this, "/", ContextScope.SUBTREE_SCOPE);

        DemoServer.print(this.remoteContext.getRootContext(), 0);

        while (!this.exit)
        {
            Thread.sleep(100);
        }
        System.exit(0);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(DemoServerInterface.EXIT_EVENT))
        {
            this.exit = true;
        }
        else
        {
            System.out.println();
            DemoServer.print(this.remoteContext.getRootContext(), 0);
        }
    }

    /**
     * @param args empty
     * @throws NamingException on context error
     * @throws IOException on error reading from stdin
     * @throws NotBoundException when server is not running
     * @throws InterruptedException for sleep
     * @throws AlreadyBoundException when object was already registered in RMI
     */
    public static void main(final String[] args)
            throws NamingException, IOException, NotBoundException, InterruptedException, AlreadyBoundException
    {
        new DemoClient();
    }
}
