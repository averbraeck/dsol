package nl.tudelft.simulation.dsol.interpreter.process;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Interpreter;
import nl.tudelft.simulation.dsol.interpreter.operations.custom.CUSTOMINVOKEVIRTUAL;
import nl.tudelft.simulation.dsol.interpreter.operations.custom.InterpreterOracleInterface;
import nl.tudelft.simulation.language.concurrent.Monitor;

/**
 * PROCESSINVOKEVIRTUAL.
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
public class PROCESSINVOKEVIRTUAL extends CUSTOMINVOKEVIRTUAL
{
    /**
     * constructs a new PROCESSINVOKEVIRTUAL.
     * @param interpreterOracle InterpreterOracleInterface; the interpreterOracle
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public PROCESSINVOKEVIRTUAL(final InterpreterOracleInterface interpreterOracle, final DataInput dataInput)
            throws IOException
    {
        super(interpreterOracle, dataInput);
    }

    /**
     * executes the operation and returns a new Frame.
     * @param frame Frame; the original frame
     * @param objectRef Object; the object on which to invoke the method
     * @param arguments Object[]; the arguments with which to invoke the method
     * @param method Method; the method to invoke
     * @throws Exception on invocation exception
     * @return a new frame
     */
    @Override
    public Frame execute(final Frame frame, final Object objectRef, final Method method, final Object[] arguments)
            throws Exception
    {
        if (!InterpretableProcess.class.isAssignableFrom(method.getDeclaringClass()))
        {
            return super.execute(frame, objectRef, method, arguments);
        }
        if (method.getName().equals("resume"))
        {
            return super.execute(frame, objectRef, method, arguments);
        }
        // Let's check for the suspend method
        if (method.equals(ProcessFactory.suspendMethod))
        {
            // we set the state of the process to suspended
            InterpretableProcess process = (InterpretableProcess) objectRef;
            process.setState(InterpretableProcess.SUSPENDED);

            // we pause the frame
            frame.setPaused(true);
            return frame;
        }
        if (Modifier.isSynchronized(method.getModifiers()))
        {
            Monitor.lock(objectRef);
        }
        return Interpreter.createFrame(objectRef, method, arguments);
    }
}
