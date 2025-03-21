package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.base.Identifiable;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

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
 */
public abstract class Resource<T extends Number & Comparable<T>> extends LocalEventProducer implements Identifiable
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the id of the resource. */
    private final String id;

    /** simulator defines the simulator on which is scheduled. */
    private final DevsSimulatorInterface<T> simulator;

    /** UTILIZATION_EVENT is fired on activity that decreases or increases the utilization. */
    public static final EventType UTILIZATION_EVENT = new EventType(new MetaData("UTILIZATION_EVENT", "Utilization changed",
            new ObjectDescriptor("newUtilization", "new utilization", Number.class)));

    /** the queue of requests for this resource. */
    private final Queue<T> requestQueue;

    /**
     * Create a new Resource with a capacity and a specific request comparator, e.g., LIFO or sorted on an attribute.
     * @param id String; the id of this resource
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     */
    public Resource(final String id, final DevsSimulatorInterface<T> simulator)
    {
        Throw.whenNull(id, "resourceId cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        this.id = id;
        this.simulator = simulator;
        this.requestQueue = new Queue<T>(id + "_queue", simulator);
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the simulator.
     * @return the simulator
     */
    public DevsSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
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
     * Resource with floating point capacity.
     * @param <T> the time type
     */
    public static class DoubleCapacity<T extends Number & Comparable<T>> extends Resource<T>
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
         * @param id String; the id of this resource
         * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
         * @param capacity double; the capacity of the resource
         */
        public DoubleCapacity(final String id, final DevsSimulatorInterface<T> simulator, final double capacity)
        {
            super(id, simulator);
            this.capacity = capacity;
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
         * Method alterClaimedCapacity.
         * @param amount double; refers the amount which is added to the claimed capacity
         */
        private synchronized void alterClaimedCapacity(final double amount)
        {
            this.claimedCapacity += amount;
            this.fireTimedEvent(Resource.UTILIZATION_EVENT, this.claimedCapacity, getSimulator().getSimulatorTime());
        }

        /**
         * sets the capacity of the resource.
         * @param capacity double; the new maximal capacity
         */
        public void setCapacity(final double capacity)
        {
            this.capacity = capacity;
            this.releaseCapacity(0.0);
        }

        /**
         * requests an amount of capacity from the resource.
         * @param amount double; the requested amount
         * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
         */
        public synchronized void requestCapacity(final double amount, final CapacityRequestor<T> requestor)
        {
            this.requestCapacity(amount, requestor, 0);
        }

        /**
         * Request an amount of capacity from the resource.
         * @param amount double; the requested amount
         * @param requestor ResourceRequestorInterface&lt;T&gt;; the RequestorInterface requesting the amount
         * @param priority int; the priority of the request
         */
        public synchronized void requestCapacity(final double amount, final CapacityRequestor<T> requestor, final int priority)
        {
            Throw.when(amount < 0.0, SimRuntimeException.class, "requested capacity on resource cannot < 0.0");
            if ((this.claimedCapacity + amount) <= this.capacity)
            {
                this.alterClaimedCapacity(amount);
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
         * releases an amount of capacity from the resource.
         * @param amount double; the amount to release
         */
        public void releaseCapacity(final double amount)
        {
            Throw.when(amount < 0.0, SimRuntimeException.class, "released capacity on resource cannot < 0.0");
            if (amount > 0.0)
            {
                this.alterClaimedCapacity(-Math.min(this.capacity, amount));
            }
            synchronized (getRequestQueue())
            {
                for (var i = getRequestQueue().iterator(); i.hasNext();)
                {
                    var request = (CapacityRequest.DoubleCapacity<T>) i.next();
                    if ((this.capacity - this.claimedCapacity) >= request.getAmount())
                    {
                        this.alterClaimedCapacity(request.getAmount());
                        request.getRequestor().receiveRequestedCapacity(request.getAmount(), this);
                        synchronized (getRequestQueue())
                        {
                            i.remove();
                        }
                    }
                    else
                    {
                        return;
                    }
                }
            }
        }
    }

}
