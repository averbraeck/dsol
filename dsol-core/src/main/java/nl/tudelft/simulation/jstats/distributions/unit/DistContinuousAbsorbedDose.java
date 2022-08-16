package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.AbsorbedDoseUnit;
import org.djunits.value.vdouble.scalar.AbsorbedDose;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousAbsorbedDose is class defining a distribution for a AbsorbedDose scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousAbsorbedDose extends DistContinuousUnit<AbsorbedDoseUnit, AbsorbedDose>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws AbsorbedDose scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit AbsorbedDoseUnit; the unit for the values of the distribution
     */
    public DistContinuousAbsorbedDose(final DistContinuous wrappedDistribution, final AbsorbedDoseUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws AbsorbedDose scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousAbsorbedDose(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, AbsorbedDoseUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public AbsorbedDose draw()
    {
        return new AbsorbedDose(this.wrappedDistribution.draw(), this.unit);
    }
}
