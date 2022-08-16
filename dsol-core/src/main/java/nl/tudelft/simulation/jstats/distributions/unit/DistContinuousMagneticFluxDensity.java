package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.MagneticFluxDensityUnit;
import org.djunits.value.vdouble.scalar.MagneticFluxDensity;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousMagneticFluxDensity is class defining a distribution for a MagneticFluxDensity scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousMagneticFluxDensity extends DistContinuousUnit<MagneticFluxDensityUnit, MagneticFluxDensity>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws MagneticFluxDensity scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit MagneticFluxDensityUnit; the unit for the values of the distribution
     */
    public DistContinuousMagneticFluxDensity(final DistContinuous wrappedDistribution, final MagneticFluxDensityUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws MagneticFluxDensity scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousMagneticFluxDensity(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, MagneticFluxDensityUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public MagneticFluxDensity draw()
    {
        return new MagneticFluxDensity(this.wrappedDistribution.draw(), this.unit);
    }
}
