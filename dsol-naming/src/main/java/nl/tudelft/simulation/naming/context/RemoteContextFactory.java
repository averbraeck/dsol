package nl.tudelft.simulation.naming.context;

import java.net.InetAddress;
import java.net.URL;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Context;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.naming.context.event.InitialEventContext;

/**
 * A factory for RemoteContextClient instances, automatically invoked by JNDI when the correct jndi.properties file has been
 * used.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RemoteContextFactory implements ContextFactory
{
    /** context refers to the static RemoteContextClient. */
    private static RemoteContext context = null;

    /** {@inheritDoc} */
    @Override
    public synchronized ContextInterface getInitialContext(final Hashtable<?, ?> environment, final String atomicName)
    {
        // If the context is already looked up, let's return immediately
        if (RemoteContextFactory.context != null)
        {
            return RemoteContextFactory.context;
        }

        // Let's look for our remote partner
        try
        {
            URL url = new URL(environment.get(Context.PROVIDER_URL).toString());
            Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());

            // If there is no registry, registry!=null, so we have to test the registry
            // to make sure whether there is one or not. We test by requesting a list. This code might be improved.
            try
            {
                registry.list();
            }
            catch (ConnectException connectException)
            {
                // Since we cannot find the registry, we must perhaps create one.
                // This is only allowed if the host is our localhost. We cannot create a registry on a remote host.
                if (!(url.getHost().equals("localhost") || url.getHost().equals("127.0.0.1")
                        || url.getHost().equals(InetAddress.getLocalHost().getHostName())
                        || url.getHost().equals(InetAddress.getLocalHost().getHostAddress())))
                {
                    throw new IllegalArgumentException("cannot create registry on remote host");
                }
                registry = LocateRegistry.createRegistry(url.getPort());
            }
            // We now have a registry. Let's resolve the context object

            RemoteContext remoteContext = null;
            try
            {
                remoteContext = (RemoteContext) registry.lookup(url.getFile());
            }
            catch (NotBoundException notBoundException)
            {
                // Since we cannot find the context, we must create one.
                // This is done based on the java.naming.wrapped properties in jndi.properties
                Hashtable<Object, Object> wrappedEnvironment = new Hashtable<Object, Object>();
                for (Iterator<?> iterator = environment.keySet().iterator(); iterator.hasNext();)
                {
                    String key = iterator.next().toString();
                    if (key.equals(InitialEventContext.WRAPPED_CONTEXT_FACTORY))
                    {
                        wrappedEnvironment.put(InitialEventContext.INITIAL_CONTEXT_FACTORY, environment.get(key));
                    }
                }
                if (wrappedEnvironment.isEmpty())
                {
                    // If we do not throw this exception and accept an empty
                    // environment, we'll get in an infinite loop
                    throw new IllegalArgumentException("no wrapped initial context factory defined");
                }
                ContextInterface wrappedContext = InitialEventContext.instantiate(wrappedEnvironment, atomicName);
                remoteContext = new RemoteContext(url, wrappedContext, url.getFile() + "_producer");
                // registry.bind(url.getFile(), remoteContext);
            }
            RemoteContextFactory.context = remoteContext;
            return RemoteContextFactory.context;
        }
        catch (Exception exception)
        {
            CategoryLogger.always().error(exception, "getInitialContext");
            return null;
        }
    }
}
