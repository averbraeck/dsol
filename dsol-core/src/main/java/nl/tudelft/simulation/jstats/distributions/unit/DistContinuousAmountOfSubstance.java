package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.AmountOfSubstanceUnit;
import org.djunits.value.vdouble.scalar.AmountOfSubstance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousAmountOfSubstance is class defining a distribution for a AmountOfSubstance scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousAmountOfSubstance extends DistContinuousUnit<AmountOfSubstanceUnit, AmountOfSubstance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws AmountOfSubstance scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit AmountOfSubstanceUnit; the unit for the values of the distribution
     */
    public DistContinuousAmountOfSubstance(final DistContinuous wrappedDistribution, final AmountOfSubstanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws AmountOfSubstance scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousAmountOfSubstance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, AmountOfSubstanceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public AmountOfSubstance draw()
    {
        return new AmountOfSubstance(this.wrappedDistribution.draw(), this.unit);
    }
}
