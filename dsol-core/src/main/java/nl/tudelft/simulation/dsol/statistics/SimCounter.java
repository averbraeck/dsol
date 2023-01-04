package nl.tudelft.simulation.dsol.statistics;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.reference.ReferenceType;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.stats.summarizers.event.EventBasedCounter;

import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The time-aware counter extends the djutils event-based counter and links it to the dsol framework.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public class SimCounter<T extends Number & Comparable<T>> extends EventBasedCounter implements StatisticsInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** the simulator to subscribe to and from. */
    private SimulatorInterface<T> simulator = null;

    /** OBSERVATION_ADDED_EVENT is fired whenever an observation is processed. */
    public static final EventType TIMED_OBSERVATION_ADDED_EVENT = new EventType(new MetaData("TIMED_OBSERVATION_ADDED_EVENT",
            "observation added to Persistent", new ObjectDescriptor("longValue", "long value to add to counter", Long.class)));

    /** INITIALIZED_EVENT is fired whenever a Tally is (re-)initialized. */
    public static final EventType TIMED_INITIALIZED_EVENT = new EventType(new MetaData("TIMED_INITIALIZED_EVENT",
            "Counter initialized", new ObjectDescriptor("simCounter", "Counter object", SimCounter.class)));

    /** gathering data stopped or not? */
    private boolean stopped = false;

    /**
     * constructs a new SimCounter.
     * @param description String; refers to the description of this counter
     * @param simulator SimulatorInterface&lt;A,R,T&gt;; the simulator
     * @throws RemoteException on network error for one of the listeners
     */
    public SimCounter(final String description, final SimulatorInterface<T> simulator) throws RemoteException
    {
        super(description);
        this.simulator = simulator;
        if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupSimTime()) >= 0)
        {
            this.initialize();
        }
        else
        {
            this.simulator.addListener(this, ReplicationInterface.WARMUP_EVENT, ReferenceType.STRONG);
        }
        try
        {
            ContextInterface context =
                    ContextUtil.lookupOrCreateSubContext(this.simulator.getReplication().getContext(), "statistics");
            context.bindObject(this);
        }
        catch (NamingException exception)
        {
            this.simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * constructs a new SimCounter.
     * @param description String; the description
     * @param simulator SimulatorInterface&lt;A,R,T&gt;; the simulator of this model
     * @param target EventProducer; the target on which to count
     * @param eventType EventType; the EventType for which counting takes place
     * @throws RemoteException on network error for one of the listeners
     */
    public SimCounter(final String description, final SimulatorInterface<T> simulator, final EventProducer target,
            final EventType eventType) throws RemoteException
    {
        this(description, simulator);
        target.addListener(this, eventType, ReferenceType.STRONG);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize()
    {
        super.initialize();
        fireTimedEvent(TIMED_INITIALIZED_EVENT, this, this.simulator.getSimulatorTime());
    }

    /** {@inheritDoc} */
    @Override
    public long register(final long value)
    {
        long result = super.register(value);
        fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, value, this.simulator.getSimulatorTime());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event)
    {
        if (this.stopped)
        {
            return;
        }
        if (event.getType().equals(ReplicationInterface.WARMUP_EVENT))
        {
            try
            {
                this.simulator.removeListener(this, ReplicationInterface.WARMUP_EVENT);
            }
            catch (RemoteException exception)
            {
                CategoryLogger.always().warn(exception);
            }
            super.initialize();
            return;
        }
        else
        {
            super.notify(event);
        }
    }

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
