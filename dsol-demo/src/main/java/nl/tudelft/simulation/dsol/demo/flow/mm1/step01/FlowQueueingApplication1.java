package nl.tudelft.simulation.dsol.demo.flow.mm1.step01;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;

/**
 * A runnable M/M/1 model based on flow simulation building blocks.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FlowQueueingApplication1
{
    /**
     * Build and execute a FlowQueueingModel.
     */
    public FlowQueueingApplication1()
    {
        var simulator = new DevsSimulator<Double>("MM1.Flow.Simulator");
        var stream = new MersenneTwister(12);
        var interArrivalTime = new DistExponential(stream, 1.0);
        var processingTime = new DistExponential(stream, 0.9);
        var model = new FlowQueueingModel1(simulator, interArrivalTime, processingTime);
        var replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        simulator.start();
    }
    
    /**
     * Command-line executable FlowQueueingApplication. 
     * @param args not used
     */
    public static void main(final String[] args)
    {
        new FlowQueueingApplication1();
    }

}
