package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.PowerUnit;
import org.djunits.value.vdouble.scalar.Power;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousPower is class defining a distribution for a Power scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousPower extends DistContinuousUnit<PowerUnit, Power>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Power scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit PowerUnit; the unit for the values of the distribution
     */
    public DistContinuousPower(final DistContinuous wrappedDistribution, final PowerUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Power scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousPower(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, PowerUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Power draw()
    {
        return new Power(this.wrappedDistribution.draw(), this.unit);
    }
}
