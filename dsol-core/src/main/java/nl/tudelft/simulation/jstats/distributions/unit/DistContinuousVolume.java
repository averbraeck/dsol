package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Volume;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousVolume is class defining a distribution for a Volume scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousVolume extends DistContinuousUnit<VolumeUnit, Volume>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws Volume scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit VolumeUnit; the unit for the values of the distribution
     */
    public DistContinuousVolume(final DistContinuous wrappedDistribution, final VolumeUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws Volume scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousVolume(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, VolumeUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public Volume draw()
    {
        return new Volume(this.wrappedDistribution.draw(), this.unit);
    }
}
