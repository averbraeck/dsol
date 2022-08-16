package nl.tudelft.simulation.introspection;

/**
 * The introspector provides introspection services, i.e. property discovery and manipulation, for any object.
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
public interface Introspector
{
    /**
     * @param introspected Object; the introspected object
     * @return Retrieves properties of the introspected object. The properties' values can themselves be introspectable. An
     *         empty array is returned if no introspected object has been set.
     */
    Property[] getProperties(Object introspected);

    /**
     * Retrieves the names of the properties of the introspected object.
     * @param introspected Object; The introspected object.
     * @return An unordered array of the introspected object's property names.
     */
    String[] getPropertyNames(Object introspected);

    /**
     * Retrieves the {see Property}with a given name from an introspected object.
     * @param introspected Object; The introspected object.
     * @param property String; The name of the property to be retrieved
     * @return A {see Property}instance for the given object and property name.
     */
    Property getProperty(Object introspected, String property);
}
