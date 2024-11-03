package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The DESS defines the interface of the DESS simulator. DESS stands for the Differential Equation System Specification. More
 * information on Modeling and Simulation can be found in "Theory of Modeling and Simulation" by Bernard Zeigler et.al.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class DessSimulator<T extends Number & Comparable<T>> extends Simulator<T> implements DessSimulatorInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** timeStep represents the timestep of the DESS simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T timeStep;

    /**
     * Construct a DessSimulator with an initial time step for the integration process.
     * @param initialTimeStep T; the initial time step to use in the integration.
     * @param id the id of the simulator, used in logging and firing of events.
     * @throws SimRuntimeException when initialTimeStep &lt;=0, NaN, or Infinity
     */
    public DessSimulator(final Serializable id, final T initialTimeStep) throws SimRuntimeException
    {
        super(id);
        setTimeStep(initialTimeStep);
    }

    @Override
    public T getTimeStep()
    {
        return this.timeStep;
    }

    @Override
    public void setTimeStep(final T timeStep) throws SimRuntimeException
    {
        synchronized (super.semaphore)
        {
            if (timeStep.doubleValue() <= 0 || Double.isNaN(timeStep.doubleValue())
                    || Double.isInfinite(timeStep.doubleValue()))
            {
                throw new SimRuntimeException(
                        "DessSimulator.setTimeStep: timeStep <= 0, NaN, or Infinity. Value provided = : " + timeStep);
            }
            this.timeStep = timeStep;
            this.fireEvent(TIME_STEP_CHANGED_EVENT, timeStep);
        }
    }

    @Override
    protected void stepImpl()
    {
        this.simulatorTime = SimTime.plus(this.simulatorTime, this.timeStep);
        if (this.simulatorTime.compareTo(this.replication.getEndTime()) > 0)
        {
            this.simulatorTime = SimTime.copy(this.replication.getEndTime());
            this.endReplication();
        }
        this.fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
    }

    @Override
    public void run()
    {
        // set the run flag semaphore to signal to startImpl() that the run method has started
        this.runflag = true;
        while (this.simulatorTime.compareTo(this.runUntilTime) < 0 && !isStoppingOrStopped())
        {
            synchronized (super.semaphore)
            {
                stepImpl();
            }
        }
    }

}
