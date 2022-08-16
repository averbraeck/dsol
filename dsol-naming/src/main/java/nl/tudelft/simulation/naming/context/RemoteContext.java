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

import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventType;
import org.djutils.event.EventTypeInterface;
import org.djutils.event.ref.Reference;
import org.djutils.event.ref.ReferenceType;
import org.djutils.event.remote.RemoteEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.rmi.RMIObject;

import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * Context that exists on another computer.
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
public class RemoteContext extends RMIObject implements RemoteContextInterface
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
     * @param host String; the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port int; the port where the RMI registry can be found or will be created
     * @param bindingKey the key under which this context will be bound in the RMI registry
     * @param embeddedContext ContextInterface; the underlying context
     * @param eventProducerBindingKey String; the key under which the event producer will be bound
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
     * @param registryURL URL; the URL of the registry, e.g., "http://localhost:1099" or "http://130.161.185.14:28452"
     * @param embeddedContext ContextInterface; the underlying context
     * @param eventProducerBindingKey String; the key under which the event producer will be bound
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

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId() throws RemoteException
    {
        return getAbsolutePath();
    }

    /** {@inheritDoc} */
    @Override
    public String getAtomicName() throws RemoteException
    {
        return this.embeddedContext.getAtomicName();
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getParent() throws RemoteException
    {
        return this.embeddedContext.getParent();
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getRootContext() throws RemoteException
    {
        return this.embeddedContext.getRootContext();
    }

    /** {@inheritDoc} */
    @Override
    public String getAbsolutePath() throws RemoteException
    {
        return this.embeddedContext.getAbsolutePath();
    }

    /** {@inheritDoc} */
    @Override
    public Object get(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public Object getObject(final String key) throws NamingException, RemoteException
    {
        return this.embeddedContext.getObject(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.exists(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasKey(final String key) throws NamingException, RemoteException
    {
        return this.embeddedContext.hasKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasObject(final Object object) throws RemoteException
    {
        return this.embeddedContext.hasObject(object);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() throws RemoteException
    {
        return this.embeddedContext.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void bind(final String name, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bind(name, object);
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bindObject(key, object);
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.bindObject(object);
    }

    /** {@inheritDoc} */
    @Override
    public void unbind(final String name) throws NamingException, RemoteException
    {
        this.embeddedContext.unbind(name);
    }

    /** {@inheritDoc} */
    @Override
    public void unbindObject(final String key) throws NamingException, RemoteException
    {
        this.embeddedContext.unbindObject(key);
    }

    /** {@inheritDoc} */
    @Override
    public void rebind(final String name, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.rebind(name, object);
    }

    /** {@inheritDoc} */
    @Override
    public void rebindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        this.embeddedContext.rebindObject(key, object);
    }

    /** {@inheritDoc} */
    @Override
    public void rename(final String oldName, final String newName) throws NamingException, RemoteException
    {
        this.embeddedContext.rename(oldName, newName);
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface createSubcontext(final String name) throws NamingException, RemoteException
    {
        return this.embeddedContext.createSubcontext(name);
    }

    /** {@inheritDoc} */
    @Override
    public void destroySubcontext(final String name) throws NamingException, RemoteException
    {
        this.embeddedContext.destroySubcontext(name);
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() throws RemoteException
    {
        return new LinkedHashSet<String>(this.embeddedContext.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Object> values() throws RemoteException
    {
        return new LinkedHashSet<Object>(this.embeddedContext.values());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> bindings() throws RemoteException
    {
        return this.embeddedContext.bindings();
    }

    /** {@inheritDoc} */
    @Override
    public void checkCircular(final Object newObject) throws NamingException, RemoteException
    {
        this.embeddedContext.checkCircular(newObject);
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws NamingException, RemoteException
    {
        // TODO: see if connection needs to be closed
        this.embeddedContext.close();
    }

    /** {@inheritDoc} */
    @Override
    public void fireObjectChangedEventValue(final Object object)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException
    {
        Throw.whenNull(object, "object cannot be null");
        fireObjectChangedEventKey(makeObjectKey(object));
    }

    /** {@inheritDoc} */
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
     * @param object Object; the object for which the key has to be generated
     * @return the key based on toString() where the "/" characters are replaced by "#"
     */
    private String makeObjectKey(final Object object)
    {
        return object.toString().replace(ContextInterface.SEPARATOR, ContextInterface.REPLACE_SEPARATOR);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        if (this.embeddedContext != null)
            return this.embeddedContext.toString();
        return "RemoteContext[null]";
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType)
            throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final ReferenceType referenceType) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, referenceType);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final int position) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, position);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final int position, final ReferenceType referenceType) throws RemoteException
    {
        return this.embeddedContext.addListener(listener, eventType, position, referenceType);
    }

    /**
     * Remove all the listeners from this event producer.
     * @return int; the number of removed event types
     * @throws RemoteException on network failure
     */
    protected synchronized int removeAllListeners() throws RemoteException
    {
        return this.remoteEventProducer.removeAllListeners();
    }

    /**
     * Removes all the listeners of a class from this event producer.
     * @param ofClass Class&lt;?&gt;; the class or superclass
     * @return int; the number of removed listeners
     * @throws RemoteException on network failure
     */
    protected synchronized int removeAllListeners(final Class<?> ofClass) throws RemoteException
    {
        return this.remoteEventProducer.removeAllListeners(ofClass);

    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean removeListener(final EventListenerInterface listener, final EventTypeInterface eventType)
            throws RemoteException
    {
        return this.embeddedContext.removeListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasListeners() throws RemoteException
    {
        return this.embeddedContext.hasListeners();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int numberOfListeners(final EventTypeInterface eventType) throws RemoteException
    {
        return this.embeddedContext.numberOfListeners(eventType);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Set<EventTypeInterface> getEventTypesWithListeners() throws RemoteException
    {
        return this.embeddedContext.getEventTypesWithListeners();
    }

    /**
     * Return a safe copy of the list of (soft or weak) references to the registered listeners for the provided event type, or
     * an empty list when nothing is registered for this event type. The method never returns a null pointer, so it is safe to
     * use the result directly in an iterator. The references to the listeners are the original references, so not safe copies.
     * @param eventType EventTypeInterface; the event type to look up the listeners for
     * @return List&lt;Reference&lt;EventListenerInterface&gt;&gt;; the list of references to the listeners for this event type,
     *         or an empty list when the event type is not registered
     */
    protected List<Reference<EventListenerInterface>> getListenerReferences(final EventTypeInterface eventType)
    {
        return this.remoteEventProducer.getListenerReferences(eventType);
    }

    /* ***************************************************************************************************************** */
    /* ****************************************** REMOTECHANGEEVENTPRODUCER ******************************************** */
    /* ***************************************************************************************************************** */

    /**
     * The RemoteChangeEventProducer is a RemoteEventProducer that can fire an OBJECT_CHANGED_EVENT on behalf of an object that
     * was changed, but does not extend an EventProducer itself.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    protected class RemoteChangeEventProducer extends RemoteEventProducer
    {
        /** */
        private static final long serialVersionUID = 20200208L;

        /** the stored binding key that acts as the source id. */
        private final String bindingKey;

        /**
         * Create a remote event listener and register the listener in the RMI registry. When the RMI registry does not exist
         * yet, it will be created, but <b>only</b> on the local host. Remote creation of a registry on another computer is not
         * possible. Any attempt to do so will cause an AccessException to be fired.
         * @param host String; the host where the RMI registry resides or will be created. Creation is only possible on
         *            localhost.
         * @param port int; the port where the RMI registry can be found or will be created
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

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return this.bindingKey;
        }

        /**
         * Transmit an changed event with a serializable object as payload to all interested listeners.
         * @param eventType EventType; the eventType of the event
         * @param value Serializable; the object sent with the event
         * @throws RemoteException on network failure
         */
        protected void fireChangedEvent(final EventType eventType, final Serializable value) throws RemoteException
        {
            super.fireEvent(eventType, value);
        }

        /** {@inheritDoc} */
        @Override
        protected synchronized int removeAllListeners() throws RemoteException
        {
            return super.removeAllListeners();
        }

        /** {@inheritDoc} */
        @Override
        protected synchronized int removeAllListeners(final Class<?> ofClass) throws RemoteException
        {
            return super.removeAllListeners(ofClass);
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasListeners() throws RemoteException
        {
            return super.hasListeners();
        }

        /** {@inheritDoc} */
        @Override
        public synchronized int numberOfListeners(final EventTypeInterface eventType) throws RemoteException
        {
            return super.numberOfListeners(eventType);
        }

        /** {@inheritDoc} */
        @Override
        public synchronized Set<EventTypeInterface> getEventTypesWithListeners() throws RemoteException
        {
            return super.getEventTypesWithListeners();
        }

        /** {@inheritDoc} */
        @Override
        protected List<Reference<EventListenerInterface>> getListenerReferences(final EventTypeInterface eventType)
        {
            return super.getListenerReferences(eventType);
        }
    }
}
