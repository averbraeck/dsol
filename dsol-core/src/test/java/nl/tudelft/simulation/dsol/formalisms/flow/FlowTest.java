package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.Sleep;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * FlowTest tests the flow objects, such as Station, Seize, Delay, Release.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FlowTest
{
    /**
     * Test the Delay.
     */
    @Test
    public void delayTest()
    {
        DEVSSimulatorInterface<Double> simulator = new DEVSSimulator<Double>("sim");
        DSOLModel<Double, DEVSSimulatorInterface<Double>> model = makeModelDouble(simulator);
        SingleReplication<Double> replication = new SingleReplication<Double>("replication", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.runUpTo(1.0);
        wait(simulator, 500);
        int nrEvents = simulator.getEventList().size();
        StreamInterface stream = new MersenneTwister(10L);
        DistContinuousSimulationTime<Double> delayDistribution =
                new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 10.0));
        Delay<Double> delay = new Delay<Double>("delay", simulator, delayDistribution);
        assertEquals(simulator, delay.getSimulator());
        assertEquals(nrEvents, simulator.getEventList().size());

        Departure<Double> departure = new Departure<Double>("departure", simulator);
        delay.setDestination(departure);
        assertEquals(departure, delay.getDestination());
        String object = "abc";
        delay.receiveObject(object);
        assertEquals(nrEvents + 1, simulator.getEventList().size());
        simulator.runUpTo(5.0);
        wait(simulator, 500);
        assertEquals(nrEvents, simulator.getEventList().size());
    }

    /**
     * Wait as long as simulator is running, or a timeout has happened.
     * @param simulator the simulator
     * @param timeoutMs timeout in ms
     */
    private void wait(final SimulatorInterface<?> simulator, final long timeoutMs)
    {
        long millis = System.currentTimeMillis();
        while (simulator.isStartingOrRunning())
        {
            Sleep.sleep(1);
            if (System.currentTimeMillis() > millis + timeoutMs)
            {
                throw new AssertionError("timeout of the simulator");
            }
        }
    }

    /**
     * @param simulator the simulator
     * @return DSOLModel<Double>
     */
    private DSOLModel<Double, DEVSSimulatorInterface<Double>> makeModelDouble(
            final DEVSSimulatorInterface<Double> simulator)
    {
        return new AbstractDSOLModel<Double, DEVSSimulatorInterface<Double>>(simulator)
        {
            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                //
            }
        };
    }
}
