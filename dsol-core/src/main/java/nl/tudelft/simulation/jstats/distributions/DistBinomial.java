package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Binomial distribution. The binomial distribution is the probability of the number of successes in a sequence of n
 * independent experiments, each with success (probability p) or failure (probability q = 1 − p). For more information on this
 * distribution see <a href="https://mathworld.wolfram.com/BinomialDistribution.html">
 * https://mathworld.wolfram.com/BinomialDistribution.html </a>
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
public class DistBinomial extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 1L;

    /** n is the number of independent experiments for the Binomial distribution. */
    private final int n;

    /** p is the probability of success for each individual trial in the binomial distribution. */
    private final double p;

    /**
     * constructs a Binomial distribution. It calculates the probability for a number of successes in n independent Bernoulli
     * trials with probability p of success on each trial.
     * @param stream StreamInterface; the random number stream
     * @param n long; the number of independent experiments for the Binomial distribution
     * @param p double; the probability of success for each individual trial in the binomial distribution
     * @throws IllegalArgumentException when n &lt;= 0 or p &lt;= 0 or p &gt;= 1
     */
    public DistBinomial(final StreamInterface stream, final int n, final double p)
    {
        super(stream);
        Throw.when(n <= 0 || p <= 0 || p >= 1, IllegalArgumentException.class, "Error Binomial - n<=0 or p<=0.0 or p>=1.0");
        this.n = n;
        this.p = p;
    }

    /** {@inheritDoc} */
    @Override
    public long draw()
    {
        long x = 0;
        for (int i = 0; i < this.n; i++)
        {
            if (this.stream.nextDouble() <= this.p)
            {
                x++;
            }
        }
        return x;
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final long observation)
    {
        if (observation <= this.n && observation >= 0)
        {
            return ProbMath.combinations(this.n, observation) * Math.pow(this.p, observation)
                    * Math.pow(1 - this.p, this.n - observation);
        }
        return 0.0;
    }

    /**
     * Return the number of independent experiments for the Binomial distribution.
     * @return int; the number of independent experiments for the Binomial distribution
     */
    public int getN()
    {
        return this.n;
    }

    /**
     * Return the probability of success for each individual trial in the binomial distribution.
     * @return double; the probability of success for each individual trial in the binomial distribution
     */
    public double getP()
    {
        return this.p;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Binomial(" + this.n + "," + this.p + ")";
    }
}
