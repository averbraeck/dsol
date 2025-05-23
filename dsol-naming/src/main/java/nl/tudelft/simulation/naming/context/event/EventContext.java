package nl.tudelft.simulation.naming.context.event;

import org.djutils.event.EventProducer;

import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * EventContext is the tagging interface combining the ContextInterface, EventContextInterface, and EventProducer.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface EventContext extends ContextInterface, EventContextInterface, EventProducer
{
    // tagging interface
}
