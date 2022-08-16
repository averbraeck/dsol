package nl.tudelft.simulation.naming.context;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The JVMContext is an in-memory, thread-safe context implementation of the ContextInterface.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class JVMContext extends EventProducer implements ContextInterface
{
    /** */
    private static final long serialVersionUID = 20200101L;

    /** the parent context. */
    protected ContextInterface parent;

    /** the atomicName. */
    private String atomicName;

    /** the absolute path of this context. */
    private String absolutePath;

    /** the children. */
    protected Map<String, Object> elements = Collections.synchronizedMap(new TreeMap<String, Object>());

    /**
     * constructs a new root JVMContext.
     * @param atomicName String; the name under which the root context will be registered
     */
    public JVMContext(final String atomicName)
    {
        this(null, atomicName);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.absolutePath;
    }

    /**
     * Constructs a new JVMContext.
     * @param parent Context; the parent context
     * @param atomicName String; the name under which the context will be registered
     */
    public JVMContext(final ContextInterface parent, final String atomicName)
    {
        Throw.whenNull(atomicName, "name under which a context is registered cannot be null");
        Throw.when(atomicName.contains(ContextInterface.SEPARATOR), IllegalArgumentException.class,
                "name %s under which a context is registered cannot contain the separator string %s", atomicName,
                ContextInterface.SEPARATOR);
        this.parent = parent;
        this.atomicName = atomicName;
        try
        {
            this.absolutePath = parent == null ? "" : parent.getAbsolutePath() + ContextInterface.SEPARATOR + this.atomicName;
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().warn(exception);
            throw new RuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getAtomicName() throws RemoteException
    {
        return this.atomicName;
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getParent() throws RemoteException
    {
        return this.parent;
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getRootContext() throws RemoteException
    {
        ContextInterface result = this;
        while (result.getParent() != null)
        {
            result = result.getParent();
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String getAbsolutePath() throws RemoteException
    {
        return this.absolutePath;
    }

    /** {@inheritDoc} */
    @Override
    public Object getObject(final String key) throws NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        if (!this.elements.containsKey(key))
        {
            throw new NameNotFoundException("key " + key + " does not exist in Context");
        }
        // can be null -- null objects are allowed in the context tree
        return this.elements.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public Object get(final String name) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        if (contextName.getName().length() == 0)
        {
            return contextName.getContext();
        }
        Object result = contextName.getContext().getObject(contextName.getName());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final String name) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        if (contextName.getName().length() == 0)
        {
            return true;
        }
        return contextName.getContext().hasKey(contextName.getName());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasKey(final String key) throws NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        return this.elements.containsKey(key);
    }

    /**
     * Indicates whether the object has been registered (once or more) in the current Context. The object may be null.
     * @param object Object; the object to look up; mey be null
     * @return boolean; whether an object with the given key has been registered once or more in the current context
     */
    /** {@inheritDoc} */
    @Override
    public boolean hasObject(final Object object) throws RemoteException
    {
        return this.elements.containsValue(object);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() throws RemoteException
    {
        return this.elements.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final String key, final Object object) throws NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        if (this.elements.containsKey(key))
        {
            throw new NameAlreadyBoundException("key " + key + " already bound to object in Context");
        }
        checkCircular(object);
        this.elements.put(key, object);
        fireEvent(ContextInterface.OBJECT_ADDED_EVENT, new Object[] {getAbsolutePath(), key, object});
    }

    /** {@inheritDoc} */
    @Override
    public void bindObject(final Object object) throws NamingException, RemoteException
    {
        Throw.whenNull(object, "object cannot be null");
        bindObject(makeObjectKey(object), object);
    }

    /** {@inheritDoc} */
    @Override
    public void bind(final String name, final Object object) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        contextName.getContext().bindObject(contextName.getName(), object);
    }

    /** {@inheritDoc} */
    @Override
    public void unbindObject(String key) throws NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        if (this.elements.containsKey(key))
        {
            Object object = this.elements.remove(key);
            fireEvent(ContextInterface.OBJECT_REMOVED_EVENT, new Object[] {getAbsolutePath(), key, object});
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unbind(final String name) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        contextName.getContext().unbindObject(contextName.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void rebindObject(String key, final Object object) throws NamingException, RemoteException
    {
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0 || key.contains(ContextInterface.SEPARATOR), NamingException.class,
                "key [%s] is the empty string or key contains '/'", key);
        checkCircular(object);
        if (this.elements.containsKey(key))
        {
            this.elements.remove(key);
            fireEvent(ContextInterface.OBJECT_REMOVED_EVENT, new Object[] {getAbsolutePath(), key, object});
        }
        this.elements.put(key, object);
        fireEvent(ContextInterface.OBJECT_ADDED_EVENT, new Object[] {getAbsolutePath(), key, object});
    }

    /** {@inheritDoc} */
    @Override
    public void rebind(final String name, final Object object) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        contextName.getContext().rebindObject(contextName.getName(), object);
    }

    /** {@inheritDoc} */
    @Override
    public void rename(final String oldName, final String newName) throws NamingException, RemoteException
    {
        ContextName contextNameOld = lookup(oldName);
        ContextName contextNameNew = lookup(newName);
        if (contextNameNew.getContext().hasKey(contextNameNew.getName()))
        {
            throw new NameAlreadyBoundException("key " + newName + " already bound to object in Context");
        }
        Object object = contextNameOld.getContext().getObject(contextNameOld.getName());
        contextNameNew.getContext().checkCircular(object);
        contextNameOld.getContext().unbindObject(contextNameOld.getName());
        contextNameNew.getContext().bindObject(contextNameNew.getName(), object);
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface createSubcontext(final String name) throws NamingException, RemoteException
    {
        return lookupAndBuild(name);
    }

    /** {@inheritDoc} */
    @Override
    public void destroySubcontext(final String name) throws NamingException, RemoteException
    {
        ContextName contextName = lookup(name);
        Object object = contextName.getContext().getObject(contextName.getName());
        if (!(object instanceof ContextInterface))
        {
            throw new NotContextException("name " + name + " is bound but does not name a context");
        }
        destroy((ContextInterface) object);
        contextName.getContext().unbindObject(contextName.getName());
    }

    /**
     * Take a (compound) name such as "sub1/sub2/key" or "key" or "/sub/key" and lookup and/or create all intermediate contexts
     * as well as the final sub-context of the path.
     * @param name the (possibly compound) name
     * @return the context, possibly built new
     * @throws NamingException as a placeholder overarching exception
     * @throws RemoteException when the JVM context is used over a network and a network error occurs
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NullPointerException when name is null
     */
    protected ContextInterface lookupAndBuild(final String name) throws NamingException, RemoteException
    {
        Throw.whenNull(name, "name cannot be null");

        // Handle current context lookup
        if (name.length() == 0 || name.equals(ContextInterface.SEPARATOR))
        {
            throw new NamingException("the terminal reference is '/' or empty");
        }

        // determine absolute or relative path
        String reference;
        ContextInterface subContext;
        if (name.startsWith(ContextInterface.ROOT))
        {
            reference = name.substring(ContextInterface.SEPARATOR.length());
            subContext = getRootContext();
        }
        else
        {
            reference = name;
            subContext = this;
        }

        while (true)
        {
            int index = reference.indexOf(ContextInterface.SEPARATOR);
            if (index == -1)
            {
                ContextInterface newContext = new JVMContext(subContext, reference);
                subContext.bind(reference, newContext);
                subContext = newContext;
                break;
            }
            String sub = reference.substring(0, index);
            reference = reference.substring(index + ContextInterface.SEPARATOR.length());
            if (!subContext.hasKey(sub))
            {
                ContextInterface newContext = new JVMContext(subContext, sub);
                subContext.bind(sub, newContext);
                subContext = newContext;
            }
            else
            {
                Object subObject = subContext.getObject(sub); // can throw NameNotFoundException
                if (!(subObject instanceof ContextInterface))
                {
                    throw new NameNotFoundException(
                            "parsing name " + name + " in context -- bound object " + sub + " is not a subcontext");
                }
                subContext = (ContextInterface) subObject;
            }
        }
        return subContext;
    }

    /**
     * Recursively unbind and destroy all keys and subcontexts from the given context, leaving it empty. All removals will fire
     * an OBJECT_REMOVED event, depth first.
     * @param context the context to empty
     * @throws NamingException on tree inconsistencies
     * @throws RemoteException on RMI error
     */
    protected void destroy(final ContextInterface context) throws RemoteException, NamingException
    {
        // depth-first subcontext removal
        Set<String> copyKeySet = new LinkedHashSet<>(context.keySet());
        for (String key : copyKeySet)
        {
            if (context.getObject(key) instanceof ContextInterface && context.getObject(key) != null)
            {
                destroy((ContextInterface) context.getObject(key));
                context.unbindObject(key);
            }
        }

        // leaf removal
        copyKeySet = new LinkedHashSet<>(context.keySet());
        for (String key : copyKeySet)
        {
            if (context.getObject(key) instanceof ContextInterface)
            {
                throw new NamingException("Tree inconsistent -- Context not removed or added during destroy operation");
            }
            context.unbindObject(key);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() throws RemoteException
    {
        return this.elements.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Object> values() throws RemoteException
    {
        return this.elements.values();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> bindings() throws RemoteException
    {
        return this.elements;
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
            fireEvent(ContextInterface.OBJECT_CHANGED_EVENT, new Object[] {getAbsolutePath(), key, getObject(key)});
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
    public void checkCircular(final Object newObject) throws NamingException, RemoteException
    {
        if (!(newObject instanceof ContextInterface))
            return;
        for (ContextInterface parentContext = getParent(); parentContext != null; parentContext = parentContext.getParent())
        {
            if (parentContext.equals(newObject))
            {
                throw new NamingException(
                        "circular reference for object " + newObject + "; prevented insertion into " + toString());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws NamingException, RemoteException
    {
        this.elements.clear();
        this.atomicName = "";
        this.parent = null;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String parentName;
        try
        {
            parentName = this.parent == null ? "null" : this.parent.getAtomicName();
        }
        catch (RemoteException exception)
        {
            parentName = "unreachable";
        }
        return "JVMContext[parent=" + parentName + ", atomicName=" + this.atomicName + "]";
    }

    /** {@inheritDoc} */
    @Override
    public String toString(final boolean verbose) throws RemoteException
    {
        if (!verbose)
        {
            return "JVMContext[" + getAtomicName() + "]";
        }
        return ContextUtil.toText(this);
    }

    /**
     * Take a (compound) name such as "sub1/sub2/key" or "key" or "/sub/key" and lookup the final sub-context of the path, and
     * store it in the ContextName. Store the key String in the ContextName without checking whether it exists.
     * @param name the (possibly compound) name
     * @return a ContextName combination string the subcontext and final reference name
     * @throws NamingException as a placeholder overarching exception
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws RemoteException when the JVM context is used over a network and a network error occurs
     * @throws NullPointerException when name is null
     */
    protected ContextName lookup(final String name) throws NamingException, RemoteException
    {
        Throw.whenNull(name, "name cannot be null");

        // Handle current context lookup
        if (name.length() == 0)
        {
            return new ContextName(this, "");
        }

        // determine absolute or relative path
        String reference;
        ContextInterface subContext;
        if (name.startsWith(ContextInterface.ROOT))
        {
            reference = name.substring(ContextInterface.SEPARATOR.length());
            subContext = getRootContext();
        }
        else
        {
            reference = name;
            subContext = this;
        }

        while (true)
        {
            int index = reference.indexOf(ContextInterface.SEPARATOR);
            if (index == -1)
            {
                break;
            }
            String sub = reference.substring(0, index);
            reference = reference.substring(index + ContextInterface.SEPARATOR.length());
            Object subObject = subContext.getObject(sub); // can throw NameNotFoundException
            if (!(subObject instanceof ContextInterface))
            {
                throw new NameNotFoundException(
                        "parsing name " + name + " in context -- bound object " + sub + " is not a subcontext");
            }
            subContext = (ContextInterface) subObject;
        }
        return new ContextName(subContext, reference);
    }

    /**
     * Record with Context and Name combination.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    public static class ContextName
    {
        /** the context. */
        private final ContextInterface context;

        /** the name (key) in the context. */
        private final String name;

        /**
         * Instantiate a Context and Name combination.
         * @param context the context
         * @param name the name (key) in the context
         */
        public ContextName(final ContextInterface context, final String name)
        {
            this.context = context;
            this.name = name;
        }

        /**
         * @return context
         */
        public ContextInterface getContext()
        {
            return this.context;
        }

        /**
         * @return name
         */
        public String getName()
        {
            return this.name;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.context == null) ? 0 : this.context.hashCode());
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ContextName other = (ContextName) obj;
            if (this.context == null)
            {
                if (other.context != null)
                    return false;
            }
            else if (!this.context.equals(other.context))
                return false;
            if (this.name == null)
            {
                if (other.name != null)
                    return false;
            }
            else if (!this.name.equals(other.name))
                return false;
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "ContextName[context=" + this.context + ", name=" + this.name + "]";
        }
    }

}
