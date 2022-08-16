package nl.tudelft.simulation.jstats.streams;

import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.language.reflection.StateSaver;

/**
 * The RandomNumberGenerator class provides an abstract basis for all pseudo random number generators.
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
     * @param seed long; the seed of the generator.
     */
    public RandomNumberGenerator(final long seed)
    {
        if (seed <= 0)
        {
            throw new IllegalArgumentException("seed(" + seed + ")<=0");
        }
        this.setSeed(seed);
        this.originalSeed = seed;
    }

    /** {@inheritDoc} */
    @Override
    public void reset()
    {
        this.setSeed(this.seed);
    }

    /**
     * returns the next value in the stream.
     * @param bits int; the number of bits used
     * @return the next value.
     */
    protected abstract long next(int bits);

    /** {@inheritDoc} */
    @Override
    public boolean nextBoolean()
    {
        return next(1) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public double nextDouble()
    {
        long l = ((next(26)) << 27) + next(27);
        return l / (double) (1L << 53);
    }

    /** {@inheritDoc} */
    @Override
    public float nextFloat()
    {
        int i = (int) this.next(24);
        return i / ((float) (1 << 24));
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt()
    {
        return (int) this.next(32);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int nextInt(final int i, final int j)
    {
        if (i < 0 || j <= 0 || i >= j)
        {
            throw new IllegalArgumentException("i, j must be positive");
        }
        return i + (int) Math.floor((j - i + 1) * this.nextDouble());
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong()
    {
        return ((next(32)) << 32) + next(32);
    }

    /** {@inheritDoc} */
    @Override
    public abstract void setSeed(long seed);

    /** {@inheritDoc} */
    @Override
    public long getSeed()
    {
        return this.seed;
    }

    /** {@inheritDoc} */
    @Override
    public long getOriginalSeed()
    {
        return this.originalSeed;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.getClass().toString() + "[" + this.seed + "]";
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
