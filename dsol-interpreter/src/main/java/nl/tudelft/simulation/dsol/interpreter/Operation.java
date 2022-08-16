package nl.tudelft.simulation.dsol.interpreter;

import nl.tudelft.simulation.dsol.interpreter.classfile.MethodDescriptor;

/**
 * Represents a Java virtual machine instruction. An operation is id-ed with a short opcode and has a predefined byte length.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @since 1.5
 */
public abstract class Operation
{
    /** OPCODE_BYTE_LENGTH. */
    public static final int OPCODE_BYTE_LENGTH = 1;

    /** RESERVED OPCODE. */
    public static final int BREAKPOINT = 202;

    /** RESERVED OPCODE. */
    public static final int IMPDEP1 = 254;

    /** RESERVED OPCODE. */
    public static final int IMPDEP2 = 255;

    /**
     * @return Returns the opcode of the operation
     */
    public abstract int getOpcode();

    /**
     * @return Returs the byteLength
     */
    public abstract int getByteLength();

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
    }

    /**
     * represents a set of operations as string.
     * @param methodDescriptor MethodDescriptor; the methodDescriptor
     * @param operations Operation[]; the operations to represent
     * @return The resulting string.
     */
    public static String toString(final MethodDescriptor methodDescriptor, final Operation[] operations)
    {
        String result = "";
        for (int i = 0; i < operations.length; i++)
        {
            result = result + i + ": " + " (" + methodDescriptor.getBytePosition(i) + ")" + operations[i].toString() + "\n";
        }
        return result;
    }
}
