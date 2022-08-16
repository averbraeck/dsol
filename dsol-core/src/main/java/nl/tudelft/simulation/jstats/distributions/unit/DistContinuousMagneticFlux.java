package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.MagneticFluxUnit;
import org.djunits.value.vdouble.scalar.MagneticFlux;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousMagneticFlux is class defining a distribution for a MagneticFlux scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousMagneticFlux extends DistContinuousUnit<MagneticFluxUnit, MagneticFlux>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws MagneticFlux scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit MagneticFluxUnit; the unit for the values of the distribution
     */
    public DistContinuousMagneticFlux(final DistContinuous wrappedDistribution, final MagneticFluxUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws MagneticFlux scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousMagneticFlux(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, MagneticFluxUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public MagneticFlux draw()
    {
        return new MagneticFlux(this.wrappedDistribution.draw(), this.unit);
    }
}
