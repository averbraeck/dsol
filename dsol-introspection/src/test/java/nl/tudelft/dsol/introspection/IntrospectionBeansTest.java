package nl.tudelft.dsol.introspection;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.dsol.introspection.beans.SubTestBean2;
import nl.tudelft.simulation.introspection.Property;
import nl.tudelft.simulation.introspection.beans.BeanIntrospector;

/**
 * A test program for the JavaBean introspection implementation.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang" >Niels Lang
 *         </a><a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public final class IntrospectionBeansTest
{
    /**
     * constructs a new PTestBeans.
     */
    private IntrospectionBeansTest()
    {
        super();
        // unreachable code;
    }

    /**
     * executes the PTestBeans.
     * @param args the commandline arguments
     */
    public static void main(final String[] args)
    {
        Property[] props = (new BeanIntrospector()).getProperties(new SubTestBean2());
        for (int i = 0; i < props.length; i++)
        {
            CategoryLogger.always().info("main - Prop name: {}", props[i].getName());
            CategoryLogger.always().info("main - Prop class: {}", props[i].getType());
            CategoryLogger.always().info("main - Prop value: {}", props[i].getValue());
        }
    }
}
