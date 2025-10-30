package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.AccelerationUnit;
import org.djunits.value.vdouble.scalar.Acceleration;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousAcceleration is class defining a distribution for a Acceleration scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousAcceleration extends DistContinuousUnit<AccelerationUnit, Acceleration>
{
    /**
     * Constructs a new continuous distribution that draws Acceleration scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousAcceleration(final DistContinuous wrappedDistribution, final AccelerationUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Acceleration scalars in SI units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousAcceleration(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, AccelerationUnit.SI);
    }

    @Override
    public Acceleration draw()
    {
        return new Acceleration(this.wrappedDistribution.draw(), this.unit);
    }
}
