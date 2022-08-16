package nl.tudelft.simulation.jstats.distributions;

import nl.tudelft.simulation.jstats.distributions.empirical.DistributionEntry;
import nl.tudelft.simulation.jstats.distributions.empirical.InterpolatedEmpiricalDistribution;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The empirical distribution is a distribution where the information is stored in an EmpiricalDistribution, consisting of pairs
 * of values and cumulative probabilities. Values are interpolated, creating a continuous distribution function.
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
public class DistEmpiricalInterpolated extends DistContinuous
{
    /** */
    private static final long serialVersionUID = 20210403L;

    /** is the distribution grouped? */
    private final InterpolatedEmpiricalDistribution empiricalDistribution;

    /**
     * constructs a new DistEmpirical.
     * @param stream StreamInterface; the stream to use
     * @param empiricalDistribution InterpolatedEmpiricalDistribution; the cumulative distribution to use
     */
    public DistEmpiricalInterpolated(final StreamInterface stream,
            final InterpolatedEmpiricalDistribution empiricalDistribution)
    {
        super(stream);
        this.empiricalDistribution = empiricalDistribution;
    }

    /** {@inheritDoc} */
    @Override
    public double draw()
    {
        double u = this.stream.nextDouble();
        DistributionEntry entry0 = this.empiricalDistribution.getFloorEntry(u);
        DistributionEntry entry1 = this.empiricalDistribution.getCeilingEntry(u);
        double v1 = entry1.getValue().doubleValue();
        double v0 = entry0 != null ? entry0.getValue().doubleValue() : v1;
        double c1 = entry1.getCumulativeProbability();
        double c0 = entry0 != null ? entry0.getCumulativeProbability() : 0.0;
        return v0 + (v1 - v0) * (u - c0) / (c1 - c0);
    }

    /** {@inheritDoc} */
    @Override
    public double getProbabilityDensity(final double x)
    {
        DistributionEntry entry0 = this.empiricalDistribution.getFloorEntryForValue(x);
        DistributionEntry entry1 = this.empiricalDistribution.getCeilingEntryForValue(x);
        if (entry0 == null || entry1 == null)
        {
            return 0.0;
        }
        double v0 = entry0.getValue().doubleValue();
        double v1 = entry1.getValue().doubleValue();
        double c0 = entry0.getCumulativeProbability();
        double c1 = entry1.getCumulativeProbability();
        return (c1 - c0) / (v1 - v0);
    }

}
