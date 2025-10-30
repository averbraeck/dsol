package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.eventlists.EventListInterface;
import nl.tudelft.simulation.dsol.eventlists.RedBlackTree;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.Executable;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.LambdaSimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The DEVS defines the interface of the DEVS simulator. DEVS stands for the Discrete Event System Specification. More
 * information on Discrete Event Simulation can be found in "Theory of Modeling and Simulation" by Bernard Zeigler et.al.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type based on the absolute and relative time.
 * @since 1.5
 */
public class DevsSimulator<T extends Number & Comparable<T>> extends Simulator<T> implements DevsSimulatorInterface<T>
{
    /** eventList represents the future event list. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected EventListInterface<T> eventList = new RedBlackTree<T>();

    /**
     * Constructs a new DevsSimulator.
     * @param id the id of the simulator, used in logging and firing of events.
     */
    public DevsSimulator(final Serializable id)
    {
        super(id);
    }

    @Override
    public boolean cancelEvent(final SimEventInterface<T> event)
    {
        return this.eventList.remove(event);
    }

    @Override
    public EventListInterface<T> getEventList()
    {
        return this.eventList;
    }

    @Override
    @SuppressWarnings({"hiding", "checkstyle:hiddenfield"})
    public void initialize(final DsolModel<T, ? extends SimulatorInterface<T>> model, final Replication<T> replication,
            final boolean cleanUp) throws SimRuntimeException
    {
        // this check HAS to be done BEFORE clearing the event list
        Throw.when(isStartingOrRunning(), SimRuntimeException.class, "Cannot initialize a running simulator");
        synchronized (super.semaphore)
        {
            this.eventList.clear();
            super.initialize(model, replication, cleanUp);
            this.scheduleEvent(new SimEvent<T>(this.getReplication().getWarmupTime(),
                    (short) (SimEventInterface.MAX_PRIORITY + 1), this, "warmup", null));
            this.scheduleEvent(new SimEvent<T>(this.getReplication().getEndTime(), (short) (SimEventInterface.MIN_PRIORITY - 1),
                    this, "endReplication", null));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEvent(final SimEventInterface<T> event) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            if (event.getAbsoluteExecutionTime().compareTo(super.simulatorTime) < 0)
            {
                throw new SimRuntimeException("cannot schedule event " + event.toString() + " in past " + this.simulatorTime
                        + ">" + event.getAbsoluteExecutionTime());
            }
            CategoryLogger.with(Cat.DSOL).trace("new event: {}", event.toString());
            this.eventList.add(event);
            return event;
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventRel(final T relativeDelay, final short priority, final Object target,
            final String method, final Object[] args) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            T absEventTime = SimTime.plus(this.simulatorTime, relativeDelay);
            return scheduleEvent(new SimEvent<T>(absEventTime, priority, target, method, args));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventRel(final T relativeDelay, final Object target, final String method,
            final Object[] args) throws SimRuntimeException
    {
        return scheduleEventRel(relativeDelay, SimEventInterface.NORMAL_PRIORITY, target, method, args);
    }

    @Override
    public SimEventInterface<T> scheduleEventAbs(final T absoluteTime, final short priority, final Object target,
            final String method, final Object[] args) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            return scheduleEvent(new SimEvent<T>(absoluteTime, priority, target, method, args));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventAbs(final T absoluteTime, final Object target, final String method,
            final Object[] args) throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime, SimEventInterface.NORMAL_PRIORITY, target, method, args);
    }

    @Override
    public SimEventInterface<T> scheduleEventNow(final short priority, final Object target, final String method,
            final Object[] args) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            T absEventTime = SimTime.copy(this.simulatorTime);
            return scheduleEvent(new SimEvent<T>(absEventTime, priority, target, method, args));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventNow(final Object target, final String method, final Object[] args)
            throws SimRuntimeException
    {
        return scheduleEventNow(SimEventInterface.NORMAL_PRIORITY, target, method, args);
    }

    @Override
    public SimEventInterface<T> scheduleEventRel(final T relativeDelay, final short priority, final Executable executable)
            throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            T absEventTime = SimTime.plus(this.simulatorTime, relativeDelay);
            return scheduleEvent(new LambdaSimEvent<T>(absEventTime, priority, executable));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventRel(final T relativeDelay, final Executable executable) throws SimRuntimeException
    {
        return scheduleEventRel(relativeDelay, SimEventInterface.NORMAL_PRIORITY, executable);
    }

    @Override
    public SimEventInterface<T> scheduleEventAbs(final T absoluteTime, final short priority, final Executable executable)
            throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            return scheduleEvent(new LambdaSimEvent<T>(absoluteTime, priority, executable));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventAbs(final T absoluteTime, final Executable executable) throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime, SimEventInterface.NORMAL_PRIORITY, executable);
    }

    @Override
    public SimEventInterface<T> scheduleEventNow(final short priority, final Executable executable) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            T absEventTime = SimTime.copy(this.simulatorTime);
            return scheduleEvent(new LambdaSimEvent<T>(absEventTime, priority, executable));
        }
    }

    @Override
    public SimEventInterface<T> scheduleEventNow(final Executable executable) throws SimRuntimeException
    {
        return scheduleEventNow(SimEventInterface.NORMAL_PRIORITY, executable);
    }

    @Override
    public synchronized void setEventList(final EventListInterface<T> eventList)
    {
        this.eventList = eventList;
        this.fireEvent(EVENTLIST_CHANGED_EVENT);
    }

    @Override
    protected void stepImpl()
    {
        synchronized (super.semaphore)
        {
            if (!this.eventList.isEmpty())
            {
                SimEventInterface<T> event = this.eventList.removeFirst();
                fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, event.getAbsoluteExecutionTime());
                super.simulatorTime = event.getAbsoluteExecutionTime();
                event.execute();
            }
        }
    }

    @Override
    public void run()
    {
        // set the run flag semaphore to signal to startImpl() that the run method has started
        this.runflag = true;
        while (!isStoppingOrStopped())
        {
            synchronized (super.semaphore)
            {
                int cmp = this.eventList.isEmpty() ? 2
                        : this.eventList.first().getAbsoluteExecutionTime().compareTo(this.runUntilTime);
                if ((cmp == 0 && !this.runUntilIncluding) || cmp > 0)
                {
                    this.simulatorTime = SimTime.copy(this.runUntilTime);
                    this.runState = RunState.STOPPING;
                    break;
                }

                SimEventInterface<T> event = this.eventList.removeFirst();
                if (event.getAbsoluteExecutionTime().compareTo(super.simulatorTime) != 0)
                {
                    fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, event.getAbsoluteExecutionTime());
                }
                super.simulatorTime = event.getAbsoluteExecutionTime();
                try
                {
                    event.execute();
                    checkStoppingCondition();
                }
                catch (Exception exception)
                {
                    handleSimulationException(exception);
                }
            }
        }
    }

    @Override
    public void endReplication()
    {
        super.endReplication();
        this.eventList.clear();
    }

    @Override
    @Deprecated
    public boolean isPauseOnError()
    {
        return getErrorStrategy().equals(ErrorStrategy.WARN_AND_PAUSE);
    }

    @Override
    @Deprecated
    public void setPauseOnError(final boolean pauseOnError)
    {
        setErrorStrategy(pauseOnError ? ErrorStrategy.WARN_AND_PAUSE : ErrorStrategy.LOG_AND_CONTINUE);
    }

}
