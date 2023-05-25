package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The reference implementation of the DEVDESS simulator.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @since 1.5
 */
public class DevDessSimulator<T extends Number & Comparable<T>> extends DevsSimulator<T>
        implements DevsSimulatorInterface<T>, DessSimulatorInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** timeStep represents the timestep of the DESS simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T timeStep;

    /**
     * Construct a DEVDessSimulator with an initial time step for the integration process.
     * @param initialTimeStep T; the initial time step to use in the integration.
     * @param id the id of the simulator, used in logging and firing of events.
     * @throws SimRuntimeException when initialTimeStep &lt;= 0, NaN, or Infinity
     */
    public DevDessSimulator(final Serializable id, final T initialTimeStep) throws SimRuntimeException
    {
        super(id);
        setTimeStep(initialTimeStep);
    }

    /** {@inheritDoc} */
    @Override
    public T getTimeStep()
    {
        return this.timeStep;
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeStep(final T timeStep) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            if (!Double.isFinite(timeStep.doubleValue()) || timeStep.doubleValue() <= 0.0)
            {
                throw new SimRuntimeException("Timestep for DESSimulator has illegal value: " + timeStep);
            }
            this.timeStep = timeStep;
            this.fireEvent(TIME_STEP_CHANGED_EVENT, timeStep);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        // set the run flag semaphore to signal to startImpl() that the run method has started
        this.runflag = true;
        while (!isStoppingOrStopped() && !this.eventList.isEmpty()
                && this.simulatorTime.compareTo(this.replication.getEndTime()) <= 0)
        {
            synchronized (super.semaphore)
            {
                T runUntil = SimTime.plus(this.simulatorTime, this.timeStep);
                while (!this.eventList.isEmpty() && !isStoppingOrStopped()
                        && runUntil.compareTo(this.eventList.first().getAbsoluteExecutionTime()) >= 0)
                {
                    int cmp = this.eventList.first().getAbsoluteExecutionTime().compareTo(this.runUntilTime);
                    if ((cmp == 0 && !this.runUntilIncluding) || cmp > 0)
                    {
                        this.simulatorTime = SimTime.copy(this.runUntilTime);
                        this.runState = RunState.STOPPING;
                        break;
                    }

                    SimEventInterface<T> event = this.eventList.removeFirst();
                    if (event.getAbsoluteExecutionTime().compareTo(super.simulatorTime) != 0)
                    {
                        super.fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null,
                                event.getAbsoluteExecutionTime());
                    }
                    this.simulatorTime = event.getAbsoluteExecutionTime();
                    try
                    {
                        event.execute();
                        if (this.eventList.isEmpty())
                        {
                            this.simulatorTime = SimTime.copy(this.runUntilTime);
                            this.runState = RunState.STOPPING;
                            break;
                        }
                    }
                    catch (Exception exception)
                    {
                        handleSimulationException(exception);
                    }
                }
                if (!isStoppingOrStopped())
                {
                    this.simulatorTime = runUntil;
                }
                this.fireTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
            }
        }
    }

}
