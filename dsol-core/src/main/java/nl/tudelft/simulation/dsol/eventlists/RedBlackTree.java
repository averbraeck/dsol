package nl.tudelft.simulation.dsol.eventlists;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * A RedBlackTree implementation of the eventlistInterface. This implementation is based on Java's TreeSet. This implementation
 * embeds the data structure in the event list instead of extending it (extension has the chance that future implementations can
 * break the EventList, and that the user can use functions that do not belong to an EventList).
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraec</a>
 * @param <T> The time type, e.g., Double, Long, Duration
 * @since 1.5
 */
public class RedBlackTree<T extends Number & Comparable<T>> implements EventListInterface<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** The embedded event list. */
    private TreeSet<SimEventInterface<T>> eventList;

    /**
     * Constructs a new <code>RedBlackTree</code>.
     */
    public RedBlackTree()
    {
        this.eventList = new TreeSet<>();
    }

    @Override
    public synchronized SimEventInterface<T> removeFirst()
    {
        SimEventInterface<T> first = first();
        if (first != null)
        {
            this.eventList.remove(first);
        }
        return first;
    }

    @Override
    public SimEventInterface<T> first()
    {
        try
        {
            return this.eventList.first();
        }
        catch (NoSuchElementException noSuchElementException)
        {
            return null;
        }
    }

    @Override
    public void add(final SimEventInterface<T> event)
    {
        this.eventList.add(event);
    }

    @Override
    public boolean contains(final SimEventInterface<T> event)
    {
        return this.eventList.contains(event);
    }

    @Override
    public void clear()
    {
        this.eventList.clear();
    }

    @Override
    public boolean isEmpty()
    {
        return this.eventList.isEmpty();
    }

    @Override
    public Iterator<SimEventInterface<T>> iterator()
    {
        return this.eventList.iterator();
    }

    @Override
    public boolean remove(final SimEventInterface<T> event)
    {
        return this.eventList.remove(event);
    }

    @Override
    public int size()
    {
        return this.eventList.size();
    }

}
