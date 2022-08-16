package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Triangular distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/TriangularDistribution.html">
 * https://mathworld.wolfram.com/TriangularDistribution.html </a>
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
public class DistTriangular extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the minimum. */
    private final double min;

    /** the mode. */
    private final double mode;

    /** the maximum. */
    private final double max;

    /**
     * constructs a new triangular distribution with a minimum, mode, and maximum.
     * @param stream StreamInterface; the random number stream
     * @param min double; the minimum
     * @param mode double; the mode
     * @param max double; the maximum
     * @throws IllegalArgumentException when mode &lt; min or mode &gt; max or min == max
     */
    public DistTriangular(final StreamInterface stream, final double min, final double mode, final double max)
    {
        super(stream);
        Throw.when(mode < min || mode > max || min == max, IllegalArgumentException.class,
                "Triangular distribution, mode < min or mode > max or min == max");
        this.min = min;
        this.mode = mode;
        this.max = max;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        double u = this.stream.nextDouble();
        if (u <= ((this.mode - this.min) / (this.max - this.min)))
        {
            return this.min + Math.sqrt((this.mode - this.min) * (this.max - this.min) * u);
        }
        return this.max - Math.sqrt((this.max - this.min) * (this.max - this.mode) * (1.0d - u));
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x >= this.min && x <= this.mode)
        {
            return 2.0 * (x - this.min) / ((this.max - this.min) * (this.mode - this.min));
        }
        if (x >= this.mode && x <= this.max)
        {
            return 2.0 * (this.max - x) / ((this.max - this.min) * (this.max - this.mode));
        }
        return 0.0;
    }

    /**
     * Return the minimum value of the distribution.
     * @return double; the minimum value of the distribution
     */
    public double getMin()
    {
        return this.min;
    }

    /**
     * Return the mode of the distribution.
     * @return double; the mode of the distribution
     */
    public double getMode()
    {
        return this.mode;
    }

    /**
     * Return the maximum value of the distribution.
     * @return double; the maximum value of the distribution.
     */
    public double getMax()
    {
        return this.max;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Triangular(" + this.min + "," + this.mode + "," + this.max + ")";
    }
}
