package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Speed;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousSpeed is class defining a distribution for a Speed scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousSpeed extends DistContinuousUnit<SpeedUnit, Speed>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Speed scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit SpeedUnit; the unit for the values of the distribution
     */
    public DistContinuousSpeed(final DistContinuous wrappedDistribution, final SpeedUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Speed scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousSpeed(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, SpeedUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Speed draw()
    {
        return new Speed(this.wrappedDistribution.draw(), this.unit);
    }
}
