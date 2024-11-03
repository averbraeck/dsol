package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.LinearDensityUnit;
import org.djunits.value.vdouble.scalar.LinearDensity;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousLinearDensity is class defining a distribution for a LinearDensity scalar. <br>
 * <br>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousLinearDensity extends DistContinuousUnit<LinearDensityUnit, LinearDensity>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws LinearDensity scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit LinearDensityUnit; the unit for the values of the distribution
     */
    public DistContinuousLinearDensity(final DistContinuous wrappedDistribution, final LinearDensityUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws LinearDensity scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousLinearDensity(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, LinearDensityUnit.SI);
    }

    @Override
    public LinearDensity draw()
    {
        return new LinearDensity(this.wrappedDistribution.draw(), this.unit);
    }
}
