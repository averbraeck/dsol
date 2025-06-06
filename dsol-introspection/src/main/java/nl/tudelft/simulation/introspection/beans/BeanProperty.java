package nl.tudelft.simulation.introspection.beans;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;

import nl.tudelft.simulation.introspection.AbstractProperty;
import nl.tudelft.simulation.introspection.Property;

/**
 * The JavaBean TM implementation of the Property interface. See {see BeanIntrospector}for details.
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

public class BeanProperty extends AbstractProperty implements Property
{
    /** the bean that owns the property. */
    private Object bean = null;

    /** the propertyDescriptor. */
    private PropertyDescriptor descriptor = null;

    /**
     * constructs a new BeanProperty.
     * @param bean the bean to introspect
     * @param descriptor the descriptor of the property
     */
    protected BeanProperty(final Object bean, final PropertyDescriptor descriptor)
    {
        this.bean = bean;
        this.descriptor = descriptor;
    }

    @Override
    public String getName()
    {
        return this.descriptor.getName();
    }

    @Override
    public Class<?> getType()
    {
        return this.descriptor.getPropertyType();
    }

    @Override
    protected void setRegularValue(final Object values)
    {
        Class<?> type = this.descriptor.getPropertyType();
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        Object newValue = values;
        if (editor != null)
        {
            if (values instanceof String)
            {
                editor.setAsText((String) values);
            }
            else
            {
                editor.setValue(values);
            }
            newValue = editor.getValue();
        }
        Method writeMethod = this.descriptor.getWriteMethod();
        try
        {
            writeMethod.invoke(this.bean, new Object[] {newValue});
        }
        catch (Throwable throwable)
        {
            throw new IllegalArgumentException(this + " - setRegularValue", throwable);
        }
    }

    @Override
    public Object getValue()
    {
        Object result = null;
        Method readMethod = this.descriptor.getReadMethod();
        try
        {
            if (readMethod != null)
            { result = readMethod.invoke(this.bean, new Object[0]); }
        }
        catch (Exception exception)
        {
            throw new IllegalArgumentException(this + "getValue of " + getName(), exception);
        }
        return result;
    }

    @Override
    public Object getInstance()
    {
        return this.bean;
    }

    @Override
    public boolean isEditable()
    {
        return !(this.descriptor.getWriteMethod() == null);
    }
}
