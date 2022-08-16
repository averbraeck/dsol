package nl.tudelft.simulation.naming.context;

import java.rmi.Remote;

import org.djutils.event.remote.RemoteEventProducerInterface;

/**
 * RemoteContextInterface is a tagging interface that extends both ContectInterface and Remote, but that does not force any new
 * methods to be implemented. All methods of the ContextInterface can already throw RemoteException, so they are ready for
 * implementation as a RemoteContext.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface RemoteContextInterface extends ContextInterface, RemoteEventProducerInterface, Remote
{
    // tagging interface that extends both ContectInterface and Remote
}
