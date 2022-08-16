package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The LogNormal distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/LogNormalDistribution.html">
 * https://mathworld.wolfram.com/LogNormalDistribution.html<br>
 * The LogNormal distribution for random variable X is such that ln(X) ~ Normal(mu, sigma). </a>
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
public class DistLogNormal extends DistNormal
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the constant in the lognormal calculation: SQRT(2 * pi * sigma^2). */
    private final double c2pisigma2;

    /** the constant in the lognormal calculation: 2 * sigma^2. */
    private final double c2sigma2;

    /**
     * Construct a new Lognormal distribution. The LogNormal distribution for random variable X is such that ln(X) ~ Normal(mu,
     * sigma).
     * @param stream StreamInterface; the random number stream
     * @param normalMean double; the mean (mu) for the underlying normal distribution
     * @param normalStDev double; the standard deviation (sigma) for the underlying normal distribution
     * @throws IllegalArgumentException when normalStDev &lt;= 0
     */
    public DistLogNormal(final StreamInterface stream, final double normalMean, final double normalStDev)
    {
        super(stream, normalMean, normalStDev);
        this.c2sigma2 = 2.0 * this.sigma * this.sigma;
        this.c2pisigma2 = Math.sqrt(Math.PI * this.c2sigma2);
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        double y = this.mu + this.sigma * super.nextGaussian();
        return Math.exp(y);
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x > 0.0)
        {
            double xminmu = Math.log(x) - this.mu;
            return Math.exp(-1 * xminmu * xminmu / this.c2sigma2) / (x * this.c2pisigma2);
        }
        return 0.0;
    }

    /** {@inheritDoc} */
    @Override
    public double getCumulativeProbability(final double x)
    {
        if (x <= 0.0)
        {
            return 0.0;
        }
        return super.getCumulativeProbability(Math.log(x));
    }

    /** {@inheritDoc} */
    @Override
    public double getInverseCumulativeProbability(final double cumulativeProbability)
    {
        return Math.exp(super.getInverseCumulativeProbability(cumulativeProbability));
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "LogNormal(" + this.mu + "," + this.sigma + ")";
    }
}
