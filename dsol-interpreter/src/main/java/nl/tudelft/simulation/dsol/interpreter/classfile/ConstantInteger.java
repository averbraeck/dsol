package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantInteger.
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
public class ConstantInteger extends Constant
{
    /** the value. */
    private int bytes;

    /**
     * constructs a new ConstantInteger.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantInteger(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readInt());
    }

    /**
     * constructs a new ConstantInteger.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param bytes int; the bytes
     */
    public ConstantInteger(final Constant[] constantPool, final int bytes)
    {
        super(constantPool);
        this.bytes = bytes;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 3;
    }

    /**
     * returns the value.
     * @return int the value
     */
    public int getValue()
    {
        return this.bytes;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantInteger[value=" + this.bytes + "]";
    }
}
