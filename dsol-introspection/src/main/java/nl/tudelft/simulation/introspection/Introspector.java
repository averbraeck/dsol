package nl.tudelft.simulation.introspection;

/**
 * The introspector provides introspection services, i.e. property discovery and manipulation, for any object.
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
public interface Introspector
{
    /**
     * @param introspected the introspected object
     * @return Retrieves properties of the introspected object. The properties' values can themselves be introspectable. An
     *         empty array is returned if no introspected object has been set.
     */
    Property[] getProperties(Object introspected);

    /**
     * Retrieves the names of the properties of the introspected object.
     * @param introspected The introspected object.
     * @return An unordered array of the introspected object's property names.
     */
    String[] getPropertyNames(Object introspected);

    /**
     * Retrieves the {see Property}with a given name from an introspected object.
     * @param introspected The introspected object.
     * @param property The name of the property to be retrieved
     * @return A {see Property}instance for the given object and property name.
     */
    Property getProperty(Object introspected, String property);
}
