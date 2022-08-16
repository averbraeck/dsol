package nl.tudelft.simulation.jstats.distributions.unit;

import org.djunits.unit.FlowMassUnit;
import org.djunits.value.vdouble.scalar.FlowMass;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * DistContinuousFlowMass is class defining a distribution for a FlowMass scalar. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistContinuousFlowMass extends DistContinuousUnit<FlowMassUnit, FlowMass>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new continuous distribution that draws FlowMass scalars.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     * @param unit FlowMassUnit; the unit for the values of the distribution
     */
    public DistContinuousFlowMass(final DistContinuous wrappedDistribution, final FlowMassUnit unit)
    {
        super(wrappedDistribution, unit);
    }

    /**
     * Constructs a new continuous distribution that draws FlowMass scalars in SI units.
     * @param wrappedDistribution DistContinuous; the wrapped continuous distribution
     */
    public DistContinuousFlowMass(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution, FlowMassUnit.SI);
    }

    /** {@inheritDoc} */
    @Override
    public FlowMass draw()
    {
        return new FlowMass(this.wrappedDistribution.draw(), this.unit);
    }
}
