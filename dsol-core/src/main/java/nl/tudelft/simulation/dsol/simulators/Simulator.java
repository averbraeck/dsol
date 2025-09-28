package nl.tudelft.simulation.dsol.simulators;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import ch.qos.logback.classic.Level;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.logger.DefaultSimTimeFormatter;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.logger.SimTimeFormatter;
import nl.tudelft.simulation.dsol.model.DsolModel;

/**
 * The Simulator class is an abstract implementation of the SimulatorInterface.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a> relative types are the same.
 * @param <T> the time type
 */
public abstract class Simulator<T extends Number & Comparable<T>> extends LocalEventProducer
        implements SimulatorInterface<T>, Runnable
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** simulatorTime represents the simulationTime. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T simulatorTime;

    /** The runUntil time in case we want to stop before the end of the replication time. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T runUntilTime;

    /** whether the runUntilTime should carry out the calculation(s) for that time or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean runUntilIncluding = true;

    /** The run state of the simulator, that indicates the state of the Simulator state machine. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected RunState runState = RunState.NOT_INITIALIZED;

    /** The replication state of the simulator, that indicates the state of the Replication state machine. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected ReplicationState replicationState = ReplicationState.NOT_INITIALIZED;

    /** The currently active replication; is null before initialize() has been called. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Replication<T> replication = null;

    /** The model that is currently active; is null before initialize() has been called. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DsolModel<T, ? extends SimulatorInterface<T>> model = null;

    /** a worker. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected transient SimulatorWorkerThread worker = null;

    /** the simulatorSemaphore. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected transient Object semaphore = new Object();

    /** the simulation time formatter. */
    private transient SimTimeFormatter<T> simTimeFormatter;

    /** the simulator id. */
    private Serializable id;

    /** the methods to execute after model initialization, e.g., to set-up the initial events. */
    private final List<SimEvent<Long>> initialmethodCalls = new ArrayList<>();

    /** The error handling strategy. */
    private ErrorStrategy errorStrategy = ErrorStrategy.WARN_AND_PAUSE;

    /** The error strategy's log level. */
    private Level errorLogLevel = Level.ERROR;

    /** the run flag semaphore indicating that the run() method has started (and might have stopped). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean runflag = false;

    /**
     * Constructs a new Simulator.
     * @param id the id of the simulator, used in logging and firing of events.
     */
    public Simulator(final Serializable id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
        new SimLogger(this);
        this.simTimeFormatter = new DefaultSimTimeFormatter(this);
    }

    @Override
    public void initialize(final DsolModel<T, ? extends SimulatorInterface<T>> model, final Replication<T> replication,
            final boolean cleanUp) throws SimRuntimeException
    {
        Throw.whenNull(model, "Simulator.initialize: model cannot be null");
        Throw.whenNull(replication, "Simulator.initialize: replication cannot be null");
        Throw.when(isStartingOrRunning(), SimRuntimeException.class, "Cannot initialize a running simulator");
        synchronized (this.semaphore)
        {
            if (this.worker != null && cleanUp)
            {
                cleanUp();
            }
            this.worker = new SimulatorWorkerThread(this.id.toString(), this);
            this.replication = replication;
            this.model = model;
            this.simulatorTime = replication.getStartTime();
            this.model.getOutputStatistics().clear();
            this.model.constructModel();
            this.runState = RunState.INITIALIZED;
            this.replicationState = ReplicationState.INITIALIZED;
            this.runflag = false;

            for (SimEvent<Long> initialMethodCall : this.initialmethodCalls)
            {
                initialMethodCall.execute();
            }
        }
        // sleep maximally 1 second till the SimulatorWorkerThread gets into the WAITING state
        int count = 0;
        while (!this.worker.isWaiting() && this.worker.isAlive() && count < 1000)
        {
            try
            {
                Thread.sleep(1);
                count++;
            }
            catch (InterruptedException exception)
            {
                // ignore
            }
        }
    }

    @Override
    public void addScheduledMethodOnInitialize(final Object target, final String method, final Object[] args)
            throws SimRuntimeException
    {
        this.initialmethodCalls.add(new SimEvent<Long>(0L, target, method, args));
    }

    /**
     * Implementation of the start method. Checks preconditions for running and fires the right events.
     * @throws SimRuntimeException when the simulator is already running, or when the replication is missing or has ended
     */
    public void startImpl() throws SimRuntimeException
    {
        Throw.when(isStartingOrRunning(), SimRuntimeException.class, "Cannot start a running simulator");
        Throw.when(isStopping(), SimRuntimeException.class, "Cannot start a stopping (not stopped yet) simulator");
        Throw.when(this.replication == null, SimRuntimeException.class, "Cannot start a simulator without replication details");
        Throw.when(!isInitialized(), SimRuntimeException.class, "Cannot start an uninitialized simulator");
        Throw.when(
                !(this.replicationState == ReplicationState.INITIALIZED || this.replicationState == ReplicationState.STARTED),
                SimRuntimeException.class, "State of the replication should be INITIALIZED or STARTED to run a simulationF");
        Throw.when(this.simulatorTime.compareTo(this.replication.getEndTime()) >= 0, SimRuntimeException.class,
                "Cannot start simulator : simulatorTime >= runLength");
        synchronized (this.semaphore)
        {
            this.runState = RunState.STARTING;
            if (this.replicationState == ReplicationState.INITIALIZED)
            {
                fireTimedEvent(Replication.START_REPLICATION_EVENT, null, getSimulatorTime());
                this.replicationState = ReplicationState.STARTED;
            }
            this.fireEvent(SimulatorInterface.STARTING_EVENT, null);
            // continue the run() of the SimulatorWorkerThread that will start the Simulator's run() method
            this.worker.interrupt();
            // wait maximally 1 second till the Simulator.run() method has been called
            int count = 0;
            while (!this.runflag && count < 1000)
            {
                try
                {
                    Thread.sleep(1);
                    count++;
                }
                catch (InterruptedException exception)
                {
                    // ignore
                }
            }
            // clear the flag
            this.runflag = false;
        }
    }

    @Override
    public void start() throws SimRuntimeException
    {
        this.runUntilTime = this.replication.getEndTime();
        this.runUntilIncluding = true;
        startImpl();
    }

    @Override
    public void runUpTo(final T stopTime) throws SimRuntimeException
    {
        this.runUntilTime = stopTime;
        this.runUntilIncluding = false;
        startImpl();
    }

    @Override
    public void runUpToAndIncluding(final T stopTime) throws SimRuntimeException
    {
        this.runUntilTime = stopTime;
        this.runUntilIncluding = true;
        startImpl();
    }

    /**
     * The implementation body of the step() method. The stepImpl() method should fire the TIME_CHANGED_EVENT before the
     * execution of the simulation event, or before executing the integration of the differential equation for the next
     * timestep. So the time is changed first to match the logic carried out for that time, and then the action for that time is
     * carried out. This is INDEPENDENT of the fact whether the time changes or not. The TIME_CHANGED_EVENT is always fired.
     */
    protected abstract void stepImpl();

    @Override
    public void step() throws SimRuntimeException
    {
        Throw.when(isStartingOrRunning(), SimRuntimeException.class, "Cannot step a running simulator");
        Throw.when(!isInitialized(), SimRuntimeException.class, "Cannot start an uninitialized simulator");
        Throw.when(
                !(this.replicationState == ReplicationState.INITIALIZED || this.replicationState == ReplicationState.STARTED),
                SimRuntimeException.class, "State of the replication should be INITIALIZED or STARTED to run a simulation");
        Throw.when(this.simulatorTime.compareTo(this.replication.getEndTime()) >= 0, SimRuntimeException.class,
                "Cannot step simulator : simulatorTime >= runLength");
        try
        {
            synchronized (this.semaphore)
            {
                if (this.replicationState == ReplicationState.INITIALIZED)
                {
                    fireTimedEvent(Replication.START_REPLICATION_EVENT, null, getSimulatorTime());
                    this.replicationState = ReplicationState.STARTED;
                }
                this.runState = RunState.STARTED;
                fireTimedEvent(SimulatorInterface.START_EVENT, null, getSimulatorTime());
                stepImpl();
            }
        }
        finally
        {
            this.runState = RunState.STOPPED;
            fireTimedEvent(SimulatorInterface.STOP_EVENT, null, getSimulatorTime());
        }
    }

    /**
     * Implementation of the stop behavior.
     */
    protected void stopImpl()
    {
        this.runState = RunState.STOPPING;
        // since the stop() method might be called from an event, do not wait
        // see https://github.com/averbraeck/dsol/issues/53
    }

    @Override
    public void stop() throws SimRuntimeException
    {
        Throw.when(isStoppingOrStopped(), SimRuntimeException.class, "Cannot stop an already stopped simulator");
        this.fireEvent(SimulatorInterface.STOPPING_EVENT, null);
        stopImpl();
    }

    /**
     * Fire the WARMUP event to clear the statistics after the warmup period. Note that for a discrete event simulator, the
     * warmup event can be scheduled, whereas for a continuous simulator, the warmup event must be detected based on the
     * simulation time.
     */
    public void warmup()
    {
        fireTimedEvent(Replication.WARMUP_EVENT, null, getSimulatorTime());
    }

    @Override
    public void cleanUp()
    {
        stopImpl();
        if (hasListeners())
        {
            this.removeAllListeners();
        }
        if (this.worker != null)
        {
            this.worker.cleanUp();
            this.worker = null;
        }
        this.runState = RunState.NOT_INITIALIZED;
        this.replicationState = ReplicationState.NOT_INITIALIZED;
    }

    @Override
    public void endReplication() throws SimRuntimeException
    {
        Throw.when(!isInitialized(), SimRuntimeException.class, "Cannot end the replication of an uninitialized simulator");
        Throw.when(
                !(this.replicationState == ReplicationState.INITIALIZED || this.replicationState == ReplicationState.STARTED),
                SimRuntimeException.class, "State of the replication should be INITIALIZED or STARTED to end it");
        this.replicationState = ReplicationState.ENDING;
        if (isStartingOrRunning())
        {
            this.runState = RunState.STOPPING;
        }
        this.worker.interrupt(); // just to be sure that the run will end, and the state will be moved to 'ENDED'
        if (this.simulatorTime.compareTo(this.getReplication().getEndTime()) < 0
                && this.replication.getStoppingCondition() == null)
        {
            CategoryLogger.with(Cat.DSOL).warn("endReplication executed, but the simulation time " + this.simulatorTime
                    + " is earlier than the replication length " + this.getReplication().getEndTime());
            this.simulatorTime = this.getReplication().getEndTime();
        }
        // since the endReplication() method might be called from an event, do not wait
        // see https://github.com/averbraeck/dsol/issues/53
    }

    /**
     * Check whether the stopping condition has been met, and take appropriate action if it has been met. Note that we only
     * check the stopping condition after the warmup period has passed.
     */
    protected void checkStoppingCondition()
    {
        if (getReplication().getStoppingCondition() != null)
        {
            if (getSimulatorTime().doubleValue() > getReplication().getWarmupTime().doubleValue())
            {
                if (getReplication().getStoppingCondition().test(getModel()))
                {
                    endReplication();
                }
            }
        }
    }

    @Override
    public final ErrorStrategy getErrorStrategy()
    {
        return this.errorStrategy;
    }

    @Override
    public final void setErrorStrategy(final ErrorStrategy errorStrategy)
    {
        this.errorStrategy = errorStrategy;
        this.errorLogLevel = errorStrategy.getDefaultLogLevel();
    }

    @Override
    public final void setErrorStrategy(final ErrorStrategy newErrorStrategy, final Level newErrorLogLevel)
    {
        this.errorStrategy = newErrorStrategy;
        this.errorLogLevel = newErrorLogLevel;
    }

    @Override
    public final Level getErrorLogLevel()
    {
        return this.errorLogLevel;
    }

    @Override
    public final void setErrorLogLevel(final Level errorLogLevel)
    {
        this.errorLogLevel = errorLogLevel;
    }

    /**
     * Handle an exception thrown by executing a SimEvent according to the ErrorStrategy. A call to this method needs to be
     * built into the run() method of every Simulator subclass.
     * @param exception the exception that was thrown when executing the SimEvent
     */
    protected void handleSimulationException(final Exception exception)
    {
        String s = "Exception during simulation at t=" + getSimulatorTime() + ": " + exception.getMessage();
        switch (this.errorLogLevel.levelInt)
        {
            case Level.DEBUG_INT:
                CategoryLogger.always().debug(s);
                break;
            case Level.TRACE_INT:
                CategoryLogger.always().trace(s);
                break;
            case Level.INFO_INT:
                CategoryLogger.always().info(s);
                break;
            case Level.WARN_INT:
                CategoryLogger.always().warn(s);
                break;
            case Level.ERROR_INT:
                CategoryLogger.always().error(s);
                break;
            default:
                break;
        }
        if (this.errorStrategy.equals(ErrorStrategy.LOG_AND_CONTINUE))
        {
            return;
        }
        System.err.println(s);
        exception.printStackTrace();
        if (this.errorStrategy.equals(ErrorStrategy.WARN_AND_PAUSE))
        {
            stop();
        }
        else if (this.errorStrategy.equals(ErrorStrategy.WARN_AND_THROW))
        {
            throw new SimRuntimeException(exception);
        }
        else if (this.errorStrategy.equals(ErrorStrategy.WARN_AND_END))
        {
            cleanUp();
        }
        else if (this.errorStrategy.equals(ErrorStrategy.WARN_AND_EXIT))
        {
            System.exit(-1);
        }
    }

    @Override
    public SimTimeFormatter<T> getSimTimeFormatter()
    {
        return this.simTimeFormatter;
    }

    @Override
    public void setSimTimeFormatter(final SimTimeFormatter<T> simTimeFormatter)
    {
        this.simTimeFormatter = simTimeFormatter;
    }

    /**
     * The run method defines the actual time step mechanism of the simulator. The implementation of this method depends on the
     * formalism. Where discrete event formalisms loop over an event list, continuous simulators take predefined time steps.
     * Make sure that:<br>
     * - SimulatorInterface.TIME_CHANGED_EVENT is fired when the time of the simulator changes<br>
     * - the warmup() method is called when the warmup period has expired (through an event or based on simulation time)<br>
     * - the endReplication() method is called when the replication has ended<br>
     * - the simulator runs until the runUntil time, which is also set by the start() method.
     */
    @Override
    public abstract void run();

    @Override
    public T getSimulatorTime()
    {
        return this.simulatorTime == null ? null : this.simulatorTime;
    }

    @Override
    public Replication<T> getReplication()
    {
        return this.replication;
    }

    @Override
    public DsolModel<T, ? extends SimulatorInterface<T>> getModel()
    {
        return this.model;
    }

    @Override
    public RunState getRunState()
    {
        return this.runState;
    }

    @Override
    public ReplicationState getReplicationState()
    {
        return this.replicationState;
    }

    /**
     * fireTimedEvent method to be called for a no-payload TimedEvent.
     * @param event the event to fire at the current time
     */
    protected void fireTimedEvent(final EventType event)
    {
        fireTimedEvent(event, null, getSimulatorTime());
    }

    /**
     * writes a serializable method to stream.
     * @param out the outputstream
     * @throws IOException on IOException
     */
    private synchronized void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.writeObject(this.id);
        out.writeObject(this.simulatorTime);
        out.writeObject(this.replication);
    }

    /**
     * reads a serializable method from stream.
     * @param in java.io.ObjectInputStream; the inputstream
     * @throws IOException on IOException
     */
    @SuppressWarnings("unchecked")
    private synchronized void readObject(final java.io.ObjectInputStream in) throws IOException
    {
        try
        {
            this.id = (Serializable) in.readObject();
            this.simulatorTime = (T) in.readObject();
            this.replication = (Replication<T>) in.readObject();
            this.semaphore = new Object();
            this.worker = new SimulatorWorkerThread(this.id.toString(), this);
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    /** The worker thread to execute the run() method of the Simulator and to start/stop the simulation. */
    protected static class SimulatorWorkerThread extends Thread
    {
        /** the job to execute. */
        private Simulator<?> simulator = null;

        /** finalized. */
        private boolean finalized = false;

        /** running. */
        private AtomicBoolean running = new AtomicBoolean(false);

        /**
         * constructs a new SimulatorRunThread.
         * @param name the name of the thread
         * @param job the job to run
         */
        protected SimulatorWorkerThread(final String name, final Simulator<?> job)
        {
            super(name);
            this.simulator = job;
            this.setDaemon(false);
            this.setPriority(Thread.NORM_PRIORITY);
            this.start();
        }

        /**
         * Clean up the worker thread. synchronized method, otherwise it does not own the Monitor on the wait.
         */
        public synchronized void cleanUp()
        {
            this.running.set(false);
            this.finalized = true;
            if (!this.isInterrupted())
            {
                this.notify(); // in case it is in the 'wait' state
            }
        }

        /**
         * @return whether the run method of the job is running or not
         */
        public synchronized boolean isRunning()
        {
            return this.running.get();
        }

        /**
         * @return whether the thread is in the waiting state
         */
        public synchronized boolean isWaiting()
        {
            return this.getState().equals(Thread.State.WAITING);
        }

        @Override
        public synchronized void run()
        {
            while (!this.finalized) // always until finalized
            {
                try
                {
                    this.wait(); // as long as possible
                }
                catch (InterruptedException interruptedException)
                {
                    if (!this.finalized)
                    {
                        if (this.simulator.replicationState != ReplicationState.ENDING)
                        {
                            this.running.set(true);
                            try
                            {
                                this.simulator.runState = RunState.STARTED;
                                this.simulator.fireTimedEvent(SimulatorInterface.START_EVENT);
                                this.simulator.run();
                                this.simulator.runState = RunState.STOPPED;
                                this.simulator.fireTimedEvent(SimulatorInterface.STOP_EVENT);
                            }
                            catch (Exception exception)
                            {
                                CategoryLogger.always().error(exception);
                                exception.printStackTrace();
                            }
                        }
                        this.running.set(false);
                        if (this.simulator.replicationState == ReplicationState.ENDING)
                        {
                            this.simulator.replicationState = ReplicationState.ENDED;
                            this.simulator.runState = RunState.ENDED;
                            this.simulator.fireTimedEvent(Replication.END_REPLICATION_EVENT);
                            this.finalized = true;
                        }
                    }
                    Thread.interrupted(); // clear the interrupted flag
                }
            }
        }
    }
}
