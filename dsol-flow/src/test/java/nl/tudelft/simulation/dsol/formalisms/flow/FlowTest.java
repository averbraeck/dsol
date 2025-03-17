package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * FlowTest tests the flow objects, such as FlowObject, Seize, Delay, Release.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class FlowTest
{
    /**
     * Test the Delay.
     */
    @Test
    public void delayTest()
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = makeModelDouble(simulator);
        SingleReplication<Double> replication = new SingleReplication<Double>("replication", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.runUpTo(1.0);
        wait(simulator, 500);
        int nrEvents = simulator.getEventList().size();
        StreamInterface stream = new MersenneTwister(10L);
        DistContinuousSimulationTime<Double> delayDistribution =
                new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 10.0));
        Delay<Double> delay = new Delay<Double>("delay", simulator).setDelayDistribution(delayDistribution);
        assertEquals(simulator, delay.getSimulator());
        assertEquals(nrEvents, simulator.getEventList().size());

        Destroy<Double> departure = new Destroy<Double>("departure", simulator);
        delay.setDestination(departure);
        assertEquals(departure, delay.getDestination());
        Entity<Double> object = new Entity<Double>("abc", 0.0);
        delay.receiveEntity(object);
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
    public void wait(final SimulatorInterface<?> simulator, final long timeoutMs)
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
     * @return DsolModel<Double>
     */
    public DsolModel<Double, DevsSimulatorInterface<Double>> makeModelDouble(
            final DevsSimulatorInterface<Double> simulator)
    {
        return new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
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
    
    /**
     * Sleep for a certain amount of ms.
     * @param ms the time to sleep in ms
     */
    public void sleep(final int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (Exception exception)
        {
            // do nothing
        }
    }
    
    /**
     * Cleanup the simulator and context.
     * @param simulator the simulator
     */
    public void cleanUp(final DevsSimulatorInterface<?> simulator)
    {
        try
        {
            ContextInterface context = simulator.getReplication().getContext();
            simulator.cleanUp();
            context.close();
        }
        catch(Exception e)
        {
            fail(e);
        }
    }
}
