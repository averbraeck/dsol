package nl.tudelft.simulation.jstats.streams;

import java.util.Random;

import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.language.reflection.StateSaver;

/**
 * The Java2Random is an extension of the <code>java.util.Random</code> class which implements the StreamInterface.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
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
     * @param seed the seed to use.
     */
    public Java2Random(final long seed)
    {
        super(seed);
        this.seed = seed;
        this.originalSeed = seed;
    }

    @Override
    public void reset()
    {
        this.setSeed(this.seed);
    }

    @Override
    public long getOriginalSeed()
    {
        return this.originalSeed;
    }

    @Override
    public int nextInt(final int i, final int j)
    {
        return i + (int) Math.floor((j - i + 1) * this.nextDouble());
    }

    @Override
    public synchronized void setSeed(final long seed)
    {
        this.seed = seed;
        super.setSeed(seed);
    }

    @Override
    public long getSeed()
    {
        return this.seed;
    }

    @Override
    public byte[] saveState() throws StreamException
    {
        try
        {
            return StateSaver.saveState(this);
        }
        catch (DsolException exception)
        {
            throw new StreamException(exception);
        }
    }

    @Override
    public void restoreState(final byte[] state) throws StreamException
    {
        try
        {
            StateSaver.restoreState(this, state);
        }
        catch (DsolException exception)
        {
            throw new StreamException(exception);
        }
    }

}
