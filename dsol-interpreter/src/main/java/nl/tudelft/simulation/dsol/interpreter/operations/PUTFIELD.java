package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Field;

import org.djutils.primitives.Primitive;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.interpreter.InterpreterException;
import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantFieldref;

/**
 * The PUTFIELD operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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

public class PUTFIELD extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 181;

    /** the index to load. */
    private final int index;

    /**
     * constructs a new PUTFIELD.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public PUTFIELD(final DataInput dataInput) throws IOException
    {
        super();
        this.index = dataInput.readUnsignedShort();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        try
        {
            Object value = stack.pop();
            Object target = stack.pop();
            Field field = null;
            ConstantFieldref constantFieldref = (ConstantFieldref) constantPool[this.index];
            field = ClassUtil.resolveField(target, constantFieldref.getConstantNameAndType().getName());
            field.setAccessible(true);
            if (!field.getType().isPrimitive())
            {
                field.set(target, value);
                return;
            }
            field.set(target, (Primitive.cast(field.getType(), value)));
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
        return OPCODE_BYTE_LENGTH + 2;
    }

    /** {@inheritDoc} */
    @Override
    public int getOpcode()
    {
        return PUTFIELD.OP;
    }
}
