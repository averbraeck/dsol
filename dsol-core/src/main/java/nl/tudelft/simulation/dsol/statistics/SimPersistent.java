package nl.tudelft.simulation.dsol.statistics;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.reference.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.stats.summarizers.event.EventBasedTimestampWeightedTally;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The time-aware Persistent extends the djutils event-based timestamp-weighed tally and links it to the dsol framework.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public class SimPersistent<T extends Number & Comparable<T>> extends EventBasedTimestampWeightedTally
        implements SimulationStatistic<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** simulator. */
    private SimulatorInterface<T> simulator = null;

    /** OBSERVATION_ADDED_EVENT is fired whenever an observation is processed. */
    public static final EventType TIMED_OBSERVATION_ADDED_EVENT = new EventType(new MetaData("TIMED_OBSERVATION_ADDED_EVENT",
            "observation added to Persistent", new ObjectDescriptor("value", "Observation value", Double.class)));

    /** INITIALIZED_EVENT is fired whenever a Persistent is (re-)initialized. */
    public static final EventType TIMED_INITIALIZED_EVENT = new EventType(new MetaData("TIMED_INITIALIZED_EVENT",
            "Persistent initialized", new ObjectDescriptor("simPersistent", "Persistent object", SimPersistent.class)));

    /**
     * constructs a new SimPersistent.
     * @param description String; refers to the description of this SimPersistent
     * @param model DsolModel&lt;T, SimulatorInterface&lt;T&gt;&gt;; the model
     */
    public SimPersistent(final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model)
    {
        super(description);
        Throw.whenNull(model, "model cannot be null");
        model.getOutputStatistics().add(this);
        this.simulator = model.getSimulator();
        try
        {
            // only if we are before the warmup time, subscribe to the warmul event
            if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupTime()) < 0)
            {
                this.simulator.addListener(this, Replication.WARMUP_EVENT, LocalEventProducer.FIRST_POSITION,
                        ReferenceType.STRONG);
            }
            ContextInterface context =
                    ContextUtil.lookupOrCreateSubContext(this.simulator.getReplication().getContext(), "statistics");
            context.bindObject(this);
        }
        catch (NamingException | RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * constructs a new SimPersistent.
     * @param model DsolModel&lt;T, SimulatorInterface&lt;T&gt;&gt;; the model
     * @param description String; the description
     * @param target EventProducer; the target on which to collect statistics
     * @param eventType EventType; the eventType for which statistics are sampled
     */
    public SimPersistent(final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model,
            final EventProducer target, final EventType eventType)
    {
        this(description, model);
        try
        {
            target.addListener(this, eventType, ReferenceType.STRONG);
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void initialize()
    {
        super.initialize();
        // note that when initialize() is called from the (super) constructor, there cannot be listeners yet
        if (this.simulator != null)
        {
            try
            {
                fireTimedEvent(TIMED_INITIALIZED_EVENT, this, this.simulator.getSimulatorTime());
            }
            catch (RemoteException exception)
            {
                this.simulator.getLogger().always().warn(exception, "initialize()");
            }
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public double register(final Number timestamp, final double value)
    {
        try
        {
            fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, value, (T) timestamp);
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "register()");
        }
        return super.register(timestamp, value);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void notify(final Event event)
    {
        if (event.getType().equals(Replication.WARMUP_EVENT))
        {
            try
            {
                this.simulator.removeListener(this, Replication.WARMUP_EVENT);
                fireTimedEvent(TIMED_INITIALIZED_EVENT, this, this.simulator.getSimulatorTime());
                super.initialize();
                return;
            }
            catch (RemoteException exception)
            {
                CategoryLogger.always().warn(exception);
            }
        }
        else if (isActive())
        {
            if (event instanceof TimedEvent<?>)
            {
                TimedEvent<?> timedEvent = (TimedEvent<?>) event;
                if (timedEvent.getTimeStamp() instanceof Number)
                {
                    // Persistent can handle Number (and therefore also Time and Duration) and Calendar but not SimTime
                    super.notify(
                            new TimedEvent<T>(timedEvent.getType(), timedEvent.getContent(), ((T) timedEvent.getTimeStamp())));
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
    }

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
