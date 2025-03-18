package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;

import com.google.gson.Gson;

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
     * Deep clone of a (non-serializable) object.
     * @param <O> the object type
     * @param object the object
     * @param clazz the class of the object
     * @return a deep copy
     */
    protected <O> O deepClone(final Object object, final Class<O> clazz)
    {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(object), clazz);
    }

    /**
     * Return a clone of the entity. This clone is a literal clone, including the creation time and id.
     * @return Entity&lt;T&gt;; a literal clone of the entity
     */
    @SuppressWarnings("unchecked")
    @Override
    public Entity<T> clone()
    {
        Entity<T> clone = new Entity<T>(this.id, this.creationTime);
        if (this.attributes != null)
        {
            clone.attributes = deepClone(this.attributes, LinkedHashMap.class);
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
