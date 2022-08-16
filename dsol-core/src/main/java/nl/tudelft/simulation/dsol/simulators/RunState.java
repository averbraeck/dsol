package nl.tudelft.simulation.dsol.simulators;

/**
 * RunState indicates the precise state of the Simulator.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum RunState
{
    /** The simulator has been instantiated, but not yet initialized with a Replication. */
    NOT_INITIALIZED,

    /** The replication has started, and the simulator has been initialized, but it has not been started yet. */
    INITIALIZED,

    /** The Simulator has been started, but the run() thread did not start yet. */
    STARTING,

    /** The Simulator run() thread has started; the simulation is running. */
    STARTED,

    /** The stopping of the simulator has been initiated, but the run() thread is still running. */
    STOPPING,

    /** The Simulator run() thread has been stopped; the simulator is not running. */
    STOPPED,

    /** The replication has ended, and the simulator cannot be restarted. */
    ENDED;
}
