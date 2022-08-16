package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantMethodref.
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
public class ConstantMethodref extends Constant
{
    /** the class index. */
    private int classIndex;

    /** the name / type index. */
    private int nameAndTypeIndex;

    /**
     * constructs a new ConstantMethodref.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantMethodref(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readUnsignedShort(), inputStream.readUnsignedShort());
    }

    /**
     * constructs a new ConstantMethodref.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param classIndex int; the classIndex
     * @param nameAndTypeIndex int; the NameAndTypeIndex
     */
    public ConstantMethodref(final Constant[] constantPool, final int classIndex, final int nameAndTypeIndex)
    {
        super(constantPool);
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 10;
    }

    /**
     * returns the classindex.
     * @return classIndex
     */
    public int getClassIndex()
    {
        return this.classIndex;
    }

    /**
     * returns the nameAndTypeIndex.
     * @return nameAndTypeIndex
     */
    public int getNameAndTypeIndex()
    {
        return this.nameAndTypeIndex;
    }

    /**
     * returns the constantClass of this constant.
     * @return ConstantClass the constantClass
     */
    public ConstantClass getConstantClass()
    {
        return ((ConstantClass) super.getConstantPool()[this.classIndex]);
    }

    /**
     * returns the nameAndType constant.
     * @return ConstantNameAndType
     */
    public ConstantNameAndType getConstantNameAndType()
    {
        return ((ConstantNameAndType) super.getConstantPool()[this.nameAndTypeIndex]);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantMethodref[classIndex=" + this.classIndex + " nameAndTypeIndex=" + this.nameAndTypeIndex + "]";
    }
}
