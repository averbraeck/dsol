package nl.tudelft.simulation.examples.dsol.terminal;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * A resource defines a shared and limited amount..
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://tudelft.nl/averbraeck">Alexander Verbraeck </a>
 * @param <T> the simulation time type to use.
 * @since 1.5
 */
public class IntResource<T extends Number & Comparable<T>> extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the counter counting the requests. package visibility, so Request can access it. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    static AtomicLong counter = new AtomicLong(0); // XXX: OUCH

    /** UTILIZATION_EVENT is fired on activity. */
    public static final EventType UTILIZATION_EVENT = new EventType(new MetaData("UTILIZATION_EVENT", "Utilization changed",
            new ObjectDescriptor("newUtilization", "new utilization", Long.class)));

    /** RESOURCE_REQUESTED_QUEUE_LENGTH fired on changes in queue length. */
    public static final EventType RESOURCE_REQUESTED_QUEUE_LENGTH =
            new EventType(new MetaData("RESOURCE_REQUESTED_QUEUE_LENGTH", "Queue length changed",
                    new ObjectDescriptor("newQueueLength", "new queue length", Long.class)));

    /** the minimum priority. */
    public static final int MIN_REQUEST_PRIORITY = 0;

    /** the maximum priority. */
    public static final int MAX_REQUEST_PRIORITY = 10;

    /** the default average priority. */
    public static final int DEFAULT_REQUEST_PRIORITY = 5;

    /** capacity defines the maximum capacity of the resource. */
    private long capacity;

    /** claimedCapacity defines the currently claimed capacity. */
    protected long claimedCapacity = 0;

    /** request defines the list of requestors for this resource. */
    protected SortedSet<Request<T>> requests =
            Collections.synchronizedSortedSet(new TreeSet<Request<T>>(new RequestComparator()));

    /** simulator defines the simulator on which is scheduled. */
    protected DevsSimulatorInterface<T> simulator;

    /** the description of the resource. */
    protected String description = "resource";

    /**
     * Method Resource.
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param description String; the description of this resource
     * @param capacity long; of the resource
     */
    public IntResource(final DevsSimulatorInterface<T> simulator, final String description, final long capacity)
    {
        super();
        this.description = description;
        this.simulator = simulator;
        this.capacity = capacity;
    }

    /**
     * Method Resource.
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param capacity long; of the resource
     */
    public IntResource(final DevsSimulatorInterface<T> simulator, final long capacity)
    {
        this(simulator, "resource", capacity);
    }

    /**
     * returns the maximum, and thus original capacity of the resource.
     * @return capacity the maximum, and thus original capacity of the resource.
     */
    public long getCapacity()
    {
        return this.capacity;
    }

    /**
     * returns the amount of currently claimed capacity.
     * @return the amount of currently claimed capacity.
     */
    public long getClaimedCapacity()
    {
        return this.claimedCapacity;
    }

    /**
     * returns the currently available capacity on this resource. This method is implemented as
     * <code>return this.getCapacity()-this.getClaimedCapacity()</code>
     * @return the currently available capacity on this resource.
     */
    public long getAvailableCapacity()
    {
        return this.capacity - this.claimedCapacity;
    }

    /**
     * returns the number of instances currently waiting for this resource.
     * @return the number of instances currently waiting for this resource
     */
    public int getQueueLength()
    {
        return this.requests.size();
    }

    /**
     * Method alterClaimedCapacity.
     * @param amount long; refers the amount which is added to the claimed capacity
     * @throws RemoteException on network failure
     */
    private synchronized void alterClaimedCapacity(final long amount) throws RemoteException
    {
        this.claimedCapacity += amount;
        this.fireTimedEvent(IntResource.UTILIZATION_EVENT, this.claimedCapacity, this.simulator.getSimulatorTime());
    }

    /**
     * sets the capacity of the resource.
     * @param capacity long; the new maximal capacity
     */
    public void setCapacity(final long capacity)
    {
        this.capacity = capacity;
        try
        {
            this.releaseCapacity(0);
        }
        catch (RemoteException remoteException)
        {
            // This exception cannot occur.
            this.simulator.getLogger().always().error(remoteException, "setCapacity");
        }
    }

    /**
     * requests an amount of capacity from the resource.
     * @param amount long; the requested amount
     * @param requestor IntResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on other failures
     */
    public synchronized void requestCapacity(final long amount, final IntResourceRequestorInterface<T> requestor)
            throws RemoteException, SimRuntimeException
    {
        this.requestCapacity(amount, requestor, IntResource.DEFAULT_REQUEST_PRIORITY);
    }

    /**
     * requests an amount of capacity from the resource.
     * @param amount long; the requested amount
     * @param requestor IntResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @param priority int; the priority of the request
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on other failures
     */
    public synchronized void requestCapacity(final long amount, final IntResourceRequestorInterface<T> requestor,
            final int priority) throws RemoteException, SimRuntimeException
    {
        if (amount < 0)
        {
            throw new SimRuntimeException("requested capacity on resource cannot < 0.0");
        }
        if ((this.claimedCapacity + amount) <= this.capacity)
        {
            this.alterClaimedCapacity(amount);
            this.simulator.scheduleEventNow(requestor, "receiveRequestedResource", new Object[] {Long.valueOf(amount), this});
        }
        else
        {
            synchronized (this.requests)
            {
                this.requests.add(new Request<T>(requestor, amount, priority));
            }
            this.fireTimedEvent(IntResource.RESOURCE_REQUESTED_QUEUE_LENGTH, Long.valueOf(this.requests.size()),
                    this.simulator.getSimulatorTime());
        }
    }

    /**
     * releases an amount of capacity from the resource.
     * @param amount long; the amount to release
     * @throws RemoteException on network failure
     */
    public void releaseCapacity(final long amount) throws RemoteException
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("released capacity on resource cannot < 0.0");
        }
        if (amount > 0)
        {
            this.alterClaimedCapacity(-Math.min(this.capacity, amount));
        }
        synchronized (this.requests)
        {
            for (Iterator<Request<T>> i = this.requests.iterator(); i.hasNext();)
            {
                Request<T> request = i.next();
                if ((this.capacity - this.claimedCapacity) >= request.getAmount())
                {
                    this.alterClaimedCapacity(request.getAmount());
                    request.getRequestor().receiveRequestedResource(request.getAmount(), this);
                    synchronized (this.requests)
                    {
                        i.remove();
                    }
                    this.fireTimedEvent(IntResource.RESOURCE_REQUESTED_QUEUE_LENGTH, Long.valueOf(this.requests.size()),
                            this.simulator.getSimulatorTime());
                }
                else
                {
                    return;
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.description;
    }

    /**
     * the RequestComparator. This comparator first checks on priority, then on ID.
     */
    protected class RequestComparator implements Comparator<Request<T>>
    {
        /** {@inheritDoc} */
        @Override
        public int compare(final Request<T> arg0, final Request<T> arg1)
        {
            if (arg0.getPriority() > arg1.getPriority())
            {
                return -1;
            }
            if (arg0.getPriority() < arg1.getPriority())
            {
                return 1;
            }
            if (arg0.getId() < arg1.getId())
            {
                return -1;
            }
            if (arg0.getId() > arg1.getId())
            {
                return 1;
            }
            return 0;
        }
    }

    /**
     * A Request.
     * @param <T> the simulation time type to use.
     */
    public static class Request<T extends Number & Comparable<T>>
    {
        /** the priority of the request. */
        private int priority = 5;

        /** the number of this request. */
        private long id = -1;

        /** requestor the resourceRequestor. */
        private IntResourceRequestorInterface<T> requestor;

        /** amount is the amount requested by the resource. */
        private long amount;

        /**
         * constructs a new Request.
         * @param requestor IntResourceRequestorInterface&lt;T&gt;; the requestor
         * @param amount long; the requested amount
         * @param priority int; the priority of the request
         */
        public Request(final IntResourceRequestorInterface<T> requestor, final long amount, final int priority)
        {
            this.requestor = requestor;
            this.amount = amount;
            this.priority = priority;
            this.id = IntResource.counter.incrementAndGet();
        }

        /**
         * gets the requested amount.
         * @return the requested amount
         */
        public long getAmount()
        {
            return this.amount;
        }

        /**
         * gets the requestor.
         * @return the Requestor.
         */
        public IntResourceRequestorInterface<T> getRequestor()
        {
            return this.requestor;
        }

        /**
         * returns the priority of the request.
         * @return the priority
         */
        public int getPriority()
        {
            return this.priority;
        }

        /**
         * returns the id of the request.
         * @return the id
         */
        public long getId()
        {
            return this.id;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "RequestForResource[requestor=" + this.requestor + ";amount=" + this.amount + ";priority=" + this.priority
                    + "]";
        }
    }

}
