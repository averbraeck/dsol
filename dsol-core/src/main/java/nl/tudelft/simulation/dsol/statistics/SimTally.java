package nl.tudelft.simulation.dsol.statistics;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.TimedEvent;
import org.djutils.event.reference.ReferenceType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.stats.summarizers.event.EventBasedTally;

import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The simulator aware Tally extends the djutils event-based tally and links it to the dsol framework.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public class SimTally<T extends Number & Comparable<T>> extends EventBasedTally implements StatisticsInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** the simulator. */
    private final SimulatorInterface<T> simulator;

    /** OBSERVATION_ADDED_EVENT is fired whenever an observation is processed. */
    public static final EventType TIMED_OBSERVATION_ADDED_EVENT = new EventType(new MetaData("TIMED_OBSERVATION_ADDED_EVENT",
            "observation added to Tally", new ObjectDescriptor("value", "Observation value", Double.class)));

    /** INITIALIZED_EVENT is fired whenever a Tally is (re-)initialized. */
    public static final EventType TIMED_INITIALIZED_EVENT = new EventType(new MetaData("TIMED_INITIALIZED_EVENT",
            "Tally initialized", new ObjectDescriptor("simTally", "Tally object", SimTally.class)));

    /**
     * constructs a new SimTally.
     * @param description String; refers to the description of this Tally.
     * @param simulator SimulatorInterface&lt;A,R,T&gt;; the simulator to schedule on.
     * @throws RemoteException on network error for one of the listeners
     */
    public SimTally(final String description, final SimulatorInterface<T> simulator) throws RemoteException
    {
        super(description);
        this.simulator = simulator;
        if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupSimTime()) > 0)
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
     * constructs a new SimTally based on an eventType for which statistics are sampled.
     * @param description String; the description of this tally.
     * @param simulator SimulatorInterface&lt;A,R,T&gt;; the simulator to schedule on
     * @param target EventProducer; the target on which to subscribe
     * @param eventType EventType; the eventType for which statistics are sampled
     * @throws RemoteException on network error for one of the listeners
     */
    public SimTally(final String description, final SimulatorInterface<T> simulator, final EventProducer target,
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
    @SuppressWarnings({"checkstyle:designforextension", "unchecked"})
    public void notify(final Event event)
    {
        if (event.getType().equals(ReplicationInterface.WARMUP_EVENT))
        {
            this.simulator.removeListener(this, ReplicationInterface.WARMUP_EVENT);
            initialize();
            return;
        }
        else if (event instanceof TimedEvent<?>)
        {
            TimedEvent<?> timedEvent = (TimedEvent<?>) event;
            if (timedEvent.getTimeStamp() instanceof Number)
            {
                // Tally can handle Number (and therefore also Time and Duration) and Calendar but not SimTime
                super.notify(new TimedEvent<T>(timedEvent.getType(), timedEvent.getContent(), ((T) timedEvent.getTimeStamp())));
            }
            else
            {
                super.notify(event);
            }
        }
        else
        {
            this.simulator.getLogger().always().warn("SimPersistent: event not a TimedEvent");
        }
    }

    /** {@inheritDoc} */
    @Override
    public double register(final double value)
    {
        super.register(value);
        fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, value, this.simulator.getSimulatorTime());
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
