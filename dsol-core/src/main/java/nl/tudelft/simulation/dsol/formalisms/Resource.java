package nl.tudelft.simulation.dsol.formalisms;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.djutils.base.Identifiable;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * A resource defines a shared and limited amount.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type
 */
public class Resource<T extends Number & Comparable<T>> extends LocalEventProducer implements Identifiable
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the id of the resource. */
    private String resourceId;
    
    /** the counter to give a unique sequence number to the requests. */
    private static long counter = 0;

    /** UTILIZATION_EVENT is fired on activity that decreases or increases the utilization. */
    public static final EventType UTILIZATION_EVENT = new EventType(new MetaData("UTILIZATION_EVENT",
            "Utilization changed", new ObjectDescriptor("newUtilization", "new utilization", Double.class)));

    /** QUEUE_LENGTH_EVENT fired on changes in queue length. */
    public static final EventType QUEUE_LENGTH_EVENT =
            new EventType(new MetaData("QUEUE_LENGTH_EVENT", "Queue length changed",
                    new ObjectDescriptor("newQueueLength", "new queue length", Integer.class)));

    /** QUEUE_TIME_EVENT is fired wwhen a request is granted and provides the waiting time (which can be 0). */
    public static final EventType QUEUE_WAITING_TIME_EVENT =
            new EventType(new MetaData("QUEUE_WAITING_TIME_EVENT", "Queue waiting time",
                    new ObjectDescriptor("queue waiting time", "queue waiting time", Number.class)));

    /** the minimum priority. */
    public static final int MIN_REQUEST_PRIORITY = 0;

    /** the maximum priority. */
    public static final int MAX_REQUEST_PRIORITY = 10;

    /** the default average priority. */
    public static final int DEFAULT_REQUEST_PRIORITY = 5;

    /** capacity defines the maximum capacity of the resource. */
    private double capacity;

    /** claimedCapacity defines the currently claimed capacity. */
    private double claimedCapacity = 0.0;

    /** request defines the list of requestors for this resource. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final SortedSet<Request<T>> requests;

    /** simulator defines the simulator on which is scheduled. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DevsSimulatorInterface<T> simulator;

    /**
     * Create a new Resource with a capacity and a specific request comparator, e.g., LIFO or sorted on an attribute.
     * @param resourceId String; the id of this resource
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     * @param capacity double; the capacity of the resource
     * @param requestComparator Comparator&lt;Request&lt;T&gt;&gt;; the comparator to use
     */
    public Resource(final String resourceId, final DevsSimulatorInterface<T> simulator, final double capacity,
            final Comparator<Request<T>> requestComparator)
    {
        Throw.whenNull(resourceId, "resourceId cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        Throw.whenNull(requestComparator, "requestComparator cannot be null");
        this.resourceId = resourceId;
        this.simulator = simulator;
        this.capacity = capacity;
        this.requests = Collections.synchronizedSortedSet(new TreeSet<Request<T>>(requestComparator));;
    }

    /**
     * Create a new Resource with a capacity and a default FIFO request comparator.
     * @param resourceId String; the id of this resource
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     * @param capacity double; the capacity of the resource
     */
    public Resource(final String resourceId, final DevsSimulatorInterface<T> simulator, final double capacity)
    {
        this(resourceId, simulator, capacity, new RequestComparator<T>());
    }

    @Override
    public String getId()
    {
        return this.resourceId;
    }

    /**
     * returns the maximum, and thus original capacity of the resource.
     * @return capacity the maximum, and thus original capacity of the resource.
     */
    public double getCapacity()
    {
        return this.capacity;
    }

    /**
     * returns the amount of currently claimed capacity.
     * @return the amount of currently claimed capacity.
     */
    public double getClaimedCapacity()
    {
        return this.claimedCapacity;
    }

    /**
     * returns the currently available capacity on this resource. This method is implemented as
     * <code>return this.getCapacity()-this.getClaimedCapacity()</code>
     * @return the currently available capacity on this resource.
     */
    public double getAvailableCapacity()
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
     * @param amount double; refers the amount which is added to the claimed capacity
     * @throws RemoteException on network failure
     */
    private synchronized void alterClaimedCapacity(final double amount) throws RemoteException
    {
        this.claimedCapacity += amount;
        this.fireTimedEvent(Resource.UTILIZATION_EVENT, this.claimedCapacity, this.simulator.getSimulatorTime());
    }

    /**
     * sets the capacity of the resource.
     * @param capacity double; the new maximal capacity
     */
    public void setCapacity(final double capacity)
    {
        this.capacity = capacity;
        try
        {
            this.releaseCapacity(0.0);
        }
        catch (RemoteException remoteException)
        {
            // This exception cannot occur.
            this.simulator.getLogger().always().warn(remoteException, "setCapacity");
        }
    }

    /**
     * requests an amount of capacity from the resource.
     * @param amount double; the requested amount
     * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on other failures
     */
    public synchronized void requestCapacity(final double amount, final ResourceRequestorInterface<T> requestor)
            throws RemoteException, SimRuntimeException
    {
        this.requestCapacity(amount, requestor, Resource.DEFAULT_REQUEST_PRIORITY);
    }

    /**
     * requests an amount of capacity from the resource.
     * @param amount double; the requested amount
     * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
     * @param priority int; the priority of the request
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on other failures
     */
    public synchronized void requestCapacity(final double amount, final ResourceRequestorInterface<T> requestor,
            final int priority) throws RemoteException, SimRuntimeException
    {
        if (amount < 0.0)
        {
            throw new SimRuntimeException("requested capacity on resource cannot <0.0");
        }
        if ((this.claimedCapacity + amount) <= this.capacity)
        {
            this.alterClaimedCapacity(amount);
            this.simulator.scheduleEventNow(requestor, "receiveRequestedResource", new Object[] {Double.valueOf(amount), this});
        }
        else
        {
            synchronized (this.requests)
            {
                this.requests.add(new Request<T>(requestor, amount, priority));
            }
            this.fireTimedEvent(Resource.QUEUE_LENGTH_EVENT, this.requests.size(),
                    this.simulator.getSimulatorTime());
        }
    }

    /**
     * releases an amount of capacity from the resource.
     * @param amount double; the amount to release
     * @throws RemoteException on network failure
     */
    public void releaseCapacity(final double amount) throws RemoteException
    {
        if (amount < 0.0)
        {
            throw new IllegalArgumentException("released capacity on resource cannot <0.0");
        }
        if (amount > 0.0)
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
                    this.fireTimedEvent(Resource.QUEUE_LENGTH_EVENT, this.requests.size(),
                            this.simulator.getSimulatorTime());
                }
                else
                {
                    return;
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return this.resourceId;
    }

    /**
     * the RequestComparator. This comparator first checks on priority, then on ID.
     * @param <T> the simulation time type to use.
     */
    public static class RequestComparator<T extends Number & Comparable<T>> implements Comparator<Request<T>>
    {
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
        private ResourceRequestorInterface<T> requestor;

        /** amount is the amount requested by the resource. */
        private double amount;

        /**
         * constructs a new Request.
         * @param requestor ResourceRequestorInterface&lt;T&gt;; the requestor
         * @param amount double; the requested amount
         * @param priority int; the priority of the request
         */
        public Request(final ResourceRequestorInterface<T> requestor, final double amount, final int priority)
        {
            this.requestor = requestor;
            this.amount = amount;
            this.priority = priority;
            Resource.counter++;
            this.id = Resource.counter;
        }

        /**
         * gets the requested amount.
         * @return the requested amount
         */
        public double getAmount()
        {
            return this.amount;
        }

        /**
         * gets the requestor.
         * @return the Requestor.
         */
        public ResourceRequestorInterface<T> getRequestor()
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

        @Override
        public String toString()
        {
            return "RequestForResource[requestor=" + this.requestor + ";amount=" + this.amount + ";priority=" + this.priority
                    + "]";
        }
    }

}
