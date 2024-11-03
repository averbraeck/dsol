package nl.tudelft.simulation.naming.context;

import java.rmi.Remote;

import org.djutils.event.EventProducer;

/**
 * RemoteContextInterface is a tagging interface that extends both ContectInterface and Remote, but that does not force any new
 * methods to be implemented. All methods of the ContextInterface can already throw RemoteException, so they are ready for
 * implementation as a RemoteContext.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface RemoteContextInterface extends ContextInterface, EventProducer, Remote
{
    // tagging interface that extends both ContectInterface and Remote
}
