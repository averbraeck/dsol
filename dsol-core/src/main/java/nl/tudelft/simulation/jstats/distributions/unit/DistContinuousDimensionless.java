package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.DimensionlessUnit;
import org.djunits.value.vdouble.scalar.Dimensionless;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousDimensionless is class defining a distribution for a Dimensionless scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousDimensionless extends DistContinuousUnit<DimensionlessUnit, Dimensionless>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Dimensionless scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit DimensionlessUnit; the unit for the values of the distribution
     */
    public DistContinuousDimensionless(final DistContinuous wrappedDistribution, final DimensionlessUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Dimensionless scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousDimensionless(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, DimensionlessUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Dimensionless draw()
    {
        return new Dimensionless(this.wrappedDistribution.draw(), this.unit);
    }
}
