package nl.tudelft.simulation.introspection;

/**
 * DelegateIntrospection takes care that an intermediate object delegates the introspection to its parent object. <br>
 * <br>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>. <br>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface DelegateIntrospection
{
    /**
     * Return an intermediate object to which the introspection is delegated.
     * @return Object; the intermediate object to which the introspection is delegated
     */
    Object getParentIntrospectionObject();

    /**
     * Check for introspection delegation to allow for the right object to be shown on the screen.
     * @param introspectedObject Object; the object that is displayed on the screen and might have delegation
     * @return the toString() of the (delegated) object
     */
    static String checkDelegation(final Object introspectedObject)
    {
        Object introspected = introspectedObject;
        while (introspected instanceof DelegateIntrospection)
        {
            introspected = ((DelegateIntrospection) introspected).getParentIntrospectionObject();
        }
        return introspected.toString();
    }
}
