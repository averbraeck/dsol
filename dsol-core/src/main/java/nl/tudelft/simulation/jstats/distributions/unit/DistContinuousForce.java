package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ForceUnit;
import org.djunits.value.vdouble.scalar.Force;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousForce is class defining a distribution for a Force scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousForce extends DistContinuousUnit<ForceUnit, Force>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Force scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ForceUnit; the unit for the values of the distribution
     */
    public DistContinuousForce(final DistContinuous wrappedDistribution, final ForceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Force scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousForce(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ForceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Force draw()
    {
        return new Force(this.wrappedDistribution.draw(), this.unit);
    }
}
