package nl.tudelft.simulation.introspection.fields;

import java.lang.reflect.Field;

import nl.tudelft.simulation.introspection.AbstractProperty;
import nl.tudelft.simulation.introspection.Property;

/**
 * The field implementation of the Property interface. See for details.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
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
     * @param owner Object; its owner
     * @param descriptor Field; the descriptor
     * @param editable boolean; is the property editable
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
     * @param owner Object; its owner
     * @param descriptor Field; the descriptor
     */
    public FieldProperty(final Object owner, final Field descriptor)
    {
        this(owner, descriptor, true);
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.descriptor.getName();
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getType()
    {
        return this.descriptor.getType();
    }

    /** {@inheritDoc} */
    @Override
    public void setRegularValue(final Object value)
    {
        Class<?> type = this.descriptor.getType();
        if (!type.isInstance(value) || !this.editable)
        {
            throw new IllegalArgumentException("Cannot assign " + value + " to " + this.owner + ", " + this.descriptor);
        }
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public Object getInstance()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEditable()
    {
        return this.editable;
    }
}
