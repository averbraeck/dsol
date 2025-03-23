package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rits.cloning.Cloner;

import nl.tudelft.simulation.dsol.eventlists.EventListInterface;
import nl.tudelft.simulation.dsol.experiment.Experiment;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.RunControl;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.experiment.Treatment;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Entity is a generic object that can flow through the model. It stores its creation time, so it can tally the 'time in system'
 * when leaving the model.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Entity<T extends Number & Comparable<T>> extends Block<T> implements Serializable
{
    /** */
    private static final long serialVersionUID = 20230524L;

    /** the creation time. */
    private final T creationTime;

    /** private attributes; the map is only created when needed. */
    private Map<String, Object> attributes = null;

    /** the cloner to clone entities. */
    private static final Cloner cloner = Cloner.standard();

    static
    {
        cloner.dontCloneInstanceOf(StreamInterface.class, SimulatorInterface.class, DsolModel.class, Replication.class,
                Experiment.class, RunControl.class, Treatment.class, StreamInformation.class, EventListInterface.class,
                InputParameter.class, SimLogger.class);
    }

    /**
     * Construct a new Entity with a creation time.
     * @param id String; the entity's id
     * @param simulator the simulator to retrieve the creation time
     */
    public Entity(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
        this.creationTime = simulator.getSimulatorTime();
    }

    /**
     * Return a clone of the entity. This clone is a literal clone, including the creation time and id.
     * @return Entity&lt;T&gt;; a literal clone of the entity
     */
    @Override
    public Entity<T> clone()
    {
        return cloner.deepClone(this);
    }

    /**
     * Add a timestamp to the entity, e.g., to register a tally statistic later.
     * @param timestampId String; the id of the timestamp
     * @param time T; the time to store
     */
    public void setTimestamp(final String timestampId, final T time)
    {
        if (this.attributes == null)
            this.attributes = new LinkedHashMap<>();
        this.attributes.put(timestampId, time);
    }

    /**
     * Retrieve a stored timestamp.
     * @param timestampId String; the id of the timestamp
     * @return T; the creation time
     */
    @SuppressWarnings("unchecked")
    public T getTimestamp(final String timestampId)
    {
        if (this.attributes == null)
            return null;
        return (T) this.attributes.get(timestampId);
    }

    /**
     * Add an object as an attribute to the entity.
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
     * Add a String as an attribute to the entity.
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
     * Add a Number as an attribute to the entity.
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
     * Return the creation time.
     * @return T; the creation time
     */
    public T getCreationTime()
    {
        return this.creationTime;
    }

    @Override
    public String toString()
    {
        return "Entity [id=" + getId() + ", creationTime=" + this.creationTime + "]";
    }

}
