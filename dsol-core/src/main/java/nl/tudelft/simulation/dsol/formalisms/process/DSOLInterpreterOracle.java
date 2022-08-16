package nl.tudelft.simulation.dsol.formalisms.process;

import java.lang.reflect.Method;

import nl.tudelft.simulation.dsol.interpreter.operations.custom.InterpreterOracleInterface;

/**
 * See if a super-reflected method is the
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class DSOLInterpreterOracle implements InterpreterOracleInterface
{
    /**
     * constructs a new DSOLInterpreterOracle.
     */
    public DSOLInterpreterOracle()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldBeInterpreted(final Method method)
    {
        if (method.getDeclaringClass().equals(Process.class) && method.getName().equals("resume"))
        {
            return false;
        }
        if (Process.class.isAssignableFrom(method.getDeclaringClass()))
        {
            return true;
        }
        return false;
    }
}
