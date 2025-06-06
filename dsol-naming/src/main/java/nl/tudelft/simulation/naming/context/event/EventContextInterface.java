package nl.tudelft.simulation.naming.context.event;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventListener;
import org.djutils.event.reference.ReferenceType;

/**
 * EventContextInterface specifies the subscription methods for a part of the context tree. The ContextScope in each method
 * indicates for which part of the tree notifications will be triggered. The listeners will be subscribed to three events for
 * the part of the context: OBJECT_ADDED_EVENT, OBJECT_REMOVED_EVENT and OBJECT_CHANGED_EVENT.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface EventContextInterface extends Remote
{
    /**
     * Add a listener for the provided scope as strong reference to the BEGINNING of a queue of listeners.
     * @param listener the listener which is interested at events of eventType.
     * @param absolutePath the absolute path of the context or object to subscribe to
     * @param contextScope the part of the tree that the listener is aimed at (current node, current node and keys, subtree).
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
    boolean addListener(EventListener listener, String absolutePath, ContextScope contextScope) throws RemoteException,
            NameNotFoundException, InvalidNameException, NotContextException, NamingException, NullPointerException;

    /**
     * Add a listener for the provided scope to the BEGINNING of a queue of listeners.
     * @param listener the listener which is interested at events of eventType.
     * @param absolutePath the absolute path of the context or object to subscribe to
     * @param contextScope the part of the tree that the listener is aimed at (current node, current node and keys, subtree).
     * @param referenceType whether the listener is added as a strong or as a weak reference.
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
    boolean addListener(EventListener listener, String absolutePath, ContextScope contextScope, ReferenceType referenceType)
            throws RemoteException, NameNotFoundException, InvalidNameException, NotContextException, NamingException,
            NullPointerException;

    /**
     * Add a listener for the provided scope as strong reference to the specified position of a queue of listeners.
     * @param listener the listener which is interested at events of eventType.
     * @param absolutePath the absolute path of the context or object to subscribe to
     * @param contextScope the part of the tree that the listener is aimed at (current node, current node and keys, subtree).
     * @param position the position of the listener in the queue.
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
    boolean addListener(EventListener listener, String absolutePath, ContextScope contextScope, int position)
            throws RemoteException, NameNotFoundException, InvalidNameException, NotContextException, NamingException,
            NullPointerException;

    /**
     * Add a listener for the provided scope to the specified position of a queue of listeners.
     * @param listener which is interested at certain events,
     * @param absolutePath the absolute path of the context or object to subscribe to
     * @param contextScope the part of the tree that the listener is aimed at (current node, current node and keys, subtree).
     * @param position the position of the listener in the queue
     * @param referenceType whether the listener is added as a strong or as a weak reference.
     * @return the success of adding the listener. If a listener was already added or an illegal position is provided false is
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
    boolean addListener(EventListener listener, String absolutePath, ContextScope contextScope, int position,
            ReferenceType referenceType) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException;

    /**
     * Remove the subscription of a listener for the provided scope for a specific event.
     * @param listener which is no longer interested.
     * @param absolutePath the absolute path of the context or object to subscribe to
     * @param contextScope ContextScope;the scope which is of no interest any more.
     * @return the success of removing the listener. If a listener was not subscribed false is returned.
     * @throws InvalidNameException when the path does not start with a slash
     * @throws NullPointerException when one of the arguments is null
     * @throws RemoteException if a network connection failure occurs
     */
    boolean removeListener(EventListener listener, String absolutePath, ContextScope contextScope)
            throws RemoteException, InvalidNameException, NullPointerException;

}
