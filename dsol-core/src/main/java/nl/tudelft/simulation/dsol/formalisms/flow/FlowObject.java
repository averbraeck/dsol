package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.base.Identifiable;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * A FlowObject that can receive and/or release Entity objects.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class FlowObject<T extends Number & Comparable<T>> extends LocalEventProducer implements Identifiable
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the simulator on which behavior is scheduled. */
    private DevsSimulatorInterface<T> simulator;

    /** the next flow object in the process-model chain. */
    private FlowObject<T> destination;

    /** the number of entities in the flow object. */
    private int numberEntities = 0;

    /** the id of the FlowObject. */
    private final String id;

    /** RECEIVE_EVENT is fired whenever an entity enters the flow object. */
    public static final EventType RECEIVE_EVENT = new EventType(new MetaData("RECEIVE_EVENT", "Entity received",
            new ObjectDescriptor("receivedEntity", "number of entities in flow object", Integer.class)));

    /** RELEASE_EVENT is fired whenever an entity leaves the flow object. */
    public static final EventType RELEASE_EVENT = new EventType(new MetaData("RELEASE_EVENT", "Entity released",
            new ObjectDescriptor("releasedEntity", "number of entities in flow object", Integer.class)));

    /**
     * Construct a new FlowObject.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the simulator on which behavior is scheduled
     */
    public FlowObject(final String id, final DevsSimulatorInterface<T> simulator)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        this.id = id;
        this.simulator = simulator;
    }

    /**
     * Arrival of a new entity into the flow object.
     * @param entity Entity; the arriving entity
     */
    public void receiveEntity(final Entity<T> entity)
    {
        this.numberEntities++;
        this.fireTimedEvent(RECEIVE_EVENT, this.numberEntities, this.simulator.getSimulatorTime());
    }

    /**
     * Set the destination of this flow object. The destination is the object the entity will go to when leaving this flow
     * object.
     * @param destination FlowObject&lt;T&gt;; the next flow object in the model
     */
    public void setDestination(final FlowObject<T> destination)
    {
        this.destination = destination;
    }

    /**
     * Release an entity, making it flow to the next flow object (the destination) when destination is not null. When
     * destination is null, the entity will be discarded.
     * @param entity Entity; the entity to release
     */
    protected synchronized void releaseEntity(final Entity<T> entity)
    {
        this.numberEntities--;
        this.fireTimedEvent(RELEASE_EVENT, this.numberEntities, this.simulator.getSimulatorTime());
        if (this.destination != null)
        {
            this.destination.receiveEntity(entity);
        }
    }

    /**
     * Return the current destination.
     * @return FlowObject; the destination of this flow object
     */
    public FlowObject<T> getDestination()
    {
        return this.destination;
    }

    /**
     * Return the simulator.
     * @return DevsSimultorInterface&lt;T&gt;; the simulator
     */
    public DevsSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "FlowObject [id=" + this.id + "]";
    }

}
