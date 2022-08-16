package nl.tudelft.simulation.introspection.beans;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.tudelft.simulation.introspection.DelegateIntrospection;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;

/**
 * The Bean introspector provides a simplified JavaBean implementation of the introspection interfaces. Its behavior adheres to
 * the following:
 * <ul>
 * <li>Properties are discovered by searching for 'getter' and / or 'setter' methods</li>
 * <li>Property value are manipulated via a property's 'setter' method. If no such method is found, the property cannot be
 * altered</li>
 * <li>Indexed properties are probably not correctly supported.</li>
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
public class BeanIntrospector implements Introspector
{
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
            BeanInfo info = java.beans.Introspector.getBeanInfo(introspected.getClass());

            PropertyDescriptor[] descrips = info.getPropertyDescriptors();
            for (int i = 0; i < descrips.length; i++)
            {
                props.add(new BeanProperty(introspected, descrips[i]));
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(this + " - getProperties", e);
        }
        return props.toArray(new Property[props.size()]);
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
            BeanInfo info = java.beans.Introspector.getBeanInfo(introspected.getClass());
            PropertyDescriptor[] descrips = info.getPropertyDescriptors();
            for (int i = 0; i < descrips.length; i++)
            {
                if (descrips[i].getName().equals(property))
                {
                    return new BeanProperty(introspected, descrips[i]);
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
            BeanInfo info = java.beans.Introspector.getBeanInfo(introspected.getClass());
            PropertyDescriptor[] descrips = info.getPropertyDescriptors();
            for (int i = 0; i < descrips.length; i++)
            {
                props.add(descrips[i].getName());
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(this + " - getPropertyNames", e);
        }
        return props.toArray(new String[props.size()]);
    }
}
