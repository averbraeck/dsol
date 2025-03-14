package nl.tudelft.simulation.dsol.formalisms.flow;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistExponential;

/**
 * DelayDemo shows the statistics of the Delay block when linked to a Create block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DelayDemo
{
    /**
     * @param args not used
     * @throws InterruptedException on sleep cancel
     */
    public static void main(final String... args) throws InterruptedException
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var stream = getSimulator().getModel().getDefaultStream();
                var create = new Create<Double>("create", this.simulator)
                        .setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 1.0)))
                        .setDefaultStatistics()
                        .setEntitySupplier(() -> new Entity<>("entity", getSimulator().getSimulatorTime()));
                var delay = new Delay<Double>("delay", this.simulator)
                        .setDelayDistribution(new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 10.0)))
                        .setDefaultStatistics();
                create.setDestination(delay);
                var destroy = new Destroy<>("destroy", this.simulator);
                delay.setDestination(destroy);
            }
        };
        
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            Thread.sleep(10);
        }
        
        for (var statistic : model.getOutputStatistics())
        {
            if (statistic instanceof SimCounter sc)
            {
                System.out.println(SimCounter.reportHeader());
                System.out.println(sc.reportLine());
                System.out.println(SimCounter.reportFooter());
            }
            else if (statistic instanceof SimPersistent sp)
            {
                System.out.println(SimPersistent.reportHeader());
                System.out.println(sp.reportLine());
                System.out.println(SimPersistent.reportFooter());
            }
            else if (statistic instanceof SimTally st)
            {
                System.out.println(SimTally.reportHeader());
                System.out.println(st.reportLine());
                System.out.println(SimTally.reportFooter());
            }
        }
    }
}
