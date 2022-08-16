package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantString.
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
public class ConstantString extends Constant
{
    /** gets the name index. */
    private int stringIndex;

    /**
     * constructs a new ConstantString.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantString(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readUnsignedShort());
    }

    /**
     * constructs a new ConstantString.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param stringIndex int; the stringIndex
     */
    public ConstantString(final Constant[] constantPool, final int stringIndex)
    {
        super(constantPool);
        this.stringIndex = stringIndex;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 8;
    }

    /**
     * returns the name index.
     * @return stringIndex
     */
    public int getStringIndex()
    {
        return this.stringIndex;
    }

    /**
     * returns the className of this constant.
     * @return String the className
     */
    public String getValue()
    {
        return ((ConstantUTF8) super.getConstantPool()[this.stringIndex]).getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantString[index=" + this.stringIndex + "]";
    }
}
