package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * A station is an object which can receive and/or release other objects that are often called entities.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public abstract class Station<T extends Number & Comparable<T>> extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** simulator is the simulator on which behavior is scheduled. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DevsSimulatorInterface<T> simulator;

    /** destination refers to the next station in the process-model chain. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Station<T> destination;

    /** the id of the Station. */
    private final Serializable id;

    /** RECEIVE_EVENT is fired whenever an entity enters the station. */
    public static final EventType RECEIVE_EVENT = new EventType(new MetaData("RECEIVE_EVENT", "Object received",
            new ObjectDescriptor("receivedObject", "received object", Serializable.class)));

    /** RECEIVE_EVENT is fired whenever an entity leaves the station. */
    public static final EventType RELEASE_EVENT = new EventType(new MetaData("RELEASE_EVENT", "Object released",
            new ObjectDescriptor("releasedObject", "released object", Serializable.class)));

    /**
     * constructs a new Station.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; is the simulator on which behavior is scheduled
     */
    public Station(final Serializable id, final DevsSimulatorInterface<T> simulator)
    {
        this.id = id;
        this.simulator = simulator;
    }

    /**
     * receives an object is invoked whenever an entity arrives.
     * @param object Object; is the entity
     */
    public void receiveObject(final Object object)
    {
        this.fireTimedEvent(RECEIVE_EVENT, 1.0, this.simulator.getSimulatorTime());
    }

    /**
     * sets the destination of this object.
     * @param destination Station&lt;A,R,T&gt;; defines the next station in the model
     */
    public void setDestination(final Station<T> destination)
    {
        this.destination = destination;
    }

    /**
     * releases an object.
     * @param object Object; is the entity
     */
    protected synchronized void releaseObject(final Object object)
    {
        this.fireTimedEvent(RELEASE_EVENT, 0.0, this.simulator.getSimulatorTime());
        if (this.destination != null)
        {
            this.destination.receiveObject(object);
        }
    }

    /**
     * Method getDestination.
     * @return Station is the destination of this station
     */
    public Station<T> getDestination()
    {
        return this.destination;
    }

    /**
     * @return simulator
     */
    public DevsSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    /**
     * @return id
     */
    public Serializable getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Station [id=" + this.id + "]";
    }

}
