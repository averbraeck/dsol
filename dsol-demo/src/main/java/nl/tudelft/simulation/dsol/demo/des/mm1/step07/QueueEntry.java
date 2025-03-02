package nl.tudelft.simulation.dsol.demo.des.mm1.step07;

/**
 * The QueueEntry class for the M/M/1 Discrete Event Simulation (DES) model example that stores waiting Entity instances. The
 * WueueEntry class stores an entity plus the time when the entity entered the queue, so we can calculate waiting times. See
 * <a href= "https://simulation.tudelft.nl/dsol/manual/1-getting-started/example-event/">DES Model Example</a> for more
 * information.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <E> the entity type in the queue
 */
public class QueueEntry<E>
{
    /** time of queue entry for statistics. */
    private final double queueInTime;

    /** entity in the queue. */
    private final E entity;

    /**
     * A combination of an entity and a time when the entity entered the queue.
     * @param entity E; the entity to insert in the queue
     * @param queueInTime double; the time it gets into the queue
     */
    public QueueEntry(final E entity, final double queueInTime)
    {
        this.entity = entity;
        this.queueInTime = queueInTime;
    }

    /**
     * Return the time when the entity entered the queue.
     * @return double; the time when the entity entered the queue.
     */
    public double getQueueInTime()
    {
        return this.queueInTime;
    }

    /**
     * Return the entity that is in the queue.
     * @return E; the entity that is in the queue
     */
    public E getEntity()
    {
        return this.entity;
    }

    @Override
    public String toString()
    {
        return "QueueEntry [queueInTime=" + this.queueInTime + ", entity=" + this.entity + "]";
    }
}
