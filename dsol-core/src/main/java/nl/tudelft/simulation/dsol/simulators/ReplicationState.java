package nl.tudelft.simulation.dsol.simulators;

/**
 * ReplicationState indicates the precise state of the replication that is being executed by the Simulator.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public enum ReplicationState
{
    /** The simulator has been instantiated, but not yet initialized with a Replication. */
    NOT_INITIALIZED,

    /**
     * The simulator has been initialized with the replication, but it has not been started yet, and the the
     * START_REPLICATION_EVENT has not yet been fired.
     */
    INITIALIZED,

    /** The execution of the replication has started, the START_REPLICATION_EVENT has been fired. */
    STARTED,

    /** The replication has ended, but the run() thread is still running; the END_REPLICATION_EVENT has not yet been fired. */
    ENDING,

    /** The replication has ended, and the simulator cannot be restarted; the END_REPLICATION_EVENT has been fired. */
    ENDED;
}
