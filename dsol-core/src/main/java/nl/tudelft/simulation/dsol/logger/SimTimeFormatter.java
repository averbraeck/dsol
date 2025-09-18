package nl.tudelft.simulation.dsol.logger;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimTimeFormatter formats the simulation time to include the simulation time in the logger.
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @param <T> The time type
 */
public abstract class SimTimeFormatter<T extends Number & Comparable<T>>
{
    /** the simulator. */
    private final SimulatorInterface<T> simulator;

    /**
     * Instantiate a formatter.
     * @param simulator the simulator
     */
    public SimTimeFormatter(final SimulatorInterface<T> simulator)
    {
        this.simulator = simulator;
    }

    /**
     * Format the current simulation time.
     * @return the formatted simulation time.
     */
    public abstract String formattedSimTime();

    /**
     * Return the simulator for which this is the formatter.
     * @return the simulator for which this is the formatter.
     */
    SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
