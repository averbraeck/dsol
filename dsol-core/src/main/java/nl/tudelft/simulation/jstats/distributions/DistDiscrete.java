package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The discrete distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/DiscreteDistribution.html"> https://mathworld.wolfram.com/DiscreteDistribution.html
 * </a>
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
public abstract class DistDiscrete extends Dist
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new discrete distribution.
     * @param stream StreamInterface; the random number stream
     */
    public DistDiscrete(final StreamInterface stream)
    {
        super(stream);
    }

    /**
     * draws the next long from the stream.
     * @return long
     */
    public abstract long draw();

    /**
     * returns the probability of the observation in this particular distribution.
     * @param observation long; the discrete observation.
     * @return double the probability.
     */
    public abstract double probability(long observation);
}
