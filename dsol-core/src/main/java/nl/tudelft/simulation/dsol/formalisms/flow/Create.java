package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;

/**
 * The Create flow object generates entities with a certain inter-arrival time. The class is abstract because the modeller has
 * to provide the generateEntity method that generates an entity with the correct id and attributes.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class Create<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    public static final long serialVersionUID = 20140805L;

    /** CREATE_EVENT is fired on creation. */
    public static final EventType CREATE_EVENT = new EventType(new MetaData("CREATE_EVENT", "Created entities)",
            new ObjectDescriptor("numberCreated", "number of entities created", Integer.class)));

    /** the inter-arrival time distribution. */
    private DistContinuousSimulationTime<T> interval;

    /** the start time distribution for the generator. */
    private final DistContinuousSimulationTime<T> startTime;

    /** the distribution of the number of objects generated at each generation event. */
    private final DistDiscrete batchSize;

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

    /** the next construction event. */
    private SimEventInterface<T> nextEvent = null;

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the start time distribution for the generator
     * @param startTime DistContinuousSimulationTime&lt;T&gt;; the inter-arrival time distribution
     * @param batchSize DistDiscrete; the distribution of the number of objects generated at each generation event
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator, final DistContinuousSimulationTime<T> interval,
            final DistContinuousSimulationTime<T> startTime, final DistDiscrete batchSize)
    {
        super(id, simulator);
        Throw.whenNull(startTime, "startTime cannot be null");
        Throw.whenNull(interval, "interval cannot be null");
        Throw.whenNull(batchSize, "batchSize cannot be null");
        this.startTime = startTime;
        this.interval = interval;
        this.batchSize = batchSize;
        this.nextEvent = getSimulator().scheduleEventAbs(this.startTime.draw(), this, "generate", null);
    }

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the start time distribution for the generator
     * @param startTime DistContinuousSimulationTime&lt;T&gt;; the inter-arrival time distribution
     * @param batchSize int; the number of objects generated at each generation event
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator, final DistContinuousSimulationTime<T> interval,
            final DistContinuousSimulationTime<T> startTime, final int batchSize)
    {
        this(id, simulator, interval, startTime, new DistDiscreteConstant(simulator.getModel().getDefaultStream(), batchSize));
    }

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method. This constructor has a maximum number
     * of entities generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the start time distribution for the generator
     * @param startTime DistContinuousSimulationTime&lt;T&gt;; the inter-arrival time distribution
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator, final DistContinuousSimulationTime<T> interval,
            final DistContinuousSimulationTime<T> startTime)
    {
        this(id, simulator, interval, startTime, 1);
    }

    /**
     * Construct a new generator for objects in a simulation, but without the interval (to be set later, e.g., by the generator
     * classes with a schedule). Constructed objects are sent to the 'destination' of the Create flow object when a
     * destination has been indicated with the setDestination method. This constructor has a maximum number of entities
     * generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param batchSize DistDiscrete; the number of objects generated at each generation event
     */
    protected Create(final String id, final DevsSimulatorInterface<T> simulator, final DistDiscrete batchSize)
    {
        super(id, simulator);
        Throw.whenNull(batchSize, "batchSize cannot be null");
        this.startTime = null;
        this.batchSize = batchSize;
        this.nextEvent = getSimulator().scheduleEventNow(this, "generate", null);
    }

    /**
     * Construct a new generator for objects in a simulation, but without the interval (to be set later, e.g., by the generator
     * classes with a schedule). Constructed objects are sent to the 'destination' of the Create flow object when a
     * destination has been indicated with the setDestination method. This constructor has a maximum number of entities
     * generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param batchSize int; the number of objects generated at each generation event
     */
    protected Create(final String id, final DevsSimulatorInterface<T> simulator, final int batchSize)
    {
        this(id, simulator, new DistDiscreteConstant(simulator.getModel().getDefaultStream(), batchSize));
    }

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method. This constructor has a maximum number
     * of entities generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the start time distribution for the generator
     * @param batchSize int; the number of objects generated at each generation event
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator, final DistContinuousSimulationTime<T> interval,
            final int batchSize)
    {
        this(id, simulator, batchSize);
        Throw.whenNull(interval, "interval cannot be null");
        this.interval = interval;
    }

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Create
     * flow object when a destination has been indicated with the setDestination method. This constructor has a maximum number
     * of entities generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the start time distribution for the generator
     */
    public Create(final String id, final DevsSimulatorInterface<T> simulator, final DistContinuousSimulationTime<T> interval)
    {
        this(id, simulator, interval, 1);
    }

    /**
     * Generate a new entity.
     */
    protected synchronized void generate()
    {
        if (this.numberCreationEvents > this.maxNumberCreationEvents)
        {
            return;
        }
        this.numberCreationEvents++;
        for (int i = 0; i < this.batchSize.draw(); i++)
        {
            Entity<T> entity = generateEntity();
            this.fireTimedEvent(Create.CREATE_EVENT, 1, getSimulator().getSimulatorTime());
            this.releaseEntity(entity);
            this.numberGeneratedEntities++;
            if (this.numberGeneratedEntities > this.maxNumberGeneratedEntities)
            {
                return;
            }
        }
        this.nextEvent =
                new SimEvent<T>(SimTime.plus(getSimulator().getSimulatorTime(), this.interval.draw()), this, "generate", null);
        if (this.endTime != null && (this.nextEvent.getAbsoluteExecutionTime().doubleValue() > this.endTime.doubleValue()))
        {
            return;
        }
        getSimulator().scheduleEvent(this.nextEvent);
    }

    /**
     * Generate a new entity. To be specified for the specific Generator.
     * @return Entity&lt;T&gt;; the generated entity
     */
    protected abstract Entity<T> generateEntity();

    @Override
    public void receiveEntity(final Entity<T> entity)
    {
        throw new SimRuntimeException("Generator should not receive any entities");
    }

    /**
     * Return the batch size distribution.
     * @return DistDiscrete; the batch size distribution
     */
    public DistDiscrete getBatchSize()
    {
        return this.batchSize;
    }

    /**
     * Return the interarrival distribution.
     * @return DistContinuousSimulationTime&lt;T&gt;; the interarrival distribution
     */
    public DistContinuousSimulationTime<T> getInterval()
    {
        return this.interval;
    }

    /**
     * Set a new interarrival distribution. This is used by, e.g., the GenerateSchedule classes that changes the arrival rate at
     * set intervals.
     * @param interval DistContinuousSimulationTime&lt;T&gt;; the new interarrival distribution
     */
    public void setInterval(final DistContinuousSimulationTime<T> interval)
    {
        Throw.whenNull(interval, "interval cannot be null");
        this.interval = interval;
        if (this.nextEvent != null)
        {
            getSimulator().cancelEvent(this.nextEvent);
            this.nextEvent = new SimEvent<T>(SimTime.plus(getSimulator().getSimulatorTime(), this.interval.draw()), this,
                    "generate", null);
            if (this.endTime != null && (this.nextEvent.getAbsoluteExecutionTime().doubleValue() > this.endTime.doubleValue()))
            {
                return;
            }
            getSimulator().scheduleEvent(this.nextEvent);
        }
    }

    /**
     * Return the maximum number of entities to be created.
     * @return long the maxNumber
     */
    public long getMaxNumber()
    {
        return this.maxNumberCreationEvents;
    }

    /**
     * sets the maximum number of entities to be created.
     * @param maxNumber long; is the maxNumber
     */
    public void setMaxNumber(final long maxNumber)
    {
        this.maxNumberCreationEvents = maxNumber;
    }

    /**
     * returns the startTime of the generator.
     * @return DistContinuous
     */
    public DistContinuousSimulationTime<T> getStartTime()
    {
        return this.startTime;
    }

    /**
     * @return maxNumberCreationEvents
     */
    public long getMaxNumberCreationEvents()
    {
        return this.maxNumberCreationEvents;
    }

    /**
     * @param maxNumberCreationEvents set maxNumberCreationEvents
     */
    public void setMaxNumberCreationEvents(final long maxNumberCreationEvents)
    {
        this.maxNumberCreationEvents = maxNumberCreationEvents;
    }

    /**
     * @return maxNumberGeneratedEntities
     */
    public long getMaxNumberGeneratedEntities()
    {
        return this.maxNumberGeneratedEntities;
    }

    /**
     * @param maxNumberGeneratedEntities set maxNumberGeneratedEntities
     */
    public void setMaxNumberGeneratedEntities(final long maxNumberGeneratedEntities)
    {
        this.maxNumberGeneratedEntities = maxNumberGeneratedEntities;
    }

    /**
     * @return endTime
     */
    public T getEndTime()
    {
        return this.endTime;
    }

    /**
     * @param endTime set endTime
     */
    public void setEndTime(final T endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @return numberCreationEvents
     */
    public long getNumberCreationEvents()
    {
        return this.numberCreationEvents;
    }

    /**
     * @return numberGeneratedEntities
     */
    public long getNumberGeneratedEntities()
    {
        return this.numberGeneratedEntities;
    }

    /**
     * @return nextEvent
     */
    public SimEventInterface<T> getNextEvent()
    {
        return this.nextEvent;
    }

}
