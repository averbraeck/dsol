package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousLength is class defining a distribution for a Length scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousLength extends DistContinuousUnit<LengthUnit, Length>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Length scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit LengthUnit; the unit for the values of the distribution
     */
    public DistContinuousLength(final DistContinuous wrappedDistribution, final LengthUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Length scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousLength(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, LengthUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Length draw()
    {
        return new Length(this.wrappedDistribution.draw(), this.unit);
    }
}
