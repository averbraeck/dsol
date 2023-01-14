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
import nl.tudelft.simulation.dsol.model.DSOLModel;
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
public class SimCounter<T extends Number & Comparable<T>> extends EventBasedCounter implements SimulationStatistic<T>
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
     * Construct a new SimCounter, and register the counter in the OutputStatistics of the model.
     * @param description String; refers to the description of this counter
     * @param model DSOLModel&lt;T, SimulatorInterface&lt;T&gt;&gt;; the model
     */
    public SimCounter(final String description, final DSOLModel<T, ? extends SimulatorInterface<T>> model)
    {
        super(description);
        Throw.whenNull(model, "model cannot be null");
        model.getOutputStatistics().add(this);
        this.simulator = model.getSimulator();
        try
        {
            if (this.simulator.getSimulatorTime().compareTo(this.simulator.getReplication().getWarmupTime()) >= 0)
            {
                this.initialize();
            }
            else
            {
                this.simulator.addListener(this, Replication.WARMUP_EVENT, ReferenceType.STRONG);
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
     * constructs a new SimCounter.
     * @param description String; the description
     * @param model DSOLModel&lt;T, SimulatorInterface&lt;T&gt;&gt;; the model
     * @param target EventProducer; the target on which to count
     * @param eventType EventType; the EventType for which counting takes place
     */
    public SimCounter(final String description, final DSOLModel<T, ? extends SimulatorInterface<T>> model,
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

    /** {@inheritDoc} */
    @Override
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
