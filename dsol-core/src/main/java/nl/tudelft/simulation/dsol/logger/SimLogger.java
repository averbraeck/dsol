package nl.tudelft.simulation.dsol.logger;

import java.util.function.Function;

import org.djutils.logger.CategoryLogger;

import ch.qos.logback.classic.Level;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimLogger contains helper methods for the logger.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulator time type
 */
public class SimLogger<T extends Number & Comparable<T>>
{
    /** the simulator. */
    private final SimulatorInterface<T> simulator;

    /** the simulation time formatter. */
    private Function<T, String> formatter = new SimTimeFormatter<>();

    /**
     * Register a logger for a simulator with time.
     * @param simulator the simulator for which to register a logger with time
     */
    public SimLogger(final SimulatorInterface<T> simulator)
    {
        this.simulator = simulator;
        Level oldLevel = CategoryLogger.getLogLevel(Cat.DSOL);
        String oldPattern = CategoryLogger.getPattern(Cat.DSOL);
        CategoryLogger.removeLogCategory(Cat.DSOL);
        CategoryLogger.addLogCategory(Cat.DSOL);
        CategoryLogger.setLogLevel(Cat.DSOL, oldLevel);
        if (oldPattern.contains("%X{simTime}"))
            CategoryLogger.setPattern(Cat.DSOL, oldPattern);
        else
            CategoryLogger.setPattern(Cat.DSOL, "%X{simTime} %-5level %-6logger{0} %class.%method:%line - %msg%n");
        CategoryLogger.addFormatter(Cat.DSOL, "simTime", () -> this.formatter.apply(this.simulator.getSimulatorTime()));
    }

    /**
     * Set a new formatter for the simulator time.
     * @param formatter the new formatter
     */
    public void setFormatter(final Function<T, String> formatter)
    {
        this.formatter = formatter;
    }

}
