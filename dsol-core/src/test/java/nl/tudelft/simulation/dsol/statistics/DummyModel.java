package nl.tudelft.simulation.dsol.statistics;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * DummyModel acts as an 'empty' DSOL model for the statistics tests.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DummyModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param simulator the simulator
     */
    public DummyModel(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        //
    }

}
