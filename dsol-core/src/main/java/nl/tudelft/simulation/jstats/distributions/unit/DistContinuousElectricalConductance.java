package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalConductanceUnit;
import org.djunits.value.vdouble.scalar.ElectricalConductance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalConductance is class defining a distribution for a ElectricalConductance scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalConductance extends DistContinuousUnit<ElectricalConductanceUnit, ElectricalConductance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalConductance scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ElectricalConductanceUnit; the unit for the values of the distribution
     */
    public DistContinuousElectricalConductance(final DistContinuous wrappedDistribution, final ElectricalConductanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalConductance scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousElectricalConductance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalConductanceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public ElectricalConductance draw()
    {
        return new ElectricalConductance(this.wrappedDistribution.draw(), this.unit);
    }
}
