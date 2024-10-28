package nl.tudelft.simulation.dsol.logger;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * DefaultSimTimeFormatter of which the format(String) method can be overridden. <br>
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class DefaultSimTimeFormatter implements SimTimeFormatter
{
    /** The simulator of which to include the time in the log messages. */
    private SimulatorInterface<?> simulator;

    /**
     * Create a default simulation time formatter with a simulator.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator to use for the formatting
     */
    public DefaultSimTimeFormatter(final SimulatorInterface<?> simulator)
    {
        this.simulator = simulator;
    }

    @Override
    public void setSimulator(final SimulatorInterface<?> simulator)
    {
        this.simulator = simulator;
    }

    @Override
    public String format(final String message)
    {
        if (this.simulator == null)
        {
            return message;
        }
        return "[T=" + this.simulator.getSimulatorTime() + "] " + message;
    }

}
