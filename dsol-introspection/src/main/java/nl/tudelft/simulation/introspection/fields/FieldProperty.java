package nl.tudelft.simulation.introspection.fields;

import java.lang.reflect.Field;

import nl.tudelft.simulation.introspection.AbstractProperty;
import nl.tudelft.simulation.introspection.Property;

/**
 * The field implementation of the Property interface. See for details.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author Niels Lang.
 * @since 1.5
 */
public class FieldProperty extends AbstractProperty implements Property
{
    /** the owner of the fieldProperty */
    private Object owner = null;

    /** the descriptor of the field. */
    private Field descriptor = null;

    /** is the property editable. */
    private boolean editable = false;

    /**
     * constructs a new FieldProperty.
     * @param owner its owner
     * @param descriptor the descriptor
     * @param editable is the property editable
     */
    public FieldProperty(final Object owner, final Field descriptor, final boolean editable)
    {
        // Check whether descriptor is valid for owner should be conducted here
        this.owner = owner;
        this.descriptor = descriptor;
        this.descriptor.setAccessible(true);
        this.editable = editable;
    }

    /**
     * constructs a new FieldProperty.
     * @param owner its owner
     * @param descriptor the descriptor
     */
    public FieldProperty(final Object owner, final Field descriptor)
    {
        this(owner, descriptor, true);
    }

    @Override
    public String getName()
    {
        return this.descriptor.getName();
    }

    @Override
    public Class<?> getType()
    {
        return this.descriptor.getType();
    }

    @Override
    public void setRegularValue(final Object value)
    {
        Class<?> type = this.descriptor.getType();
        if (!type.isInstance(value) || !this.editable)
        { throw new IllegalArgumentException("Cannot assign " + value + " to " + this.owner + ", " + this.descriptor); }
        synchronized (this.owner)
        {
            try
            {
                this.descriptor.set(this.owner, value);
            }
            catch (Exception exception)
            {
                throw new IllegalArgumentException(this + " - setRegularValue", exception);
            }
        }
    }

    @Override
    public Object getValue()
    {
        try
        {
            return this.descriptor.get(this.owner);
        }
        catch (Exception exception)
        {
            throw new IllegalArgumentException(this + " - getValue", exception);
        }
    }

    @Override
    public Object getInstance()
    {
        return this.owner;
    }

    @Override
    public boolean isEditable()
    {
        return this.editable;
    }
}
