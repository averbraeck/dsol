package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Normal distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/NormalDistribution.html"> https://mathworld.wolfram.com/NormalDistribution.html </a>
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
public class DistNormal extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** mu refers to the mean of the normal distribution. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public double mu;

    /** sigma refers to the standard deviation of the normal distribution. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public double sigma;

    /** nextNextGaussian is a helper attribute. */
    private double nextNextGaussian;

    /** haveNextNextGaussian is a helper attribute. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean haveNextNextGaussian;

    /**
     * constructs a standard normal distribution with mu=0 and sigma=1. Models probabilities that are the sum of a large number
     * of other probabilities by the virtue of the central limit theorem.
     * @param stream StreamInterface; the random number stream
     */
    public DistNormal(final StreamInterface stream)
    {
        super(stream);
        this.mu = 0.0;
        this.sigma = 1.0;
    }

    /**
     * constructs a normal distribution with provided mu and sigma.
     * @param stream StreamInterface; the random number stream
     * @param mu double; the mean
     * @param sigma double; the standard deviation
     * @throws IllegalArgumentException when sigma &lt;= 0
     */
    public DistNormal(final StreamInterface stream, final double mu, final double sigma)
    {
        super(stream);
        Throw.when(sigma <= 0.0, IllegalArgumentException.class, "Error Normal distribution - sigma<=0.0");
        this.sigma = sigma;
        this.mu = mu;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        return this.mu + this.sigma * nextGaussian();
    }

    /**
     * returns the cumulative probability of the x-value.
     * @param x double; the observation x
     * @return double the cumulative probability
     */
    public double getCumulativeProbability(final double x)
    {
        return 0.5 + 0.5 * ProbMath.erf((x - this.mu) / (Math.sqrt(2.0) * this.sigma));
    }

    /**
     * returns the x-value of the given cumulativePropability.
     * @param cumulativeProbability double; reflects cum prob
     * @return double the inverse cumulative probability
     */
    public double getInverseCumulativeProbability(final double cumulativeProbability)
    {
        return this.mu + this.sigma * Math.sqrt(2.0) * ProbMath.erfInv(2.0 * cumulativeProbability - 1.0);
    }

    /**
     * Generates the next pseudorandom, Gaussian (normally) distributed double value, with mean 0.0 and standard deviation 1.0
     * see section 3.4.1 of The Art of Computer Programming, Volume 2 by Donald Knuth.
     * @return double the next Gaussian value
     */
    protected synchronized double nextGaussian()
    {
        if (this.haveNextNextGaussian)
        {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        }
        double v1, v2, s;
        do
        {
            v1 = 2 * this.stream.nextDouble() - 1; // between -1.0 and 1.0
            v2 = 2 * this.stream.nextDouble() - 1; // between -1.0 and 1.0
            s = v1 * v1 + v2 * v2;
        }
        while (s >= 1);
        double norm = Math.sqrt(-2 * Math.log(s) / s);
        this.nextNextGaussian = v2 * norm;
        this.haveNextNextGaussian = true;
        return v1 * norm;
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        return 1.0 / (this.sigma * Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * Math.pow((x - this.mu) / this.sigma, 2));
    }

    /**
     * @return mu
     */
    public double getMu()
    {
        return this.mu;
    }

    /**
     * @return sigma
     */
    public double getSigma()
    {
        return this.sigma;
    }

    /** {@inheritDoc} */
    @Override
    public void setStream(final StreamInterface stream)
    {
        super.setStream(stream);
        this.haveNextNextGaussian = false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Normal(" + this.mu + "," + this.sigma + ")";
    }
}
