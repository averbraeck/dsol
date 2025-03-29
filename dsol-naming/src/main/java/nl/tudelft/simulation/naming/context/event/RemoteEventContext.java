package nl.tudelft.simulation.naming.context.event;

import java.net.URL;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventListener;
import org.djutils.event.reference.ReferenceType;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.RemoteContext;

/**
 * RemoteEventContext.java.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RemoteEventContext extends RemoteContext implements RemoteEventContextInterface
{
    /** */
    private static final long serialVersionUID = 20200208L;

    /** The event producer to which context events will be delegated for handling. */
    private final ContextEventProducerImpl contextEventProducerImpl;

    /**
     * Constructs a new RemoteContext. Register the new context in the RMI registry. When the RMI registry does not exist yet,
     * it will be created, but <b>only</b> on the local host. Remote creation of a registry on another computer is not possible.
     * Any attempt to do so will cause an AccessException to be fired.
     * @param host the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port the port where the RMI registry can be found or will be created
     * @param bindingKey the key under which this context will be bound in the RMI registry
     * @param embeddedContext the underlying context
     * @param eventProducerBindingKey the key under which the event producer will be bound
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when host, path, or bindingKey is null
     * @throws IllegalArgumentException when port &lt; 0 or port &gt; 65535
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RemoteEventContext(final String host, final int port, final String bindingKey,
            final ContextInterface embeddedContext, final String eventProducerBindingKey)
            throws RemoteException, AlreadyBoundException
    {
        super(host, port, bindingKey, embeddedContext, eventProducerBindingKey);
        this.contextEventProducerImpl = new ContextEventProducerImpl(this);
    }

    /**
     * Constructs a new RemoteContext. Register the new context in the RMI registry. When the host has not been specified in the
     * URL, 127.0.0.1 will be used. When the port has not been specified in the URL, the default RMI port 1099 will be used.
     * When the RMI registry does not exist yet, it will be created, but <b>only</b> on the local host. Remote creation of a
     * registry on another computer is not possible. Any attempt to do so will cause an AccessException to be fired.
     * @param registryURL the URL of the registry, e.g., "http://localhost:1099" or "http://130.161.185.14:28452"
     * @param embeddedContext the underlying context
     * @param eventProducerBindingKey the key under which the event producer will be bound
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when registryURL, bindingKey, or embeddedContext is null
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RemoteEventContext(final URL registryURL, final ContextInterface embeddedContext,
            final String eventProducerBindingKey) throws RemoteException, AlreadyBoundException
    {
        super(registryURL, embeddedContext, eventProducerBindingKey);
        this.contextEventProducerImpl = new ContextEventProducerImpl(this);
    }

    @Override
    public boolean addListener(final EventListener listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException
    {
        return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope);
    }

    @Override
    public boolean addListener(final EventListener listener, final String absolutePath,
            final ContextScope contextScope, final ReferenceType referenceType) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, referenceType);
    }

    @Override
    public boolean addListener(final EventListener listener, final String absolutePath,
            final ContextScope contextScope, final int position) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, position);
    }

    @Override
    public boolean addListener(final EventListener listener, final String absolutePath,
            final ContextScope contextScope, final int position, final ReferenceType referenceType) throws RemoteException,
            NameNotFoundException, InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, position, referenceType);
    }

    @Override
    public boolean removeListener(final EventListener listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, InvalidNameException, NullPointerException
    {
        return this.contextEventProducerImpl.removeListener(listener, absolutePath, contextScope);
    }

}
