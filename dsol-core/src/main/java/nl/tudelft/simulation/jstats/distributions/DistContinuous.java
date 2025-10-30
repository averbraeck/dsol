package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The Continuous distribution. For more information on this distribution see
 * <a href="https://mathworld.wolfram.com/ContinuousDistribution.html">
 * https://mathworld.wolfram.com/ContinuousDistribution.html </a>
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public abstract class DistContinuous extends Dist
{
    /**
     * constructs a new continuous distribution.
     * @param stream the stream
     */
    public DistContinuous(final StreamInterface stream)
    {
        super(stream);
    }

    /**
     * draws the next stream value according to the probability of this this distribution.
     * @return the next double value drawn.
     */
    public abstract double draw();

    /**
     * returns the probability density value of a value x.
     * @param x the value for which the density function needs to be calculated
     * @return the probability density for value x
     */
    public abstract double getProbabilityDensity(double x);
}
