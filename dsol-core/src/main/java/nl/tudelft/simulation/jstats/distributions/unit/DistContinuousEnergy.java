package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.EnergyUnit;
import org.djunits.value.vdouble.scalar.Energy;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousEnergy is class defining a distribution for a Energy scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousEnergy extends DistContinuousUnit<EnergyUnit, Energy>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Energy scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit EnergyUnit; the unit for the values of the distribution
     */
    public DistContinuousEnergy(final DistContinuous wrappedDistribution, final EnergyUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Energy scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousEnergy(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, EnergyUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Energy draw()
    {
        return new Energy(this.wrappedDistribution.draw(), this.unit);
    }
}
