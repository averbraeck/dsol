package nl.tudelft.simulation.dsol.interpreter.operations;

import nl.tudelft.simulation.dsol.interpreter.Frame;
import nl.tudelft.simulation.dsol.interpreter.Operation;
import nl.tudelft.simulation.dsol.interpreter.classfile.ExceptionEntry;

/**
 * The ATHROW operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class ATHROW extends Operation
{
    /** OP refers to the operand code. */
    public static final int OP = 191;

    /** the bytePosition of this frame. */
    private int bytePosition;

    /**
     * constructs a new ARETURN.
     * @param bytePosition int; where the ARETURN starts
     */
    public ATHROW(final int bytePosition)
    {
        super();
        this.bytePosition = bytePosition;
    }

    /**
     * executes the aThrow operation on a frame.
     * @param frame Frame; the frame
     * @return the frame
     */
    public Frame execute(final Frame frame)
    {
        Throwable throwable = new NullPointerException();
        if (!frame.getOperandStack().isEmpty())
        {
            throwable = (Throwable) frame.getOperandStack().pop();
        }
        // Let's clear all but the exception from the stack
        frame.getOperandStack().clear();
        frame.getOperandStack().push(throwable);
        // Now we search for a handler
        ExceptionEntry[] exceptionEntries = frame.getMethodDescriptor().getExceptionTable();
        ExceptionEntry exceptionEntry =
                ExceptionEntry.resolveExceptionEntry(exceptionEntries, throwable.getClass(), this.bytePosition);
        frame.getOperandStack().push(exceptionEntry);
        return frame;
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
        return ATHROW.OP;
    }

    /**
     * @return the bytePosition
     */
    public int getBytePosition()
    {
        return this.bytePosition;
    }

    /**
     * As an exception, the Interpreter class needs to be able to set the byte position.
     * @param bytePosition int; set bytePosition
     */
    public void setBytePosition(final int bytePosition)
    {
        this.bytePosition = bytePosition;
    }
}
