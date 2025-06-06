package nl.tudelft.simulation.jstats.streams;

import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.language.reflection.StateSaver;

/**
 * The RandomNumberGenerator class provides an abstract basis for all pseudo random number generators.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public abstract class RandomNumberGenerator implements StreamInterface
{
    /** */
    private static final long serialVersionUID = 20150426L;

    /** The seed of the generator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected long seed;

    /** The original seed of the generator. */
    private final long originalSeed;

    /**
     * Construct a new RandomNumberGenerator. The seed value used in the rng is set to System.currentTimeMillis();
     */
    public RandomNumberGenerator()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Construct a new RandomNumberGenerator.
     * @param seed the seed of the generator.
     */
    public RandomNumberGenerator(final long seed)
    {
        if (seed <= 0)
        { throw new IllegalArgumentException("seed(" + seed + ")<=0"); }
        this.setSeed(seed);
        this.originalSeed = seed;
    }

    @Override
    public void reset()
    {
        this.setSeed(this.seed);
    }

    /**
     * returns the next value in the stream.
     * @param bits the number of bits used
     * @return the next value.
     */
    protected abstract long next(int bits);

    @Override
    public boolean nextBoolean()
    {
        return next(1) != 0;
    }

    @Override
    public double nextDouble()
    {
        long l = ((next(26)) << 27) + next(27);
        return l / (double) (1L << 53);
    }

    @Override
    public float nextFloat()
    {
        int i = (int) this.next(24);
        return i / ((float) (1 << 24));
    }

    @Override
    public int nextInt()
    {
        return (int) this.next(32);
    }

    @Override
    public synchronized int nextInt(final int i, final int j)
    {
        if (i < 0 || j <= 0 || i >= j)
        { throw new IllegalArgumentException("i, j must be positive"); }
        return i + (int) Math.floor((j - i + 1) * this.nextDouble());
    }

    @Override
    public long nextLong()
    {
        return ((next(32)) << 32) + next(32);
    }

    @Override
    public abstract void setSeed(long seed);

    @Override
    public long getSeed()
    {
        return this.seed;
    }

    @Override
    public long getOriginalSeed()
    {
        return this.originalSeed;
    }

    @Override
    public String toString()
    {
        return this.getClass().toString() + "[" + this.seed + "]";
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
