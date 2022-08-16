package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Constant distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/ContinuousDistribution.html">
 * https://mathworld.wolfram.com/ContinuousDistribution.html </a>
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
public class DistConstant extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 1L;

    /** value is the value of the constant distribution. */
    private final double constant;

    /**
     * constructs a new constant distribution.
     * @param stream StreamInterface; the random number stream
     * @param constant double; the value
     */
    public DistConstant(final StreamInterface stream, final double constant)
    {
        super(stream);
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        this.stream.nextDouble();
        return this.constant;
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        if (x == this.constant)
        {
            return 1.0; // actually this value should be infinity...
        }
        return 0.0;
    }

    /**
     * Return the constant.
     * @return double; the constant
     */
    public double getConstant()
    {
        return this.constant;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Constant(" + this.constant + ")";
    }
}
