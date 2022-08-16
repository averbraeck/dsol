package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Geometric distribution. The geometric distribution is the only discrete memoryless random distribution. It is a discrete
 * analog of the exponential distribution. There are two variants, one that indicates the number of Bernoulli trials to get the
 * first success (1, 2, 3, ...), and one that indicates the number of failures before the first success (0, 1, 2, ...). In line
 * with Law &amp; Kelton, the version of the number of failures before the first success is modeled here, so X ={0, 1, 2, ...}.
 * For more information on this distribution see <a href="https://mathworld.wolfram.com/GeometricDistribution.html">
 * https://mathworld.wolfram.com/GeometricDistribution.html </a>
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
public class DistGeometric extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 1L;

    /** p is the the probability of success for each individual trial. */
    private final double p;

    /** lnp is a helper variable with value ln(1-p) to avoid repetitive calculation. */
    private final double lnp;

    /**
     * Construct a new geometric distribution for a repeated set of Bernoulli trials, indicating the number of failures before
     * the first success.
     * @param stream StreamInterface; the random number stream
     * @param p double; the probability of success for each individual trial
     * @throws IllegalArgumentException when p &lt;= 0 or p &gt;= 1
     */
    public DistGeometric(final StreamInterface stream, final double p)
    {
        super(stream);
        Throw.when(p <= 0.0 || p >= 1.0, IllegalArgumentException.class, "Error Geometric - p <= 0 or p >= 1");
        this.p = p;
        this.lnp = Math.log(1.0 - this.p);
    }

    /** {@inheritDoc} */
    @Override
    public long draw()
    {
        double u = this.stream.nextDouble();
        return (long) (Math.floor(Math.log(u) / this.lnp));
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final long observation)
    {
        if (observation >= 0)
        {
            return this.p * Math.pow(1 - this.p, observation);
        }
        return 0.0;
    }

    /**
     * Return the probability of success for each individual trial.
     * @return double; the probability of success for each individual trial
     */
    public double getP()
    {
        return this.p;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Geometric(" + this.p + ")";
    }
}
