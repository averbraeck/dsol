package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.PressureUnit;
import org.djunits.value.vdouble.scalar.Pressure;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousPressure is class defining a distribution for a Pressure scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousPressure extends DistContinuousUnit<PressureUnit, Pressure>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Pressure scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit PressureUnit; the unit for the values of the distribution
     */
    public DistContinuousPressure(final DistContinuous wrappedDistribution, final PressureUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Pressure scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousPressure(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, PressureUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Pressure draw()
    {
        return new Pressure(this.wrappedDistribution.draw(), this.unit);
    }
}
