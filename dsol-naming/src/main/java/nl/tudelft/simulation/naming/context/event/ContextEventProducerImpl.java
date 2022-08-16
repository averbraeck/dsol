package nl.tudelft.simulation.naming.context.event;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducer;
import org.djutils.event.EventProducerInterface;
import org.djutils.event.ref.ReferenceType;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * ContextEventProducerImpl carries out the implementation for the EventContext classes. The class registers as a listener on
 * the root of the InitialEventContext or the RemoteEventContext. Whenever sub-contexts are added (these will typically not be
 * of type EventContext, but rather of type JVMContext, FileContext, RemoteContext, or other), this class also registers as a
 * listener on these sub-contexts. Thereby, it remains aware of all changes happening in the sub-contexts of which it may be
 * notified.<br>
 * <br>
 * For listening, four different ContextScope options exist:
 * <ul>
 * <li><b>OBJECT_SCOPE</b>: listen to the changes in the object. OBJECT_SCOPE can be applied to regular objects and to context
 * objects. When the object is at a leaf position in the tree, OBJECT_CHANGED events will be fired to the listener(s) on a
 * change of the leaf object, and OBJECT_REMOVED events when the leaf is removed from the context. When the object is a context,
 * OBJECT_REMOVED is fired when the sub-context is removed from its parent context.</li>
 * <li><b>LEVEL_SCOPE</b>: fires events when changes happen in the provided context. LEVEL_SCOPE can only be applied to context
 * objects. OBJECT_ADDED is fired when an object (context or leaf) is added to the context; OBJECT_REMOVED when an object
 * (context or leaf) is removed from the context; OBJECT_CHANGED when one of the leaf objects present in the provided context
 * had changes in its content. OBJECT_REMOVED is <b>not</b> fired when the sub-context is removed from the parent context.</li>
 * <li><b>LEVEL_OBJECT_SCOPE</b>: fires events when changes happen in the provided context. LEVEL_OBJECT_SCOPE can only be
 * applied to context objects. OBJECT_ADDED is fired when an object (context or leaf) is added to the context; OBJECT_REMOVED
 * when an object (context or leaf) is removed from the context; OBJECT_CHANGED when one of the leaf objects present in the
 * provided context had changes in its content. OBJECT_REMOVED <b>is also</b> fired when the sub-context is removed from the
 * parent context.</li>
 * <li><b>SUBTREE_SCOPE</b>: fires events when changes happen in the provided context, and any context deeper in the tree
 * descending from the provided context. SUBTREE_SCOPE can only be applied to context objects. OBJECT_ADDED is fired when an
 * object (context or leaf) is added to the context, or any of its descendant contexts; OBJECT_REMOVED when an object (context
 * or leaf) is removed from the context or a descendant context; OBJECT_CHANGED when one of the leaf objects present in the
 * provided context or any descendant context had changes in its content. OBJECT_REMOVED <b>is also</b> fired when the
 * sub-context is removed from the parent context.</li>
 * </ul>
 * The listeners to be notified are determined with a regular expression. Examples of this regular expression are given below.
 * <ul>
 * <li><b>OBJECT_SCOPE</b>: Suppose we are interested in changes to the object registered under "/simulation1/sub1/myobject". In
 * that case, the regular expression is "/simulation1/sub1/myobject" as we are only interested in changes to the object
 * registered under this key.</li>
 * <li><b>LEVEL_SCOPE</b>: Suppose we are interested in changes to the objects registered <i>directly</i> under
 * "/simulation1/sub1", excluding the sub1 context itself. In that case, the regular expression is "/simulation1/sub1/[^/]+$" as
 * we are only interested in changes to the objects registered under this key, so minimally one character after the key that
 * cannot be a forward slash, as that would indicate a subcontext.</li>
 * <li><b>LEVEL_OBJECT_SCOPE</b>: Suppose we are interested in changes to the objects registered <i>directly</i> under
 * "/simulation1/sub1", including the "/simulation1/sub1" context itself. In that case, the regular expression is
 * "/simulation1/sub1(/[^/]*$)?" as we are interested in changes to the objects directly registered under this key, so minimally
 * one character after the key that cannot be a forward slash. The context "sub1" itself is also included, with or without a
 * forward slash at the end.</li>
 * <li><b>SUBTREE_SCOPE</b>: Suppose we are interested in changes to the objects registered in the total subtree under
 * "/simulation1/sub1", including the "/simulation1/sub1" context itself. In that case, the regular expression is
 * "/simulation1/sub1(/.*)?". The context "sub1" itself is also included, with or without a forward slash at the end.</li>
 * </ul>
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ContextEventProducerImpl extends EventProducer implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20200209L;

    /**
     * The registry for the listeners. The key of the map is made from the given path to listen followed by a hash sign and the
     * toString of the used ContextScope enum. String has a cached hash.
     */
    private Map<String, PatternListener> regExpListenerMap = new LinkedHashMap<>();

    /**
     * The records for the scope listeners so we can find them back for removal. The key of the map is made from the given path
     * to listen followed by a hash sign and the toString of the used ContextScope enum. String has a cached hash.
     */
    private Map<String, PatternListener> registryMap = new LinkedHashMap<>();

    /** the EventContext for which we do the work. */
    private final EventContext parent;

    /**
     * Create the ContextEventProducerImpl and link to the parent class.
     * @param parent EventContext; the EventContext for which we do the work
     * @throws RemoteException on network error
     */
    public ContextEventProducerImpl(final EventContext parent) throws RemoteException
    {
        super();
        this.parent = parent;

        // we subscribe ourselves to the OBJECT_ADDED, OBJECT_REMOVED and OBJECT_CHANGED events of the parent
        this.parent.addListener(this, ContextInterface.OBJECT_ADDED_EVENT, ReferenceType.WEAK);
        this.parent.addListener(this, ContextInterface.OBJECT_REMOVED_EVENT, ReferenceType.WEAK);
        this.parent.addListener(this, ContextInterface.OBJECT_CHANGED_EVENT, ReferenceType.WEAK);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        try
        {
            return this.parent.getSourceId();
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        Object[] content = (Object[]) event.getContent();
        if (event.getType().equals(ContextInterface.OBJECT_ADDED_EVENT))
        {
            if (content[2] instanceof ContextInterface)
            {
                ContextInterface context = (ContextInterface) content[2];
                context.addListener(this, ContextInterface.OBJECT_ADDED_EVENT);
                context.addListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
                context.addListener(this, ContextInterface.OBJECT_CHANGED_EVENT);
            }
            for (Entry<String, PatternListener> entry : this.regExpListenerMap.entrySet())
            {
                String path = (String) content[0] + ContextInterface.SEPARATOR + (String) content[1];
                if (entry.getValue().getPattern().matcher(path).matches())
                {
                    entry.getValue().getListener().notify(event);
                }
            }
        }
        else if (event.getType().equals(ContextInterface.OBJECT_REMOVED_EVENT))
        {
            if (content[2] instanceof ContextInterface)
            {
                ContextInterface context = (ContextInterface) content[2];
                context.removeListener(this, ContextInterface.OBJECT_ADDED_EVENT);
                context.removeListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
                context.removeListener(this, ContextInterface.OBJECT_CHANGED_EVENT);
            }
            for (Entry<String, PatternListener> entry : this.regExpListenerMap.entrySet())
            {
                String path = (String) content[0] + ContextInterface.SEPARATOR + (String) content[1];
                if (entry.getValue().getPattern().matcher(path).matches())
                {
                    entry.getValue().getListener().notify(event);
                }
            }
        }
        else if (event.getType().equals(ContextInterface.OBJECT_CHANGED_EVENT))
        {
            for (Entry<String, PatternListener> entry : this.regExpListenerMap.entrySet())
            {
                String path = (String) content[0] + ContextInterface.SEPARATOR + (String) content[1];
                if (entry.getValue().getPattern().matcher(path).matches())
                {
                    entry.getValue().getListener().notify(event);
                }
            }
        }
    }

    /**
     * Make a key consisting of the full path of the subcontext (without the trailing slash) or object in the context tree,
     * followed by a hash code (#) and the context scope string (OBJECT_SCOPE, LEVEL_SCOPE, LEVEL_OBJECT_SCOPE, or
     * SUBTREE_SCOPE).
     * @param absolutePath String; the path for which the key has to be made. The path can point to an object or a subcontext
     * @param contextScope ContextScope; the scope for which the key has to be made
     * @return a concatenation of the path, a hash (#) and the context scope
     */
    protected String makeRegistryKey(final String absolutePath, final ContextScope contextScope)
    {
        String key = absolutePath;
        if (key.endsWith("/"))
        {
            key = key.substring(0, key.length() - 1);
        }
        key += "#" + contextScope.name();
        return key;
    }

    /**
     * Make a regular expression that matches the right paths to be matched. The regular expression per scope is:
     * <ul>
     * <li><b>OBJECT_SCOPE</b>: Suppose we are interested in changes to the object registered under
     * "/simulation1/sub1/myobject". In that case, the regular expression is "/simulation1/sub1/myobject" as we are only
     * interested in changes to the object registered under this key.</li>
     * <li><b>LEVEL_SCOPE</b>: Suppose we are interested in changes to the objects registered <i>directly</i> under
     * "/simulation1/sub1", excluding the sub1 context itself. In that case, the regular expression is
     * "/simulation1/sub1/[^/]+$" as we are only interested in changes to the objects registered under this key, so minimally
     * one character after the key that cannot be a forward slash, as that would indicate a subcontext.</li>
     * <li><b>LEVEL_OBJECT_SCOPE</b>: Suppose we are interested in changes to the objects registered <i>directly</i> under
     * "/simulation1/sub1", including the "/simulation1/sub1" context itself. In that case, the regular expression is
     * "/simulation1/sub1(/[^/]*$)?" as we are interested in changes to the objects directly registered under this key, so
     * minimally one character after the key that cannot be a forward slash. The context "sub1" itself is also included, with or
     * without a forward slash at the end.</li>
     * <li><b>SUBTREE_SCOPE</b>: Suppose we are interested in changes to the objects registered in the total subtree under
     * "/simulation1/sub1", including the "/simulation1/sub1" context itself. In that case, the regular expression is
     * "/simulation1/sub1(/.*)?". The context "sub1" itself is also included, with or without a forward slash at the end.</li>
     * </ul>
     * @param absolutePath String; the path for which the key has to be made. The path can point to an object or a subcontext
     * @param contextScope ContextScope; the scope for which the key has to be made
     * @return a concatenation of the path, a hash (#) and the context scope
     */
    protected String makeRegex(final String absolutePath, final ContextScope contextScope)
    {
        String key = absolutePath;
        if (key.endsWith("/"))
        {
            key = key.substring(0, key.length() - 1);
        }
        switch (contextScope)
        {
            case LEVEL_SCOPE:
                key += "/[^/]+$";
                break;

            case LEVEL_OBJECT_SCOPE:
                key += "(/[^/]*$)?";
                break;

            case SUBTREE_SCOPE:
                key += "(/.*)?";
                break;

            default:
                break;
        }
        return key;
    }

    /**
     * Add a listener for the provided scope as strong reference to the BEGINNING of a queue of listeners.
     * @param listener EventListenerInterface; the listener which is interested at events of eventType.
     * @param absolutePath String; the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope; the part of the tree that the listener is aimed at (current node, current node and
     *            keys, subtree).
     * @return the success of adding the listener. If a listener was already added false is returned.
     * @throws NameNotFoundException when the absolutePath could not be found in the parent context, or when an intermediate
     *             context does not exist
     * @throws InvalidNameException when the scope is OBJECT_SCOPE, but the key points to a (sub)context
     * @throws NotContextException when the scope is LEVEL_SCOPE, OBJECT_LEVEL_SCOPE or SUBTREE_SCOPE, and the key points to an
     *             ordinary object
     * @throws NamingException as an overarching exception for context errors
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs.
     */
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException
    {
        return addListener(listener, absolutePath, contextScope, EventProducerInterface.FIRST_POSITION, ReferenceType.STRONG);
    }

    /**
     * Add a listener for the provided scope to the BEGINNING of a queue of listeners.
     * @param listener EventListenerInterface; the listener which is interested at events of eventType.
     * @param absolutePath String; the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope; the part of the tree that the listener is aimed at (current node, current node and
     *            keys, subtree).
     * @param referenceType ReferenceType; whether the listener is added as a strong or as a weak reference.
     * @return the success of adding the listener. If a listener was already added false is returned.
     * @throws NameNotFoundException when the absolutePath could not be found in the parent context, or when an intermediate
     *             context does not exist
     * @throws InvalidNameException when the scope is OBJECT_SCOPE, but the key points to a (sub)context
     * @throws NotContextException when the scope is LEVEL_SCOPE, OBJECT_LEVEL_SCOPE or SUBTREE_SCOPE, and the key points to an
     *             ordinary object
     * @throws NamingException as an overarching exception for context errors
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs.
     * @see org.djutils.event.ref.Reference
     */
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final ReferenceType referenceType) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        return addListener(listener, absolutePath, contextScope, EventProducerInterface.FIRST_POSITION, referenceType);
    }

    /**
     * Add a listener for the provided scope as strong reference to the specified position of a queue of listeners.
     * @param listener EventListenerInterface; the listener which is interested at events of eventType.
     * @param absolutePath String; the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope; the part of the tree that the listener is aimed at (current node, current node and
     *            keys, subtree).
     * @param position int; the position of the listener in the queue.
     * @return the success of adding the listener. If a listener was already added, or an illegal position is provided false is
     *         returned.
     * @throws NameNotFoundException when the absolutePath could not be found in the parent context, or when an intermediate
     *             context does not exist
     * @throws InvalidNameException when the scope is OBJECT_SCOPE, but the key points to a (sub)context
     * @throws NotContextException when the scope is LEVEL_SCOPE, OBJECT_LEVEL_SCOPE or SUBTREE_SCOPE, and the key points to an
     *             ordinary object
     * @throws NamingException as an overarching exception for context errors
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs.
     */
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final int position) throws RemoteException, NameNotFoundException,
            InvalidNameException, NotContextException, NamingException, NullPointerException
    {
        return addListener(listener, absolutePath, contextScope, position, ReferenceType.STRONG);
    }

    /**
     * Add a listener for the provided scope to the specified position of a queue of listeners.
     * @param listener EventListenerInterface; which is interested at certain events,
     * @param absolutePath String; the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope; the part of the tree that the listener is aimed at (current node, current node and
     *            keys, subtree).
     * @param position int; the position of the listener in the queue
     * @param referenceType ReferenceType; whether the listener is added as a strong or as a weak reference.
     * @return the success of adding the listener. If a listener was already added or an illegal position is provided false is
     *         returned.
     * @throws InvalidNameException when the path does not start with a slash
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs
     * @see org.djutils.event.ref.Reference
     */
    public boolean addListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope, final int position, final ReferenceType referenceType) throws RemoteException,
            InvalidNameException, NullPointerException
    {
        Throw.when(listener == null, NullPointerException.class, "listener cannot be null");
        Throw.when(absolutePath == null, NullPointerException.class, "absolutePath cannot be null");
        Throw.when(contextScope == null, NullPointerException.class, "contextScope cannot be null");
        Throw.when(referenceType == null, NullPointerException.class, "referenceType cannot be null");
        Throw.when(!absolutePath.startsWith("/"), InvalidNameException.class, "absolute path %s does not start with '/'",
                absolutePath);

        String registryKey = makeRegistryKey(absolutePath, contextScope);
        boolean added = !this.registryMap.containsKey(registryKey);
        String regex = makeRegex(absolutePath, contextScope);
        PatternListener patternListener = new PatternListener(Pattern.compile(regex), listener);
        this.regExpListenerMap.put(registryKey, patternListener);
        this.registryMap.put(registryKey, patternListener);
        return added;
    }

    /**
     * Remove the subscription of a listener for the provided scope for a specific event.
     * @param listener EventListenerInterface; which is no longer interested.
     * @param absolutePath String; the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope;the scope which is of no interest any more.
     * @return the success of removing the listener. If a listener was not subscribed false is returned.
     * @throws InvalidNameException when the path does not start with a slash
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs
     */
    public boolean removeListener(final EventListenerInterface listener, final String absolutePath,
            final ContextScope contextScope) throws RemoteException, InvalidNameException,
            NullPointerException
    {
        Throw.when(listener == null, NullPointerException.class, "listener cannot be null");
        Throw.when(absolutePath == null, NullPointerException.class, "absolutePath cannot be null");
        Throw.when(contextScope == null, NullPointerException.class, "contextScope cannot be null");
        Throw.when(!absolutePath.startsWith("/"), InvalidNameException.class, "absolute path %s does not start with '/'",
                absolutePath);
        
        String registryKey = makeRegistryKey(absolutePath, contextScope);
        boolean removed = this.registryMap.containsKey(registryKey);
        this.regExpListenerMap.remove(registryKey);
        this.registryMap.remove(registryKey);
        return removed;
    }

    /**
     * Pair of regular expression pattern and event listener.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    public class PatternListener implements Serializable
    {
        /** */
        private static final long serialVersionUID = 20200210L;

        /** the compiled regular expression pattern. */
        private final Pattern pattern;

        /** the event listener for this pattern. */
        private final EventListenerInterface listener;

        /**
         * Construct a pattern - listener pair.
         * @param pattern Pattern; the compiled pattern
         * @param listener EventListenerInterface; the registered listener for this pattern
         */
        public PatternListener(Pattern pattern, EventListenerInterface listener)
        {
            super();
            this.pattern = pattern;
            this.listener = listener;
        }

        /**
         * return the compiled pattern.
         * @return Pattern; the compiled pattern
         */
        public Pattern getPattern()
        {
            return this.pattern;
        }

        /**
         * Return the registered listener for this pattern.
         * @return EventListenerInterface; the registered listener for this pattern
         */
        public EventListenerInterface getListener()
        {
            return this.listener;
        }
    }
}
