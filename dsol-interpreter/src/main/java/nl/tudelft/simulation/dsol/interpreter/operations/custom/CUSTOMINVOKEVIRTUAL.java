package nl.tudelft.simulation.dsol.interpreter.operations.custom;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Interpreter;
import nl.tudelft.simulation.language.concurrent.Monitor;

/**
 * The CUSTOMINVOKEVIRTUAL operation as defined in
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5 </a>.
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
public class CUSTOMINVOKEVIRTUAL extends nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEVIRTUAL
{
    /** the interpreterOracle to use. */
    private final InterpreterOracleInterface interpreterOracle;

    /**
     * constructs a new CUSTOMINVOKEVIRTUAL.
     * @param interpreterOracle InterpreterOracleInterface; the interpreterOracle to use
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public CUSTOMINVOKEVIRTUAL(final InterpreterOracleInterface interpreterOracle, final DataInput dataInput) throws IOException
    {
        super(dataInput);
        this.interpreterOracle = interpreterOracle;
    }

    /** {@inheritDoc} */
    @Override
    public Frame execute(final Frame frame, final Object objectRef, final Method method, final Object[] arguments)
            throws Exception
    {
        if (!this.interpreterOracle.shouldBeInterpreted(method) || Modifier.isNative(method.getModifiers()))
        {
            return super.execute(frame, objectRef, method, arguments);
        }
        if (Modifier.isSynchronized(method.getModifiers()))
        {
            Monitor.lock(objectRef);
        }
        return Interpreter.createFrame(objectRef, method, arguments);
    }
}
