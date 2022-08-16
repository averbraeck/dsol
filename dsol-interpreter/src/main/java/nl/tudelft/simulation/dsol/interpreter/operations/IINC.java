package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import org.djutils.primitives.Primitive;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The IINC operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class IINC extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 132;

    /** the index to the localVariables. */
    private final int index;

    /** the constant. */
    private int constant = -1;

    /** see the wide statement. */
    private final boolean widened;

    /**
     * constructs a new IINC.
     * @param dataInput DataInput; the dataInput to read
     * @throws IOException on IOException
     */
    public IINC(final DataInput dataInput) throws IOException
    {
        this(dataInput, false);
    }

    /**
     * constructs a new IINC.
     * @param dataInput DataInput; the dataInput to read
     * @param widened boolean; is the operation widened
     * @throws IOException on IOException
     */
    public IINC(final DataInput dataInput, final boolean widened) throws IOException
    {
        super();
        this.widened = widened;
        if (widened)
        {
            this.index = dataInput.readUnsignedShort();
            this.constant = dataInput.readShort();
        }
        else
        {
            this.index = dataInput.readUnsignedByte();
            this.constant = dataInput.readByte();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        int oldValue = Primitive.toInteger(localVariables[this.index].getValue()).intValue();
        localVariables[this.index].setValue(Integer.valueOf(oldValue + this.constant));
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        int result = OPCODE_BYTE_LENGTH + 2;
        if (this.widened)
        {
            result = result + 2;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return IINC.OP;
    }
}
