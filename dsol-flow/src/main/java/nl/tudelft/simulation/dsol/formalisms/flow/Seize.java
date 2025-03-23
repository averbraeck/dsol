package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToDoubleFunction;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Seize flow block requests a certain amount of capacity from a resource and keeps the entity within the flow block's
 * storage until the resource is actually claimed. Note that the Seize block has a storage in which the Entity is waiting, while
 * the Resource has a queue in which the request is waiting. This sounds like we store the same information twice. This is,
 * however, not the case. (1) Multiple Seize blocks can share the same Resource, each holding their own entities that make the
 * request, where the total set of requests is stored at the Resource. (2) A Seize could potentially request access to two
 * resources, where the entity is held at the Seize block until both are available. Each Resource keeps and grants its own
 * requests. So, there is an n:m relationship between Seize blocks and Resources, each have their own queue / storage.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class Seize<T extends Number & Comparable<T>> extends FlowBlock<T, Seize<T>> implements CapacityRequestor<T>
{
    /** */
    private static final long serialVersionUID = 20140911L;

    /** NUMBER_STORED_EVENT is fired when the queue length changes. */
    public static final EventType NUMBER_STORED_EVENT = new EventType(new MetaData("NUMBER_STORED_EVENT",
            "Number of entities stored", new ObjectDescriptor("numberStored", "Number entities stored", Integer.class)));

    /** STORAGE_TIME is fired when an entity leaves the storage. */
    public static final EventType STORAGE_TIME_EVENT = new EventType(new MetaData("STORAGE_TIME_EVENT", "Storage time",
            new ObjectDescriptor("storageTime", "Storage time (as a double)", Double.class)));

    /** Storage for waiting entities with their arrival time. */
    protected final Set<StoredEntity<T>> storage = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructor for Seize flow object.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which behavior is scheduled
     */
    public Seize(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Return the storage for waiting entities with their arrival time.
     * @return the storage for waiting entities with their arrival time
     */
    public Set<StoredEntity<T>> getStorage()
    {
        return this.storage;
    }

    /**
     * Return the resource that is claimed by entities in this Seize block.
     * @return the resource that is claimed by entities in this Seize block
     */
    public abstract Resource<T, ?> getResource();

    /**
     * Resource with floating point capacity.
     * @param <T> the time type
     */
    public static class DoubleCapacity<T extends Number & Comparable<T>> extends Seize<T>
            implements CapacityRequestor.DoubleCapacity<T>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** The resource that is claimed by entities in this Seize block. */
        protected final Resource.DoubleCapacity<T> resource;

        /** The fixed amount of resource requested by each entity. */
        private double fixedCapacityClaim = Double.NaN;

        /** The flexible, possibly entity-dependent, amount of resource requested by an entity. */
        private ToDoubleFunction<Entity<T>> capacityClaimFunction;

        /**
         * Constructor for Seize flow object with floating point capacity.
         * @param id String; the id of the FlowObject
         * @param simulator DevsSimulatorInterface&lt;T&gt;; on which behavior is scheduled
         * @param resource Resource&lt;T&gt;; that is claimed in this Seize block
         */
        public DoubleCapacity(final String id, final DevsSimulatorInterface<T> simulator,
                final Resource.DoubleCapacity<T> resource)
        {
            super(id, simulator);
            this.resource = resource;
        }

        /**
         * Set a fixed capacity claim. The alternative is a capacity claim calculated by a function.
         * @param fixedCapacityClaim the fixed claim that every entity makes for a fixed capacity
         * @return the object for method chaining
         */
        public Seize.DoubleCapacity<T> setFixedCapacityClaim(final double fixedCapacityClaim)
        {
            Throw.when(fixedCapacityClaim < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.fixedCapacityClaim = fixedCapacityClaim;
            this.capacityClaimFunction = (entity) ->
            {
                return this.fixedCapacityClaim;
            };
            return this;
        }

        /**
         * Set an entity-specific capacity claim as indicated by a function.
         * @param capacityClaimFunction the function that calculates the needed capacity
         * @return the object for method chaining
         */
        public Seize.DoubleCapacity<T> setFlexibleCapacityClaim(final ToDoubleFunction<Entity<T>> capacityClaimFunction)
        {
            this.fixedCapacityClaim = Double.NaN;
            this.capacityClaimFunction = capacityClaimFunction;
            return this;
        }

        /**
         * Return the resource that is claimed by entities in this Seize block.
         * @return the resource that is claimed by entities in this Seize block
         */
        @Override
        public Resource.DoubleCapacity<T> getResource()
        {
            return this.resource;
        }

        /**
         * Receive an object that requests an amount of units from a resource.
         * @param entity Entity&lt;T&gt;; the object
         * @param requestedCapacity double; the requested capacity
         */
        protected synchronized void receiveEntity(final Entity<T> entity, final double requestedCapacity)
        {
            var storedEntity = new StoredEntity<T>(entity, requestedCapacity, getSimulator().getSimulatorTime());
            synchronized (this.storage)
            {
                this.storage.add(storedEntity);
            }
            this.fireTimedEvent(Seize.NUMBER_STORED_EVENT, this.storage.size(), getSimulator().getSimulatorTime());
            getResource().requestCapacity(requestedCapacity, this);
        }

        @Override
        public void receiveEntity(final Entity<T> entity)
        {
            super.receiveEntity(entity);
            double capacityClaim = this.capacityClaimFunction.applyAsDouble(entity);
            Throw.when(capacityClaim < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.receiveEntity(entity, capacityClaim);
        }

        @Override
        public void receiveRequestedCapacity(final double capacityClaim, final Resource.DoubleCapacity<T> resource)
        {
            for (StoredEntity<T> storedEntity : this.storage)
            {
                if (storedEntity.amount().doubleValue() == capacityClaim)
                {
                    synchronized (this.storage)
                    {
                        this.storage.remove(storedEntity);
                    }
                    this.fireTimedEvent(Seize.NUMBER_STORED_EVENT, this.storage.size(), getSimulator().getSimulatorTime());
                    T delay = SimTime.minus(getSimulator().getSimulatorTime(), storedEntity.storeTime());
                    this.fireTimedEvent(Seize.STORAGE_TIME_EVENT, delay.doubleValue(), getSimulator().getSimulatorTime());
                    this.releaseEntity(storedEntity.entity());
                    return;
                }
            }
        }
    }

    /**
     * The stored entity.
     * @param <T> the time type
     * @param entity the entity making the capacity request
     * @param amount the amount of resources that the entity requested
     * @param storeTime the time of storage
     */
    static record StoredEntity<T extends Number & Comparable<T>>(Entity<T> entity, Number amount, T storeTime)
    {
    }
}
