package nl.tudelft.simulation.dsol.interpreter.operations.reflection;

import java.io.DataInput;
import java.io.IOException;

import nl.tudelft.simulation.dsol.interpreter.Operation;
import nl.tudelft.simulation.dsol.interpreter.operations.InterpreterFactory;

/**
 * The extension of the InterpreterFactory to deal specially with the INVOKE operations.
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
public class ReflectionFactory extends InterpreterFactory
{
    /**
     * constructs a new InterpreterFactory.
     */
    public ReflectionFactory()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Operation readOperation(final int operand, final DataInput dataInput, final int startBytePostion)
            throws IOException
    {
        switch (operand)
        {
            case INVOKEINTERFACE.OP:
                return new INVOKEINTERFACE(dataInput);
            case INVOKESPECIAL.OP:
                return new INVOKESPECIAL(dataInput);
            case INVOKESTATIC.OP:
                return new INVOKESTATIC(dataInput);
            case INVOKEVIRTUAL.OP:
                return new INVOKEVIRTUAL(dataInput);
            default:
                return super.readOperation(operand, dataInput, startBytePostion);
        }
    }
}
