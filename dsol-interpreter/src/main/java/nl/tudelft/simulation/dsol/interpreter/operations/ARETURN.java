package nl.tudelft.simulation.dsol.interpreter.operations;

import java.lang.reflect.Method;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.language.concurrent.Monitor;

/**
 * The ARETURN operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class ARETURN extends ReturnOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 176;

    /**
     * constructs a new ARETURN.
     */
    public ARETURN()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Object execute(final Frame frame)
    {
        if (ReturnOperation.isSynchronized(frame.getMethodDescriptor().getMethod()))
        {
            Object monitor = null;
            if (ReturnOperation.isStatic(frame.getMethodDescriptor().getMethod()))
            {
                monitor = ((Method) frame.getMethodDescriptor().getMethod()).getDeclaringClass();
            }
            else
            {
                monitor = frame.getLocalVariables()[0].getValue();
            }
            Monitor.unlock(monitor);
        }
        return frame.getOperandStack().pop();
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return ARETURN.OP;
    }
}
