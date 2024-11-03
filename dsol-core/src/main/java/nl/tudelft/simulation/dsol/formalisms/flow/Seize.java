package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.formalisms.ResourceRequestorInterface;
import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Seize flow block requests a resource and keeps the entity within the flow block's queue until the resource is actually
 * claimed. Note that the Seize block has a queue in which the Entity is waiting, while the Resource has a queue in which the
 * request is waiting. This sounds like we store the same information twice. This is, however, not the case. (1) Multiple Seize
 * blocks can share the same Resource, each holding their own entities that make the request, where the total set of requests is
 * stored at the Resource. (2) A Seize could potentially request access to two resources, where the entity is held at the Seize
 * block until both are available. Each Resource keeps and grants its own requests. Since there nam be an n:m relationship
 * between Seize blocks and Resources, each have their own queue, with its own sorting mechanism (ideally the same).
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Seize<T extends Number & Comparable<T>> extends FlowObject<T> implements ResourceRequestorInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140911L;

    /** QUEUE_LENGTH_EVENT is fired when the queue length is changed. */
    public static final EventType QUEUE_LENGTH_EVENT = new EventType(new MetaData("QUEUE_LENGTH_EVENT", "Queue length",
            new ObjectDescriptor("queueLength", "Queue length", Integer.class)));

    /** DELAY_TIME is fired when a new delayTime is computed. */
    public static final EventType DELAY_TIME = new EventType(new MetaData("DELAY_TIME", "Delay time",
            new ObjectDescriptor("delayTime", "Delay time (as a double)", Double.class)));

    /** queue refers to the list of waiting requestors. */
    private List<Request<T>> queue = Collections.synchronizedList(new ArrayList<Request<T>>());

    /** requestedCapacity is the amount of resource requested on the resource. */
    private double requestedCapacity = Double.NaN;

    /** resource on which the capacity is requested. */
    private Resource<T> resource;

    /**
     * Constructor for Seize flow object.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which behavior is scheduled
     * @param resource Resource&lt;T&gt;; which is claimed
     */
    public Seize(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource)
    {
        this(id, simulator, resource, 1.0);
    }

    /**
     * Constructor for Seize flow object.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which behavior is scheduled
     * @param resource Resource&lt;T&gt;; which is claimed
     * @param requestedCapacity double; is the amount which is claimed by the seize
     */
    public Seize(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource,
            final double requestedCapacity)
    {
        super(id, simulator);
        if (requestedCapacity < 0.0)
        {
            throw new IllegalArgumentException("requestedCapacity cannot < 0.0");
        }
        this.requestedCapacity = requestedCapacity;
        this.resource = resource;
    }

    /**
     * Receive an object that requests an amount of units from a resource.
     * @param entity Entity&lt;T&gt;; the object
     * @param pRequestedCapacity double; the requested capacity
     */
    public synchronized void receiveObject(final Entity<T> entity, final double pRequestedCapacity)

    {
        super.receiveEntity(entity);
        Request<T> request = new Request<T>(entity, pRequestedCapacity, getSimulator().getSimulatorTime());
        synchronized (this.queue)
        {
            this.queue.add(request);
        }
        try
        {
            this.fireTimedEvent(Seize.QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
            this.resource.requestCapacity(pRequestedCapacity, this);
        }
        catch (Exception exception)
        {
            getSimulator().getLogger().always().warn(exception, "receiveObject");
        }
    }

    @Override
    public void receiveEntity(final Entity<T> entity)
    {
        this.receiveObject(entity, this.requestedCapacity);
    }

    /**
     * sets the queue to this seize. This enables seize blocks to share one queue.
     * @param queue List&lt;Request&lt;T&gt;&gt;; is a new queue.
     */
    public void setQueue(final List<Request<T>> queue)
    {
        this.queue = queue;
    }

    /**
     * Return the queue.
     * @return List&lt;Request&lt;T&gt;&gt;; the queue with requests to claim the resource
     */
    public List<Request<T>> getQueue()
    {
        return this.queue;
    }

    @Override
    public void receiveRequestedResource(final double pRequestedCapacity, final Resource<T> pResource)

    {
        for (Request<T> request : this.queue)
        {
            if (request.getAmount() == pRequestedCapacity)
            {
                synchronized (this.queue)
                {
                    this.queue.remove(request);
                }
                this.fireTimedEvent(Seize.QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
                T delay = SimTime.minus(getSimulator().getSimulatorTime(), request.getQueueEntryTime());
                this.fireTimedEvent(Seize.DELAY_TIME, delay.doubleValue(), getSimulator().getSimulatorTime());
                this.releaseEntity(request.getEntity());
                return;
            }
        }
    }

    /**
     * The Request Class defines the requests for resource.
     * @param <T> the time type
     */
    public static class Request<T extends Number & Comparable<T>>
    {
        /** amount is the requested amount. */
        private final double amount;

        /** entity is the object requesting the amount. */
        private final Entity<T> entity;

        /** the time when the request was created. */
        private final T queueEntryTime;

        /**
         * Method Request.
         * @param entity Entity&lt;T&gt;; the requesting entity
         * @param amount double; is the requested amount
         * @param queueEntryTime T; the time the request was created
         */
        public Request(final Entity<T> entity, final double amount, final T queueEntryTime)
        {
            this.entity = entity;
            this.amount = amount;
            this.queueEntryTime = queueEntryTime;
        }

        /**
         * Return the requested amount.
         * @return double; the requested amount
         */
        public double getAmount()
        {
            return this.amount;
        }

        /**
         * Return the entity.
         * @return Entity&lt;T&gt;; the entity
         */
        public Entity<T> getEntity()
        {
            return this.entity;
        }

        /**
         * Returns the time when the request was made.
         * @return T; the time when the request was made
         */
        public T getQueueEntryTime()
        {
            return this.queueEntryTime;
        }
    }

}
