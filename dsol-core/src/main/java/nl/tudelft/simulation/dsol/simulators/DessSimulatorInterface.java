package nl.tudelft.simulation.dsol.simulators;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;

/**
 * The DessSimulatorInterface defines the methods for a DESS simulator. DESS stands for the Differential Equation System
 * Specification. More information on Modeling and Simulation can be found in "Theory of Modeling and Simulation" by Bernard
 * Zeigler et.al.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @since 1.5
 */

public interface DessSimulatorInterface<T extends Number & Comparable<T>> extends SimulatorInterface<T>
{
    /** TIME_STEP_CHANGED_EVENT is fired when the time step is set. */
    EventType TIME_STEP_CHANGED_EVENT = new EventType(new MetaData("TIME_STEP_CHANGED_EVENT", "Timestep for integrator changed",
            new ObjectDescriptor("newTimestep", "New timestep", Number.class)));

    /**
     * returns the time step of the DESS simulator.
     * @return the timeStep
     */
    T getTimeStep();

    /**
     * Method setTimeStep sets the time step of the simulator.
     * @param timeStep the new timeStep. Its value should be &gt; 0.0
     * @throws SimRuntimeException when timestep &lt;= 0, NaN, or Infinity
     */
    void setTimeStep(T timeStep) throws SimRuntimeException;

}
