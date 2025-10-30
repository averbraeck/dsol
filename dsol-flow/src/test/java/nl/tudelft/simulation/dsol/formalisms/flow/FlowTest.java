package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.fail;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
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
     * @return DsolModel&lt;Double&gt;
     */
    public DsolModel<Double, DevsSimulatorInterface<Double>> makeModelDouble(final DevsSimulatorInterface<Double> simulator)
    {
        return new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
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
     * Wait for the simulator to stop in a number of milliseconds.
     * @param simulator the simulator
     * @param maxMs the maximum number of milliseconds to run
     */
    public void waitForCompletion(final DevsSimulatorInterface<?> simulator, final long maxMs)
    {
        long count = 0;
        while (simulator.isStartingOrRunning() && count < maxMs)
        {
            sleep(10);
            count += 10;
        }
        if (count >= maxMs)
        {
            fail("simulation run took longer than maxMs milliseconds");
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
        catch (Exception e)
        {
            fail(e);
        }
    }
}
