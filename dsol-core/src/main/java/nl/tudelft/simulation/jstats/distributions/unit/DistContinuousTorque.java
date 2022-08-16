package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.TorqueUnit;
import org.djunits.value.vdouble.scalar.Torque;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousTorque is class defining a distribution for a Torque scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousTorque extends DistContinuousUnit<TorqueUnit, Torque>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Torque scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit TorqueUnit; the unit for the values of the distribution
     */
    public DistContinuousTorque(final DistContinuous wrappedDistribution, final TorqueUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Torque scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousTorque(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, TorqueUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Torque draw()
    {
        return new Torque(this.wrappedDistribution.draw(), this.unit);
    }
}
