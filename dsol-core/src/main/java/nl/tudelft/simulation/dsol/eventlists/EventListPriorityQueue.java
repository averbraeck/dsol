package nl.tudelft.simulation.dsol.eventlists;

import java.util.Iterator;
import java.util.PriorityQueue;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * A RedBlackTree implementation of the eventlistInterface. This implementation is based on Java's TreeSet.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the type of simulation time, e.g. SimTimeCalendarLong or Double or DoubleUnit.
 * @since 1.5
 */
public class EventListPriorityQueue<T extends Number & Comparable<T>> implements EventListInterface<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** The embedded event list. */
    private PriorityQueue<SimEventInterface<T>> eventList;

    /**
     * Constructs a new <code>RedBlackTree</code>.
     */
    public EventListPriorityQueue()
    {
        this.eventList = new PriorityQueue<>();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized SimEventInterface<T> removeFirst()
    {
        return this.eventList.poll();
    }

    /** {@inheritDoc} */
    @Override
    public SimEventInterface<T> first()
    {
        return this.eventList.peek();
    }

    /** {@inheritDoc} */
    @Override
    public void add(final SimEventInterface<T> event)
    {
        this.eventList.add(event);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(final SimEventInterface<T> event)
    {
        return this.eventList.contains(event);
    }

    /** {@inheritDoc} */
    @Override
    public void clear()
    {
        this.eventList.clear();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty()
    {
        return this.eventList.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<SimEventInterface<T>> iterator()
    {
        return this.eventList.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public boolean remove(final SimEventInterface<T> event)
    {
        return this.eventList.remove(event);
    }

    /** {@inheritDoc} */
    @Override
    public int size()
    {
        return this.eventList.size();
    }

}
