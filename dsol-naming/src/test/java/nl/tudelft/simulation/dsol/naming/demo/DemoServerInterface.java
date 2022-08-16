package nl.tudelft.simulation.dsol.naming.demo;

import org.djutils.event.EventType;
import org.djutils.event.remote.RemoteEventProducerInterface;
import org.djutils.metadata.MetaData;

/**
 * DemoServerInterface.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface DemoServerInterface extends RemoteEventProducerInterface
{
    /** the exit event. */
    public static final EventType EXIT_EVENT = new EventType(new MetaData("EXIT", "exit"));

}
