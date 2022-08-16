package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Uniform distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/UniformDistribution.html"> https://mathworld.wolfram.com/UniformDistribution.html </a>
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
public class DistUniform extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the minimum. */
    private final double min;

    /** the maximum. */
    private final double max;

    /**
     * constructs a new uniform distribution. min and max are real numbers with min less than max. min is a location parameter,
     * max-min is a scale parameter.
     * @param stream StreamInterface; the random number stream
     * @param min double; the minimum value
     * @param max double; the maximum value
     */
    public DistUniform(final StreamInterface stream, final double min, final double max)
    {
        super(stream);
        Throw.when(max <= min, IllegalArgumentException.class, "Error Uniform - max <= min");
        this.min = min;
        this.max = max;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        return this.min + (this.max - this.min) * this.stream.nextDouble();
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x >= this.min && x <= this.max)
        {
            return 1.0 / (this.max - this.min);
        }
        return 0.0;
    }

    /**
     * Return the minimum value.
     * @return double; the minimum value
     */
    public double getMin()
    {
        return this.min;
    }

    /**
     * Return the maximum value.
     * @return double; the maximum value
     */
    public double getMax()
    {
        return this.max;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Uniform(" + this.min + "," + this.max + ")";
    }
}
