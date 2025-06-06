package nl.tudelft.simulation.dsol.logger;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimTimeFormatter formats the message to include the simulation time.
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public interface SimTimeFormatter
{
    /**
     * Format the message to include the simulation time.
     * @param message the message to format
     * @return the formatted message.
     */
    String format(String message);

    /**
     * Set the simulator used (can be null).
     * @param simulator set the simulator
     */
    void setSimulator(SimulatorInterface<?> simulator);
}
