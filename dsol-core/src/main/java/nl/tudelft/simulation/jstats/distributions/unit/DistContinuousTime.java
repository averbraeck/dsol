package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousTime is class defining a distribution for a Time scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousTime extends DistContinuousUnit<TimeUnit, Time>
{
    /**
     * Constructs a new continuous distribution that draws Time scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousTime(final DistContinuous wrappedDistribution, final TimeUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Time scalars in BASE units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousTime(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, TimeUnit.DEFAULT);
    }

    @Override
    public Time draw()
    {
        return new Time(this.wrappedDistribution.draw(), this.unit);
    }
}
