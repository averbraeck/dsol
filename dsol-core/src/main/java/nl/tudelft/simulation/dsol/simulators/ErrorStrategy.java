package nl.tudelft.simulation.dsol.simulators;

import org.pmw.tinylog.Level;

/**
 * ErrorStrategy indicates what to do when there is an error in the execution of the simulation. In order to set the error
 * handling, the setPauseOnError() and isPauseOnError() methods are deprecated and replaced by new
 * setErrorStrategy(ErrorStrategy strategy) and getErrorStrategy() methods. The log level can be overridden (and even be set to
 * NONE).
 * <p>
 * Copyright (c) 2021-2021 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum ErrorStrategy
{
    /**
     * Send the error to the logger as WARNING. Both RunState and ReplicationState remain in the RUNNING state. The
     * Simulator.run() continues as if the error did not occur.
     */
    LOG_AND_CONTINUE(Level.WARNING),

    /**
     * Send the error to logger as ERROR and print the exception on stderr. Both RunState and ReplicationState remain in the
     * RUNNING state. The Simulator.run() continues as if the error did not occur.
     */
    WARN_AND_CONTINUE(Level.ERROR),

    /**
     * Send the error to logger as ERROR and print the exception on stderr The RunState goes to STOPPING, leading to the stop of
     * the loop in the Simulator.run() method and a subsequent STOPPED state in the SimulatorWorkerThread.run() method. The
     * SimulatorWorkerThread will go into a Thread.wait(), to wait for start (or cleanup).
     */
    WARN_AND_PAUSE(Level.ERROR),

    /**
     * Send the error to logger as ERROR and print the exception on stderr The Simulator.cleanup() method is called to ensure
     * the SimulatorWorkerThread.run() method completely ends and can be garbage collected. If there is a UI thread, it will
     * keep running.
     */
    WARN_AND_END(Level.ERROR),

    /**
     * Send the error to logger as ERROR and print the exception on stderr The Simulator.cleanup() method is called to ensure
     * the stop of the run() in SimulatorWorkerThread; the System.exit() method is called to end the complete program.
     */
    WARN_AND_EXIT(Level.ERROR);

    /** the default log level for the ErrorStrategy (can be overridden in the Simulator). */
    private final Level defaultLogLevel;

    /**
     * Create the enum for the ErrorStrategy and define the default log level to report the error in the logger with.
     * @param defaultLogLevel LogLevel: the default log level for the ErrorStrategy (can be overridden in the Simulator)
     */
    ErrorStrategy(final Level defaultLogLevel)
    {
        this.defaultLogLevel = defaultLogLevel;
    }

    /**
     * Return the default log level for the ErrorStrategy (can be overridden in the Simulator) .
     * @return Level: the default log level for the ErrorStrategy
     */
    public final Level getDefaultLogLevel()
    {
        return this.defaultLogLevel;
    }

}
