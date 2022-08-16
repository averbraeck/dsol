package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Negative Binomial distribution. It is also known as the Pascal distribution or P&oacute;lya distribution. It gives the
 * probability of x failures where there are s-1 successes in a total of x+s-1 Bernoulli trials, and trial (x+s) is a success.
 * The chance for success is p for each trial. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/NegativeBinomialDistribution.html">
 * https://mathworld.wolfram.com/NegativeBinomialDistribution.html </a>
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
public class DistNegBinomial extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 1L;

    /** s is the number of successes in the sequence of (x+n) trials, where trial (x+n) is a success. */
    private int s;

    /** p is the probability of success for each individual trial in the negative binomial distribution. */
    private double p;

    /** lnp is a helper variable equal to ln(1-p) to avoid repetitive calculation. */
    private double lnp;

    /**
     * constructs a new negative binomial distribution.
     * @param stream StreamInterface; the random number stream
     * @param s int; the number of successes in the sequence of (x+n) trials, where trial (x+n) is a success
     * @param p double; the probability of success for each individual trial in the negative binomial distribution
     * @throws IllegalArgumentException when s &lt;= 0 or p &lt;= 0 or p &gt;= 1
     */
    public DistNegBinomial(final StreamInterface stream, final int s, final double p)
    {
        super(stream);
        Throw.when(s <= 0 || p <= 0.0 || p >= 1.0, IllegalArgumentException.class,
                "Error NegBinomial - s<=0 or p<=0.0 or p>=1.0");
        this.s = s;
        this.p = p;
        this.lnp = Math.log(1.0 - this.p);
    }

    /** {@inheritDoc} */
    @Override
    public long draw()
    {
        long x = 0;
        for (int i = 0; i < this.s; i++)
        {
            double u = this.stream.nextDouble();
            x = x + (long) (Math.floor(Math.log(u) / this.lnp));
        }
        return x;
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final long observation)
    {
        if (observation >= 0)
        {
            return ProbMath.combinations(this.s + observation - 1, observation) * Math.pow(this.p, this.s)
                    * Math.pow(1 - this.p, observation);
        }
        return 0.0;
    }

    /**
     * Return the number of successes in the sequence of (x+n) trials, where trial (x+n) is a success.
     * @return int; the number of successes in the sequence of (x+n) trials, where trial (x+n) is a success
     */
    public int getS()
    {
        return this.s;
    }

    /**
     * Return the probability of success for each individual trial in the negative binomial distribution.
     * @return double; the probability of success for each individual trial in the negative binomial distribution
     */
    public double getP()
    {
        return this.p;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "NegBinomial(" + this.s + "," + this.p + ")";
    }
}
