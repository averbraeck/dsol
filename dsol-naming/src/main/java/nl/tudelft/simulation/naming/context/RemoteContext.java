package nl.tudelft.simulation.naming.context;

import java.io.Serializable;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.djutils.event.EventListener;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.reference.Reference;
import org.djutils.event.reference.ReferenceType;
import org.djutils.event.rmi.RmiEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.rmi.RmiObject;

import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * Context that exists on another computer.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class RemoteContext extends RmiObject implements RemoteContextInterface, EventProducer
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** The underlying event context. */
    protected ContextInterface embeddedContext = null;

    /** The (remote) event producer for this context. */
    protected RemoteChangeEventProducer remoteEventProducer;

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
    public RemoteContext(final String host, final int port, final String bindingKey, final ContextInterface embeddedContext,
            final String eventProducerBindingKey) throws RemoteException, AlreadyBoundException
    {
        super(host, port, bindingKey);
        Throw.whenNull(embeddedContext, "embedded context cannot be null");
        this.embeddedContext = embeddedContext;
        this.remoteEventProducer = new RemoteChangeEventProducer(host, port, eventProducerBindingKey);
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
    public RemoteContext(final URL registryURL, final ContextInterface embeddedContext, final String eventProducerBindingKey)
            throws RemoteException, AlreadyBoundException
    {
        super(registryURL, registryURL.getPath());
        Throw.whenNull(embeddedContext, "embedded context cannot be null");
        this.embeddedContext = embeddedContext;
        String host = registryURL.getHost() == null ? "127.0.0.1" : registryURL.getHost();
        int port = registryURL.getPort() == -1 ? 1099 : registryURL.getPort();
        this.remoteEventProducer = new RemoteChangeEventProducer(host, port, eventProducerBindingKey);
    }

    @Override
    public String getAtomicName() throws RemoteException
    {
        return this.embeddedContext.getAtomicName();
    }

    @Override
    public ContextInterface getParent() throws RemoteException
    {
        return this.embeddedContext.getParent();
    }

    @Override
    public ContextInterface getRootContext() throws RemoteException
    {
        return this.embeddedContext.getRootContext();
    }

    @Override
    public String getAbsolutePath() throws RemoteException
    {
        return this.embeddedContext.getAbsolutePath();
    }

    @Override
    public Object get(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.get(name);
    }

    @Override
    public Object getObject(final String key) throws NamingException, RemoteException
    {
        return this.embeddedContext.getObject(key);
    }

    @Override
    public boolean exists(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.exists(name);
    }

    @Override
    public boolean hasKey(final String key) throws NamingException, RemoteException
    {
        return this.embeddedContext.hasKey(key);
    }

    @Override
    public boolean hasObject(final Object object) throws RemoteException
    {
        return this.embeddedContext.hasObject(object);
    }

    @Override
    public boolean isEmpty() throws RemoteException
    {
        return this.embeddedContext.isEmpty();
    }

    @Override
    public void bind(final String name, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bind(name, object);
    }

    @Override
    public void bindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bindObject(key, object);
    }

    @Override
    public void bindObject(final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bindObject(object);
    }

    @Override
    public void unbind(final String name) throws NamingException, RemoteException
    {
        this.embeddedContext.unbind(name);
    }

    @Override
    public void unbindObject(final String key) throws NamingException, RemoteException
    {
        this.embeddedContext.unbindObject(key);
    }

    @Override
    public void rebind(final String name, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.rebind(name, object);
    }

    @Override
    public void rebindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.rebindObject(key, object);
    }

    @Override
    public void rename(final String oldName, final String newName) throws NamingException, RemoteException
    {
        this.embeddedContext.rename(oldName, newName);
    }

    @Override
    public ContextInterface createSubcontext(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.createSubcontext(name);
    }

    @Override
    public void destroySubcontext(final String name) throws NamingException, RemoteException
    {
        this.embeddedContext.destroySubcontext(name);
    }

    @Override
    public Set<String> keySet() throws RemoteException
    {
        return new LinkedHashSet<String>(this.embeddedContext.keySet());
    }

    @Override
    public Collection<Object> values() throws RemoteException
    {
        return new LinkedHashSet<Object>(this.embeddedContext.values());
    }

    @Override
    public Map<String, Object> bindings() throws RemoteException
    {
        return this.embeddedContext.bindings();
    }

    @Override
    public void checkCircular(final Object newObject) throws NamingException, RemoteException
    {
        this.embeddedContext.checkCircular(newObject);
    }

    @Override
    public void close() throws NamingException, RemoteException
    {
        // TODO: see if connection needs to be closed
        this.embeddedContext.close();
    }

    @Override
    public void fireObjectChangedEventValue(final Object object)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException
    {
        Throw.whenNull(object, "object cannot be null");
        fireObjectChangedEventKey(makeObjectKey(object));
    }

    @Override
    public void fireObjectChangedEventKey(final String key)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        if (!hasKey(key))
        {
            throw new NameNotFoundException("Could not find object with key " + key + " for fireObjectChangedEvent");
        }
        try
        {
            this.remoteEventProducer.fireChangedEvent(ContextInterface.OBJECT_CHANGED_EVENT,
                    new Object[] {this, key, getObject(key)});
        }
        catch (Exception exception)
        {
            throw new NamingException(exception.getMessage());
        }
    }

    /**
     * Make a key for the object based on object.toString() where the "/" characters are replaced by "#".
     * @param object the object for which the key has to be generated
     * @return the key based on toString() where the "/" characters are replaced by "#"
     */
    private String makeObjectKey(final Object object)
    {
        return object.toString().replace(ContextInterface.SEPARATOR, ContextInterface.REPLACE_SEPARATOR);
    }

    @Override
    public String toString()
    {
        if (this.embeddedContext != null)
            return this.embeddedContext.toString();
        return "RemoteContext[null]";
    }

    @Override
    public String toString(final boolean verbose) throws RemoteException
    {
        if (!verbose)
        {
            return "RemoteContext[" + getAtomicName() + "]";
        }
        return ContextUtil.toText(this);
    }

    /* ***************************************************************************************************************** */
    /* **************************************** EVENTPRODUCER IMPLEMENTATION ******************************************* */
    /* ***************************************************************************************************************** */

    @Override
    public synchronized boolean addListener(final EventListener listener, final EventType eventType) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType);
    }

    @Override
    public synchronized boolean addListener(final EventListener listener, final EventType eventType,
            final ReferenceType referenceType) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, referenceType);
    }

    @Override
    public synchronized boolean addListener(final EventListener listener, final EventType eventType, final int position) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, position);
    }

    @Override
    public synchronized boolean addListener(final EventListener listener, final EventType eventType, final int position,
            final ReferenceType referenceType) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, position, referenceType);
    }

    @Override
    public synchronized int removeAllListeners() throws RemoteException
    {
        return this.remoteEventProducer.removeAllListeners();
    }

    @Override
    public synchronized int removeAllListeners(final Class<?> ofClass) throws RemoteException
    {
        return this.remoteEventProducer.removeAllListeners(ofClass);
    }

    @Override
    public synchronized boolean removeListener(final EventListener listener, final EventType eventType) throws RemoteException
    {
        return this.embeddedContext.removeListener(listener, eventType);
    }

    @Override
    public List<Reference<EventListener>> getListenerReferences(final EventType eventType)  throws RemoteException
    {
        return this.remoteEventProducer.getListenerReferences(eventType);
    }

    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.remoteEventProducer.getEventListenerMap();
    }
    
    /* ***************************************************************************************************************** */
    /* ****************************************** REMOTECHANGEEVENTPRODUCER ******************************************** */
    /* ***************************************************************************************************************** */

    /**
     * The RemoteChangeEventProducer is a RmiEventProducer that can fire an OBJECT_CHANGED_EVENT on behalf of an object that was
     * changed, but does not extend an EventProducer itself.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    protected class RemoteChangeEventProducer extends RmiEventProducer
    {
        /** */
        private static final long serialVersionUID = 20200208L;

        /** the stored binding key that acts as the source id. */
        private final String bindingKey;

        /**
         * Create a remote event listener and register the listener in the RMI registry. When the RMI registry does not exist
         * yet, it will be created, but <b>only</b> on the local host. Remote creation of a registry on another computer is not
         * possible. Any attempt to do so will cause an AccessException to be fired.
         * @param host the host where the RMI registry resides or will be created. Creation is only possible on
         *            localhost.
         * @param port the port where the RMI registry can be found or will be created
         * @param bindingKey the key under which this object will be bound in the RMI registry
         * @throws RemoteException when there is a problem with the RMI registry
         * @throws AlreadyBoundException when there is already another object bound to the bindingKey
         * @throws NullPointerException when host, path, or bindingKey is null
         * @throws IllegalArgumentException when port &lt; 0 or port &gt; 65535
         * @throws AccessException when there is an attempt to create a registry on a remote host
         */
        public RemoteChangeEventProducer(final String host, final int port, final String bindingKey)
                throws RemoteException, AlreadyBoundException
        {
            super(host, port, bindingKey);
            this.bindingKey = bindingKey;
        }

        /**
         * Transmit an changed event with a serializable object as payload to all interested listeners.
         * @param eventType the eventType of the event
         * @param value the object sent with the event
         * @throws RemoteException on network error
         */
        protected void fireChangedEvent(final EventType eventType, final Serializable value) throws RemoteException
        {
            super.fireEvent(eventType, value);
        }
    }

}
