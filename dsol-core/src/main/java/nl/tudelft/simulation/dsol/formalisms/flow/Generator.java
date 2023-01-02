package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.language.reflection.SerializableConstructor;

/**
 * This class defines a generator.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Generator<T extends Number & Comparable<T>> extends Station<T>
{
    /** */
    public static final long serialVersionUID = 20140805L;

    /** CREATE_EVENT is fired on creation. */
    public static final EventType CREATE_EVENT = new EventType(new MetaData("CREATE_EVENT", "Created object(s)",
            new ObjectDescriptor("numberCreated", "number of objects created", Integer.class)));

    /** constructorArguments refer to the arguments of the class invoked by the generator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Object[] constructorArguments;

    /** interval defines the inter-construction time. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistContinuousSimulationTime<T> interval;

    /** startTime defines the absolute startTime for the generator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistContinuousSimulationTime<T> startTime;

    /** batchsize refers to the number of objects constructed. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistDiscrete batchSize;

    /** constructor refers to the constructor to be invoked. */
    private SerializableConstructor constructor;

    /** maxNumber is the max number of objects to be created. -1=Long.infinity. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected long maxNumber = -1;

    /** number refers to the currently constructed number. */
    private long number = 0;

    /** nextEvent is an internal variable that refers to the next simEvent. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected SimEvent<T> nextEvent = null;

    /**
     * constructs a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the
     * Generator when a destination has been indicated with the setDestination method. This constructor has a maximum number of
     * entities generated, which results in stopping the generator when the maximum number of entities has been reached.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; is the on which the construction of the objects must be scheduled.
     * @param myClass Class&lt;?&gt;; is the class of which entities are created
     * @param constructorArguments Object[]; are the parameters for the constructor of myClass. of arguments.
     *            <code>constructorArgument[n]=Integer.valueOf(12)</code> may have constructorArgumentClasses[n]=int.class;
     * @throws SimRuntimeException on constructor invocation.
     */
    public Generator(final Serializable id, final DEVSSimulatorInterface<T> simulator, final Class<?> myClass,
            final Object[] constructorArguments) throws SimRuntimeException
    {
        super(id, simulator);
        try
        {
            Constructor<?> c = ClassUtil.resolveConstructor(myClass, constructorArguments);
            this.constructor = new SerializableConstructor(c);
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
        this.constructorArguments = constructorArguments;
    }

    /**
     * generates a new entity with the basic constructorArguments.
     * @throws SimRuntimeException on construction failure
     */
    public void generate() throws SimRuntimeException
    {
        this.generate(this.constructorArguments);
    }

    /**
     * generates a new entity.
     * @param specialConstructorArguments Object[]; are the parameters used in the constructor.
     * @throws SimRuntimeException on construction failure
     */
    public synchronized void generate(final Object[] specialConstructorArguments) throws SimRuntimeException
    {
        try
        {
            if (this.maxNumber == -1 || this.number < this.maxNumber)
            {
                this.number++;
                for (int i = 0; i < this.batchSize.draw(); i++)
                {
                    Object object = this.constructor.deSerialize().newInstance(specialConstructorArguments);
                    this.simulator.getLogger().filter(Cat.DSOL).trace("generate created {}th instance of {}", this.number,
                            this.constructor.deSerialize().getDeclaringClass());
                    this.fireTimedEvent(Generator.CREATE_EVENT, 1, this.simulator.getSimulatorTime());
                    this.releaseObject(object);
                }
                this.nextEvent = new SimEvent<T>(SimTime.plus(this.simulator.getSimulatorTime(), this.interval.draw()), this,
                        this, "generate", null);
                this.simulator.scheduleEvent(this.nextEvent);
            }
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void receiveObject(final Object object)
    {
        this.releaseObject(object);
    }

    /**
     * returns the batchSize.
     * @return DistDiscrete
     */
    public DistDiscrete getBatchSize()
    {
        return this.batchSize;
    }

    /**
     * returns the interarrival interval.
     * @return DistContinuous
     */
    public DistContinuousSimulationTime<T> getInterval()
    {
        return this.interval;
    }

    /**
     * returns the maximum number of entities to be created.
     * @return long the maxNumber
     */
    public long getMaxNumber()
    {
        return this.maxNumber;
    }

    /**
     * sets the batchsize of the generator.
     * @param batchSize DistDiscrete; is the number of entities simultaneously constructed
     */
    public void setBatchSize(final DistDiscrete batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * sets the interarrival distribution.
     * @param interval DistContinuousSimulationTime&lt;R&gt;; is the interarrival time
     */
    public void setInterval(final DistContinuousSimulationTime<T> interval)
    {
        this.interval = interval;
    }

    /**
     * sets the maximum number of entities to be created.
     * @param maxNumber long; is the maxNumber
     */
    public void setMaxNumber(final long maxNumber)
    {
        this.maxNumber = maxNumber;
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
     * sets the startTime.
     * @param startTime DistContinuousSimTime&lt;A,R,T&gt;; is the absolute startTime
     */
    public synchronized void setStartTime(final DistContinuousSimulationTime<T> startTime)
    {
        this.startTime = startTime;
        try
        {
            this.nextEvent = new SimEvent<T>(startTime.draw(), this, this, "generate", null);
            this.simulator.scheduleEvent(this.nextEvent);
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "setStartTime");
        }
    }

}
