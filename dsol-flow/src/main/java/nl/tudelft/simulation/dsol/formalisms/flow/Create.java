package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.function.Supplier;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;

/**
 * The Create flow object generates entities with a certain inter-arrival time.
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
public class Create<T extends Number & Comparable<T>> extends FlowObject<T, Create<T>>
{
    /** */
    public static final long serialVersionUID = 20140805L;

    /** CREATE_EVENT is fired on creation. */
    public static final EventType CREATE_EVENT = new EventType(new MetaData("CREATE_EVENT", "Created entities)",
            new ObjectDescriptor("numberCreated", "number of entities created", Integer.class)));

    /** the inter-arrival time distribution. */
    private DistContinuousSimulationTime<T> intervalDist;

    /** the start time distribution for the generator, can be null when no start time distribution is used. */
    private DistContinuousSimulationTime<T> startTimeDist;

    /** the start time for the generator, can be null when no start time is used. */
    private T startTime;

    /** the distribution of the number of objects generated at each generation event. */
    private DistDiscrete batchSizeDist;

    /** the supplier of entities; to be filled with, e.g., a lambda expression. */
    private Supplier<Entity<T>> entitySupplier;

    /** the maximum number of creation events. */
    private long maxNumberCreationEvents = Long.MAX_VALUE;

    /** the maximum number of generated entities. */
    private long maxNumberGeneratedEntities = Long.MAX_VALUE;

    /** the end time for the generator; when null, there is no end time. */
    private T endTime = null;

    /** the number of creation events; note: not the number of generated entities. */
    private long numberCreationEvents = 0;

    /** the number of generated entities. */
    private long numberGeneratedEntities = 0;

    /** the next create event. */
    private SimEventInterface<T> nextCreateEvent = null;

    /** a potential count statistic; when null, no statistic is calculated. */
    private SimCounter<T> countStatistic;

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
        this.batchSizeDist = new DistDiscreteConstant(simulator.getModel().getDefaultStream(), 1);
    }

    /**
     * Generate a new entity, if allowed by the maximum number of creation events, the maximum number of created entities, and
     * the end time when the generator has to stop.
     */
    protected synchronized void generate()
    {
        Throw.whenNull(this.entitySupplier, "Entity supplier has not been initialized");
        Throw.whenNull(this.intervalDist, "Interval distribution has not been initialized");
        if (this.numberCreationEvents >= this.maxNumberCreationEvents)
        {
            this.nextCreateEvent = null;
            return;
        }
        if (this.endTime != null && getSimulator().getSimulatorTime().doubleValue() > this.endTime.doubleValue())
        {
            this.nextCreateEvent = null;
            return;
        }
        this.numberCreationEvents++;
        for (int i = 0; i < this.batchSizeDist.draw(); i++)
        {
            Entity<T> entity = this.entitySupplier.get();
            this.fireTimedEvent(Create.CREATE_EVENT, 1, getSimulator().getSimulatorTime());
            this.releaseEntity(entity);
            this.numberGeneratedEntities++;
            if (this.numberGeneratedEntities >= this.maxNumberGeneratedEntities)
            {
                this.nextCreateEvent = null;
                return;
            }
        }
        this.nextCreateEvent = getSimulator().scheduleEventRel(this.intervalDist.draw(), this, "generate", null);
    }

    /**
     * Provide a supplier for the entity to be generated.
     * @param entitySupplier the supplier to generate an entity
     * @return the Create instance for method chaining
     */
    public Create<T> setEntitySupplier(final Supplier<Entity<T>> entitySupplier)
    {
        this.entitySupplier = entitySupplier;
        if (this.nextCreateEvent == null)
        {
            this.nextCreateEvent = getSimulator().scheduleEventNow(this, "generate", null);
        }
        return this;
    }

    /**
     * Set a (new) start time distribution for this create block. An IllegalStateException will be thrown when the create block
     * has already generated its first entities.
     * @param startTimeDist the new startTime distribution
     * @return the Create instance for method chaining
     * @throws IllegalStateException when the create block has already generated entities
     */
    public Create<T> setStartTimeDist(final DistContinuousSimulationTime<T> startTimeDist)
    {
        Throw.when(this.numberCreationEvents > 0, IllegalStateException.class,
                "startTime distribution cannot be changed after entities have been created");
        this.startTimeDist = startTimeDist;
        if (this.nextCreateEvent != null)
        {
            getSimulator().cancelEvent(this.nextCreateEvent);
        }
        this.startTime = this.startTimeDist.draw();
        this.nextCreateEvent = getSimulator().scheduleEventRel(this.startTime, this, "generate", null);
        return this;
    }

    /**
     * Set a (new) non-stochastic start time for this create block. An IllegalStateException will be thrown when the create
     * block has already generated its first entities.
     * @param startTime the new startTime
     * @return the Create instance for method chaining
     * @throws IllegalStateException when the create block has already generated entities
     */
    public Create<T> setStartTime(final T startTime)
    {
        Throw.when(this.numberCreationEvents > 0, IllegalStateException.class,
                "startTime cannot be changed after entities have been created");
        this.startTimeDist = null;
        this.startTime = startTime;
        if (this.nextCreateEvent != null)
        {
            getSimulator().cancelEvent(this.nextCreateEvent);
        }
        this.nextCreateEvent = getSimulator().scheduleEventRel(startTime, this, "generate", null);
        return this;
    }

    /**
     * Set a new interarrival distribution. This is used by, e.g., classes that changes the arrival rate at set intervals.
     * @param intervalDist DistContinuousSimulationTime&lt;T&gt;; the new interarrival distribution
     * @return the Create instance for method chaining
     */
    public Create<T> setIntervalDist(final DistContinuousSimulationTime<T> intervalDist)
    {
        Throw.whenNull(intervalDist, "Interval distribution cannot be null");
        this.intervalDist = intervalDist;
        if (this.numberCreationEvents == 0)
        {
            // start means generation at t=0 or t ~ startTimeDist
            return this;
        }
        if (this.nextCreateEvent != null)
        {
            getSimulator().cancelEvent(this.nextCreateEvent);
        }
        this.nextCreateEvent = getSimulator().scheduleEventRel(this.intervalDist.draw(), this, "generate", null);
        return this;
    }

    /**
     * Set a new fixed batch size.
     * @param batchSize the new batch size
     * @return the Create instance for method chaining
     */
    public Create<T> setBatchSize(final int batchSize)
    {
        Throw.when(batchSize < 0, IllegalArgumentException.class, "Batch size should not be negative");
        this.batchSizeDist = new DistDiscreteConstant(getSimulator().getModel().getDefaultStream(), batchSize);
        return this;
    }

    /**
     * Set a new batch size distribution.
     * @param batchSizeDist the new batch size distribution
     * @return the Create instance for method chaining
     */
    public Create<T> setBatchSizeDist(final DistDiscrete batchSizeDist)
    {
        Throw.whenNull(batchSizeDist, "Batch size distribution cannot be null");
        this.batchSizeDist = batchSizeDist;
        return this;
    }

    /**
     * Set a new value for the maximum number of creation events.
     * @param maxNumberCreationEvents the new maximum number of creation events
     * @return the Create instance for method chaining
     */
    public Create<T> setMaxNumberCreationEvents(final long maxNumberCreationEvents)
    {
        this.maxNumberCreationEvents = maxNumberCreationEvents;
        return this;
    }

    /**
     * Set a new value for the maximum number of entities to be created.
     * @param maxNumberGeneratedEntities the new the maximum number of entities to be created
     * @return the Create instance for method chaining
     */
    public Create<T> setMaxNumberGeneratedEntities(final long maxNumberGeneratedEntities)
    {
        this.maxNumberGeneratedEntities = maxNumberGeneratedEntities;
        return this;
    }

    /**
     * Set a new value for the end time when the generator has to stop generating entities. A null value means that there is no
     * end time. The end time is inclusive. If entities are to be generated exactly at the end time, this is done before
     * checking for the end time.
     * @param endTime the new value for the end time when the generator has to stop, can be null
     * @return the Create instance for method chaining
     */
    public Create<T> setEndTime(final T endTime)
    {
        this.endTime = endTime;
        return this;
    }

    /**
     * Turn on the default statistics for this flow block.
     * @return the Create instance for method chaining
     */
    public Create<T> setDefaultStatistics()
    {
        super.setDefaultFlowObjectStatistics();
        this.countStatistic =
                new SimCounter<>(getId() + " generated entity count", getSimulator().getModel(), this, Create.CREATE_EVENT);
        this.countStatistic.initialize();
        return this;
    }

    @Override
    public void receiveEntity(final Entity<T> entity)
    {
        throw new SimRuntimeException("Create block should not receive any entities");
    }

    /**
     * Return the batch size distribution.
     * @return the batch size distribution
     */
    public DistDiscrete getBatchSizeDist()
    {
        return this.batchSizeDist;
    }

    /**
     * Return the interarrival distribution.
     * @return the interarrival distribution
     */
    public DistContinuousSimulationTime<T> getIntervalDist()
    {
        return this.intervalDist;
    }

    /**
     * Return the maximum number of entities to be generated.
     * @return the maximum number of entities to be generated
     */
    public long getMaxNumberGeneratedEntities()
    {
        return this.maxNumberGeneratedEntities;
    }

    /**
     * Return the maximum number of create events.
     * @return the maximum number of create events
     */
    public long getMaxNumberCreationEvents()
    {
        return this.maxNumberCreationEvents;
    }

    /**
     * Return the startTime distribution of the generator.
     * @return the startTime distribution of the generator, can be null
     */
    public DistContinuousSimulationTime<T> getStartTimeDist()
    {
        return this.startTimeDist;
    }

    /**
     * Return the start time of the generator, either provided or drawn from the start time distribution. When startTime is
     * null, generation of entities will start immediately when the model starts.
     * @return the start time of the generator, either provided or drawn from the start time distribution, can be null
     */
    public T getStartTime()
    {
        return this.startTime;
    }

    /**
     * Return the time when entity generation has to stop. This value can be null (no stop time).
     * @return endTime the time when entity generation has to stop, can be null
     */
    public T getEndTime()
    {
        return this.endTime;
    }

    /**
     * Return the number of creation events. This is not equal to the number of generated entities, since that depends on the
     * batch size (which is drawn from a distribution, and can be zero or larger than one for each generation).
     * @return the number of creation events
     */
    public long getNumberCreationEvents()
    {
        return this.numberCreationEvents;
    }

    /**
     * Return the number of generated entities.
     * @return the number of generated entities
     */
    public long getNumberGeneratedEntities()
    {
        return this.numberGeneratedEntities;
    }

    /**
     * Return the next event when an entity will be created. If null, no further entities will be created.
     * @return the next event when an entity will be created, can be null
     */
    public SimEventInterface<T> getNextCreateEvent()
    {
        return this.nextCreateEvent;
    }

    /**
     * Return whether statistics are turned on for this create block.
     * @return whether statistics are turned on for this create block.
     */
    public boolean hasDefaultStatistics()
    {
        return this.countStatistic != null;
    }

    /**
     * Return the count statistic for this create block.
     * @return the count statistic for this create block.
     */
    public SimCounter<T> getCountStatistic()
    {
        return this.countStatistic;
    }

}
