package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Bernoulli distribution, with p as the probability for success. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/BernouilliDistribution.html">
 * https://mathworld.wolfram.com/BernouilliDistribution.html </a>
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistBernoulli extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 1L;

    /** p is the probability for success (X=1) of the Bernouilli distribution. */
    private final double p;

    /**
     * constructs a new Bernoulli distribution, with p as the probability for success (X=1), where failure is associated with
     * X=0.
     * @param stream StreamInterface; is the stream
     * @param p double; the probability for success of the Bernoulli distribution
     * @throws IllegalArgumentException when p &lt; 0 or p &gt; 1
     */
    public DistBernoulli(final StreamInterface stream, final double p)
    {
        super(stream);
        Throw.when(p < 0.0 || p > 1.0, IllegalArgumentException.class, "Error Bernoulli - p<0 or p>1 (p=" + p + ")");
        this.p = p;
    }

    /**
     * draws the next value from the Bernoulli distribution. More information on this distribution can be found at <br>
     * <a href="https://mathworld.wolfram.com/BernoulliDistribution.html">https://mathworld.wolfram.com/
     * BernoulliDistribution.html</a>.
     * @return the next value {0,1}, where 1 denotes success (probability p), and 0 denotes failure (probability (1-p).
     */
    @Override
    public long draw()
    {
        if (this.stream.nextDouble() <= this.p)
        {
            return 1L;
        }
        return 0L;
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final long observation)
    {
        if (observation == 0)
        {
            return 1 - this.p;
        }
        if (observation == 1)
        {
            return this.p;
        }
        return 0;
    }

    /**
     * Return p, the probability for success (X=1), where failure is associated with X=0.
     * @return double; p, the probability for success (X=1)
     */
    public double getP()
    {
        return this.p;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Bernoulli(" + this.p + ")";
    }
}
