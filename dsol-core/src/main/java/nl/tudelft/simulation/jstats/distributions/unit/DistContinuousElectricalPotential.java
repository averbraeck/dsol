package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalPotentialUnit;
import org.djunits.value.vdouble.scalar.ElectricalPotential;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalPotential is class defining a distribution for a ElectricalPotential scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalPotential extends DistContinuousUnit<ElectricalPotentialUnit, ElectricalPotential>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalPotential scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ElectricalPotentialUnit; the unit for the values of the distribution
     */
    public DistContinuousElectricalPotential(final DistContinuous wrappedDistribution, final ElectricalPotentialUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalPotential scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousElectricalPotential(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalPotentialUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public ElectricalPotential draw()
    {
        return new ElectricalPotential(this.wrappedDistribution.draw(), this.unit);
    }
}
