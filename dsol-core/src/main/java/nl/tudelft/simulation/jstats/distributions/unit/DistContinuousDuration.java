package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousDuration is class defining a distribution for a Duration scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousDuration extends DistContinuousUnit<DurationUnit, Duration>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Duration scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit DurationUnit; the unit for the values of the distribution
     */
    public DistContinuousDuration(final DistContinuous wrappedDistribution, final DurationUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Duration scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousDuration(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, DurationUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Duration draw()
    {
        return new Duration(this.wrappedDistribution.draw(), this.unit);
    }
}
