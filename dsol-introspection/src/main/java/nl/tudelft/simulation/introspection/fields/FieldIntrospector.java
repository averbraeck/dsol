package nl.tudelft.simulation.introspection.fields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.tudelft.simulation.introspection.DelegateIntrospection;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;

/**
 * The IntrospectionField introspector provides a field manipulation implementation of the introspection interfaces. Its
 * behavior adheres to the following:
 * <ul>
 * <li>Properties are discovered by searching for an object's fields (visibility neutral)</li>
 * <li>Property value are manipulated by setting field values (visibility neutral)</li>
 * </ul>
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

public class FieldIntrospector implements Introspector
{
    /** useDeepIntrospection. */
    private boolean useDeepIntrospection = true;

    /**
     * constructs a new FieldIntrospector.
     */
    public FieldIntrospector()
    {
        this(false);
    }

    /**
     * constructs a new FieldIntrospector.
     * @param useDeepIntrospection boolean; whether to use deep introspection
     */
    public FieldIntrospector(final boolean useDeepIntrospection)
    {
        this.useDeepIntrospection = useDeepIntrospection;
    }

    /** {@inheritDoc} */
    @Override
    public Property[] getProperties(final Object introspectedObject)
    {
        Object introspected = introspectedObject;
        while (introspected instanceof DelegateIntrospection)
        {
            introspected = ((DelegateIntrospection) introspected).getParentIntrospectionObject();
        }
        Set<Property> props = new LinkedHashSet<Property>();
        try
        {
            Field[] fields = collectFields(introspected.getClass());
            for (int i = 0; i < fields.length; i++)
            {
                props.add(new FieldProperty(introspected, fields[i]));
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(this + " - getProperties", e);
        }
        return props.toArray(new Property[props.size()]);
    }

    /**
     * Collect the fields for the given class, taking the preference for deep introspection into account.
     * @param clasz Class&lt;?&gt;; the class to use
     * @return Field[] the fields
     */
    private Field[] collectFields(final Class<?> clasz)
    {
        List<Field> fields = new ArrayList<Field>(10);
        this.addFields(fields, clasz, this.useDeepIntrospection);
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Add fields of 'clasz' to the fieldList. Optionally iterate over the class hierarchy.
     * @param fieldList List&lt;Field&gt;; the fieldList
     * @param clasz Class&lt;?&gt;; the class
     * @param iterate boolean; whether to iterate
     */
    private void addFields(final List<Field> fieldList, final Class<?> clasz, final boolean iterate)
    {
        fieldList.addAll(Arrays.asList(clasz.getDeclaredFields()));
        if (iterate && clasz.getSuperclass() != null)
        {
            addFields(fieldList, clasz.getSuperclass(), iterate);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Property getProperty(final Object introspectedObject, final String property)
    {
        Object introspected = introspectedObject;
        while (introspected instanceof DelegateIntrospection)
        {
            introspected = ((DelegateIntrospection) introspected).getParentIntrospectionObject();
        }
        try
        {
            Field[] fields = collectFields(introspected.getClass());
            for (int i = 0; i < fields.length; i++)
            {
                if (fields[i].getName().equals(property))
                {
                    return new FieldProperty(introspected, fields[i]);
                }
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(this + " - getProperty", e);
        }
        throw new IllegalArgumentException("Property '" + property + "' not found for " + introspected);
    }

    /** {@inheritDoc} */
    @Override
    public String[] getPropertyNames(final Object introspectedObject)
    {
        Object introspected = introspectedObject;
        while (introspected instanceof DelegateIntrospection)
        {
            introspected = ((DelegateIntrospection) introspected).getParentIntrospectionObject();
        }
        Set<String> props = new LinkedHashSet<String>();
        try
        {
            Field[] fields = collectFields(introspected.getClass());
            for (int i = 0; i < fields.length; i++)
            {
                props.add(fields[i].getName());
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(this + " - getPropertyNames", e);
        }
        return props.toArray(new String[props.size()]);
    }
}
