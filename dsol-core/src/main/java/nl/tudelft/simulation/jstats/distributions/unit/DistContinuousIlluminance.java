package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.IlluminanceUnit;
import org.djunits.value.vdouble.scalar.Illuminance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousIlluminance is class defining a distribution for a Illuminance scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousIlluminance extends DistContinuousUnit<IlluminanceUnit, Illuminance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Illuminance scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit IlluminanceUnit; the unit for the values of the distribution
     */
    public DistContinuousIlluminance(final DistContinuous wrappedDistribution, final IlluminanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Illuminance scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousIlluminance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, IlluminanceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Illuminance draw()
    {
        return new Illuminance(this.wrappedDistribution.draw(), this.unit);
    }
}
