package nl.tudelft.simulation.naming.context.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;

import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventTypeInterface;
import org.djutils.event.ref.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.io.URLResource;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.naming.context.ContextFactory;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * InitialEventContext class. This class is the starting context for performing naming operations. The class is loosely based on
 * InitialContext in the Java JNDI package, but creates a lightweight Context that implements the DSOL ContextInterface. The
 * InitialEventContext is a singleton class.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
@SuppressWarnings("checkstyle:needbraces")
public final class InitialEventContext implements EventContext
{
    /** */
    private static final long serialVersionUID = 20200101L;

    /**
     * Constant that holds the name of the environment property for specifying the initial context factory to use. The value of
     * the property should be the fully qualified class name of the factory class that will create an initial context. This
     * property may be specified in the environment parameter passed to the initial context constructor, a system property, or
     * an application resource file. If it is not specified in any of these sources, {@code NoInitialContextException} is thrown
     * when an initial context is required to complete an operation.
     * <p>
     * The value of this constant is "java.naming.factory.initial".
     * </p>
     */
    public static final String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";

    /**
     * Constant that holds the name of the environment property for specifying configuration information for the service
     * provider to use. The value of the property should contain a URL string (e.g. "http://localhost:1099/remoteContext"). This
     * property may be specified in the environment, a system property, or a resource file. If it is not specified in any of
     * these sources, the default configuration is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.provider.url".
     * </p>
     */
    public static final String PROVIDER_URL = "java.naming.provider.url";

    /**
     * Constant that holds the name of the environment property for specifying the initial context factory to use, as embedded
     * factory in a remote context that is specified by the INITIAL_CONTEXT_FACTORY constant. The value of the property should
     * be the fully qualified class name of the factory class that will create an initial context that will be embedded within
     * the remote context. This property may be specified in the environment parameter passed to the initial context
     * constructor, a system property, or an application resource file. If it is not specified in any of these sources,
     * {@code NoInitialContextException} is thrown when an initial context is required to complete an operation.
     * <p>
     * The value of this constant is "wrapped.naming.factory.initial".
     * </p>
     */
    public static final String WRAPPED_CONTEXT_FACTORY = "wrapped.naming.factory.initial";

    /** the singleton instance of the InitialEventContext. */
    private static InitialEventContext INSTANCE;

    /** the properties of the initialEventContext. */
    protected Hashtable<?, ?> properties = null;

    /**
     * The initial context, usually built by a factory. It is set by getDefaultInitCtx() the first time getDefaultInitCtx() is
     * called. Subsequent invocations of getDefaultInitCtx() return the value of defaultInitCtx until close() is called.
     */
    protected ContextInterface defaultInitCtx = null;

    /** has the default context been generated? */
    protected boolean gotDefault = false;

    /** The event producer to which context events will be delegated for handling. */
    public ContextEventProducerImpl contextEventProducerImpl;

    /**
     * Constructs an initial context. No environment properties are supplied. Equivalent to
     * <code>new InitialContext(null)</code>.
     * @param environment the overriding environment variables for the Factory that constructs the wrapped Context
     * @param atomicName String; the name under which the root context will be registered
     * @throws NamingException if a naming exception is encountered
     * @throws RemoteException if a network connection failure occurs
     */
    private InitialEventContext(final Hashtable<?, ?> environment, final String atomicName)
            throws NamingException, RemoteException
    {
        init(environment, atomicName);
        this.contextEventProducerImpl = new ContextEventProducerImpl(this);
    }

    /**
     * Constructs an initial context using the supplied environment; no overriding environment variables are provided.
     * @param atomicName String; the name under which the root context will be registered
     * @return a singleton instance of InitialEventContext
     * @throws NamingException when the provided ContextFactory was not able to instantiate the wrapped context
     * @throws RemoteException if a network connection failure occurs
     */
    public static InitialEventContext instantiate(final String atomicName) throws NamingException, RemoteException
    {
        return instantiate(null, atomicName);
    }

    /**
     * Constructs an initial context using the supplied environment.
     * @param environment Hashtable&lt;?,?&gt;; environment used to create the initial context. Null indicates an empty
     *            environment.
     * @param atomicName String; the name under which the root context will be registered
     * @return a singleton instance of InitialEventContext
     * @throws NamingException when the provided ContextFactory was not able to instantiate the wrapped context
     * @throws RemoteException if a network connection failure occurs
     */
    public static InitialEventContext instantiate(final Hashtable<?, ?> environment, final String atomicName)
            throws NamingException, RemoteException
    {
        if (INSTANCE != null)
        {
            return INSTANCE;
        }

        INSTANCE = new InitialEventContext(environment, atomicName);
        INSTANCE.init(environment == null ? null : (Hashtable<?, ?>) environment.clone(), atomicName);
        return INSTANCE;
    }

    /**
     * Initializes the initial context using the supplied environment.
     * @param environment Hashtable&lt;?,?&gt;; environment used to create the initial context. Null indicates an empty
     *            environment.
     * @param atomicName String; the name under which the root context will be registered
     * @throws NamingException when the provided ContextFactory was not able to instantiate the wrapped context
     * @throws RemoteException if a network connection failure occurs
     */
    protected void init(final Hashtable<?, ?> environment, final String atomicName) throws NamingException, RemoteException
    {
        this.properties = buildEnvironment(environment);
        if (this.properties.get(Context.INITIAL_CONTEXT_FACTORY) != null)
        {
            getDefaultInitCtx(atomicName);
        }
    }

    /**
     * Build the properties that the generation of the initial event context will use. In sequence, the following sources of
     * properties are explored: (1) default values, (2) system environment, (3) Java system properties, (4) content of the
     * /resources/jndi.properties file, (5) the provided environment. If a property is available in a later evaluation, it takes
     * precedence over an earlier definition.
     * @param environment the final overwriting environment to use (can be null)
     * @return Hashtable&lt;?,?&gt;; a combined Hashtable with information from system properties, jndi.properties and the
     *         provided environment Hashtable
     */
    protected Hashtable<?, ?> buildEnvironment(final Hashtable<?, ?> environment)
    {
        Hashtable<String, String> result = new Hashtable<>();
        String[] keys = new String[] {INITIAL_CONTEXT_FACTORY, PROVIDER_URL, WRAPPED_CONTEXT_FACTORY};

        // (1) defaults
        result.put(INITIAL_CONTEXT_FACTORY, "nl.tudelft.simulation.naming.context.JVMContextFactory");

        // (2) system properties
        Map<String, String> sysEnv = System.getenv();
        for (String key : keys)
        {
            if (sysEnv.containsKey(key))
            {
                result.put(key, sysEnv.get(key));
            }
        }

        // (3) Java system properties
        Properties javaProps = System.getProperties();
        for (String key : keys)
        {
            if (javaProps.containsKey(key))
            {
                result.put(key, javaProps.get(key).toString());
            }
        }

        // (4) content of the /resources/jndi.properties file
        InputStream stream = URLResource.getResourceAsStream("/resources/jndi.properties");
        if (stream != null)
        {
            Properties jndiProps = new Properties();
            try
            {
                jndiProps.load(stream);
                for (String key : keys)
                {
                    if (jndiProps.containsKey(key))
                    {
                        result.put(key, jndiProps.get(key).toString());
                    }
                }
            }
            catch (IOException exception)
            {
                CategoryLogger.always().error(exception);
            }
        }

        // (5) the provided environment (if provided)
        if (environment != null)
        {
            for (String key : keys)
            {
                if (environment.containsKey(key))
                {
                    result.put(key, environment.get(key).toString());
                }
            }
        }

        return result;
    }

    /**
     * Retrieves the initial context by calling NamingManager.getInitialContext() and cache it in defaultInitCtx. Set
     * <code>gotDefault</code> so that we know we've tried this before.
     * @param atomicName String; the name under which the root context will be registered
     * @return The non-null cached initial context.
     * @throws NamingException if a naming exception was encountered
     * @throws RemoteException if a network connection failure occurs
     */
    protected ContextInterface getDefaultInitCtx(final String atomicName) throws NamingException, RemoteException
    {
        try
        {
            if (!this.gotDefault)
            {
                String factoryName = this.properties.get(Context.INITIAL_CONTEXT_FACTORY).toString();
                Throw.when(factoryName == null, NamingException.class,
                        "value of " + Context.INITIAL_CONTEXT_FACTORY + " not in properties");
                @SuppressWarnings("unchecked")
                Class<ContextFactory> factoryClass = (Class<ContextFactory>) Class.forName(factoryName);
                Constructor<ContextFactory> factoryConstructor = factoryClass.getDeclaredConstructor();
                ContextFactory factory = factoryConstructor.newInstance();
                this.defaultInitCtx = factory.getInitialContext(this.properties, atomicName);
                this.gotDefault = true;
            }
            if (this.defaultInitCtx == null)
            {
                throw new NoInitialContextException();
            }
            return this.defaultInitCtx;
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception)
        {
            throw new NamingException("Unable to get default initial context: " + exception.getMessage());
        }
    }

    /**
     * Return a safe copy of the used environment variables.
     * @return Hashtable&lt;?, ?&gt;; a safe copy of the used environment variables
     */
    public Hashtable<?, ?> getEnvironment()
    {
        return (Hashtable<?, ?>) this.properties.clone();
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return ""; // the empty (root) path
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws NamingException, RemoteException
    {
        this.properties = null;
        if (this.defaultInitCtx != null)
        {
            this.defaultInitCtx.close();
            this.defaultInitCtx = null;
        }
        this.gotDefault = false;
    }

    /** {@inheritDoc} */
    @Override
    public String getAtomicName() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getAtomicName();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getParent() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getParent();
        throw new RuntimeException(new NoInitialContextException());

    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getRootContext() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getRootContext();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public String getAbsolutePath() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getAbsolutePath();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public Object get(final String name) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.get(name);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public Object getObject(final String key) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getObject(key);
        throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final String name) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.exists(name);
        throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasKey(final String key) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.hasKey(key);
        throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasObject(final Object object) throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.hasObject(object);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.isEmpty();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public void bind(final String name, final Object object) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.bind(name, object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.bindObject(key, object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final Object object) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.bindObject(object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void unbind(final String name) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.unbind(name);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void unbindObject(final String key) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.unbindObject(key);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void rebind(final String name, final Object object) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.rebind(name, object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void rebindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.rebindObject(key, object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void rename(final String oldName, final String newName) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.rename(oldName, newName);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface createSubcontext(final String name) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.createSubcontext(name);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void destroySubcontext(final String name) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.destroySubcontext(name);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void checkCircular(final Object newObject) throws NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.checkCircular(newObject);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.keySet();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Object> values() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.values();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> bindings() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.bindings();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public void fireObjectChangedEventValue(final Object object)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.fireObjectChangedEventValue(object);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public void fireObjectChangedEventKey(final String key)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException
    {
        if (this.defaultInitCtx != null)
            this.defaultInitCtx.fireObjectChangedEventKey(key);
        else
            throw new NoInitialContextException();
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        if (this.defaultInitCtx != null)
            return "InitialEventContext[" + this.defaultInitCtx.toString() + "]";
        return "InitialEventContext[null]";
    }

    /** {@inheritDoc} */
    @Override
    public String toString(final boolean verbose) throws RemoteException
    {
        if (!verbose)
        {
            return "InitialEventContext[" + getAtomicName() + "]";
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
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.addListener(listener, eventType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final ReferenceType referenceType) throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.addListener(listener, eventType, referenceType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final int position) throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.addListener(listener, eventType, position);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean addListener(final EventListenerInterface listener, final EventTypeInterface eventType,
            final int position, final ReferenceType referenceType) throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.addListener(listener, eventType, position, referenceType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean removeListener(final EventListenerInterface listener, final EventTypeInterface eventType)
            throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.removeListener(listener, eventType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasListeners() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.hasListeners();
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int numberOfListeners(final EventTypeInterface eventType) throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.numberOfListeners(eventType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Set<EventTypeInterface> getEventTypesWithListeners() throws RemoteException
    {
        if (this.defaultInitCtx != null)
            return this.defaultInitCtx.getEventTypesWithListeners();
        throw new RuntimeException(new NoInitialContextException());
    }

    /* ***************************************************************************************************************** */
    /* *************************************** EVENTCONTEXTINTERFACE LISTENERS ***************************************** */
    /* ***************************************************************************************************************** */

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException
    {
        if (this.defaultInitCtx != null)
            return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final ReferenceType referenceType) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        if (this.defaultInitCtx != null)
            return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, referenceType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final int position) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        if (this.defaultInitCtx != null)
            return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, position);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final int position, final ReferenceType referenceType) throws RemoteException,
            NameNotFoundException, InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        if (this.defaultInitCtx != null)
            return this.contextEventProducerImpl.addListener(listener, absolutePath, contextScope, position, referenceType);
        throw new RuntimeException(new NoInitialContextException());
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, InvalidNameException, NullPointerException
    {
        if (this.defaultInitCtx != null)
            return this.contextEventProducerImpl.removeListener(listener, absolutePath, contextScope);
        throw new RuntimeException(new NoInitialContextException());
    }

}
