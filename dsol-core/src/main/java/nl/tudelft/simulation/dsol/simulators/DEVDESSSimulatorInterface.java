package nl.tudelft.simulation.dsol.simulators;

/**
 * The DEVSDESS simulator embodies both the continuous and the discrete formalism. This simulator takes pre-defined time steps
 * in between it loops over its eventlist. A better name for this formalism would therefore be the DEVSinDESS formalism. More
 * information on Modeling and Simulation can be found in Theory of Modeling and Simulation by Bernard Zeigler et.al.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
@SuppressWarnings("linelength")
public interface DEVDESSSimulatorInterface<T extends Number & Comparable<T>>
        extends DevsxSimulatorInterface<T>, DESSSimulatorInterface<T>
{
    // This interface combines the DESS and DEVS interfaces and does not add any operations.
}
