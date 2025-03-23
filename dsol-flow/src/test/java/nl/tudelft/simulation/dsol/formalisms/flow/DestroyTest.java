package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DestroyTest tests the Destroy flow block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DestroyTest extends FlowTest
{
    /** */
    private DestroyTest()
    {
        // do not instantiate
    }

    /**
     * Test the construction of the Destroy flow block.
     */
    @Test
    public void testDestroyMethods()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var d1 = new Destroy<Double>("d1", this.simulator);
                assertEquals("d1", d1.getId());
                assertEquals(this.simulator, d1.getSimulator());
                assertNull(d1.getDestination());
                assertNull(d1.getCountReceivedStatistic());
                assertNull(d1.getCountReleasedStatistic());
                assertNull(d1.getTimeInSystemStatistic());
                assertEquals(0, d1.getNumberEntitiesInMinusOut());
                assertFalse(d1.hasDefaultStatistics());
                assertNotNull(d1.toString());

                // turn on statistics
                d1.setDefaultStatistics();
                assertNotNull(d1.getCountReceivedStatistic());
                assertNotNull(d1.getCountReleasedStatistic());
                assertNotNull(d1.getTimeInSystemStatistic());
                assertEquals(0, d1.getCountReceivedStatistic().getCount());
                assertEquals(0, d1.getCountReleasedStatistic().getCount());
                assertEquals(Double.NaN, d1.getTimeInSystemStatistic().getPopulationMean());
                assertTrue(d1.hasDefaultStatistics());
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test errors in the Destroy flow block.
     */
    @Test
    public void testDestroyErrors()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                Try.testFail(() -> new Destroy<Double>(null, this.simulator), NullPointerException.class);
                Try.testFail(() -> new Destroy<Double>("d", null), NullPointerException.class);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test destroying 100 entities, 50% with delay time 4 and 50 with delay time 8.
     */
    @Test
    public void testDestroy100Entities()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Destroy<Double>[] destroyBlock = new Destroy[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                StreamInterface stream = new MersenneTwister(12L);
                var delay = new Delay<Double>("d3", this.simulator);
                delay.executeFunction(() ->
                {
                    delay.setAttribute("count", 0);
                });
                delay.setDelayFunction((entity) ->
                {
                    if (delay.getNumberAttribute("count").intValue() % 2 == 0)
                        return 4.0;
                    return 8.0;
                });
                delay.setReceiveFunction((entity) ->
                {
                    delay.setAttribute("count", delay.getNumberAttribute("count").intValue() + 1);
                });

                var generator = new Create<Double>("c3", this.simulator);
                generator.setBatchSize(1);
                generator.setDestination(delay);
                generator.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 1)));
                generator.setMaxNumberGeneratedEntities(100);
                generator.setStartTime(0.0);
                generator.setEntitySupplier(() -> new Entity<>("e", this.simulator));

                var destroy = new Destroy<Double>("destroy", this.simulator);
                destroy.setDefaultStatistics();
                delay.setDestination(destroy);
                destroyBlock[0] = destroy;
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 200.0));
        simulator.start();
        waitForCompletion(simulator, 1000);

        assertEquals(100, destroyBlock[0].getCountReceivedStatistic().getCount());
        assertEquals(0, destroyBlock[0].getCountReleasedStatistic().getCount());
        assertEquals(6.0, destroyBlock[0].getTimeInSystemStatistic().getPopulationMean(), 1E-6);

        cleanUp(simulator);
    }

}
