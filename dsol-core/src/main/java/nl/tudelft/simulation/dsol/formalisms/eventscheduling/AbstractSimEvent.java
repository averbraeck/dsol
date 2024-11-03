package nl.tudelft.simulation.dsol.formalisms.eventscheduling;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import nl.tudelft.simulation.dsol.SimRuntimeException;

/**
 * The AbstractSimEvent forms the basement for SimEvents and defines a compare method by which eventLists can compare priority
 * of the event.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the type of simulation time, e.g. Double, Long or Duration
 * @since 1.5
 */
public abstract class AbstractSimEvent<T extends Number & Comparable<T>>
        implements SimEventInterface<T>, Serializable
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** a counter counting the number of constructed simEvents. */
    private static AtomicLong constructorCounter = new AtomicLong();

    /** absoluteExecutionTime reflects the time at which the event is scheduled. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T absoluteExecutionTime;

    /** priority reflects the priority of the event. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short priority = SimEvent.NORMAL_PRIORITY;

    /** the id used in compare statements. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected long id = 0L;

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime T; reflects the time the event has to be executed.
     */
    public AbstractSimEvent(final T executionTime)
    {
        this(executionTime, SimEvent.NORMAL_PRIORITY);
    }

    /**
     * The constructor of the event stores the time the event must be executed and the object and method to invoke.
     * @param executionTime T; reflects the time the event has to be executed.
     * @param priority short; reflects the priority of the event
     */
    public AbstractSimEvent(final T executionTime, final short priority)
    {
        this.absoluteExecutionTime = executionTime;
        if (priority < SimEvent.MIN_PRIORITY - 1 || priority > SimEvent.MAX_PRIORITY + 1)
        {
            throw new IllegalArgumentException("priority must be between [" + SimEvent.MIN_PRIORITY + ".."
                    + SimEvent.MAX_PRIORITY + "]");
        }
        this.priority = priority;
        this.id = AbstractSimEvent.constructorCounter.incrementAndGet();
    }

    @Override
    public abstract void execute() throws SimRuntimeException;

    @Override
    public T getAbsoluteExecutionTime()
    {
        return this.absoluteExecutionTime;
    }

    @Override
    public short getPriority()
    {
        return this.priority;
    }

    @Override
    public long getId()
    {
        return this.id;
    }

}
