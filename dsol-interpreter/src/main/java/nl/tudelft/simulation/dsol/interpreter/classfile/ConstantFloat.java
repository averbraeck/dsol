package nl.tudelft.simulation.dsol.interpreter.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * A ConstantFloat.
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
public class ConstantFloat extends Constant
{
    /** the value. */
    private float bytes;

    /**
     * constructs a new ConstantFloat.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param inputStream DataInput; the inputstream to read from
     * @throws IOException on failure
     */
    public ConstantFloat(final Constant[] constantPool, final DataInput inputStream) throws IOException
    {
        this(constantPool, inputStream.readFloat());
    }

    /**
     * constructs a new ConstantFloat.
     * @param constantPool Constant[]; the constantPool it is part of
     * @param bytes float; the bytes
     */
    public ConstantFloat(final Constant[] constantPool, final float bytes)
    {
        super(constantPool);
        this.bytes = bytes;
    }

    /** {@inheritDoc} */
    @Override
    public int getTag()
    {
        return 4;
    }

    /**
     * returns the value.
     * @return float the value
     */
    public float getValue()
    {
        return this.bytes;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ConstantFloat[value=" + this.bytes + "]";
    }
}
