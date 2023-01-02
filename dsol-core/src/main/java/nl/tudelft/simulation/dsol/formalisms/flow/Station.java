package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import org.djutils.event.LocalEventProducer;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * A station is an object which can accept other objects.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public abstract class Station<T extends Number & Comparable<T>> extends LocalEventProducer implements StationInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** simulator is the simulator on which behavior is scheduled. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DEVSSimulatorInterface<T> simulator;

    /** destination refers to the next station in the process-model chain. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected StationInterface<T> destination;

    /** the id of the Station. */
    private Serializable id;

    /**
     * constructs a new Station.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; is the simulator on which behavior is scheduled
     */
    public Station(final Serializable id, final DEVSSimulatorInterface<T> simulator)
    {
        this.id = id;
        this.simulator = simulator;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveObject(final Object object)
    {
        this.fireTimedEvent(StationInterface.RECEIVE_EVENT, 1.0, this.simulator.getSimulatorTime());
    }

    /** {@inheritDoc} */
    @Override
    public void setDestination(final StationInterface<T> destination)
    {
        this.destination = destination;
    }

    /**
     * releases an object.
     * @param object Object; is the entity
     */
    protected synchronized void releaseObject(final Object object)
    {
        this.fireTimedEvent(StationInterface.RELEASE_EVENT, 0.0, this.simulator.getSimulatorTime());
        if (this.destination != null)
        {
            this.destination.receiveObject(object);
        }
    }

    /** {@inheritDoc} */
    @Override
    public StationInterface<T> getDestination()
    {
        return this.destination;
    }

    /**
     * @return simulator
     */
    public DEVSSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

}
