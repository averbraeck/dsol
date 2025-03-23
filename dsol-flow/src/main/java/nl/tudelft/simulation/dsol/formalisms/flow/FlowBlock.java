package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;

/**
 * A FlowBlock that can receive and/or release Entity objects.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @param <F> the flow block for the return type in method chaining
 */
public abstract class FlowBlock<T extends Number & Comparable<T>, F extends FlowBlock<T, F>> extends Block<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the next flow object in the process-model chain. */
    private FlowBlock<T, ?> destination;

    /** the number of entities that entered the flow object minus the number of entities that left the object. */
    private int numberEntitiesInMinusOut = 0;

    /** a potential count statistic for the number of received objects; when null, no statistic is calculated. */
    private SimCounter<T> countReceivedStatistic;

    /** a potential count statistic for the number of released objects; when null, no statistic is calculated. */
    private SimCounter<T> countReleasedStatistic;

    /** private flow block attributes; the map is only created when needed. */
    private Map<String, Object> attributes = null;

    /** the function to apply after receiving an entity. */
    private Consumer<Entity<T>> receiveFunction = null;

    /** the function to apply just before releasing an entity. */
    private Consumer<Entity<T>> releaseFunction = null;

    /** RECEIVE_EVENT is fired whenever an entity enters the flow object. */
    public static final EventType RECEIVE_EVENT = new EventType(new MetaData("RECEIVE_EVENT", "Entity received",
            new ObjectDescriptor("receivedEntity", "the number of received entities (1)", Integer.class)));

    /** RELEASE_EVENT is fired whenever an entity leaves the flow object. */
    public static final EventType RELEASE_EVENT = new EventType(new MetaData("RELEASE_EVENT", "Entity released",
            new ObjectDescriptor("releasedEntity", "the number of released entities (1)", Integer.class)));

    /**
     * Construct a new FlowObject.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the simulator on which behavior is scheduled
     */
    public FlowBlock(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Execute the given function for the flow object. The function itself will not be stored.
     * @param function the function to execute
     * @return the flow object for method chaining
     */
    @SuppressWarnings("unchecked")
    public F executeFunction(final Runnable function)
    {
        Throw.whenNull(function, "function to execute cannot be null");
        function.run();
        return (F) this;
    }

    /**
     * Set the function to apply after receiving an entity.
     * @param receiveFunction the function to apply after receiving an entity, can be null to remove a function
     * @return the flow object for method chaining
     */
    @SuppressWarnings("unchecked")
    public F setReceiveFunction(final Consumer<Entity<T>> receiveFunction)
    {
        this.receiveFunction = receiveFunction;
        return (F) this;
    }

    /**
     * Set the function to apply just before releasing an entity.
     * @param releaseFunction the function to apply just before releasing an entity, can be null to remove a function
     * @return the flow object for method chaining
     */
    @SuppressWarnings("unchecked")
    public F setReleaseFunction(final Consumer<Entity<T>> releaseFunction)
    {
        this.releaseFunction = releaseFunction;
        return (F) this;
    }

    /**
     * Arrival of a new entity into the flow object.
     * @param entity Entity; the arriving entity
     */
    public void receiveEntity(final Entity<T> entity)
    {
        this.numberEntitiesInMinusOut++;
        this.fireTimedEvent(RECEIVE_EVENT, 1, getSimulator().getSimulatorTime());
        if (this.receiveFunction != null)
            this.receiveFunction.accept(entity);
    }

    /**
     * Set the destination of this flow object. The destination is the object the entity will go to when leaving this flow
     * object. The destination can be null, indicating the entity does not go to a next flow object.
     * @param destination FlowObject&lt;T&gt;; the next flow object in the model, can be null
     * @return the flow object for method chaining
     */
    @SuppressWarnings("unchecked")
    public F setDestination(final FlowBlock<T, ?> destination)
    {
        this.destination = destination;
        return (F) this;
    }

    /**
     * Add two statistics to count the number of received and the number of released entities for this flow block.
     * @return the flow object for method chaining
     */
    @SuppressWarnings("unchecked")
    public F setDefaultFlowObjectStatistics()
    {
        this.countReceivedStatistic =
                new SimCounter<>(getId() + " # of received entities", getSimulator().getModel(), this, RECEIVE_EVENT);
        this.countReceivedStatistic.initialize();
        this.countReleasedStatistic =
                new SimCounter<>(getId() + " # of released entities", getSimulator().getModel(), this, RELEASE_EVENT);
        this.countReleasedStatistic.initialize();
        return (F) this;
    }

    /**
     * Release an entity, making it flow to the next flow object (the destination) when destination is not null. When
     * destination is null, the entity will be discarded.
     * @param entity Entity; the entity to release
     */
    protected synchronized void releaseEntity(final Entity<T> entity)
    {
        this.numberEntitiesInMinusOut--;
        this.fireTimedEvent(RELEASE_EVENT, 1, getSimulator().getSimulatorTime());
        if (this.releaseFunction != null)
            this.releaseFunction.accept(entity);
        if (this.destination != null)
        {
            this.destination.receiveEntity(entity);
        }
    }

    /**
     * Add an object as an attribute to the flow object.
     * @param key String; the key of the attribute
     * @param value Object; the value to store
     */
    public void setAttribute(final String key, final Object value)
    {
        if (this.attributes == null)
            this.attributes = new LinkedHashMap<>();
        this.attributes.put(key, value);
    }

    /**
     * Retrieve a typed object attribute value.
     * @param key String; the id of the attribute
     * @param clazz Class&lt;VT&gt; the class of the object to return
     * @return VT; the stored value
     * @param <VT> the class of the attribute value to return
     */
    @SuppressWarnings("unchecked")
    public <VT> VT getAttribute(final String key, final Class<VT> clazz)
    {
        if (this.attributes == null)
            return null;
        return (VT) this.attributes.get(key);
    }

    /**
     * Retrieve an object attribute value.
     * @param key String; the id of the attribute
     * @return Object; the stored value
     */
    public Object getAttribute(final String key)
    {
        return getAttribute(key, Object.class);
    }

    /**
     * Add a String as an attribute to the flow object.
     * @param key String; the key of the attribute
     * @param value String; the value to store
     */
    public void setStringAttribute(final String key, final String value)
    {
        setAttribute(key, value);
    }

    /**
     * Retrieve a stored String attribute value.
     * @param key String; the id of the attribute
     * @return String; the stored value
     */
    public String getStringAttribute(final String key)
    {
        if (this.attributes == null)
            return null;
        return this.attributes.get(key).toString();
    }

    /**
     * Add a Number as an attribute to the flow object.
     * @param key String; the key of the attribute
     * @param value Number; the value to store
     */
    public void setNumberAttribute(final String key, final Number value)
    {
        setAttribute(key, value);
    }

    /**
     * Retrieve a stored Number attribute value.
     * @param key String; the id of the attribute
     * @return Number; the stored value
     */
    public Number getNumberAttribute(final String key)
    {
        return getAttribute(key, Number.class);
    }

    /**
     * Return the current destination.
     * @return FlowObject; the destination of this flow object
     */
    public FlowBlock<T, ?> getDestination()
    {
        return this.destination;
    }

    /**
     * Return the count statistic for the number of received objects.
     * @return the count statistic for the number of received objects, can be null if no statistics are calculated.
     */
    public SimCounter<T> getCountReceivedStatistic()
    {
        return this.countReceivedStatistic;
    }

    /**
     * Return the count statistic for the number of released objects
     * @return the count statistic for the number of released objects, can be null if no statistics are calculated.
     */
    public SimCounter<T> getCountReleasedStatistic()
    {
        return this.countReleasedStatistic;
    }

    /**
     * Return the number of entities that entered the flow object minus the number of entities that left the object.
     * @return the number of entities that entered the flow object minus the number of entities that left the object
     */
    public int getNumberEntitiesInMinusOut()
    {
        return this.numberEntitiesInMinusOut;
    }

    @Override
    public String toString()
    {
        return "FlowObject [id=" + getId() + "]";
    }

}
