package nl.tudelft.simulation.jstats.streams;

import java.util.Random;

import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.language.reflection.StateSaver;

/**
 * The Java2Random is an extension of the <code>java.util.Random</code> class which implements the StreamInterface.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class Java2Random extends Random implements StreamInterface
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /**
     * Seed is a link to the current seed value. The reason to store the seed in this variable is that there is no getSeed() on
     * the Random class in Java.
     */
    private long seed;

    /** The original seed of the generator. */
    private final long originalSeed;

    /**
     * Create a new Java2Random and initializes with System.currentTimeMillis.
     */
    public Java2Random()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Create a new Java2Random and initialize with a given seed.
     * @param seed long; the seed to use.
     */
    public Java2Random(final long seed)
    {
        super(seed);
        this.seed = seed;
        this.originalSeed = seed;
    }

    /** {@inheritDoc} */
    @Override
    public void reset()
    {
        this.setSeed(this.seed);
    }

    /** {@inheritDoc} */
    @Override
    public long getOriginalSeed()
    {
        return this.originalSeed;
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt(final int i, final int j)
    {
        return i + (int) Math.floor((j - i + 1) * this.nextDouble());
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setSeed(final long seed)
    {
        this.seed = seed;
        super.setSeed(seed);
    }

    /** {@inheritDoc} */
    @Override
    public long getSeed()
    {
        return this.seed;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] saveState() throws StreamException
    {
        try
        {
            return StateSaver.saveState(this);
        }
        catch (DSOLException exception)
        {
            throw new StreamException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void restoreState(final byte[] state) throws StreamException
    {
        try
        {
            StateSaver.restoreState(this, state);
        }
        catch (DSOLException exception)
        {
            throw new StreamException(exception);
        }
    }

}
