package nl.tudelft.simulation.introspection;

/**
 * A property defines a characteristic of an object. It has a name, a type and provides methods to view and alter its value.
 * Different introspection implementation may provide different definitions for what exactly are regarded to be the 'properties'
 * of an object.
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
public interface Property
{
    /**
     * Retrieves the name of the property.
     * @return The name of the property
     */
    String getName();

    /**
     * Returns the type of this property's value.
     * @return A {see java.lang.Class}instance denoting the type of this property.
     */
    Class<?> getType();

    /**
     * Returns whether the value of this property may be altered.
     * @return 'True', when this property's value can be altered, 'false' otherwise.
     */
    boolean isEditable();

    /**
     * Set the value of this property. However, if isEditable() returns 'false', the value of this property will not be altered.
     * Composite property values (i.e. {see java.util.Collection}or arrays) should be provided as an instance of {see
     * java.util.Collection}.
     * @param value Object; The new value of this property.
     */
    void setValue(Object value);

    /**
     * Returns the current value of this property.
     * @return The current value of this property.
     */
    Object getValue();

    /**
     * Retrieves the introspected object, which contains this Property.
     * @return the instance
     */
    Object getInstance();

    /**
     * Returns the collection type of the contained value (i.e. a composite value). The definition whether a value is considered
     * composite depends on the property paradigm used by this Property.
     * @return the CollectionTypeEnum of this Property.
     */
    ComposedTypeEnum getComposedType();

    /**
     * Returns the type of the collection components contained in this Property.
     * @return The type of the collection components contained in this Property. Returns null when isCollection() returns false,
     *         or when the component type could not be determined by this Property.
     */
    Class<?> getComponentType();
}
