package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Exponential distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/ExponentialDistribution.html">
 * https://mathworld.wolfram.com/ExponentialDistribution.html </a><br>
 * The exponential distribution describes the interarrival times of entities to a system that occur randomly at a constant rate.
 * The exponential distribution here is characterized by the mean interarrival time, but can also be characterized by this rate
 * parameter &lambda;; mean = 1 / &lambda;.
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
public class DistExponential extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** mean is the mean value of the exponential distribution. */
    private final double mean;

    /**
     * constructs a new exponential function. The exponential distribution describes the interarrival times of entities to a
     * system that occur randomly at a constant rate. The exponential distribution can also be characterized by this rate
     * parameter &lambda;; mean = 1 / &lambda;.
     * @param stream StreamInterface; the random number stream
     * @param mean double; the mean (mean &gt; 0) value of the exponential distribution. The exponential distribution can also
     *            be characterized by the rate parameter &lambda;; mean = 1 / &lambda;
     * @throws IllegalArgumentException in case mean &lt;= 0
     */
    public DistExponential(final StreamInterface stream, final double mean)
    {
        super(stream);
        Throw.when(mean <= 0.0, IllegalArgumentException.class, "Error Exponential - mean<=0");
        this.mean = mean;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        return -this.mean * Math.log(this.stream.nextDouble());
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x >= 0)
        {
            return (1 / this.mean) * Math.exp(-x / this.mean);
        }
        return 0.0;
    }

    /**
     * @return mean
     */
    public double getMean()
    {
        return this.mean;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Exponential(" + this.mean + ")";
    }
}
