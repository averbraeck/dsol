package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantUTF8.
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
public class ConstantUTF8 extends Constant
{
    /** gets the name index. */
    private String value;

    /**
     * constructs a new ConstantUTF8.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantUTF8(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readUTF());
    }

    /**
     * constructs a new ConstantUTF8.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param value String; the content
     */
    public ConstantUTF8(final Constant[] constantPool, final String value)
    {
        super(constantPool);
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 1;
    }

    /**
     * returns the name index.
     * @return nameIndex
     */
    public String getValue()
    {
        return this.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantUTF8[" + this.value + "]";
    }
}
