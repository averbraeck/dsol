package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import org.djutils.logger.CategoryLogger;
import org.djutils.reflection.FieldSignature;

import nl.tudelft.simulation.dsol.interpreter.LocalVariable;
import nl.tudelft.simulation.dsol.interpreter.OperandStack;
import nl.tudelft.simulation.dsol.interpreter.classfile.Constant;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantClass;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantFloat;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantInteger;
import nl.tudelft.simulation.dsol.interpreter.classfile.ConstantString;

/**
 * The LDC operation as defined in <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5">
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
public class LDC extends VoidOperation
{
    /** OP refers to the operand code. */
    public static final int OP = 18;

    /** the index to load. */
    private final int index;

    /**
     * constructs a new LDC.
     * @param dataInput DataInput; the dataInput
     * @throws IOException on IOfailure
     */
    public LDC(final DataInput dataInput) throws IOException
    {
        super();
        this.index = dataInput.readUnsignedByte();
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final OperandStack stack, final Constant[] constantPool, final LocalVariable[] localVariables)
    {
        Constant constant = constantPool[this.index];
        if (constant instanceof ConstantInteger)
        {
            stack.push(Integer.valueOf(((ConstantInteger) constant).getValue()));
        }
        else if (constant instanceof ConstantFloat)
        {
            stack.push(Float.valueOf(((ConstantFloat) constant).getValue()));
        }
        else if (constant instanceof ConstantString)
        {
            stack.push(((ConstantString) constant).getValue());
        }
        else if (constant instanceof ConstantClass)
        {
            FieldSignature object = ((ConstantClass) constant).getValue();
            try
            {
                stack.push(object.getClassValue());
            }
            catch (ClassNotFoundException classNotFoundException)
            {
                CategoryLogger.always().warn(classNotFoundException, "execute");
            }
        }
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
        return LDC.OP;
    }
}
