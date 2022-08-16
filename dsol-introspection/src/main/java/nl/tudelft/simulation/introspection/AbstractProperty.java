package nl.tudelft.simulation.introspection;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.djutils.immutablecollections.ImmutableCollection;
import org.djutils.immutablecollections.ImmutableMap;

/**
 * A default Property implementation that provides a standard way to handle composite values.
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
public abstract class AbstractProperty implements Property
{
    /**
     * Basic 'setValue' implementation. It is checked whether this property contains a composite value. If so, the composite
     * value of this property is updated. Composite values are expected to be supplied as a {see java.util.Collection}. If
     * needed, array conversion takes place. If the property is not composite, the value-setting is delegated to the
     * 'setRegularValue' method. Maps cannot be updated at this moment.
     * @see nl.tudelft.simulation.introspection.Property#setValue(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setValue(final Object value)
    {
        if (!this.getComposedType().isComposed())
        {
            this.setRegularValue(value);
            return;
        }
        if (!(value instanceof Collection))
        {
            throw new IllegalArgumentException(this + " - tried to assign a singular value to composite properties");
        }
        if (this.getComposedType().isArray())
        {
            Object[] array = (Object[]) Array.newInstance(getType().getComponentType(), 0);
            this.setRegularValue(((Collection<?>) value).toArray(array));
        }
        else if (this.getComposedType().isCollection())
        {
            synchronized (this.getInstance())
            {
                Collection<Object> oldValues = (Collection<Object>) getValue();
                try
                {
                    oldValues.clear();
                    oldValues.addAll((Collection<Object>) value);
                }
                catch (UnsupportedOperationException e)
                {
                    throw new IllegalArgumentException(
                            this + " - setValue: could not empty " + oldValues + "setValue method canceled");
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(this + " - tried to assign a value to a map or an immutable collection");
        }

    }

    /**
     * Method used to set a regular (i.e. not-composite) property value.
     * @param value Object; the new value
     */
    protected abstract void setRegularValue(final Object value);

    /** {@inheritDoc} */
    @Override
    public ComposedTypeEnum getComposedType()
    {
        if (getType().isArray())
        {
            return ComposedTypeEnum.ARRAY;
        }
        else if (Collection.class.isAssignableFrom(getType()))
        {
            return ComposedTypeEnum.COLLECTION;
        }
        else if (ImmutableCollection.class.isAssignableFrom(getType()))
        {
            return ComposedTypeEnum.IMMUTABLECOLLECTION;
        }
        else if (Map.class.isAssignableFrom(getType()))
        {
            return ComposedTypeEnum.MAP;
        }
        else if (ImmutableMap.class.isAssignableFrom(getType()))
        {
            return ComposedTypeEnum.IMMUTABLEMAP;
        }
        return ComposedTypeEnum.NONE;
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getComponentType()
    {
        if (!this.getComposedType().isComposed())
        {
            return null;
        }
        if (getComposedType().isArray())
        {
            return getType().getComponentType();
        }
        if (getComposedType().isCollection())
        {
            Collection<?> value = (Collection<?>) getValue();
            if (value == null || value.size() == 0)
            {
                return null;
            }
            return value.toArray()[0].getClass();
        }
        if (getComposedType().isImmutableCollection())
        {
            ImmutableCollection<?> value = (ImmutableCollection<?>) getValue();
            if (value == null || value.size() == 0)
            {
                return null;
            }
            return value.toArray()[0].getClass();
        }
        // TODO: is this ok? Map or ImmutableMap do not have a single type...
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Property [getName()=" + this.getName() + "]";
    }
}
