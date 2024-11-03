package nl.tudelft.simulation.dsol.logger;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.reference.WeakReference;
import org.djutils.immutablecollections.ImmutableSet;
import org.djutils.logger.CategoryLogger;
import org.djutils.logger.CategoryLogger.DelegateLogger;
import org.djutils.logger.LogCategory;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.writers.Writer;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimLogger implements a logger with functionality of the CategoryLogger, but the logger is simulator-aware and can print the
 * simulator time as part of the log message. Because multiple simulators can run in parallel, the SimLogger class is specific
 * for each instantiated Simulator.
 * <p>
 * Copyright (c) 2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See for
 * project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
@SuppressWarnings("checkstyle:needbraces")
public class SimLogger
{
    /** a weak reference to the simulator to which this logger belongs. */
    private final WeakReference<SimulatorInterface<?>> simulatorReference;

    /** The current message format. */
    private String defaultMessageFormat = CategoryLogger.DEFAULT_MESSAGE_FORMAT;

    /** The current logging level. */
    private Level defaultLevel = Level.INFO;

    /** The writers registered with this CategoryLogger. */
    private final Set<Writer> writers = new LinkedHashSet<>();

    /** The log level per Writer. */
    private final Map<Writer, Level> writerLevels = new LinkedHashMap<>();

    /** The message format per Writer. */
    private final Map<Writer, String> writerFormats = new LinkedHashMap<>();

    /** The categories to log. */
    private final Set<LogCategory> logCategories = new LinkedHashSet<>(256);

    /** A cached immutable copy of the log categories to return to `extending` classes. */
    private ImmutableSet<LogCategory> immutableLogCategories;

    /** The delegate logger instance that does the actual logging work, after a positive filter outcome. */
    private final DelegateSimLogger delegateSimLogger = new DelegateSimLogger();

    /** The delegate logger that returns immediately after a negative filter outcome. */
    public static final DelegateLogger NO_LOGGER = new DelegateLogger(false);

    /**
     * Construct a simulator-specific logger.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator to which this logger belongs
     */
    public SimLogger(final SimulatorInterface<?> simulator)
    {
        this.simulatorReference = new WeakReference<>(simulator);
    }

    /**
     * Set the formatter to include the simulator time in the log messages.
     * @param simTimeFormatter SimTimeFormatter; the new formatter
     */
    public void setSimTimeFormatter(final SimTimeFormatter simTimeFormatter)
    {
        this.delegateSimLogger.simTimeFormatter = simTimeFormatter;
    }

    /**
     * Set a new logging format for the message lines of all writers. The default message format is:<br>
     * {class_name}.{method}:{line} {message|indent=4}<br>
     * <br>
     * A few popular placeholders that can be used:<br>
     * - {class} Fully-qualified class name where the logging request is issued<br>
     * - {class_name} Class name (without package) where the logging request is issued<br>
     * - {date} Date and time of the logging request, e.g. {date:yyyy-MM-dd HH:mm:ss} [SimpleDateFormat]<br>
     * - {level} Logging level of the created log entry<br>
     * - {line} Line number from where the logging request is issued<br>
     * - {message} Associated message of the created log entry<br>
     * - {method} Method name from where the logging request is issued<br>
     * - {package} Package where the logging request is issued<br>
     * @see <a href="https://tinylog.org/configuration#format">https://tinylog.org/configuration</a>
     * @param newMessageFormat String; the new formatting pattern to use for all registered writers
     */
    public void setAllLogMessageFormat(final String newMessageFormat)
    {
        CategoryLogger.setAllLogMessageFormat(newMessageFormat);
    }

    /**
     * Set a new logging level for all registered writers.
     * @param newLevel Level; the new log level for all registered writers
     */
    public void setAllLogLevel(final Level newLevel)
    {
        CategoryLogger.setAllLogLevel(newLevel);
    }

    /**
     * Set a new logging format for the message lines of a writer. The default message format is:<br>
     * {class_name}.{method}:{line} {message|indent=4}<br>
     * <br>
     * A few popular placeholders that can be used:<br>
     * - {class} Fully-qualified class name where the logging request is issued<br>
     * - {class_name} Class name (without package) where the logging request is issued<br>
     * - {date} Date and time of the logging request, e.g. {date:yyyy-MM-dd HH:mm:ss} [SimpleDateFormat]<br>
     * - {level} Logging level of the created log entry<br>
     * - {line} Line number from where the logging request is issued<br>
     * - {message} Associated message of the created log entry<br>
     * - {method} Method name from where the logging request is issued<br>
     * - {package} Package where the logging request is issued<br>
     * @see <a href="https://tinylog.org/configuration#format">https://tinylog.org/configuration</a>
     * @param writer Writer; the writer to change the mesage format for
     * @param newMessageFormat String; the new formatting pattern to use for all registered writers
     */
    public void setLogMessageFormat(final Writer writer, final String newMessageFormat)
    {
        CategoryLogger.setLogMessageFormat(writer, newMessageFormat);
    }

    /**
     * Set a new logging level for one of the registered writers.
     * @param writer Writer; the writer to change the log level for
     * @param newLevel Level; the new log level for the writer
     */
    public void setLogLevel(final Writer writer, final Level newLevel)
    {
        CategoryLogger.setLogLevel(writer, newLevel);
    }

    /**
     * Add a category to be logged to the Writers.
     * @param logCategory LogCategory; the LogCategory to add
     */
    public void addLogCategory(final LogCategory logCategory)
    {
        CategoryLogger.addLogCategory(logCategory);
    }

    /**
     * Remove a category to be logged to the Writers.
     * @param logCategory LogCategory; the LogCategory to remove
     */
    public void removeLogCategory(final LogCategory logCategory)
    {
        CategoryLogger.removeLogCategory(logCategory);
    }

    /**
     * Set the categories to be logged to the Writers.
     * @param newLogCategories LogCategory...; the LogCategories to set, replacing the previous ones
     */
    public void setLogCategories(final LogCategory... newLogCategories)
    {
        CategoryLogger.setLogCategories(newLogCategories);
    }

    /* ****************************************** FILTER ******************************************/

    /**
     * The "pass" filter that will result in always trying to log.
     * @return the logger that tries to execute logging (delegateLogger)
     */
    public DelegateLogger always()
    {
        return this.delegateSimLogger;
    }

    /**
     * Check whether the provided category needs to be logged. Note that when LogCategory.ALL is contained in the categories,
     * filter will return true.
     * @param logCategory LogCategory; the category to check for.
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public DelegateLogger filter(final LogCategory logCategory)
    {
        if (CategoryLogger.getLogCategories().contains(LogCategory.ALL))
            return this.delegateSimLogger;
        if (CategoryLogger.getLogCategories().contains(logCategory))
            return this.delegateSimLogger;
        return CategoryLogger.NO_LOGGER;
    }

    /**
     * Check whether the provided categories contain one or more categories that need to be logged. Note that when
     * LogCategory.ALL is contained in the categories, filter will return true.
     * @param filterCategories LogCategory...; elements or array with the categories to check for
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public DelegateLogger filter(final LogCategory... filterCategories)
    {
        if (CategoryLogger.getLogCategories().contains(LogCategory.ALL))
            return this.delegateSimLogger;
        for (LogCategory logCategory : filterCategories)
        {
            if (CategoryLogger.getLogCategories().contains(logCategory))
                return this.delegateSimLogger;
        }
        return CategoryLogger.NO_LOGGER;
    }

    /**
     * Check whether the provided categories contain one or more categories that need to be logged. Note that when
     * LogCategory.ALL is contained in the categories, filter will return true.
     * @param filterCategories Set&lt;LogCategory&gt;; the categories to check for
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public DelegateLogger filter(final Set<LogCategory> filterCategories)
    {
        if (CategoryLogger.getLogCategories().contains(LogCategory.ALL))
            return this.delegateSimLogger;
        for (LogCategory logCategory : filterCategories)
        {
            if (CategoryLogger.getLogCategories().contains(logCategory))
                return this.delegateSimLogger;
        }
        return CategoryLogger.NO_LOGGER;
    }

    /**
     * DelegateSimLogger class that takes care of actually logging the message and/or exception. The methods take care of
     * inserting the simulation time in the message.<br>
     * <br>
     * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
     */
    public static class DelegateSimLogger extends DelegateLogger
    {
        /** the simulation time formatter. */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        SimTimeFormatter simTimeFormatter = new DefaultSimTimeFormatter(null);

        /**
         * Create an instance of the DelegateSimLogger that takes care of actually logging the message and/or exception.
         */
        public DelegateSimLogger()
        {
            super(true);
        }

        /* ****************************************** TRACE ******************************************/

        @Override
        public void trace(final Object object)
        {
            LogEntryForwarder.forward(1, Level.TRACE, this.simTimeFormatter.format(object.toString()));
        }

        @Override
        public void trace(final String message)
        {
            LogEntryForwarder.forward(1, Level.TRACE, this.simTimeFormatter.format(message));
        }

        @Override
        public void trace(final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.TRACE, this.simTimeFormatter.format(message), arguments);
        }

        @Override
        public void trace(final Throwable exception)
        {
            LogEntryForwarder.forward(1, Level.TRACE, exception, this.simTimeFormatter.format(exception.getMessage()));
        }

        @Override
        public void trace(final Throwable exception, final String message)
        {
            LogEntryForwarder.forward(1, Level.TRACE, exception, this.simTimeFormatter.format(message));
        }

        @Override
        public void trace(final Throwable exception, final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.TRACE, exception, this.simTimeFormatter.format(message), arguments);
        }

        /* ****************************************** DEBUG ******************************************/

        @Override
        public void debug(final Object object)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, this.simTimeFormatter.format(object.toString()));
        }

        @Override
        public void debug(final String message)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, this.simTimeFormatter.format(message));
        }

        @Override
        public void debug(final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, this.simTimeFormatter.format(message), arguments);
        }

        @Override
        public void debug(final Throwable exception)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, exception, this.simTimeFormatter.format(exception.getMessage()));
        }

        @Override
        public void debug(final Throwable exception, final String message)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, exception, this.simTimeFormatter.format(message));
        }

        @Override
        public void debug(final Throwable exception, final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.DEBUG, exception, this.simTimeFormatter.format(message), arguments);
        }

        /* ****************************************** INFO ******************************************/

        @Override
        public void info(final Object object)
        {
            LogEntryForwarder.forward(1, Level.INFO, this.simTimeFormatter.format(object.toString()));
        }

        @Override
        public void info(final String message)
        {
            LogEntryForwarder.forward(1, Level.INFO, this.simTimeFormatter.format(message));
        }

        @Override
        public void info(final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.INFO, this.simTimeFormatter.format(message), arguments);
        }

        @Override
        public void info(final Throwable exception)
        {
            LogEntryForwarder.forward(1, Level.INFO, exception, this.simTimeFormatter.format(exception.getMessage()));
        }

        @Override
        public void info(final Throwable exception, final String message)
        {
            LogEntryForwarder.forward(1, Level.INFO, exception, this.simTimeFormatter.format(message));
        }

        @Override
        public void info(final Throwable exception, final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.INFO, exception, this.simTimeFormatter.format(message), arguments);
        }

        /* ****************************************** WARN ******************************************/

        @Override
        public void warn(final Object object)
        {
            LogEntryForwarder.forward(1, Level.WARNING, this.simTimeFormatter.format(object.toString()));
        }

        @Override
        public void warn(final String message)
        {
            LogEntryForwarder.forward(1, Level.WARNING, this.simTimeFormatter.format(message));
        }

        @Override
        public void warn(final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.WARNING, this.simTimeFormatter.format(message), arguments);
        }

        @Override
        public void warn(final Throwable exception)
        {
            LogEntryForwarder.forward(1, Level.WARNING, exception, this.simTimeFormatter.format(exception.getMessage()));
        }

        @Override
        public void warn(final Throwable exception, final String message)
        {
            LogEntryForwarder.forward(1, Level.WARNING, exception, this.simTimeFormatter.format(message));
        }

        @Override
        public void warn(final Throwable exception, final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.WARNING, exception, this.simTimeFormatter.format(message), arguments);
        }

        /* ****************************************** ERROR ******************************************/

        @Override
        public void error(final Object object)
        {
            LogEntryForwarder.forward(1, Level.ERROR, this.simTimeFormatter.format(object.toString()));
        }

        @Override
        public void error(final String message)
        {
            LogEntryForwarder.forward(1, Level.ERROR, this.simTimeFormatter.format(message));
        }

        @Override
        public void error(final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.ERROR, this.simTimeFormatter.format(message), arguments);
        }

        @Override
        public void error(final Throwable exception)
        {
            LogEntryForwarder.forward(1, Level.ERROR, exception, this.simTimeFormatter.format(exception.getMessage()));
        }

        @Override
        public void error(final Throwable exception, final String message)
        {
            LogEntryForwarder.forward(1, Level.ERROR, exception, this.simTimeFormatter.format(message));
        }

        @Override
        public void error(final Throwable exception, final String message, final Object... arguments)
        {
            LogEntryForwarder.forward(1, Level.ERROR, exception, this.simTimeFormatter.format(message), arguments);
        }

    }
}
