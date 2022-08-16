package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The TABLESWITCH operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class TABLESWITCH extends JumpOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 170;

    /** the byteLength of this statement. */
    private int byteLength = 0;

    /** the start position of the table. */
    private int lowValue = -1;

    /** the end position of the table. */
    private int highValue = -1;

    /** the offset table. */
    private List<Integer> offsets = new ArrayList<Integer>();

    /**
     * constructs a new TABLESWITCH.
     * @param dataInput DataInput; the dataInput
     * @param padding int; the number of bytes to pad
     * @throws IOException on IOfailure
     */
    public TABLESWITCH(final DataInput dataInput, final int padding) throws IOException
    {
        super();

        // First we pad
        if (padding > 0)
        {
            dataInput.skipBytes(padding);
            this.byteLength = this.byteLength + padding;
        }

        // Now we place the default value at position 0
        this.offsets.add(Integer.valueOf(dataInput.readInt()));
        this.lowValue = dataInput.readInt();
        this.highValue = dataInput.readInt();
        int entries = this.highValue - this.lowValue + 1;
        this.byteLength = this.byteLength + 12;
        for (int i = 0; i < entries; i++)
        {
            Integer offset = Integer.valueOf(dataInput.readInt());
            this.offsets.add(offset);
            this.byteLength = this.byteLength + 4;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        int index = ((Integer) stack.pop()).intValue();
        int offset = -1;
        if (index < this.lowValue || index > this.highValue)
        {
            offset = this.offsets.get(0).intValue();
        }
        else
        {
            offset = this.offsets.get(index - this.lowValue + 1).intValue();
        }
        return offset;
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH + this.byteLength;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return TABLESWITCH.OP;
    }
}
