package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The exit station on which statistics are updated and entities destroyed.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author Peter Jacobs, Alexander Verbraeck
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Departure<T extends Number & Comparable<T>> extends Station<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for Departure.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; on which is scheduled
     */
    public Departure(final Serializable id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void receiveObject(final Object object)
    {
        this.fireTimedEvent(Station.RECEIVE_EVENT, (Serializable) object, getSimulator().getSimulatorTime());
        this.fireTimedEvent(Station.RELEASE_EVENT, (Serializable) object, getSimulator().getSimulatorTime());
    }

}
