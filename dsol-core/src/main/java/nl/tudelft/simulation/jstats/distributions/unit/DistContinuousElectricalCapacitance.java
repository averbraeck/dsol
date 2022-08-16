package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalCapacitanceUnit;
import org.djunits.value.vdouble.scalar.ElectricalCapacitance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalCapacitance is class defining a distribution for a ElectricalCapacitance scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalCapacitance extends DistContinuousUnit<ElectricalCapacitanceUnit, ElectricalCapacitance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalCapacitance scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ElectricalCapacitanceUnit; the unit for the values of the distribution
     */
    public DistContinuousElectricalCapacitance(final DistContinuous wrappedDistribution, final ElectricalCapacitanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalCapacitance scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousElectricalCapacitance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalCapacitanceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public ElectricalCapacitance draw()
    {
        return new ElectricalCapacitance(this.wrappedDistribution.draw(), this.unit);
    }
}
