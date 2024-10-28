package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

/**
 * Entity is a generic object that can flow through the model. It stores its creation time, so it can tally the 'time in system'
 * when leaving the model.
 * <p>
 * Copyright (c) 2023-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Entity<T extends Number & Comparable<T>> implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20230524L;

    /** the entity's id. */
    private String id;

    /** the creation time. */
    private final T creationTime;

    /** private attributes; the map is only created when needed. */
    private Map<String, Object> attributes = null;

    /**
     * Construct a new Entity with a creation time.
     * @param id String; the entity's id
     * @param creationTime T; the creation time
     */
    public Entity(final String id, final T creationTime)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(creationTime, "creation time cannot be null");
        this.id = id;
        this.creationTime = creationTime;
    }
    
    /** 
     * Return a clone of the entity. This clone is a literal clone, including the creation time and id.
     * @return Entity&lt;T&gt;; a literal clone of the entity
     */
    @Override
    public Entity<T> clone()
    {
        Entity<T> clone = new Entity<T>(this.id, this.creationTime);
        if (this.attributes != null)
        {
            clone.attributes = new LinkedHashMap<>(this.attributes);
        }
        return clone;
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
     * Add a String as an attribute to the entity.
     * @param key String; the key of the attribute
     * @param value String; the value to store
     */
    public void setStringValue(final String key, final String value)
    {
        if (this.attributes == null)
            this.attributes = new LinkedHashMap<>();
        this.attributes.put(key, value);
    }

    /**
     * Retrieve a stored String attribute value.
     * @param key String; the id of the attribute
     * @return String; the stored value
     */
    public String getStringValue(final String key)
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
    public void setNumberValue(final String key, final Number value)
    {
        if (this.attributes == null)
            this.attributes = new LinkedHashMap<>();
        this.attributes.put(key, value);
    }

    /**
     * Retrieve a stored Number attribute value.
     * @param key String; the id of the attribute
     * @return Number; the stored value
     */
    public Number getNumberValue(final String key)
    {
        if (this.attributes == null)
            return null;
        return (Number) this.attributes.get(key);
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
    public String getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return "Entity [id=" + this.id + ", creationTime=" + this.creationTime + "]";
    }

}
