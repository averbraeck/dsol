package nl.tudelft.simulation.dsol.demo.des.mm1.step7;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

/**
 * Discrete event queueing application.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
class DesQueueingApplication7
{
    /**
     * Constructor for the discrete event queueing application.
     */
    DesQueueingApplication7()
    {
        var simulator = new DevsSimulator<Double>("MM1.Simulator");
        double lambda = 1.0;
        double mu = 0.9;
        var model = new DesQueueingModel7(simulator, lambda, mu);
        var replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        simulator.start();
    }

    /**
     * Main program to start the discrete-event queueing application.
     * @param args not used
     */
    public static void main(final String[] args)
    {
        new DesQueueingApplication7();
    }

}
