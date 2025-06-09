package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Release flow object releases a given quantity of a released resource.
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
public abstract class Release<T extends Number & Comparable<T>> extends FlowBlock<T, Release<T>>
{
    /** */
    private static final long serialVersionUID = 20151028L;

    /**
     * Construct a Release flow object to release seized resource units.
     * @param id the id of the FlowObject
     * @param simulator on which is scheduled
     */
    public Release(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Resource with floating point capacity.
     * @param <T> the time type
     */
    public static class DoubleCapacity<T extends Number & Comparable<T>> extends Release<T>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** The resource that is released by entities in this Seize block. */
        protected final Resource.DoubleCapacity<T> resource;

        /** The fixed amount of resource released by each entity. */
        private double fixedCapacityRelease;

        /** The flexible, possibly entity-dependent, amount of resource released by an entity. */
        private ToDoubleFunction<Entity<T>> capacityReleaseFunction;

        /**
         * Construct a Release flow object to release seized resource units with floating point capacity.
         * @param id the id of the FlowObject
         * @param simulator on which is scheduled
         * @param resource the resource of which capacity is released
         */
        public DoubleCapacity(final String id, final DevsSimulatorInterface<T> simulator,
                final Resource.DoubleCapacity<T> resource)
        {
            super(id, simulator);
            this.resource = resource;
            setFixedCapacityRelease(1.0);
        }

        /**
         * Set a fixed capacity release. The alternative is a capacity release calculated by a function.
         * @param fixedCapacityRelease the fixed amount that every entity releases for a fixed capacity
         * @return the object for method chaining
         */
        public Release.DoubleCapacity<T> setFixedCapacityRelease(final double fixedCapacityRelease)
        {
            Throw.when(fixedCapacityRelease < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.fixedCapacityRelease = fixedCapacityRelease;
            this.capacityReleaseFunction = (entity) ->
            {
                return this.fixedCapacityRelease;
            };
            return this;
        }

        /**
         * Set an entity-specific capacity release as indicated by a function.
         * @param capacityReleaseFunction the function that calculates the needed capacity
         * @return the object for method chaining
         */
        public Release.DoubleCapacity<T> setFlexibleCapacityRelease(final ToDoubleFunction<Entity<T>> capacityReleaseFunction)
        {
            Throw.whenNull(capacityReleaseFunction, "capacityReleaseFunction cannot be null");
            this.fixedCapacityRelease = Double.NaN;
            this.capacityReleaseFunction = capacityReleaseFunction;
            return this;
        }

        /**
         * Return the resource that is released by entities in this Seize block.
         * @return the resource that is released by entities in this Seize block
         */
        public Resource.DoubleCapacity<T> getResource()
        {
            return this.resource;
        }

        /**
         * Return the fixed capacity release, or NaN when the capacity release is calculated by a provided function.
         * @return the fixed capacity release, or NaN when the capacity release is calculated by a provided function
         */
        public double getFixedCapacityRelease()
        {
            return this.fixedCapacityRelease;
        }

        /**
         * Receive an object that releases an amount of units from a resource.
         * @param entity the object
         * @param releasedCapacity the capacity to release
         */
        protected synchronized void receiveEntity(final Entity<T> entity, final double releasedCapacity)
        {
            super.receiveEntity(entity);
            getResource().releaseCapacity(releasedCapacity);
            this.releaseEntity(entity);
        }

        @Override
        public void receiveEntity(final Entity<T> entity)
        {
            double capacityRelease = this.capacityReleaseFunction.applyAsDouble(entity);
            Throw.when(capacityRelease < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.receiveEntity(entity, capacityRelease);
        }
    }
    
    /**
     * Resource with integer capacity.
     * @param <T> the time type
     */
    public static class IntegerCapacity<T extends Number & Comparable<T>> extends Release<T>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** The resource that is claimed by entities in this Seize block. */
        protected final Resource.IntegerCapacity<T> resource;

        /** The fixed amount of resource released by each entity. */
        private int fixedCapacityRelease;

        /** The flexible, possibly entity-dependent, amount of resource released by an entity. */
        private ToIntFunction<Entity<T>> capacityReleaseFunction;

        /**
         * Construct a Release flow object to release seized resource units with integer capacity.
         * @param id the id of the FlowObject
         * @param simulator on which is scheduled
         * @param resource the resource of which capacity is released
         */
        public IntegerCapacity(final String id, final DevsSimulatorInterface<T> simulator,
                final Resource.IntegerCapacity<T> resource)
        {
            super(id, simulator);
            this.resource = resource;
            setFixedCapacityRelease(1);
        }

        /**
         * Set a fixed capacity release. The alternative is a capacity release calculated by a function.
         * @param fixedCapacityRelease the fixed amount that every entity releases for a fixed capacity
         * @return the object for method chaining
         */
        public Release.IntegerCapacity<T> setFixedCapacityRelease(final int fixedCapacityRelease)
        {
            Throw.when(fixedCapacityRelease < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.fixedCapacityRelease = fixedCapacityRelease;
            this.capacityReleaseFunction = (entity) ->
            {
                return this.fixedCapacityRelease;
            };
            return this;
        }

        /**
         * Set an entity-specific capacity release as indicated by a function.
         * @param capacityReleaseFunction the function that calculates the needed capacity
         * @return the object for method chaining
         */
        public Release.IntegerCapacity<T> setFlexibleCapacityRelease(final ToIntFunction<Entity<T>> capacityReleaseFunction)
        {
            Throw.whenNull(capacityReleaseFunction, "capacityReleaseFunction cannot be null");
            this.fixedCapacityRelease = -1;
            this.capacityReleaseFunction = capacityReleaseFunction;
            return this;
        }

        /**
         * Return the resource that is released by entities in this Seize block.
         * @return the resource that is released by entities in this Seize block
         */
        public Resource.IntegerCapacity<T> getResource()
        {
            return this.resource;
        }

        /**
         * Return the fixed capacity release, or NaN when the capacity release is calculated by a provided function.
         * @return the fixed capacity release, or NaN when the capacity release is calculated by a provided function
         */
        public int getFixedCapacityRelease()
        {
            return this.fixedCapacityRelease;
        }

        /**
         * Receive an object that releases an amount of units from a resource.
         * @param entity the object
         * @param releasedCapacity the capacity to release
         */
        protected synchronized void receiveEntity(final Entity<T> entity, final int releasedCapacity)
        {
            super.receiveEntity(entity);
            getResource().releaseCapacity(releasedCapacity);
            this.releaseEntity(entity);
        }

        @Override
        public void receiveEntity(final Entity<T> entity)
        {
            int capacityRelease = this.capacityReleaseFunction.applyAsInt(entity);
            Throw.when(capacityRelease < 0.0, IllegalArgumentException.class, "capacity cannot be < 0");
            this.receiveEntity(entity, capacityRelease);
        }
    }
}
