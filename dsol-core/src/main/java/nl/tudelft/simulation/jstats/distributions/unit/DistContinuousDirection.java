package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.DirectionUnit;
import org.djunits.value.vdouble.scalar.Direction;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousDirection is class defining a distribution for a Direction scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousDirection extends DistContinuousUnit<DirectionUnit, Direction>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Direction scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousDirection(final DistContinuous wrappedDistribution, final DirectionUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Direction scalars in BASE units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousDirection(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, DirectionUnit.DEFAULT);
    }

    @Override
    public Direction draw()
    {
        return new Direction(this.wrappedDistribution.draw(), this.unit);
    }
}
