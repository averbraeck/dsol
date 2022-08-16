package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.SolidAngleUnit;
import org.djunits.value.vdouble.scalar.SolidAngle;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousSolidAngle is class defining a distribution for a SolidAngle scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousSolidAngle extends DistContinuousUnit<SolidAngleUnit, SolidAngle>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws SolidAngle scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit SolidAngleUnit; the unit for the values of the distribution
     */
    public DistContinuousSolidAngle(final DistContinuous wrappedDistribution, final SolidAngleUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws SolidAngle scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousSolidAngle(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, SolidAngleUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public SolidAngle draw()
    {
        return new SolidAngle(this.wrappedDistribution.draw(), this.unit);
    }
}
