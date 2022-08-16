package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The LOOKUPSWITCH operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class LOOKUPSWITCH extends JumpOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 171;

    /** the byteLength of this statement. */
    private int byteLength = 0;

    /** the offset table. */
    private Map<Object, Integer> offsets = new LinkedHashMap<Object, Integer>();

    /**
     * constructs a new LOOKUPSWITCH.
     * @param dataInput DataInput; the dataInput
     * @param padding int; the amount of bytes to pad
     * @throws IOException on IOfailure
     */
    public LOOKUPSWITCH(final DataInput dataInput, final int padding) throws IOException
    {
        super();
        // If we pad, we pad!
        if (padding > 0)
        {
            dataInput.skipBytes(padding);
            this.byteLength = this.byteLength + padding;
        }

        this.offsets.put("default", Integer.valueOf(dataInput.readInt()));
        int entries = dataInput.readInt();
        this.byteLength = this.byteLength + 8;
        for (int i = 0; i < entries; i++)
        {
            Integer match = Integer.valueOf(dataInput.readInt());
            Integer offset = Integer.valueOf(dataInput.readInt());
            this.offsets.put(match, offset);
            this.byteLength = this.byteLength + 8;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        Integer key = (Integer) stack.pop();
        Integer offset = this.offsets.get(key);
        if (offset == null)
        {
            offset = this.offsets.get("default");
        }
        return offset.intValue();
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
        return LOOKUPSWITCH.OP;
    }
}
