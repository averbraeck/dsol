package nl.tudelft.simulation.dsol.interpreter.process;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Method;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.interpreter.Operation;
import nl.tudelft.simulation.dsol.interpreter.operations.custom.CustomFactory;
import nl.tudelft.simulation.dsol.interpreter.operations.custom.InterpreterOracleInterface;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKESPECIAL;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEVIRTUAL;

/**
 * An InterpreterFactory for processes that can be suspended without threads.
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
public class ProcessFactory extends CustomFactory
{
    /** the SUSPEND METHOD. */
    protected static Method suspendMethod;

    static
    {
        try
        {
            suspendMethod = InterpretableProcess.class.getMethod("suspend");
        }
        catch (Exception exception)
        {
            CategoryLogger.always().error(exception);
        }
    }

    /**
     * constructs a new InterpreterFactory.
     * @param interpreterOracle InterpreterOracleInterface; the interpreterOracle to use
     */
    public ProcessFactory(final InterpreterOracleInterface interpreterOracle)
    {
        super(interpreterOracle);
    }

    /**
     * reads a sequence of bytes and returns the appropriate bytecode operations.
     * @param operand int; the operatand (short value)
     * @param dataInput DataInput; the dataInput to read from
     * @param startBytePostion int; the position in the current block of bytecode.
     * @return the assemnbly Operation
     * @throws IOException on IO exception
     */
    @Override
    public Operation readOperation(final int operand, final DataInput dataInput, final int startBytePostion)
            throws IOException
    {
        switch (operand)
        {
            case INVOKESPECIAL.OP:
                return new PROCESSINVOKESPECIAL(getInterpreterOracle(), dataInput);
            case INVOKEVIRTUAL.OP:
                return new PROCESSINVOKEVIRTUAL(getInterpreterOracle(), dataInput);
            default:
                return super.readOperation(operand, dataInput, startBytePostion);
        }
    }
}
