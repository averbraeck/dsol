package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Array;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;

/**
 * The NEWARRAY operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class NEWARRAY extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 188;

    /** the type to load. */
    private int atype = -1;

    /**
     * constructs a new NEWARRAY.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public NEWARRAY(final DataInput dataInput) throws IOException
    {
        super();
        this.atype = dataInput.readUnsignedByte();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        int length = ((Integer) stack.pop()).intValue();
        Class<?> clazz = null;
        switch (this.atype)
        {
            case 4:
                clazz = boolean.class;
                break;
            case 5:
                clazz = char.class;
                break;
            case 6:
                clazz = float.class;
                break;
            case 7:
                clazz = double.class;
                break;
            case 8:
                clazz = byte.class;
                break;
            case 9:
                clazz = short.class;
                break;
            case 10:
                clazz = int.class;
                break;
            case 11:
                clazz = long.class;
                break;
            default:
                throw new RuntimeException("unknown atype in NEWARRAY");
        }
        Object array = Array.newInstance(clazz, length);
        stack.push(array);
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH + 1;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return NEWARRAY.OP;
    }
}
