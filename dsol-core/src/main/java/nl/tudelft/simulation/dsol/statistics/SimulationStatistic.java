package nl.tudelft.simulation.dsol.statistics;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimulationStatistic is an interface for the DSOL statistics objects. The simulator can unsubscribe all statistics objects at
 * the end of a run using this interface.
 * <p>
 * Copyright (c) 2020- 2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://opentrafficsim.org/docs/current/license.html">OpenTrafficSim License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public interface SimulationStatistic<T extends Number & Comparable<T>>
{
    /**
     * Return the simulator.
     * @return SimulatorInterface; the simulator for this dsol statistic
     */
    SimulatorInterface<T> getSimulator();

    /**
     * Return the description of this statistic.
     * @return String; the description of this statistic
     */
    String getDescription();

    /**
     * Return the number of samples for this statistic.
     * @return int; the number of samples of this statistic
     */
    long getN();

}
