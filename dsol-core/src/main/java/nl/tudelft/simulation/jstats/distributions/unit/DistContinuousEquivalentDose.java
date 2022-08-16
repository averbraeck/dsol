package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.EquivalentDoseUnit;
import org.djunits.value.vdouble.scalar.EquivalentDose;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousEquivalentDose is class defining a distribution for a EquivalentDose scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousEquivalentDose extends DistContinuousUnit<EquivalentDoseUnit, EquivalentDose>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws EquivalentDose scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit EquivalentDoseUnit; the unit for the values of the distribution
     */
    public DistContinuousEquivalentDose(final DistContinuous wrappedDistribution, final EquivalentDoseUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws EquivalentDose scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousEquivalentDose(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, EquivalentDoseUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public EquivalentDose draw()
    {
        return new EquivalentDose(this.wrappedDistribution.draw(), this.unit);
    }
}
