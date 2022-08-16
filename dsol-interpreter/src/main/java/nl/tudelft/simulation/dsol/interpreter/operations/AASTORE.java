package nl.tudelft.simulation.dsol.interpreter.operations;

import java.lang.reflect.Array;

import org.djutils.primitives.Primitive;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The AASTORE operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class AASTORE extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 83;

    /**
     * constructs a new AASTORE.
     */
    public AASTORE()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        Object value = stack.pop();
        int index = Primitive.toInteger(stack.pop()).intValue();
        Object arrayref = stack.pop();
        Array.set(arrayref, index, value);
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
        return AASTORE.OP;
    }
}
