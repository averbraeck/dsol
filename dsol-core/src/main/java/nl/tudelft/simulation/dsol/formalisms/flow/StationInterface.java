package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.TimedEventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * A station is an object which accepts other objects and is linked to a destination.
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

public interface StationInterface<
        T extends Number & Comparable<T>> extends EventProducerInterface
{
    /** RECEIVE_EVENT is fired whenever an entity enters the station. */
    TimedEventType RECEIVE_EVENT = new TimedEventType(new MetaData("RECEIVE_EVENT", "Object received",
            new ObjectDescriptor("receivedObject", "received object", Serializable.class)));

    /** RECEIVE_EVENT is fired whenever an entity leaves the station. */
    TimedEventType RELEASE_EVENT = new TimedEventType(new MetaData("RELEASE_EVENT", "Object released",
            new ObjectDescriptor("releasedObject", "released object", Serializable.class)));

    /**
     * Method getDestination.
     * @return StationInterface is the destination of this station
     */
    StationInterface<T> getDestination();

    /**
     * receives an object is invoked whenever an entity arrives.
     * @param object Object; is the entity
     */
    void receiveObject(Object object);

    /**
     * sets the destination of this object.
     * @param destination StationInterface&lt;A,R,T&gt;; defines the next station in the model
     */
    void setDestination(StationInterface<T> destination);

}
