package nl.tudelft.simulation.dsol.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
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
 */
public class SimLogger
{
    /** the simulator. */
    private final SimulatorInterface<?> simulator;

    /**
     * Register a logger for a simulator with time.
     * @param simulator the simulator for which to register a logger with time
     */
    public SimLogger(final SimulatorInterface<?> simulator)
    {
        this.simulator = simulator;

        LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 1) Register custom conversion word -> converter class
        @SuppressWarnings("unchecked")
        Map<String, String> registry = (Map<String, String>) ctx.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (registry == null)
        {
            registry = new HashMap<>();
            ctx.putObject(CoreConstants.PATTERN_RULE_REGISTRY, registry);
        }
        registry.put("simTime", "nl.tudelft.simulation.dsol.logger.SimLogger.SimTimeConverter");

        // 2) Build appender/encoder programmatically
        // TODO: This has to be left to the standard CategoryLogger methods
        PatternLayoutEncoder enc = new PatternLayoutEncoder();
        enc.setContext(ctx);
        enc.setPattern("[%XsimTime] %-5level %logger{36} - %msg%n");
        enc.start();

        ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
        console.setContext(ctx);
        console.setEncoder(enc);
        console.start();

        ch.qos.logback.classic.Logger root =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.addAppender(console);
        root.setAdditive(false);
    }

    /** */
    public class SimTimeConverter extends ClassicConverter
    {
        @Override
        public String convert(final ILoggingEvent event)
        {
            return SimLogger.this.simulator.getSimTimeFormatter().formattedSimTime();
        }
    }
}
