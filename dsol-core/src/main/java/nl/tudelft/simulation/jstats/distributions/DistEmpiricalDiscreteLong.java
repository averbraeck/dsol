package nl.tudelft.simulation.jstats.distributions;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.empirical.DiscreteEmpiricalDistribution;
import nl.tudelft.simulation.jstats.distributions.empirical.DistributionEntry;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * A discrete empirical distribution as defined on page 326 of Law &amp; Kelton, based on an EmpiricalDistribution object.
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
public class DistEmpiricalDiscreteLong extends DistDiscrete
{
    /** */
    private static final long serialVersionUID = 20210403L;

    /** the empirical distribution. */
    private final DiscreteEmpiricalDistribution empiricalDistribution;

    /**
     * constructs a new DistEmpirical distribution.
     * @param stream StreamInterface; the stream to use
     * @param empiricalDistribution EmpiricalDistributionInterface; the cumulative distribution to use
     * @throws IllegalArgumentException when the empirical distribution has non-integer values
     */
    public DistEmpiricalDiscreteLong(final StreamInterface stream, final DiscreteEmpiricalDistribution empiricalDistribution)
    {
        super(stream);
        // check that the values in the distribution are integer valued and we do not interpolate
        for (Number n : empiricalDistribution.getValues())
        {
            Throw.when(n instanceof Double || n instanceof Float, IllegalArgumentException.class,
                    "empirical distribution can only contain integer or long values");
        }
        this.empiricalDistribution = empiricalDistribution;
    }

    /** {@inheritDoc} */
    @Override
    public long draw()
    {
        double u = this.stream.nextDouble();
        return this.empiricalDistribution.getCeilingEntry(u).getValue().longValue();
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final long observation)
    {
        DistributionEntry entry1 = this.empiricalDistribution.getFloorEntryForValue(observation);
        if (entry1 == null || entry1.getValue().longValue() != observation)
        {
            return 0.0;
        }
        double c1 = entry1.getCumulativeProbability();
        DistributionEntry entry0 = this.empiricalDistribution.getPrevEntry(c1);
        return (entry0 == null) ? c1 : c1 - entry0.getCumulativeProbability();
    }

}
