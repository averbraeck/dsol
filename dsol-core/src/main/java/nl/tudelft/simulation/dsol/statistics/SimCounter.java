package nl.tudelft.simulation.dsol.statistics;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.reference.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.stats.summarizers.event.EventBasedCounter;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The time-aware counter extends the djutils event-based counter and links it to the dsol framework.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public class SimCounter<T extends Number & Comparable<T>> extends EventBasedCounter implements SimulationStatistic<T>
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /** the simulator to subscribe to and from. */
    private SimulatorInterface<T> simulator = null;
    
    /** the unique key by which to retrieve this simulation statistic. */
    private String key;

    /** OBSERVATION_ADDED_EVENT is fired whenever an observation is processed. */
    public static final EventType TIMED_OBSERVATION_ADDED_EVENT = new EventType(new MetaData("TIMED_OBSERVATION_ADDED_EVENT",
            "observation added to Persistent", new ObjectDescriptor("longValue", "long value to add to counter", Long.class)));

    /** INITIALIZED_EVENT is fired whenever a Tally is (re-)initialized. */
    public static final EventType TIMED_INITIALIZED_EVENT = new EventType(new MetaData("TIMED_INITIALIZED_EVENT",
            "Counter initialized", new ObjectDescriptor("simCounter", "Counter object", SimCounter.class)));

    /** gathering data stopped or not? */
    private boolean stopped = false;

    /**
     * Construct a new SimCounter, and register the counter in the OutputStatistics of the model.
     * @param key unique key for identifying the statistic
     * @param description refers to the description of this counter
     * @param model the model
     */
    public SimCounter(final String key, final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model)
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
            // only if we are before the warmup time, subscribe to the warmul event
            if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupTime()) < 0)
            { this.simulator.addListener(this, Replication.WARMUP_EVENT, ReferenceType.STRONG); }
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
     * constructs a new SimCounter.
     * @param key unique key for identifying the statistic
     * @param description the description
     * @param model the model
     * @param target the target on which to count
     * @param eventType the EventType for which counting takes place
     */
    public SimCounter(final String key, final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model,
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
            }
            catch (RemoteException exception)
            {
                this.simulator.getLogger().always().warn(exception, "initialize()");
            }
        }
    }

    @Override
    public long register(final long value)
    {
        long result = super.register(value);
        try
        {
            fireTimedEvent(TIMED_OBSERVATION_ADDED_EVENT, value, this.simulator.getSimulatorTime());
        }
        catch (RemoteException exception)
        {
            this.simulator.getLogger().always().warn(exception, "register()");
        }
        return result;
    }

    @Override
    public void notify(final Event event)
    {
        if (this.stopped)
        { return; }
        if (event.getType().equals(Replication.WARMUP_EVENT))
        {
            try
            {
                this.simulator.removeListener(this, Replication.WARMUP_EVENT);
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
