package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.MassUnit;
import org.djunits.value.vdouble.scalar.Mass;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousMass is class defining a distribution for a Mass scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousMass extends DistContinuousUnit<MassUnit, Mass>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Mass scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit MassUnit; the unit for the values of the distribution
     */
    public DistContinuousMass(final DistContinuous wrappedDistribution, final MassUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Mass scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousMass(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, MassUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Mass draw()
    {
        return new Mass(this.wrappedDistribution.draw(), this.unit);
    }
}
