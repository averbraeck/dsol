package nl.tudelft.simulation.dsol.interpreter.operations.custom;

import java.lang.reflect.Method;

/**
 * The InterpreterOracleInterface specifies an interface for selecting which methods must be interpreted.
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
public interface InterpreterOracleInterface
{
    /**
     * whether to interpret methods defined in instances of myClass.
     * @param method Method; the method to inspect
     * @return whether to interpret methods defined in instances of myClass
     */
    boolean shouldBeInterpreted(final Method method);
}
