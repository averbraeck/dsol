package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;

/**
 * A resource defines a shared and limited amount that can be claimed by, e.g., an entity.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type
 * @param <R> the resource type
 */
public abstract class Resource<T extends Number & Comparable<T>, R extends Resource<T, R>> extends Block<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** persistent statistic for the resource utilization. */
    private SimPersistent<T> utilizationStatistic = null;

    /** The release type that indicates how the queue is searched for requests when capacity is available. */
    private ReleaseType releaseType = ReleaseType.FIRST_ONLY;

    /** the queue of requests for this resource. */
    private final Queue<T> requestQueue;

    /** UTILIZATION_EVENT is fired on activity that decreases or increases the utilization. */
    public static final EventType UTILIZATION_EVENT = new EventType(new MetaData("UTILIZATION_EVENT", "Utilization changed",
            new ObjectDescriptor("newUtilization", "new utilization", Double.class)));

    /**
     * Create a new Resource with a capacity and a specific request comparator, e.g., LIFO or sorted on an attribute.
     * @param id the id of this resource
     * @param simulator the simulator
     */
    public Resource(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
        this.requestQueue = new Queue<T>(id + "_queue", simulator);
    }

    /**
     * Return the request queue for this resource.
     * @return the request queue for this resource
     */
    public Queue<T> getRequestQueue()
    {
        return this.requestQueue;
    }

    /**
     * Set the release type that indicates how the queue is searched for requests when capacity is available.
     * @param releaseType the new release type
     * @return this object for method chaining
     */
    @SuppressWarnings("unchecked")
    public R setReleaseType(final ReleaseType releaseType)
    {
        this.releaseType = releaseType;
        return (R) this;
    }

    /**
     * Return the release type that indicates how the queue is searched for requests when capacity is available.
     * @return the release type that indicates how the queue is searched for requests when capacity is available
     */
    public ReleaseType getReleaseType()
    {
        return this.releaseType;
    }

    /**
     * Return the maximum, and thus original capacity of the resource.
     * @return capacity the maximum, and thus original capacity of the resource.
     */
    public abstract Number getCapacity();

    /**
     * Return the amount of currently claimed capacity.
     * @return the amount of currently claimed capacity.
     */
    public abstract Number getClaimedCapacity();

    /**
     * Turn on the default statistics for this queue block.
     * @return the Queue instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public R setDefaultStatistics()
    {
        if (!hasDefaultStatistics())
        {
            this.utilizationStatistic =
                    new SimPersistent<>(getId() + " utilization", getSimulator().getModel(), this, UTILIZATION_EVENT);
            fireTimedEvent(UTILIZATION_EVENT,
                    getCapacity().doubleValue() == 0.0 ? 0.0 : getClaimedCapacity().doubleValue() / getCapacity().doubleValue(),
                    getSimulator().getSimulatorTime());
            this.requestQueue.setDefaultStatistics();
        }
        return (R) this;
    }

    /**
     * Return whether statistics are turned on for this Storage block.
     * @return whether statistics are turned on for this Storage block.
     */
    public boolean hasDefaultStatistics()
    {
        return this.utilizationStatistic != null;
    }

    /**
     * Return the utilization statistic.
     * @return the utilization statistic
     */
    public SimPersistent<T> getUtilizationStatistic()
    {
        return this.utilizationStatistic;
    }

    /**
     * Resource with floating point capacity.
     * @param <T> the time type
     */
    public static class DoubleCapacity<T extends Number & Comparable<T>> extends Resource<T, DoubleCapacity<T>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** capacity defines the maximum capacity of the resource. */
        private double capacity;

        /** claimedCapacity defines the currently claimed capacity. */
        private double claimedCapacity = 0.0;

        /**
         * Create a new Resource with floating point capacity and a specific request comparator, e.g., LIFO or sorted on an
         * attribute.
         * @param id the id of this resource
         * @param simulator the simulator
         * @param capacity the capacity of the resource
         */
        public DoubleCapacity(final String id, final DevsSimulatorInterface<T> simulator, final double capacity)
        {
            super(id, simulator);
            this.capacity = capacity;
        }

        @Override
        public Double getCapacity()
        {
            return this.capacity;
        }

        @Override
        public Double getClaimedCapacity()
        {
            return this.claimedCapacity;
        }

        /**
         * Return the currently available capacity on this resource. This method is implemented as
         * <code>return this.getCapacity()-this.getClaimedCapacity()</code>
         * @return the currently available capacity on this resource.
         */
        public double getAvailableCapacity()
        {
            return this.capacity - this.claimedCapacity;
        }

        /**
         * Add the amount to the claimed capacity.
         * @param amount the amount which is added to the claimed capacity
         */
        private synchronized void changeClaimedCapacity(final double amount)
        {
            this.claimedCapacity += amount;
            fireTimedEvent(UTILIZATION_EVENT,
                    getCapacity().doubleValue() == 0.0 ? 0.0 : getClaimedCapacity().doubleValue() / getCapacity().doubleValue(),
                    getSimulator().getSimulatorTime());
        }

        /**
         * Set the capacity of the resource to a new value. The <code>releaseCapacity</code> method is called after updating the
         * capacity because in case of a capacity increase, the resource could allow one or more new claims on the capacity from
         * the queue.
         * @param capacity the new maximal capacity
         */
        public void setCapacity(final double capacity)
        {
            this.capacity = capacity;
            this.releaseCapacity(0.0);
        }

        /**
         * Request an amount of capacity from the resource, without a priority. A dummy priority value of 0 is used.
         * @param amount the requested amount
         * @param requestor the RequestorInterface requesting the amount
         */
        public synchronized void requestCapacity(final double amount, final CapacityRequestor.DoubleCapacity<T> requestor)
        {
            this.requestCapacity(amount, requestor, 0);
        }

        /**
         * Request an amount of capacity from the resource, with an integer priority value.
         * @param amount the requested amount
         * @param requestor the RequestorInterface requesting the amount
         * @param priority the priority of the request
         */
        public synchronized void requestCapacity(final double amount, final CapacityRequestor.DoubleCapacity<T> requestor,
                final int priority)
        {
            Throw.when(amount < 0.0, SimRuntimeException.class, "requested capacity on resource cannot be < 0.0");
            if ((this.claimedCapacity + amount) <= this.capacity)
            {
                changeClaimedCapacity(amount);
                getSimulator().scheduleEventNow(requestor, "receiveRequestedCapacity",
                        new Object[] {Double.valueOf(amount), this});
            }
            else
            {
                synchronized (getRequestQueue())
                {
                    getRequestQueue().add(amount, requestor, priority);
                }
            }
        }

        /**
         * Release an amount of capacity from the resource.
         * @param amount the amount to release
         */
        public void releaseCapacity(final double amount)
        {
            Throw.when(amount < 0.0, SimRuntimeException.class, "released capacity on resource cannot be < 0.0");
            changeClaimedCapacity(-Math.min(this.claimedCapacity, amount));
            synchronized (getRequestQueue())
            {
                for (var cr = getRequestQueue().iterator(); cr.hasNext();)
                {
                    var request = (CapacityRequest.DoubleCapacity<T>) cr.next();
                    if ((this.capacity - this.claimedCapacity) >= request.getAmount())
                    {
                        this.changeClaimedCapacity(request.getAmount());
                        request.getRequestor().receiveRequestedCapacity(request.getAmount(), this);
                        synchronized (getRequestQueue())
                        {
                            cr.remove();
                            getRequestQueue().remove(request);
                        }
                    }
                    else if (getReleaseType().equals(ReleaseType.FIRST_ONLY))
                    {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Resource with integer capacity.
     * @param <T> the time type
     */
    public static class IntegerCapacity<T extends Number & Comparable<T>> extends Resource<T, IntegerCapacity<T>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** capacity defines the maximum capacity of the resource. */
        private int capacity;

        /** claimedCapacity defines the currently claimed capacity. */
        private int claimedCapacity = 0;

        /**
         * Create a new Resource with floating point capacity and a specific request comparator, e.g., LIFO or sorted on an
         * attribute.
         * @param id the id of this resource
         * @param simulator the simulator
         * @param capacity the capacity of the resource
         */
        public IntegerCapacity(final String id, final DevsSimulatorInterface<T> simulator, final int capacity)
        {
            super(id, simulator);
            this.capacity = capacity;
        }

        @Override
        public Integer getCapacity()
        {
            return this.capacity;
        }

        @Override
        public Integer getClaimedCapacity()
        {
            return this.claimedCapacity;
        }

        /**
         * Return the currently available capacity on this resource. This method is implemented as
         * <code>return this.getCapacity()-this.getClaimedCapacity()</code>
         * @return the currently available capacity on this resource.
         */
        public int getAvailableCapacity()
        {
            return this.capacity - this.claimedCapacity;
        }

        /**
         * Add the amount to the claimed capacity.
         * @param amount the amount which is added to the claimed capacity
         */
        private synchronized void changeClaimedCapacity(final int amount)
        {
            this.claimedCapacity += amount;
            fireTimedEvent(UTILIZATION_EVENT,
                    getCapacity().intValue() == 0 ? 0.0 : getClaimedCapacity().doubleValue() / getCapacity().doubleValue(),
                    getSimulator().getSimulatorTime());
        }

        /**
         * Set the capacity of the resource to a new value. The <code>releaseCapacity</code> method is called after updating the
         * capacity because in case of a capacity increase, the resource could allow one or more new claims on the capacity from
         * the queue.
         * @param capacity the new maximal capacity
         */
        public void setCapacity(final int capacity)
        {
            this.capacity = capacity;
            this.releaseCapacity(0);
        }

        /**
         * Request an amount of capacity from the resource, without a priority. A dummy priority value of 0 is used.
         * @param amount the requested amount
         * @param requestor the RequestorInterface requesting the amount
         */
        public synchronized void requestCapacity(final int amount, final CapacityRequestor.IntegerCapacity<T> requestor)
        {
            this.requestCapacity(amount, requestor, 0);
        }

        /**
         * Request an amount of capacity from the resource, with an integer priority value.
         * @param amount the requested amount
         * @param requestor the RequestorInterface requesting the amount
         * @param priority the priority of the request
         */
        public synchronized void requestCapacity(final int amount, final CapacityRequestor.IntegerCapacity<T> requestor,
                final int priority)
        {
            Throw.when(amount < 0, SimRuntimeException.class, "requested capacity on resource cannot be < 0");
            if ((this.claimedCapacity + amount) <= this.capacity)
            {
                changeClaimedCapacity(amount);
                getSimulator().scheduleEventNow(requestor, "receiveRequestedCapacity",
                        new Object[] {Integer.valueOf(amount), this});
            }
            else
            {
                synchronized (getRequestQueue())
                {
                    getRequestQueue().add(amount, requestor, priority);
                }
            }
        }

        /**
         * Release an amount of capacity from the resource.
         * @param amount the amount to release
         */
        public void releaseCapacity(final int amount)
        {
            Throw.when(amount < 0, SimRuntimeException.class, "released capacity on resource cannot be < 0");
            changeClaimedCapacity(-Math.min(this.claimedCapacity, amount));
            synchronized (getRequestQueue())
            {
                for (var cr = getRequestQueue().iterator(); cr.hasNext();)
                {
                    var request = (CapacityRequest.IntegerCapacity<T>) cr.next();
                    if ((this.capacity - this.claimedCapacity) >= request.getAmount())
                    {
                        this.changeClaimedCapacity(request.getAmount());
                        request.getRequestor().receiveRequestedCapacity(request.getAmount(), this);
                        synchronized (getRequestQueue())
                        {
                            cr.remove();
                            getRequestQueue().remove(request);
                        }
                    }
                    else if (getReleaseType().equals(ReleaseType.FIRST_ONLY))
                    {
                        return;
                    }
                }
            }
        }
    }

    /**
     * The release type, indicating in what way available capacity will be provided to waiting requests: is it only the first
     * requests in the queue (depending on the sorting algorithm) that receive capacity, or is the entire queue searched for
     * requests that may benefit from the still available capacity?
     */
    public static enum ReleaseType
    {
        /** FIRST_ONLY means that the search for requests that can use capacity stops at the first non-fit. */
        FIRST_ONLY,

        /** ENTIRE_QUEUE means that the search for requests that can use capacity searches the entire queue. */
        ENTIRE_QUEUE;
    }
}
