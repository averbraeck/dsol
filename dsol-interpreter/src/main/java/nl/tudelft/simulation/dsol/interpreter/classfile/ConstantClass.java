package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

import org.djutils.reflection.FieldSignature;

/**
 * A ConstantClass.
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
public class ConstantClass extends Constant
{
    /** gets the name index. */
    private int nameIndex;

    /**
     * constructs a new ConstantClass.
     * @param dataInput DataInput; the inputstream to read from
     * @param constantPool Constant[]; the constantPool it is part of
     * @throws IOException on failure
     */
    public ConstantClass(final Constant[] constantPool, final DataInput dataInput) throws IOException
    {
        this(constantPool, dataInput.readUnsignedShort());
    }

    /**
     * constructs a new ClassConstant.
     * @param nameIndex int; the nameIndex
     * @param constantPool Constant[]; the constantPool it is part of
     */
    public ConstantClass(final Constant[] constantPool, final int nameIndex)
    {
        super(constantPool);
        this.nameIndex = nameIndex;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 7;
    }

    /**
     * returns the name index.
     * @return nameIndex
     */
    public int getNameIndex()
    {
        return this.nameIndex;
    }

    /**
     * returns the className of this constant.
     * @return String the className
     */
    public FieldSignature getValue()
    {
        return new FieldSignature(((ConstantUTF8) super.getConstantPool()[this.nameIndex]).getValue());
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantClass[index=" + this.nameIndex + "]";
    }
}
