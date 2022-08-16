package nl.tudelft.simulation.naming.context;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * ContextInterface is the lightweight and simplified version of the JNDI EventContext interface in the standard Java
 * distribution. It just contains the services for binding and unbinding of objects, for retrieving objects and Context trees,
 * and for creating an initial context and creating and removing subcontexts. As it extends the EventProducerInterface, it is
 * able to fire events for adding and removing of elements in the Context.
 * <p>
 * A name for a Context can be compound, i.e. consisting of parts separated by a separator string. Usually the separator string
 * is the forward slash. As an example, when the name is "sub1/sub2/ref", sub1 and sub2 must be pointing to a Context, whereas
 * ref can be either another Context, or a regular Object.
 * </p>
 * <p>
 * The context names support <i>absolute</i> addresses that start with a forward slash. For an absolute address, the context
 * tree is traversed to the root, and the (compound) name is resolved from the root context. For <i>relative</i> addresses that
 * start with any character but the forward slash, the (compound) name is resolved from the current context.
 * </p>
 * <p>
 * Values that are stored in the context are required to be Serializable. Both the FileContext and the RemoteContext need to
 * serialize the values when storing them, so serializability of the Context's content is a necessary property.
 * </p>
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ContextInterface extends EventProducerInterface, Serializable
{
    /** The default root sign for a context. */
    public static final String ROOT = "/";

    /** The default separator sign for a context. */
    public static final String SEPARATOR = "/";

    /** The replacement string for the separator, in case it is part of a name. */
    public static final String REPLACE_SEPARATOR = "#";

    /**
     * OBJECT_ADDED_EVENT is fired whenever an object is added to the Context. The implementations of the ContextInterface in
     * the nl.tudelft.simulation.naming.context package take care of firing this event. <br>
     * Payload: Object[] - the absolute path of the context in which the object was bound, the relative key in the context, and
     * the bound object.
     */
    public static final EventType OBJECT_ADDED_EVENT = new EventType(new MetaData("OBJECT_ADDED", "Object added to context",
            new ObjectDescriptor("path", "absolute path in context", String.class),
            new ObjectDescriptor("key", "relative key in the context", String.class),
            new ObjectDescriptor("object", "bound object", Object.class)));

    /**
     * OBJECT_REMOVED_EVENT is fired whenever an object is removed from the Context. The implementations of the ContextInterface
     * in the nl.tudelft.simulation.naming.context package take care of firing this event. <br>
     * Payload: Object[] - the absolute path of the context in which the object was bound, the relative key in the context, and
     * the removed object.
     */
    public static final EventType OBJECT_REMOVED_EVENT = new EventType(new MetaData("OBJECT_REMOVED",
            "Object removed from context", new ObjectDescriptor("path", "absolute path in context", String.class),
            new ObjectDescriptor("key", "relative key in the context", String.class),
            new ObjectDescriptor("object", "bound object", Object.class)));

    /**
     * OBJECT_CHANGED_EVENT can be fired whenever an object present in the Context is changed. The object making changes has to
     * fire this Event itself, because the Context has no way of knowing that the content of the object has changed. The method
     * fireObjectChangedEvent(...) in the ContextInterface can be used to fire this event. <br>
     * Payload: Object[] - the absolute path of the context in which the object is bound, the relative key in the context, the
     * object after change.
     */
    public static final EventType OBJECT_CHANGED_EVENT = new EventType(new MetaData("OBJECT_CHANGED",
            "Object in context changed", new ObjectDescriptor("path", "absolute path in context", String.class),
            new ObjectDescriptor("key", "relative key in the context", String.class),
            new ObjectDescriptor("object", "changed object", Object.class)));

    /**
     * Retrieves the atomic name of this context.
     * @return String; the atomic name of this context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    String getAtomicName() throws RemoteException;

    /**
     * Retrieves the parent context for this context, or null when this is the root context.
     * @return ContextInterface; parent context for this context, or null when this is the root context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    ContextInterface getParent() throws RemoteException;

    /**
     * Retrieves the root context for this context, or the current context when this is the root context.
     * @return ContextInterface; the root context for this context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    ContextInterface getRootContext() throws RemoteException;

    /**
     * Returns the absolute path for this context, or an empty string when this is the root context.
     * @return String; the absolute path for this context, or an empty string when this is the root context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    String getAbsolutePath() throws RemoteException;

    /**
     * Retrieves the named object from the Context. The name may be a compound name where parts are separated by separation
     * strings indicating subcontexts. All intermediate subcontexts must exist. When name is the empty String or "/", the
     * current Context is returned.
     * @param name String; the name of the object to look up; may be a compound name
     * @return Object; the Context or Object indicated by the name
     * @throws NamingException as a placeholder overarching exception
     * @throws NameNotFoundException if an intermediate context or the reference does not exist
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    Object get(String name) throws NamingException, RemoteException;

    /**
     * Retrieves the named object from the Context. The key is <b>not</b> compound. Key cannot be empty or contain "/".
     * @param key String; the name of the object to look up; NOT a compound name
     * @return Object; the Context or Object indicated by the key
     * @throws NamingException when key is the empty string or when key contains "/"
     * @throws NameNotFoundException if the key does not exist in the current context
     * @throws NullPointerException when key is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    Object getObject(String key) throws NamingException, RemoteException;

    /**
     * Indicates whether an object with the given name has been registered in the Context. The name may be a compound name where
     * parts are separated by separation strings indicating subcontexts. All intermediate subcontexts must exist. When name is
     * the empty String or "/", the method returns true.
     * @param name String; the name of the object to look up; may be a compound name
     * @return boolean; whether an object with the given name has been registered
     * @throws NamingException as a placeholder overarching exception
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    boolean exists(String name) throws NamingException, RemoteException;

    /**
     * Indicates whether the key has been registered in the current Context. The name is <b>not</b> compound. When name is the
     * empty String or "/", the method returns an exception.
     * @param key String; the name of the object to look up; NOT compound
     * @return boolean; whether an object with the given key has been registered in the current context
     * @throws NamingException when key is the empty string or when key contains "/"
     * @throws NullPointerException when key is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    boolean hasKey(String key) throws NamingException, RemoteException;

    /**
     * Indicates whether the object has been registered (once or more) in the current Context. The object may be null.
     * @param object Object; the object to look up; mey be null
     * @return boolean; whether an object with the given key has been registered once or more in the current context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    boolean hasObject(Object object) throws RemoteException;

    /**
     * Indicates whether the current context is empty.
     * @return boolean; whether the current context is empty
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    boolean isEmpty() throws RemoteException;

    /**
     * Binds an object into the Context using the name. The name may be a compound name where parts are separated by separation
     * strings indicating subcontexts. All intermediate subcontexts must already exist. The object will be registered using the
     * terminal atomic reference in the deepest subcontext provided. Name cannot be empty or "/". An OBJECT_ADDED_EVENT is fired
     * containing an object array with a pointer to the context, the relative key, and the object when a new binding has taken
     * place.
     * @param name String; the name under which the object will be stored; may be a compound name with the terminal reference
     *            indicating the key under which the object will be stored
     * @param object Object; the Context or Object to be stored into the given context; a null object is allowed
     * @throws NamingException when the reference is "/" or empty
     * @throws NameAlreadyBoundException if name is already bound to an object
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void bind(String name, Object object) throws NamingException, RemoteException;

    /**
     * Binds an object into this Context using the key. The key is <b>not </b> compound. The key cannot be empty or "/". An
     * OBJECT_ADDED_EVENT is fired containing an object array with a pointer to the context, the relative key, and the object
     * when a new binding has taken place.
     * @param key String; the key under which the object will be stored; NOT a compound name
     * @param object Object; the Context or Object to be stored into this Context using the given name; a null object is allowed
     * @throws NamingException when key is the empty string or when key contains "/"
     * @throws NameAlreadyBoundException if key is already bound to an object
     * @throws NullPointerException when key is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void bindObject(String key, Object object) throws NamingException, RemoteException;

    /**
     * Binds an object into the Context using the toString() method of the object as the key. All "/" characters in the
     * toString() result will be replaced by "#" characters. An OBJECT_ADDED_EVENT is fired containing an object array with a
     * pointer to the context, the relative key, and the object when a new binding has taken place.
     * @param object Object; the Context or Object to be stored into this Context using the toString() result as the key; a null
     *            object is not allowed
     * @throws NamingException when the toString() of the object results in an empty string
     * @throws NameAlreadyBoundException if key is already bound to an object
     * @throws NullPointerException when object is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void bindObject(Object object) throws NamingException, RemoteException;

    /**
     * Removes the binding for an object in the Context with the given name. The name may be a compound name where parts are
     * separated by separation strings indicating subcontexts. Name cannot be empty or "/". It is not a problem when there is no
     * object registered with the given name. An OBJECT_REMOVED_EVENT is fired containing an object array with a pointer to the
     * context, the relative key, and the removed object when a binding has been removed.
     * @param name String; the name of the object that has to be removed; may be a compound name with the terminal reference
     *            indicating the key under which the object is stored
     * @throws NamingException when the reference is "/" or empty
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void unbind(String name) throws NamingException, RemoteException;

    /**
     * Removes the binding for an object in this Context with the given key. The key is <b>not</b> compound. The key cannot be
     * empty or "/". It is not a problem when there is no object registered with the given key. An OBJECT_REMOVED_EVENT is fired
     * containing an object array with a pointer to the context, the relative key, and the removed object when a binding has
     * been removed.
     * @param key String; the key of the object that has to be removed; NOT a compound name
     * @throws NamingException when key is the empty string or when key contains "/"
     * @throws NullPointerException when key is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void unbindObject(String key) throws NamingException, RemoteException;

    /**
     * Replaces an object in the Context with the given name, or registers the object when a registration with the name does not
     * exist yet. The name may be a compound name where parts are separated by separation strings indicating subcontexts. All
     * intermediate subcontexts must already exist. The object will be (re)registered using the terminal atomic reference in the
     * deepest subcontext provided. Name cannot be empty or "/". An OBJECT_REMOVED_EVENT is fired containing an object array
     * with a pointer to the context, the relative key, and the removed object when a binding had to be removed, followed by an
     * OBJECT_ADDED_EVENT containing an object array with a pointer to the context, the key, and the new object when a new
     * binding has taken place.
     * @param name String; the name of the object to be replaced, or under which the object will be stored; may be a compound
     *            name with the terminal reference indicating the key under which the object will be stored
     * @param object Object; the Context or Object to be replaced or stored into this Context using the given name; a null
     *            object is allowed
     * @throws NamingException when the reference is "/" or empty
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void rebind(String name, Object object) throws NamingException, RemoteException;

    /**
     * Replaces an object in this Context with the given key, or registers the object when a registration with the key does not
     * exist yet. The key is <b>not</b> a compound name. Name cannot be empty or "/". An OBJECT_REMOVED_EVENT is fired
     * containing an object array with a pointer to the context, the relative key, and the removed object when a binding had to
     * be removed, followed by an OBJECT_ADDED_EVENT containing an object array with a pointer to the context, the key, and the
     * new object when a new binding has taken place.
     * @param key String; the key of the object to be replaced, or under which the object will be stored; NOT a compound name
     * @param object Object; the Context or Object to be replaced or stored into this Context using the given name; a null
     *            object is allowed
     * @throws NamingException when key is the empty string or when key contains "/"
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void rebindObject(String key, Object object) throws NamingException, RemoteException;

    /**
     * Replaces the name under which an object has been registered in the Context. Both names are relative to the current
     * Context, and may be compound names where parts are separated by separation strings indicating subcontexts. All
     * intermediate subcontexts of the new name must already exist. The object will be deregistered under the old name, and
     * reregistered using the terminal atomic reference in the deepest subcontext of the newName. Nether the oldName, nor the
     * newName can be empty or "/". An OBJECT_REMOVED_EVENT is fired containing an object array with a pointer to the old
     * context, the old relative key, and the object when a binding had to be removed, followed by an OBJECT_ADDED_EVENT
     * containing an object array with a pointer to the new context, the new relative key, and the object when a new binding has
     * taken place. When the new name is illegal or occupied, the object will not be renamed.
     * @param oldName String; the (compound) name of the object to be moved to a new location
     * @param newName String; the (compound) name with the terminal reference indicating where the object will be stored
     * @throws NamingException when the reference of oldName or newName is "/" or empty
     * @throws NameNotFoundException when an intermediate context of oldName or newName does not exist, or when there is no
     *             registration at the oldName reference
     * @throws NameAlreadyBoundException if newName is already bound to an object
     * @throws NullPointerException when oldName or newName is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void rename(String oldName, String newName) throws NamingException, RemoteException;

    /**
     * Creates and binds a new context into the current Context using the name. The name may be a compound name where parts are
     * separated by separation strings indicating subcontexts. All intermediate subcontexts that do not yet exist, will be
     * created. The new context will be registered using the terminal atomic reference in the deepest subcontext provided. Name
     * cannot be empty or "/". On success, OBJECT_ADDED_EVENTs are fired for the created context and each new intermediate
     * context, containing an object array with an object array with a pointer to the embedding context, the local key in the
     * embedding context, and the newly created (intermediate) context.
     * @param name String; the name under which the new context will be stored; may be a compound name with the terminal
     *            reference indicating the key under which the new context will be stored
     * @return ContextInterface; a pointer to the newly created subcontext
     * @throws NamingException when the terminal reference is "/" or empty
     * @throws NameNotFoundException when an intermediate context does not exist
     * @throws NameAlreadyBoundException if name is already bound to an object or context
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    ContextInterface createSubcontext(String name) throws NamingException, RemoteException;

    /**
     * Removes the binding for an existing, empty subcontext with the given name, and recursively unbinds all objects and
     * contexts in the context indicated by the name. The name may be a compound name where parts are separated by separation
     * strings indicating subcontexts. Name cannot be empty or "/". A context has to be registered with the given name. For all
     * removed objects and contexts, OBJECT_REMOVED_EVENT events are fired containing an object array with a pointer to the
     * context from which content has been removed, the key of the binding that has been removed, and the removed object. The
     * OBJECT_REMOVED_EVENT events are fired in depth-first order.
     * @param name String; the name of the object that has to be removed; may be a compound name with the terminal reference
     *            indicating the key under which the object is stored
     * @throws NamingException when the reference is "/" or empty
     * @throws NotContextException if the name is bound but does not name a context
     * @throws NameNotFoundException when an intermediate context does not exist, or when no object is registered with the
     *             terminating atomic reference
     * @throws NullPointerException when name is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void destroySubcontext(String name) throws NamingException, RemoteException;

    /**
     * Fire an OBJECT_CHANGED_EVENT for an object within the current context. Look up if the the object exists using the
     * toString() method of the object as the key. All "/" characters in the toString() result will be replaced by "#"
     * characters. When the object does not exist directly in the context, a NameNotFoundException is thrown.
     * @param object Object; the object that has changed and for which an event should be fired
     * @throws NamingException on general error in the method, e.g., a problem with remote connections
     * @throws NameNotFoundException when no object is registered with object.toString() as the key
     * @throws NullPointerException when object is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void fireObjectChangedEventValue(final Object object)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException;

    /**
     * Fire an OBJECT_CHANGED_EVENT for an object with the given key in the current context. If the the key does not exist
     * directly in the context, a NameNotFoundException is thrown. The key should not be the empty String or contain "/"
     * characters.
     * @param key String; the key within the context of the object that has changed and for which an event should be fired
     * @throws NamingException when key is the empty string or when key contains "/", or on a general error in the method, e.g.,
     *             a problem with remote connections
     * @throws NameNotFoundException when no object is registered with the key in the given context
     * @throws NullPointerException when key is null
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void fireObjectChangedEventKey(final String key)
            throws NameNotFoundException, NullPointerException, NamingException, RemoteException;

    /**
     * Returns a set of registered keys in the current context.
     * @return Set&lt;String&gt;; a set of registered keys in the current context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    Set<String> keySet() throws RemoteException;

    /**
     * Returns a (raw) collection of registered values in the current context.
     * @return Collection&lt;Object&gt;; a raw set of registered objects in the current context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    Collection<Object> values() throws RemoteException;

    /**
     * Returns a (raw) map of bindings in the current context, mapping the name on the registered objects. Both regular objects
     * and subcontexts are returned. Mappings to null objects can exist.
     * @return Map&lt;String, Object&gt;; a map of registered names and their bound (possibly null) object in the current
     *         context
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    Map<String, Object> bindings() throws RemoteException;

    /**
     * Check whether a circular reference would occur when the object would be inserted into the current context. Insertion
     * potentially causes a problem when the object to be inserted is a Context.
     * @param newObject the object to be inserted
     * @throws NamingException when a circular reference would occur
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void checkCircular(Object newObject) throws NamingException, RemoteException;

    /**
     * Closes the context and removes all content from the context. No events will be fired that the content of the context has
     * been removed. Some contexts, such as the FileContext could have to be really closed as well.
     * @throws NamingException when a problem occurs during closing (e.g., of a FileContext)
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    void close() throws NamingException, RemoteException;

    /**
     * Return a String with the hierarchical content of the Context in case verbose is true; otherwise return the atomic name.
     * @param verbose boolean; whether the information is exhaustive or very brief
     * @return String; formatted content of the context when verbose is true; otherwise the atomic name
     * @throws RemoteException on a network error when the Context is used over RMI
     */
    String toString(final boolean verbose) throws RemoteException;
}
