package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.PositionUnit;
import org.djunits.value.vdouble.scalar.Position;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousPosition is class defining a distribution for a Position scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousPosition extends DistContinuousUnit<PositionUnit, Position>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Position scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit PositionUnit; the unit for the values of the distribution
     */
    public DistContinuousPosition(final DistContinuous wrappedDistribution, final PositionUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Position scalars in BASE units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousPosition(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, PositionUnit.DEFAULT);
    }

    /** {@inheritDoc} */
    @Override
    public Position draw()
    {
        return new Position(this.wrappedDistribution.draw(), this.unit);
    }
}
