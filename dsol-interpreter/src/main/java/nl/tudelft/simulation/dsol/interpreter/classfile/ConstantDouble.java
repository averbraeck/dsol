package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantDouble.
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
public class ConstantDouble extends Constant
{
    /** the value. */
    private double bytes;

    /**
     * constructs a new ConstantDouble.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantDouble(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readDouble());
    }

    /**
     * constructs a new ConstantDouble.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param bytes double; the bytes
     */
    public ConstantDouble(final Constant[] constantPool, final double bytes)
    {
        super(constantPool);
        this.bytes = bytes;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 6;
    }

    /**
     * returns the value.
     * @return double the value
     */
    public double getValue()
    {
        return this.bytes;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantDouble[value=" + this.bytes + "]";
    }
}
