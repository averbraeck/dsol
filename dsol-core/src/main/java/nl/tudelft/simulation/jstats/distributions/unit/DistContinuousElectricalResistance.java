package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalResistanceUnit;
import org.djunits.value.vdouble.scalar.ElectricalResistance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalResistance is class defining a distribution for a ElectricalResistance scalar. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalResistance extends DistContinuousUnit<ElectricalResistanceUnit, ElectricalResistance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalResistance scalars.
     * @param wrappedDistribution the wrapped continuous distribution
     * @param unit the unit for the values of the distribution
     */
    public DistContinuousElectricalResistance(final DistContinuous wrappedDistribution, final ElectricalResistanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalResistance scalars in SI units.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousElectricalResistance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalResistanceUnit.SI);
    }

    @Override
    public ElectricalResistance draw()
    {
        return new ElectricalResistance(this.wrappedDistribution.draw(), this.unit);
    }
}
