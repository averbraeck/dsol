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
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
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

    /** the unique key by which to retrieve this simulation statistic. */
    private String key;

    /** OBSERVATION_ADDED_EVENT is fired whenever an observation is processed. */
    public static final EventType TIMED_OBSERVATION_ADDED_EVENT = new EventType(new MetaData("TIMED_OBSERVATION_ADDED_EVENT",
            "observation added to Persistent", new ObjectDescriptor("value", "Observation value", Double.class)));

    /** INITIALIZED_EVENT is fired whenever a Persistent is (re-)initialized. */
    public static final EventType TIMED_INITIALIZED_EVENT = new EventType(new MetaData("TIMED_INITIALIZED_EVENT",
            "Persistent initialized", new ObjectDescriptor("simPersistent", "Persistent object", SimPersistent.class)));

    /**
     * constructs a new SimPersistent.
     * @param key unique key for identifying the statistic
     * @param description refers to the description of this SimPersistent
     * @param model the model
     */
    public SimPersistent(final String key, final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model)
    {
        super(description);
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(key, "key cannot be null");
        Throw.when(key.length() == 0, IllegalArgumentException.class, "key cannot be empty");
        this.key = key;
        model.getOutputStatistics().add(this);
        this.simulator = model.getSimulator();
        try
        {
            // only if we are before the warmup time, subscribe to the warmup event
            if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupTime()) < 0)
            {
                this.simulator.addListener(this, Replication.WARMUP_EVENT, LocalEventProducer.FIRST_POSITION,
                        ReferenceType.STRONG);
            }
            this.simulator.addListener(this, Replication.END_REPLICATION_EVENT, LocalEventProducer.LAST_POSITION,
                    ReferenceType.STRONG);
            if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getStartTime()) <= 0)
            {
                this.simulator.addListener(this, Replication.START_REPLICATION_EVENT, LocalEventProducer.LAST_POSITION,
                        ReferenceType.STRONG);
            }
            ContextInterface context =
                    ContextUtil.lookupOrCreateSubContext(this.simulator.getReplication().getContext(), "statistics");
            context.bindObject(key);
        }
        catch (NamingException | RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * constructs a new SimPersistent.
     * @param key unique key for identifying the statistic
     * @param description the description
     * @param model the model
     * @param target the target on which to collect statistics
     * @param eventType the eventType for which statistics are sampled
     */
    public SimPersistent(final String key, final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model,
            final EventProducer target, final EventType eventType)
    {
        this(key, description, model);
        try
        {
            target.addListener(this, eventType, ReferenceType.STRONG);
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "<init>");
        }
    }

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
                register(this.simulator.getSimulatorTime(), 0.0);
            }
            catch (RemoteException exception)
            {
                this.simulator.getLogger().always().warn(exception, "initialize()");
            }
        }
    }

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

    /**
     * Register a 0.0 as the first observed value at the start of the replication to start the persistent.
     */
    public void startReplication()
    {
        try
        {
            fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, 0.0, this.simulator.getSimulatorTime());
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "endReplication()");
        }
        super.register(this.simulator.getSimulatorTime(), 0.0);
    }

    /**
     * Register the last observed value at the end of the replication to complete the persistent.
     */
    public void endReplication()
    {
        try
        {
            fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, super.getLastValue(), this.simulator.getSimulatorTime());
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "endReplication()");
        }
        super.register(this.simulator.getSimulatorTime(), super.getLastValue());
    }

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
                register(this.simulator.getSimulatorTime(), 0.0);
                return;
            }
            catch (RemoteException exception)
            {
                CategoryLogger.always().warn(exception);
            }
        }
        else if (event.getType().equals(Replication.END_REPLICATION_EVENT))
        {
            endReplication();
        }
        else if (event.getType().equals(Replication.START_REPLICATION_EVENT))
        {
            startReplication();
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

    @Override
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

}
