package nl.tudelft.simulation.dsol.formalisms.devs;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.AbstractDevsPortModel;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.CoupledModel;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.InputPort;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * AtomicModel class. Implements the Classic Parallel DEVS Atomic Model with Ports cf Zeigler et al (2000), section 4.2.2. and
 * section 4.3 (pp. 84 ff). The algorithms for parallel DEVS are explained in Chapters 6 and 7.
 * <p>
 * Copyright (c) 2009-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 */
public abstract class AtomicModel<T extends Number & Comparable<T>> extends AbstractDevsPortModel<T>
{
    /** the default serialVersionUId. */
    private static final long serialVersionUID = 1L;

    /** future Execution of the Internal Transition. */
    private SimEvent<T> nextEvent;

    /** remaining TimeAdvance. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T sigma;

    /** the current phase (if applicable). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Phase phase = new Phase("");

    /** the time of the previous event in this component. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T timeLastEvent;

    /** the time of the next scheduled event in this component. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T timeNextEvent;

    /** the time span since the last event. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T elapsedTime;

    /** the active input port that is currently processed in Parallel DEVS. */
    @Deprecated
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected InputPort<T, ?> activePort = null;

    /** conflict handling static: first the internal event. */
    public static final boolean INTERNAL_FIRST = true;

    /** conflict handling static: first the external event. */
    public static final boolean EXTERNAL_FIRST = false;

    /** applied conflict handling strategy in this component. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean conflictStrategy = AtomicModel.INTERNAL_FIRST;

    /**
     * conflict means that both an external event and an internal event happen at the same time; the strategy applied indicates
     * what to do when this happens.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean conflict = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS AND INITIALIZATION
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructor for a stand-alone atomic model with explicit phases.
     * @param modelName String; the name of this component
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator to schedule on
     * @param e T; initial elapsed time
     * @param initphase Phase; the initial phase of the model
     */
    public AtomicModel(final String modelName, final DevsSimulatorInterface<T> simulator, final T e, final Phase initphase)
    {
        this(modelName, simulator, e, initphase, AtomicModel.INTERNAL_FIRST);
    }

    /**
     * Constructor for an atomic model within a coupled model with explicit phases.
     * @param modelName String; the name of this component
     * @param parentModel CoupledModel&lt;T&gt;; the coupled model this atomic model is part of
     * @param e T; initial elapsed time
     * @param initphase Phase; the initial phase of the model
     */
    public AtomicModel(final String modelName, final CoupledModel<T> parentModel, final T e, final Phase initphase)
    {
        this(modelName, parentModel, e, initphase, AtomicModel.INTERNAL_FIRST);
    }

    /**
     * @param modelName String; the name of this component
     * @param parentModel CoupledModel&lt;T&gt;; the coupled model this atomic model is part of
     */
    public AtomicModel(final String modelName, final CoupledModel<T> parentModel)
    {
        this(modelName, parentModel, SimTime.zero(parentModel.getSimulator().getSimulatorTime()), new Phase(""),
                AtomicModel.INTERNAL_FIRST);
    }

    /**
     * @param modelName String; the name of this component
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator to schedule on
     */
    public AtomicModel(final String modelName, final DevsSimulatorInterface<T> simulator)
    {
        this(modelName, simulator, SimTime.zero(simulator.getSimulatorTime()), new Phase(""), AtomicModel.INTERNAL_FIRST);
    }

    /**
     * Constructor for a stand-alone atomic model with explicit phases and a conflict strategy.
     * @param modelName String; the name of this component
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator to schedule on
     * @param e T; initial elapsed time
     * @param initphase Phase; the initial phase of the model to use for explicit phase models
     * @param conflictStrategy boolean; the conflict strategy to use when internal and external events take place at the same
     *            time
     */
    public AtomicModel(final String modelName, final DevsSimulatorInterface<T> simulator, final T e, final Phase initphase,
            final boolean conflictStrategy)
    {
        super(modelName, simulator, null);
        this.elapsedTime = e;
        this.timeLastEvent = SimTime.copy(simulator.getSimulatorTime());
        this.phase = initphase;
        this.conflictStrategy = conflictStrategy;
    }

    /**
     * Constructor for an atomic model within a coupled model with explicit phases and a conflict strategy.
     * @param modelName String; the name of this component
     * @param parentModel CoupledModel&lt;T&gt;; the coupled model this atomic model is part of
     * @param e T; initial elapsed time
     * @param initphase Phase; the initial phase of the model to use for explicit phase models
     * @param conflictStrategy boolean; the conflict strategy to use when internal and external events take place at the same
     *            time
     */
    public AtomicModel(final String modelName, final CoupledModel<T> parentModel, final T e, final Phase initphase,
            final boolean conflictStrategy)
    {
        super(modelName, parentModel.getSimulator(), parentModel);
        this.elapsedTime = e;
        this.phase = initphase;
        this.timeLastEvent = SimTime.copy(parentModel.getSimulator().getSimulatorTime());
        this.conflictStrategy = conflictStrategy;
        // adding to the parent model's components' list
        this.parentModel.addModelComponent(this);
    }

    /**
     * Initialize the atomic model. Start the first internal event based on the time 'e'. See Zeigler's model definition for the
     * definition of 'e'.
     * @param e T; elapsed time since the last state transition
     */
    public void initialize(final T e)
    {
        if (this.timeAdvance().doubleValue() != Double.POSITIVE_INFINITY)
        {
            try
            {
                this.nextEvent =
                        new SimEvent<T>(SimTime.minus(SimTime.plus(getSimulator().getSimulatorTime(), this.timeAdvance()), e),
                                this,"deltaInternalEventHandler", null);
                this.timeLastEvent = this.getSimulator().getSimulatorTime();
                this.simulator.scheduleEvent(this.nextEvent);
            }
            catch (SimRuntimeException exception)
            {
                this.simulator.getLogger().always().error(exception, "initialize");
            }
        }
        else
        {
            this.nextEvent = null;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BASIC ATOMIC MODEL FUNCTIONALITY
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Return the elapsed time (e) since the last event.
     * @param eventTime T; the time of the event for which we want to calculate the elapsed time.
     * @return the elapsed time (e) since the last event.
     * @throws RemoteException a remote exception occurred
     */
    public T elapsedTime(final T eventTime) throws RemoteException
    {
        return (SimTime.minus(eventTime, this.timeLastEvent));
    }

    /**
     * Schedule the next event.
     */
    private void schedule()
    {
        if (this.timeAdvance().doubleValue() != Double.POSITIVE_INFINITY && !this.conflict)
        {
            try
            {
                if (this.timeAdvance().doubleValue() != Double.POSITIVE_INFINITY)
                {
                    this.nextEvent =
                            new SimEvent<T>((SimTime.plus(SimTime.minus(this.simulator.getSimulatorTime(), this.timeAdvance()),
                                    this.elapsedTime)), this,"deltaInternalEventHandler", null);
                    this.timeLastEvent = this.simulator.getSimulatorTime();
                    this.simulator.getLogger().filter(Cat.DSOL).trace("schedule {}", this.nextEvent.toString());
                    this.simulator.scheduleEvent(this.nextEvent);
                    // this.simulator.setAuthorization(false);
                }
            }
            catch (Exception e1)
            {
                this.simulator.getLogger().always().error(e1);
            }
        }
        else
        {
            this.nextEvent = null;
        }
    }

    /**
     * This method handles an incoming external event. As part of its function, it calls the deltaExternal method that is
     * defined in an extension of this class.
     * @param e T; the elapsed time since the last state transition
     * @param value Object; the value that is passed through the port, which triggered the external event
     */
    public void deltaExternalEventHandler(final T e, final Object value)
    {
        this.deltaExternal(e, value);
        this.schedule();
        this.fireUpdatedState();
    }

    /**
     * @param e T; the elapsed time since the last state transition
     * @param value Object; the value that is passed through the port, which triggered the external event
     */
    public void deltaConfluent(final T e, final Object value)
    {
        this.simulator.getLogger().filter(Cat.DSOL).debug("deltaConfluent: CONFLUENT");
        if (this.conflictStrategy == AtomicModel.INTERNAL_FIRST)
        {
            this.deltaInternalEventHandler();
            this.conflict = false;
            this.deltaExternalEventHandler(SimTime.zero(getSimulator().getSimulatorTime()), value);
        }
        else
        {
            this.deltaExternalEventHandler(e, value);
            this.conflict = false;
            this.deltaInternalEventHandler();
        }
    }

    /**
     * This method handles an internal event. As part of its function, it calls the deltaInternal method that is defined in an
     * extension of this class.
     */
    public void deltaInternalEventHandler()
    {
        this.lambda();
        this.deltaInternal();
        this.schedule();
        this.fireUpdatedState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printModel(final String space)
    {
        System.out.println(space + "Atomicmodel: " + this.getClass().getName());
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTET AND SETTET METHODS
    // ///////////////////////////////////////////////////////////////////////////

    
    /**
     * @return the active port
     */
    @Deprecated
    public InputPort<T, ?> getActivePort()
    {
        return this.activePort;
    }
    
    /**
     * @param activePort set activePort
     */
    @Deprecated
    public void setActivePort(final InputPort<T, ?> activePort)
    {
        this.activePort = activePort;
    }

    /**
     * @return the next simulation event for this atomic model.
     */
    public SimEvent<T> getNextEvent()
    {
        return this.nextEvent;
    }

    /**
     * @return the timestamp of the last executed simulation event.
     */
    public T getTimeLastEvent()
    {
        return this.timeLastEvent;
    }

    /**
     * @return the timestamp of the simulation event to execute next.
     */
    public T getTimeNextEvent()
    {
        return this.timeNextEvent;
    }

    /**
     * @return if there is a conflict between an intenal event and an external event that take place at the same time.
     */
    public boolean isConflict()
    {
        return this.conflict;
    }

    /**
     * @param conflict boolean; indicate whether there is a conflict between an intenal event and an external event that take
     *            place at the same time.
     */
    public void setConflict(final boolean conflict)
    {
        this.conflict = conflict;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ABSTRACT METHODS TO BE DEFINED IN AN EXTENSION CLASS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * the delta internal function that should be implemented by the extending class.
     */
    public abstract void deltaInternal();

    /**
     * The user defined deltaExternal method that is defined in an extension of this class.
     * @param e T; the elapsed time since the last state transition
     * @param value Object; the value that has been passed through the port
     */
    public abstract void deltaExternal(T e, Object value);

    /**
     * the lambda function that should be implemented by the extending class.
     */
    public abstract void lambda();

    /**
     * the time advance function that should be implemented by the extending class.
     * @return the ta, which is the time advance from one state to the next.
     */
    public abstract T timeAdvance();
}
