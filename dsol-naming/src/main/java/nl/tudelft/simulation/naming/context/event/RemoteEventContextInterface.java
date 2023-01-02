package nl.tudelft.simulation.naming.context.event;

import java.rmi.Remote;

import org.djutils.event.EventProducer;

import nl.tudelft.simulation.naming.context.RemoteContextInterface;

/**
 * RemoteEventContextInterface.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface RemoteEventContextInterface
        extends EventProducer, EventContext, RemoteContextInterface, Remote
{
    // tagging interface
}
