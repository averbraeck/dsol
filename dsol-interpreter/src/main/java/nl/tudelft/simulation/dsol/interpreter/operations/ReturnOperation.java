package nl.tudelft.simulation.dsol.interpreter.operations;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Operation;

/**
 * The ReturnOperation is an abstract class for all operations which do return any value.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class ReturnOperation extends Operation
{
    /**
     * executes the operation.
     * @param frame Frame; the current frame
     * @return Object the result
     */
    public abstract Object execute(final Frame frame);

    /**
     * is the accessibleObject synchronized?
     * @param object AccessibleObject; the method or constructor
     * @return isSynchronized?
     */
    public static boolean isSynchronized(final AccessibleObject object)
    {
        if (object instanceof Method)
        {
            return Modifier.isSynchronized(((Method) object).getModifiers());
        }
        return Modifier.isSynchronized(((Constructor<?>) object).getModifiers());
    }

    /**
     * is the accessibleObject static?
     * @param object AccessibleObject; the method or constructor
     * @return isStatic?
     */
    public static boolean isStatic(final AccessibleObject object)
    {
        if (object instanceof Method)
        {
            return Modifier.isStatic(((Method) object).getModifiers());
        }
        return Modifier.isStatic(((Constructor<?>) object).getModifiers());
    }
}
