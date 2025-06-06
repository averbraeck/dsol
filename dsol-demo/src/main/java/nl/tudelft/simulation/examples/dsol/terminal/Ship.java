package nl.tudelft.simulation.examples.dsol.terminal;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Ship extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the number of loaded containers. */
    private int containers = 0;

    /** the ship's capacity. */
    private final int capacity;

    /** the ship-full event. */
    public static final EventType SHIP_FULL_EVENT = new EventType("SHIP_FULL_EVENT");

    /**
     * @param capacity the ship's capacity
     */
    public Ship(final int capacity)
    {
        this.capacity = capacity;
    }

    /**
     * increase the number of containers and fire an event when full.
     */
    public synchronized void incContainers()
    {
        this.containers++;
        if (this.containers >= this.capacity)
        {
            if (Terminal.DEBUG)
            { System.out.println("SHIP IS FULL -- EVENT FIRED"); }
            fireEvent(SHIP_FULL_EVENT, this.containers);
        }
    }

    /**
     * @return containers
     */
    public int getContainers()
    {
        return this.containers;
    }
}
