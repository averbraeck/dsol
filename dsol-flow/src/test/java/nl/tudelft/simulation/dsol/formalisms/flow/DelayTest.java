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
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DelayTest tests the Delay flow block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DelayTest extends FlowTest
{
    /** */
    private DelayTest()
    {
        // do not instantiate
    }

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
        Entity<Double> object = new Entity<Double>("abc", simulator);
        delay.receiveEntity(object);
        assertEquals(nrEvents + 1, simulator.getEventList().size());
        simulator.runUpTo(5.0);
        wait(simulator, 500);
        assertEquals(nrEvents, simulator.getEventList().size());
        cleanUp(simulator);
    }

    /**
     * Test the construction of the Delay flow block.
     */
    @Test
    public void testDelayMethods()
    {
        var simulator = new DevsSimulator<Double>("sim");        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        @SuppressWarnings("unchecked")
        final Delay<Double>[] delayBlock = new Delay[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var d1 = new Delay<Double>("d1", this.simulator);
                delayBlock[0] = d1;
                assertEquals("d1", d1.getId());
                assertEquals(this.simulator, d1.getSimulator());
                assertNull(d1.getDelayDistribution());
                assertNull(d1.getDestination());
                assertNull(d1.getCountReceivedStatistic());
                assertNull(d1.getCountReleasedStatistic());
                assertNull(d1.getNumberDelayedStatistic());
                assertNotNull(d1.getDelayedEntityList());
                assertEquals(0, d1.getDelayedEntityList().size());
                assertEquals(0, d1.getNumberEntitiesInMinusOut());
                assertFalse(d1.hasDefaultStatistics());
                assertNotNull(d1.toString());

                // set delay distribution
                StreamInterface stream = new MersenneTwister(12L);
                var delayDist = new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 0.9));
                d1.setDelayDistribution(delayDist);
                assertEquals(delayDist, d1.getDelayDistribution());

                // turn on statistics
                d1.setDefaultStatistics();
                assertNotNull(d1.getCountReceivedStatistic());
                assertNotNull(d1.getCountReleasedStatistic());
                assertNotNull(d1.getNumberDelayedStatistic());
                assertEquals(0, d1.getCountReceivedStatistic().getCount());
                assertEquals(0, d1.getCountReleasedStatistic().getCount());
                assertEquals(0, d1.getNumberDelayedStatistic().getN());
                assertTrue(d1.hasDefaultStatistics());
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test errors in the delay flow block.
     */
    @Test
    public void testDelayErrors()
    {
        var simulator = new DevsSimulator<Double>("sim");        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                Try.testFail(() -> new Delay<Double>(null, this.simulator), NullPointerException.class);
                Try.testFail(() -> new Delay<Double>("d", null), NullPointerException.class);

                var d2 = new Delay<Double>("d2", this.simulator);

                // delay block does not have a delay distribution
                Try.testFail(() -> d2.receiveEntity(new Entity<Double>("e", this.simulator)), NullPointerException.class);

                // delay distribution should not be null
                Try.testFail(() -> d2.setDelayDistribution(null), NullPointerException.class);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test delay of 1 entity.
     */
    @Test
    public void testDelay1Entity()
    {
        var simulator = new DevsSimulator<Double>("sim");        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        @SuppressWarnings("unchecked")
        final Delay<Double>[] delayBlock = new Delay[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                StreamInterface stream = new MersenneTwister(12L);
                var delay = new Delay<Double>("d3", this.simulator);
                delayBlock[0] = delay;
                delay.setDelayDistribution(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 20.0)));
                delay.setDefaultStatistics();

                var generator = new Create<Double>("c3", this.simulator);
                generator.setBatchSize(1);
                generator.setDestination(delay);
                generator.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 10.0)));
                generator.setStartTime(5.0);
                generator.setEntitySupplier(() -> new Entity<>("e", this.simulator));

                this.simulator.scheduleEventAbs(2.0, () ->
                {
                    assertEquals(0, delay.getNumberDelayedEntities());
                });
                this.simulator.scheduleEventAbs(6.0, () ->
                {
                    assertEquals(1, delay.getNumberDelayedEntities());
                    assertEquals(5.0, delay.getDelayedEntityList().get(0).getCreationTime(), 1E-6);
                });
                this.simulator.scheduleEventAbs(16.0, () ->
                {
                    assertEquals(2, delay.getNumberDelayedEntities());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        waitForCompletion(simulator, 1000);
        cleanUp(simulator);
    }

    /**
     * Test delay of 100 entities.
     */
    @Test
    public void testDelay100Entities()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        @SuppressWarnings("unchecked")
        final Delay<Double>[] delayBlock = new Delay[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                StreamInterface stream = new MersenneTwister(12L);
                var delay = new Delay<Double>("d3", this.simulator);
                delayBlock[0] = delay;
                delay.setDelayDistribution(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 5.0)));
                delay.setDefaultStatistics();

                var generator = new Create<Double>("c3", this.simulator);
                generator.setBatchSize(10);
                generator.setDestination(delay);
                generator.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 10.00001)));
                generator.setStartTime(0.0);
                generator.setEntitySupplier(() -> new Entity<>("e", this.simulator));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        waitForCompletion(simulator, 1000);

        assertEquals(100, delayBlock[0].getCountReceivedStatistic().getCount());
        assertEquals(100, delayBlock[0].getCountReleasedStatistic().getCount());
        assertEquals(10.0, delayBlock[0].getNumberDelayedStatistic().getMax(), 1E-6);
        assertEquals(0.0, delayBlock[0].getNumberDelayedStatistic().getMin(), 1E-6);
        assertEquals(5.0, delayBlock[0].getNumberDelayedStatistic().getWeightedPopulationMean(), 1E-6);

        cleanUp(simulator);
    }

}
