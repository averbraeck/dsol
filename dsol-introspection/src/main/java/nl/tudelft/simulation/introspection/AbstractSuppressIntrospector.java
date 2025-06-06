package nl.tudelft.simulation.introspection;

import java.util.ArrayList;
import java.util.List;

/**
 * The AbstractSupressIntrospector.
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
public abstract class AbstractSuppressIntrospector implements Introspector
{
    /** the parent introspector. */
    protected Introspector parent;

    /**
     * Constructor for AbstractSuppressIntrospector.
     * @param parent the parent introspector
     */
    public AbstractSuppressIntrospector(final Introspector parent)
    {
        super();
        this.parent = parent;
    }

    @Override
    public Property[] getProperties(final Object arg0)
    {
        Property[] original = this.parent.getProperties(arg0);
        List<Property> result = new ArrayList<Property>();
        for (int i = 0; i < original.length; i++)
        {
            if (!this.suppress(original[i].getType()) && !this.suppress(original[i].getName()))
            { result.add(original[i]); }
        }
        return result.toArray(new Property[0]);
    }

    @Override
    public String[] getPropertyNames(final Object arg0)
    {
        Property[] properties = this.getProperties(arg0);
        String[] result = new String[properties.length];
        for (int i = 0; i < properties.length; i++)
        {
            result[i] = properties[i].getName();
        }
        return result;
    }

    @Override
    public Property getProperty(final Object arg0, final String arg1)
    {
        Property[] properties = this.getProperties(arg0);
        for (int i = 0; i < properties.length; i++)
        {
            if (properties[i].getName().equals(arg1))
            { return properties[i]; }
        }
        return null;
    }

    /**
     * Method suppress.
     * @param type the type of the class
     * @return boolean whether to supress
     */
    protected boolean suppress(final Class<?> type)
    {
        if (type.equals(Class.class))
        { return true; }
        return false;
    }

    /**
     * Method suppress.
     * @param propertyName the propertyName
     * @return whether to supress
     */
    protected abstract boolean suppress(final String propertyName);
}
