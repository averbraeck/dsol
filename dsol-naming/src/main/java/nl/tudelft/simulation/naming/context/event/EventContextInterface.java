package nl.tudelft.simulation.naming.context.event;

import java.rmi.RemoteException;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.djutils.event.EventListenerInterface;
import org.djutils.event.ref.ReferenceType;

/**
 * EventContextInterface specifies the subscription methods for a part of the context tree. The ContextScope in each method
 * indicates for which part of the tree notifications will be triggered. The listeners will be subscribed to three events for
 * the part of the context: OBJECT_ADDED_EVENT, OBJECT_REMOVED_EVENT and OBJECT_CHANGED_EVENT.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface EventContextInterface
{
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
     * @see org.djutils.event.ref.WeakReference
     */
    boolean addListener(EventListenerInterface listener, String absolutePath, ContextScope contextScope) throws RemoteException,
            NameNotFoundException, InvalidNameException, NotContextException, NamingException, NullPointerException;

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
    boolean addListener(EventListenerInterface listener, String absolutePath, ContextScope contextScope,
            ReferenceType referenceType) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException;

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
    boolean addListener(EventListenerInterface listener, String absolutePath, ContextScope contextScope, int position)
            throws RemoteException, NameNotFoundException, InvalidNameException, NotContextException, NamingException,
            NullPointerException;

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
    boolean addListener(EventListenerInterface listener, String absolutePath, ContextScope contextScope, int position,
            ReferenceType referenceType) throws RemoteException, NameNotFoundException, InvalidNameException,
            NotContextException, NamingException, NullPointerException;

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
    boolean removeListener(EventListenerInterface listener, String absolutePath, ContextScope contextScope)
            throws RemoteException, InvalidNameException, NullPointerException;

}
