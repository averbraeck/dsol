package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.FrequencyUnit;
import org.djunits.value.vdouble.scalar.Frequency;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousFrequency is class defining a distribution for a Frequency scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousFrequency extends DistContinuousUnit<FrequencyUnit, Frequency>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Frequency scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit FrequencyUnit; the unit for the values of the distribution
     */
    public DistContinuousFrequency(final DistContinuous wrappedDistribution, final FrequencyUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Frequency scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousFrequency(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, FrequencyUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Frequency draw()
    {
        return new Frequency(this.wrappedDistribution.draw(), this.unit);
    }
}
