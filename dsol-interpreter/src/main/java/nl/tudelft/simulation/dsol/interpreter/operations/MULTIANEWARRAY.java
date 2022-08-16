package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Array;

import org.djutils.primitives.Primitive;
import org.djutils.reflection.FieldSignature;

import nl.tudelft.simulation.dsol.interpreter.InterpreterException;
import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantClass;

/**
 * The MULTINEWARRAY operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class MULTIANEWARRAY extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 197;

    /** the index to load. */
    private final int index;

    /** the dimensions of the new array. */
    private int numDimensions = -1;

    /**
     * constructs a new MULTIANEWARRAY.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public MULTIANEWARRAY(final DataInput dataInput) throws IOException
    {
        super();
        this.index = dataInput.readUnsignedShort();
        this.numDimensions = dataInput.readUnsignedByte();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        ConstantClass constant = (ConstantClass) constantPool[this.index];
        Class<?> clazz = null;
        try
        {
            clazz = constant.getValue().getClassValue();
        }
        catch (Exception exception)
        {
            throw new InterpreterException(exception);
        }
        int[] dimensions = new int[this.numDimensions];
        for (int i = dimensions.length - 1; i >= 0; i--)
        {
            dimensions[i] = Primitive.toInteger(stack.pop()).intValue();
        }
        try
        {
            Class<?> componentType = FieldSignature.toClass(clazz.getName().replaceAll("\\[", ""));
            Object result = newArray(0, dimensions, componentType);
            stack.push(result);
        }
        catch (ClassNotFoundException exception)
        {
            throw new InterpreterException(exception);
        }
    }

    /**
     * creates a new Array.
     * @param depth int; the depth
     * @param dimensions int[]; the dimensions
     * @param clazz Class&lt;?&gt;; the clazz
     * @return Object the array
     */
    private Object newArray(final int depth, final int[] dimensions, final Class<?> clazz)
    {
        try
        {
            if (depth == dimensions.length)
            {
                // last level; now we make 'basicClass' instances
                // these can be either simple types or objects
                if (clazz.isPrimitive())
                {
                    throw new InterpreterException("may not occur");
                }
                return clazz.newInstance();
            }
            if (depth == dimensions.length - 1)
            {
                if (clazz.isPrimitive())
                {
                    return Array.newInstance(clazz, dimensions[depth]);
                }
            }
            Object arrayref = Array.newInstance(Object.class, dimensions[depth]);
            for (int i = 0; i < dimensions[depth]; i++)
            {
                Array.set(arrayref, i, newArray(depth + 1, dimensions, clazz));
            }
            return arrayref;
        }
        catch (Exception exception)
        {
            throw new InterpreterException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getByteLength()
    {
        return OPCODE_BYTE_LENGTH + 3;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return MULTIANEWARRAY.OP;
    }
}
