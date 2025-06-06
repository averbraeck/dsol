package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Constant distribution. This distribution maskers a constant discrete value.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class DistDiscreteConstant extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 1L;

    /** value is the value of the distribution. */
    private final long constant;

    /**
     * creates a new discrete constant distribution.
     * @param stream the random number stream
     * @param constant the value for this distribution
     */
    public DistDiscreteConstant(final StreamInterface stream, final long constant)
    {
        super(stream);
        this.constant = constant;
    }

    @Override
    public long draw()
    {
        this.stream.nextDouble();
        return this.constant;
    }

    @Override
    public double probability(final long observation)
    {
        if (observation == this.constant)
        { return 1.0; }
        return 0.0;
    }

    /**
     * Return the constant.
     * @return the constant
     */
    public long getConstant()
    {
        return this.constant;
    }

    @Override
    public String toString()
    {
        return "DiscreteConstant(" + this.constant + ")";
    }
}
