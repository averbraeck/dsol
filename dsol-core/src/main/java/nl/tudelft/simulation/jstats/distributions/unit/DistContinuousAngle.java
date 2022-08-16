package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.AngleUnit;
import org.djunits.value.vdouble.scalar.Angle;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousAngle is class defining a distribution for a Angle scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousAngle extends DistContinuousUnit<AngleUnit, Angle>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Angle scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit AngleUnit; the unit for the values of the distribution
     */
    public DistContinuousAngle(final DistContinuous wrappedDistribution, final AngleUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Angle scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousAngle(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, AngleUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Angle draw()
    {
        return new Angle(this.wrappedDistribution.draw(), this.unit);
    }
}
