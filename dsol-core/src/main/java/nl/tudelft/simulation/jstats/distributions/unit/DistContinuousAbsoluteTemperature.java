package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.AbsoluteTemperatureUnit;
import org.djunits.value.vdouble.scalar.AbsoluteTemperature;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousAbsoluteTemperature is class defining a distribution for a AbsoluteTemperature scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousAbsoluteTemperature extends DistContinuousUnit<AbsoluteTemperatureUnit, AbsoluteTemperature>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws AbsoluteTemperature scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit AbsoluteTemperatureUnit; the unit for the values of the distribution
     */
    public DistContinuousAbsoluteTemperature(final DistContinuous wrappedDistribution, final AbsoluteTemperatureUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws AbsoluteTemperature scalars in BASE units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousAbsoluteTemperature(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, AbsoluteTemperatureUnit.DEFAULT);
    }

    /** {@inheritDoc} */
    @Override
    public AbsoluteTemperature draw()
    {
        return new AbsoluteTemperature(this.wrappedDistribution.draw(), this.unit);
    }
}
