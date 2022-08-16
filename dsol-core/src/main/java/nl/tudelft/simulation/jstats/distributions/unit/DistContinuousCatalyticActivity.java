package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.CatalyticActivityUnit;
import org.djunits.value.vdouble.scalar.CatalyticActivity;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousCatalyticActivity is class defining a distribution for a CatalyticActivity scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousCatalyticActivity extends DistContinuousUnit<CatalyticActivityUnit, CatalyticActivity>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws CatalyticActivity scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit CatalyticActivityUnit; the unit for the values of the distribution
     */
    public DistContinuousCatalyticActivity(final DistContinuous wrappedDistribution, final CatalyticActivityUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws CatalyticActivity scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousCatalyticActivity(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, CatalyticActivityUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public CatalyticActivity draw()
    {
        return new CatalyticActivity(this.wrappedDistribution.draw(), this.unit);
    }
}
