package nl.tudelft.simulation.dsol.eventlists;

import java.io.Serializable;
import java.util.Iterator;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * The EventListInterface defines the required methods for discrete event lists. A number of competitive algoritms can be used
 * to implement such eventlist. Among these implementations are the Red-Black, the SplayTree, and others.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> The time type, e.g., Double, Long, Duration
 * @since 1.5
 */
public interface EventListInterface<T extends Number & Comparable<T>> extends Serializable, Iterable<SimEventInterface<T>>
{
    /**
     * Add an event to the event list.
     * @param event SimEventInterface&lt;T&gt;; the event to add
     */
    void add(SimEventInterface<T> event);

    /**
     * Return whether this event list contains the given event.
     * @param event SimEventInterface&lt;T&gt;; the event to search for
     * @return boolean; whether the event list contains the given event
     */
    boolean contains(SimEventInterface<T> event);

    /**
     * Clear the event list.
     */
    void clear();

    /**
     * Return whether the event list is empty.
     * @return boolean; whether the event list is empty
     */
    boolean isEmpty();

    /**
     * Provide an iterator to loop over the event list.
     * @return Iterator&lt;SimEventInterface&lt;T&gt;&gt;; an iterator to loop over the event list
     */
    @Override
    Iterator<SimEventInterface<T>> iterator();

    /**
     * Remove the given event from the event list.
     * @param event SimEventInterface&lt;T&gt;; the event to remove
     * @return boolean to indicate success of the removal operation
     */
    boolean remove(SimEventInterface<T> event);

    /**
     * Return the number of events on the event list.
     * @return int; the number of events on the event list
     */
    int size();

    /**
     * Returns the first event (lowest time / priority) of the event list. The method returns null when the event list is empty.
     * @return SimEventInterface&lt;T&gt;; the first element currently in this event list, or null when the list is empty
     */
    SimEventInterface<T> first();

    /**
     * Returns and removes the first event (lowest time / priority) of the event list. The method returns null when the event
     * list is empty.
     * @return SimEventInterface&lt;T&gt;; the first element in this event list before removal, or null when the list is empty
     */
    SimEventInterface<T> removeFirst();

}
