package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import nl.tudelft.simulation.dsol.interpreter.InterpreterException;
import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantClass;

/**
 * The CHECKCAST operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class CHECKCAST extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 192;

    /** the index to load. */
    private final int index;

    /**
     * constructs a new CHECKCAST.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public CHECKCAST(final DataInput dataInput) throws IOException
    {
        super();
        this.index = dataInput.readUnsignedShort();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        Object objectReference = stack.peek();
        if (objectReference == null)
        {
            return;
        }
        Class<?> clazz = null;
        try
        {
            clazz = ((ConstantClass) constantPool[this.index]).getValue().getClassValue();
        }
        catch (ClassNotFoundException exception)
        {
            throw new InterpreterException(exception);
        }
        if (!clazz.isAssignableFrom(objectReference.getClass()))
        {
            throw new ClassCastException("CHECKCAST operation failed");
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH + 2;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return CHECKCAST.OP;
    }
}
