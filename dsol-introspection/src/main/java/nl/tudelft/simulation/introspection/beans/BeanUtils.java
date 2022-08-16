package nl.tudelft.simulation.introspection.beans;

import java.beans.PropertyDescriptor;

/**
 * Utility class for bean tests.
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

public class BeanUtils extends Object
{
    /**
     * resolves whether the bean is null
     * @param bean Object; A bean instance.
     * @param pd PropertyDescriptor; A PropertyDescriptor for the property to be examined.
     * @return True, if the value of the property described by 'pd' for the instance 'bean' is null indeed. Returns false
     *         otherwise.
     */
    public static boolean isNull(final Object bean, final PropertyDescriptor pd)
    {
        Object result = null;
        try
        {
            result = pd.getReadMethod().invoke(bean, new Object[0]);
        }
        catch (Exception e)
        {
            return true;
        }
        return (result == null);
    }
}
