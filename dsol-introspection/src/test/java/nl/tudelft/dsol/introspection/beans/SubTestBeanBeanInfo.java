package nl.tudelft.dsol.introspection.beans;

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * <p>
 * (c) 2002-2021-2004 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl">www.simulation.tudelft.nl </a>.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang">Niels Lang
 *         </a><a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SubTestBeanBeanInfo extends SimpleBeanInfo
{

    /** Property identifiers //GEN-FIRST:Properties. */
    private static final int PROPERTY_font = 0;

    /** */
    private static final int PROPERTY_secondProperty = 1;

    /** */
    private static final int PROPERTY_firstProperty = 2;

    /** */
    private static final int PROPERTY_color = 3;

    /** */
    private static final int PROPERTY_intProp = 4;

    /** Property array. */
    private static PropertyDescriptor[] properties = new PropertyDescriptor[5];

    static
    {
        try
        {
            properties[PROPERTY_font] = new PropertyDescriptor("font", SubTestBean.class, "getFont", "setFont");
            properties[PROPERTY_secondProperty] =
                    new PropertyDescriptor("secondProperty", SubTestBean.class, "getSecondProperty", "setSecondProperty");
            properties[PROPERTY_firstProperty] =
                    new PropertyDescriptor("firstProperty", SubTestBean.class, "getFirstProperty", "setFirstProperty");
            properties[PROPERTY_color] = new PropertyDescriptor("color", SubTestBean.class, "getColor", "setColor");
            properties[PROPERTY_intProp] = new PropertyDescriptor("intProp", SubTestBean.class, "getIntProp", "setIntProp");
        }
        catch (IntrospectionException e)
        {
            e.printStackTrace();
        } // GEN-HEADEREND:Properties

        // Here you can add code for customizing the properties array.

    }// GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events

    /** EventSet array. */
    private static EventSetDescriptor[] eventSets = new EventSetDescriptor[0];

    // GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

    // GEN-LAST:Events

    // Method identifiers //GEN-FIRST:Methods

    /** Method array. */
    private static MethodDescriptor[] methods = new MethodDescriptor[0];

    // GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

    // GEN-LAST:Methods

    /** */
    private static java.awt.Image iconColor16 = null; // GEN-BEGIN:IconsDef

    /** */
    private static java.awt.Image iconColor32 = null;

    /** */
    private static java.awt.Image iconMono16 = null;

    /** */
    private static java.awt.Image iconMono32 = null; // GEN-END:IconsDef

    /** */
    private static String iconNameC16 = null; // GEN-BEGIN:Icons

    /** */
    private static String iconNameC32 = null;

    /** */
    private static String iconNameM16 = null;

    /** */
    private static String iconNameM32 = null; // GEN-END:Icons

    /** */
    private static int defaultPropertyIndex = -1; // GEN-BEGIN:Idx

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * @return An array of PropertyDescriptors describing the editable properties supported by this bean. May return null if the
     *         information should be obtained by automatic analysis.
     *         <p>
     *         If a property is indexed, then its entry in the result array will belong to the IndexedPropertyDescriptor
     *         subclass of PropertyDescriptor. A client of getPropertyDescriptors can use "instanceof" to check if a given
     *         PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return properties;
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * @return An array of EventSetDescriptors describing the kinds of events fired by this bean. May return null if the
     *         information should be obtained by automatic analysis.
     */
    @Override
    public EventSetDescriptor[] getEventSetDescriptors()
    {
        return eventSets;
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * @return An array of MethodDescriptors describing the methods implemented by this bean. May return null if the information
     *         should be obtained by automatic analysis.
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors()
    {
        return methods;
    }

    /**
     * A bean may have a "default" property that is the property that will mostly commonly be initially chosen for update by
     * human's who are customizing the bean.
     * @return Index of default property in the PropertyDescriptor array returned by getPropertyDescriptors.
     *         <P>
     *         Returns -1 if there is no default property.
     */
    @Override
    public int getDefaultPropertyIndex()
    {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array returned by getEventSetDescriptors.
     *         <P>
     *         Returns -1 if there is no default event.
     */
    @Override
    public int getDefaultEventIndex()
    {
        return defaultPropertyIndex;
    }

    /**
     * This method returns an image object that can be used to represent the bean in toolboxes, toolbars, etc. Icon images will
     * typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from this method.
     * <p>
     * There are four possible flavors of icons (16x16 color, 32x32 color, 16x16 mono, 32x32 mono). If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background so they can be rendered onto an existing background.
     * @param iconKind The kind of icon requested. This should be one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
     *            ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return An image object representing the requested icon. May return null if no suitable icon is available.
     */
    @Override
    public java.awt.Image getIcon(int iconKind)
    {
        switch (iconKind)
        {
            case ICON_COLOR_16x16:
                if (iconNameC16 == null)
                {
                    return null;
                }

                if (iconColor16 == null)
                {
                    iconColor16 = loadImage(iconNameC16);
                }
                return iconColor16;

            case ICON_COLOR_32x32:
                if (iconNameC32 == null)
                {
                    return null;
                }

                if (iconColor32 == null)
                {
                    iconColor32 = loadImage(iconNameC32);
                }
                return iconColor32;

            case ICON_MONO_16x16:
                if (iconNameM16 == null)
                {
                    return null;
                }

                if (iconMono16 == null)
                {
                    iconMono16 = loadImage(iconNameM16);
                }
                return iconMono16;

            case ICON_MONO_32x32:
                if (iconNameM32 == null)
                {
                    return null;
                }

                if (iconNameM32 == null)
                {
                    iconMono32 = loadImage(iconNameM32);
                }
                return iconMono32;
        }

        return null;
    }

}
