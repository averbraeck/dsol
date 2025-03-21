package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * Queue implements the queueing behavior when requests (on behalf of entities or not) have to wait for a resource.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Queue<T extends Number & Comparable<T>> extends FlowObject<T, Queue<T>> implements Iterable<CapacityRequest<T>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The sorted set of capacity requests. */
    private SortedSet<CapacityRequest<T>> queue = new TreeSet<>(new FcfsPriorityRequestComparator<T>());

    /** The counter for a unique id for the requests. */
    private long counter = 0L;

    /** QUEUE_LENGTH_EVENT fired on changes in queue length. */
    public static final EventType QUEUE_LENGTH_EVENT = new EventType(new MetaData("QUEUE_LENGTH_EVENT", "Queue length changed",
            new ObjectDescriptor("newQueueLength", "new queue length", Integer.class)));

    /** QUEUE_TIME_EVENT is fired wwhen a request is granted and provides the waiting time (which can be 0). */
    public static final EventType QUEUE_WAITING_TIME_EVENT = new EventType(new MetaData("QUEUE_WAITING_TIME_EVENT",
            "Queue waiting time", new ObjectDescriptor("queue waiting time", "queue waiting time", Number.class)));

    /**
     * Create a new queue, with a priority-based, first-come-first-served sorting mechanism.
     * @param id the id of the queue
     * @param simulator the simulator
     */
    public Queue(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Set a new comparator (sorting mechanism) for the queue. The method has been designed in such a way that this can be
     * applied during the simulation.
     * @param comparator the new comparator
     * @return the queue object for method chaining
     */
    public Queue<T> setComparator(final Comparator<CapacityRequest<T>> comparator)
    {
        SortedSet<CapacityRequest<T>> newQueue = new TreeSet<>(comparator);
        newQueue.addAll(this.queue);
        this.queue = newQueue;
        return this;
    }

    /**
     * Add a request for capacity to the queue.
     * @param amount double; the requested amount
     * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @param priority int; the priority of the request
     */
    public void add(final double amount, final CapacityRequestor<T> requestor, final int priority)
    {
        var request = new CapacityRequest.DoubleCapacity<>(this.counter++, requestor, amount, priority,
                getSimulator().getSimulatorTime());
        this.queue.add(request);
        this.fireTimedEvent(QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
    }

    /**
     * Add a request for capacity to the queue.
     * @param amount int; the requested amount
     * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @param priority int; the priority of the request
     */
    public void add(final int amount, final CapacityRequestor<T> requestor, final int priority)
    {
        var request = new CapacityRequest.IntegerCapacity<>(this.counter++, requestor, amount, priority,
                getSimulator().getSimulatorTime());
        this.queue.add(request);
        this.fireTimedEvent(QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
    }

    /**
     * Return the first request in the queue, without removing it.
     * @return the first request in the queue, without removing it
     */
    public CapacityRequest<T> peek()
    {
        return this.queue.first();
    }
    
    /**
     * Remove the first request in the queue.
     * @return the first request in the queue, after removing it
     */
    public CapacityRequest<T> remove()
    {
        var request = peek();
        this.queue.remove(request);
        this.fireTimedEvent(QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
        return request;
    }

    @Override
    public Iterator<CapacityRequest<T>> iterator()
    {
        return this.queue.iterator();
    }
    
    /**
     * the FCFS RequestComparator. This comparator ignores priority, and sorts on the time-of-entry with the id as a tie
     * breaker, where an earlier time comes first.
     * @param <T> the simulation time type to use.
     */
    public static class FcfsRequestComparator<T extends Number & Comparable<T>> implements Comparator<CapacityRequest<T>>
    {
        @Override
        public int compare(final CapacityRequest<T> arg0, final CapacityRequest<T> arg1)
        {
            int result = Double.compare(arg0.getQueueEntryTime().doubleValue(), arg1.getQueueEntryTime().doubleValue());
            if (result == 0)
                result = Long.compare(arg0.getId(), arg1.getId());
            return result;
        }
    }

    /**
     * the LCFS RequestComparator. This comparator ignores priority, and sorts on the time-of-entry with the id as a tie
     * breaker, where a later time comes first.
     * @param <T> the simulation time type to use.
     */
    public static class LcfsRequestComparator<T extends Number & Comparable<T>> implements Comparator<CapacityRequest<T>>
    {
        @Override
        public int compare(final CapacityRequest<T> arg0, final CapacityRequest<T> arg1)
        {
            int result = Double.compare(arg1.getQueueEntryTime().doubleValue(), arg0.getQueueEntryTime().doubleValue());
            if (result == 0)
                result = Long.compare(arg1.getId(), arg0.getId());
            return result;
        }
    }

    /**
     * the FCFS PriorityRequestComparator. This comparator first checks on priority, then on the time-of-entry with the id as a
     * tie breaker, where an earlier time comes first.
     * @param <T> the simulation time type to use.
     */
    public static class FcfsPriorityRequestComparator<T extends Number & Comparable<T>>
            implements Comparator<CapacityRequest<T>>
    {
        @Override
        public int compare(final CapacityRequest<T> arg0, final CapacityRequest<T> arg1)
        {
            int result = Integer.compare(arg1.getPriority(), arg0.getPriority());
            if (result == 0)
                result = Double.compare(arg0.getQueueEntryTime().doubleValue(), arg1.getQueueEntryTime().doubleValue());
            if (result == 0)
                result = Long.compare(arg0.getId(), arg1.getId());
            return result;
        }
    }

    /**
     * the LCFS PriorityRequestComparator. This comparator first checks on priority, then on the time-of-entry with the id as a
     * tie breaker, where a later time comes first.
     * @param <T> the simulation time type to use.
     */
    public static class LcfsPriorityRequestComparator<T extends Number & Comparable<T>>
            implements Comparator<CapacityRequest<T>>
    {
        @Override
        public int compare(final CapacityRequest<T> arg0, final CapacityRequest<T> arg1)
        {
            int result = Integer.compare(arg1.getPriority(), arg0.getPriority());
            if (result == 0)
                result = Double.compare(arg1.getQueueEntryTime().doubleValue(), arg0.getQueueEntryTime().doubleValue());
            if (result == 0)
                result = Long.compare(arg1.getId(), arg0.getId());
            return result;
        }
    }

}
