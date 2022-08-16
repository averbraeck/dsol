package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import org.djutils.primitives.Primitive;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The RET operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class RET extends JumpOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 169;

    /** the index to use. */
    private final int index;

    /** see the wide statement. */
    private final boolean widened;

    /**
     * constructs a new RET.
     * @param dataInput DataInput; the dataInput to read
     * @throws IOException on IOException
     */
    public RET(final DataInput dataInput) throws IOException
    {
        this(dataInput, false);
    }

    /**
     * constructs a new RET.
     * @param dataInput DataInput; the dataInput to read
     * @param widened boolean; whether or not to widen
     * @throws IOException on IOException
     */
    public RET(final DataInput dataInput, final boolean widened) throws IOException
    {
        super();
        this.widened = widened;
        if (widened)
        {
            this.index = dataInput.readUnsignedShort();
        }
        else
        {
            this.index = dataInput.readUnsignedByte();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        return Primitive.toInteger(localVariables[this.index].getValue()).intValue();
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        int result = OPCODE_BYTE_LENGTH + 1;
        if (this.widened)
        {
            result++;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return RET.OP;
    }
}
