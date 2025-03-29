package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Uniform distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/UniformDistribution.html"> https://mathworld.wolfram.com/UniformDistribution.html </a>
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
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
     * @param stream the random number stream
     * @param min the minimum value
     * @param max the maximum value
     */
    public DistUniform(final StreamInterface stream, final double min, final double max)
    {
        super(stream);
        Throw.when(max <= min, IllegalArgumentException.class, "Error Uniform - max <= min");
        this.min = min;
        this.max = max;
    }

    @Override
    public double draw()
    {
        return this.min + (this.max - this.min) * this.stream.nextDouble();
    }

    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x >= this.min && x <= this.max)
        { return 1.0 / (this.max - this.min); }
        return 0.0;
    }

    /**
     * Return the minimum value.
     * @return the minimum value
     */
    public double getMin()
    {
        return this.min;
    }

    /**
     * Return the maximum value.
     * @return the maximum value
     */
    public double getMax()
    {
        return this.max;
    }

    @Override
    public String toString()
    {
        return "Uniform(" + this.min + "," + this.max + ")";
    }
}
