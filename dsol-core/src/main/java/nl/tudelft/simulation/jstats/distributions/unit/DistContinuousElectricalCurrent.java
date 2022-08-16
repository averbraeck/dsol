package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalCurrentUnit;
import org.djunits.value.vdouble.scalar.ElectricalCurrent;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalCurrent is class defining a distribution for a ElectricalCurrent scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalCurrent extends DistContinuousUnit<ElectricalCurrentUnit, ElectricalCurrent>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalCurrent scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ElectricalCurrentUnit; the unit for the values of the distribution
     */
    public DistContinuousElectricalCurrent(final DistContinuous wrappedDistribution, final ElectricalCurrentUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalCurrent scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousElectricalCurrent(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalCurrentUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public ElectricalCurrent draw()
    {
        return new ElectricalCurrent(this.wrappedDistribution.draw(), this.unit);
    }
}
