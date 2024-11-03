package nl.tudelft.simulation.dsol.simulators;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;

/**
 * The DevsSimulatorTestmodel specifies the model.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class DevsTestModel extends TestModel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new DEVSTestModel.
     * @param simulator the simulator
     */
    public DevsTestModel(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    @Override
    public void constructModel()
    {
        DevsSimulatorInterface<Double> devsSimulator = (DevsSimulatorInterface<Double>) getSimulator();
        super.constructModel();
        for (int i = 0; i < 100; i++)
        {
            try
            {
                devsSimulator.scheduleEvent(
                        new SimEvent<Double>(Math.random() * 100, this, "run", null));
            }
            catch (Exception exception)
            {
                this.simulator.getLogger().always().warn(exception, "constructModel");
            }
        }
    }

    /**
     * the method which is scheduled.
     */
    public void run()
    {
        // Testing method
    }
}
