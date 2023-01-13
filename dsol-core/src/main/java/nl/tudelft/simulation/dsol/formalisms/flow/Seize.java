package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
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
 * The Seize station requests a resource and keeps the entity within the station's queue until the resource is actually claimed.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Seize<T extends Number & Comparable<T>> extends Station<T> implements ResourceRequestorInterface<T>
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
     * Constructor for Seize station.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;T&gt;; on which behavior is scheduled
     * @param resource Resource&lt;T&gt;; which is claimed
     */
    public Seize(final Serializable id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource)
    {
        this(id, simulator, resource, 1.0);
    }

    /**
     * Constructor for Seize station.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;T&gt;; on which behavior is scheduled
     * @param resource Resource&lt;T&gt;; which is claimed
     * @param requestedCapacity double; is the amount which is claimed by the seize
     */
    public Seize(final Serializable id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource,
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
     * @param object Object; the object
     * @param pRequestedCapacity double; the requested capacity
     */
    public synchronized void receiveObject(final Object object, final double pRequestedCapacity)

    {
        super.receiveObject(object);
        Request<T> request = new Request<T>(object, pRequestedCapacity, this.simulator.getSimulatorTime());
        synchronized (this.queue)
        {
            this.queue.add(request);
        }
        try
        {
            this.fireTimedEvent(Seize.QUEUE_LENGTH_EVENT, this.queue.size(), this.simulator.getSimulatorTime());
            this.resource.requestCapacity(pRequestedCapacity, this);
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "receiveObject");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void receiveObject(final Object object)
    {
        this.receiveObject(object, this.requestedCapacity);
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

    /** {@inheritDoc} */
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
                this.fireTimedEvent(Seize.QUEUE_LENGTH_EVENT, this.queue.size(), this.simulator.getSimulatorTime());
                T delay = SimTime.minus(this.simulator.getSimulatorTime(), request.getCreationTime());
                this.fireTimedEvent(Seize.DELAY_TIME, delay.doubleValue(), this.simulator.getSimulatorTime());
                this.releaseObject(request.getEntity());
                return;
            }
        }
    }

    /**
     * The Request Class defines the requests for resource.
     * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
     */
    public static class Request<T extends Number & Comparable<T>>
    {
        /** amount is the requested amount. */
        private final double amount;

        /** entity is the object requesting the amount. */
        private final Object entity;

        /** creationTime refers to the moment the request was created. */
        private final T creationTime;

        /**
         * Method Request.
         * @param entity Object; the requesting entity
         * @param amount double; is the requested amount
         * @param creationTime T; the time the request was created
         */
        public Request(final Object entity, final double amount, final T creationTime)
        {
            this.entity = entity;
            this.amount = amount;
            this.creationTime = creationTime;
        }

        /**
         * Returns the amount.
         * @return double
         */
        public double getAmount()
        {
            return this.amount;
        }

        /**
         * Returns the entity.
         * @return Object
         */
        public Object getEntity()
        {
            return this.entity;
        }

        /**
         * Returns the creationTime.
         * @return double
         */
        public T getCreationTime()
        {
            return this.creationTime;
        }
    }

}
