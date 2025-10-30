package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.LuminousIntensityUnit;
import org.djunits.value.vdouble.scalar.LuminousIntensity;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousLuminousIntensity is class defining a distribution for a LuminousIntensity scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousLuminousIntensity extends DistContinuousUnit<LuminousIntensityUnit, LuminousIntensity>
{
    /**
     * Constructs a new continuous distribution that draws LuminousIntensity scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousLuminousIntensity(final DistContinuous wrappedDistribution, final LuminousIntensityUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws LuminousIntensity scalars in SI units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousLuminousIntensity(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, LuminousIntensityUnit.SI);
    }

    @Override
    public LuminousIntensity draw()
    {
        return new LuminousIntensity(this.wrappedDistribution.draw(), this.unit);
    }
}
