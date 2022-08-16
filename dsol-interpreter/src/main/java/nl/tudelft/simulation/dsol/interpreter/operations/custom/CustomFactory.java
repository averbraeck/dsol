package nl.tudelft.simulation.dsol.interpreter.operations.custom;

import java.io.DataInput;
import java.io.IOException;

import nl.tudelft.simulation.dsol.interpreter.Operation;
import nl.tudelft.simulation.dsol.interpreter.operations.InterpreterFactory;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEINTERFACE;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKESPECIAL;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKESTATIC;
import nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEVIRTUAL;

/**
 * A InterpreterFactory.
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
public class CustomFactory extends InterpreterFactory
{
    /** the interpreterOracle to use. */
    private final InterpreterOracleInterface interpreterOracle;

    /**
     * constructs a new InterpreterFactory.
     * @param interpreterOracle InterpreterOracleInterface; the oracle to use
     */
    public CustomFactory(final InterpreterOracleInterface interpreterOracle)
    {
        super();
        this.interpreterOracle = interpreterOracle;
    }

    /** {@inheritDoc} */
    @Override
    public Operation readOperation(final int operand, final DataInput dataInput, final int startBytePostion) throws IOException
    {
        switch (operand)
        {
            case INVOKEINTERFACE.OP:
                return new CUSTOMINVOKEINTERFACE(this.interpreterOracle, dataInput);
            case INVOKESPECIAL.OP:
                return new CUSTOMINVOKESPECIAL(this.interpreterOracle, dataInput);
            case INVOKESTATIC.OP:
                return new CUSTOMINVOKESTATIC(this.interpreterOracle, dataInput);
            case INVOKEVIRTUAL.OP:
                return new CUSTOMINVOKEVIRTUAL(this.interpreterOracle, dataInput);
            default:
                return super.readOperation(operand, dataInput, startBytePostion);
        }
    }

    /**
     * @return interpreterOracle
     */
    public InterpreterOracleInterface getInterpreterOracle()
    {
        return this.interpreterOracle;
    }
}
