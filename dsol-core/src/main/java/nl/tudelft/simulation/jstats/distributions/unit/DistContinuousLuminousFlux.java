package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.LuminousFluxUnit;
import org.djunits.value.vdouble.scalar.LuminousFlux;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousLuminousFlux is class defining a distribution for a LuminousFlux scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousLuminousFlux extends DistContinuousUnit<LuminousFluxUnit, LuminousFlux>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws LuminousFlux scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousLuminousFlux(final DistContinuous wrappedDistribution, final LuminousFluxUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws LuminousFlux scalars in SI units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousLuminousFlux(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, LuminousFluxUnit.SI);
    }

    @Override
    public LuminousFlux draw()
    {
        return new LuminousFlux(this.wrappedDistribution.draw(), this.unit);
    }
}
