package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.ElectricalInductanceUnit;
import org.djunits.value.vdouble.scalar.ElectricalInductance;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousElectricalInductance is class defining a distribution for a ElectricalInductance scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousElectricalInductance extends DistContinuousUnit<ElectricalInductanceUnit, ElectricalInductance>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws ElectricalInductance scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit ElectricalInductanceUnit; the unit for the values of the distribution
     */
    public DistContinuousElectricalInductance(final DistContinuous wrappedDistribution, final ElectricalInductanceUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws ElectricalInductance scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousElectricalInductance(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, ElectricalInductanceUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public ElectricalInductance draw()
    {
        return new ElectricalInductance(this.wrappedDistribution.draw(), this.unit);
    }
}
