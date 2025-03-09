package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * CreateTest tests the Create flow block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class CreateTest
{
    /** */
    private CreateTest()
    {
        // do not instantiate
    }

    /**
     * Test the construction of the create flow block.
     */
    @Test
    public void testCreateMethods()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c1 = new Create<Double>("c1", this.simulator);
                createBlock[0] = c1;
                assertEquals("c1", c1.getId());
                assertEquals(this.simulator, c1.getSimulator());
                assertNotNull(c1.getBatchSizeDist());
                assertNull(c1.getCountStatistic());
                assertNull(c1.getDestination());
                assertNull(c1.getEndTime());
                assertNull(c1.getIntervalDist());
                assertNull(c1.getNextCreateEvent());
                assertNull(c1.getStartTimeDist());
                assertEquals(Long.MAX_VALUE, c1.getMaxNumberCreationEvents());
                assertEquals(Long.MAX_VALUE, c1.getMaxNumberGeneratedEntities());
                assertEquals(0L, c1.getNumberCreationEvents());
                assertEquals(0L, c1.getNumberGeneratedEntities());
                assertFalse(c1.hasStatistics());

                // set start time dist should create an event
                StreamInterface stream = new MersenneTwister(12L);
                int nrEvents = this.simulator.getEventList().size();
                var startTimeDist = new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 2.0));
                c1.setStartTimeDist(startTimeDist);
                assertEquals(startTimeDist, c1.getStartTimeDist());
                assertNotNull(c1.getNextCreateEvent());
                assertEquals(nrEvents + 1, this.simulator.getEventList().size());

                // applying set start time dist again should replace the existing event
                startTimeDist = new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 5.0));
                c1.setStartTimeDist(startTimeDist);
                assertEquals(startTimeDist, c1.getStartTimeDist());
                assertNotNull(c1.getNextCreateEvent());
                assertEquals(nrEvents + 1, this.simulator.getEventList().size());

                var intervalDist = new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 2.5));
                c1.setIntervalDist(intervalDist);
                assertEquals(intervalDist, c1.getIntervalDist());
                assertEquals(nrEvents + 1, this.simulator.getEventList().size()); // no extra event

                // at t=0, the event is the startTimeDist event that should NOT be replaced
                var oldNextCreateEvent = c1.getNextCreateEvent();
                intervalDist = new DistContinuousSimulationTime.TimeDouble(new DistExponential(stream, 3.0));
                c1.setIntervalDist(intervalDist);
                assertEquals(intervalDist, c1.getIntervalDist());
                assertEquals(nrEvents + 1, this.simulator.getEventList().size());
                assertEquals(oldNextCreateEvent, c1.getNextCreateEvent());

                var batchSizeDist = new DistDiscreteConstant(stream, 2);
                c1.setBatchSizeDist(batchSizeDist);
                assertEquals(batchSizeDist, c1.getBatchSizeDist());
                
                c1.setBatchSize(2);
                assertNotEquals(batchSizeDist, c1.getBatchSizeDist());

                c1.setEndTime(90.0);
                assertEquals(90.0, c1.getEndTime());

                c1.setMaxNumberCreationEvents(100);
                assertEquals(100, c1.getMaxNumberCreationEvents());

                c1.setMaxNumberGeneratedEntities(1000);
                assertEquals(1000, c1.getMaxNumberGeneratedEntities());

                int nrListeners = c1.getEventListenerMap().size();
                c1.setStatistics();
                assertTrue(c1.hasStatistics());
                assertNotNull(c1.getCountStatistic());
                assertEquals(nrListeners + 1, c1.getEventListenerMap().size());

                c1.setEntitySupplier(() -> new Entity<Double>("entity", this.simulator.getSimulatorTime()));

                c1.setDestination(new FlowObject<Double>("receiver", this.simulator)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void receiveEntity(final Entity<Double> entity)
                    {
                        assertEquals("entity", entity.getId());
                        assertEquals(getSimulator().getSimulatorTime(), entity.getCreationTime());
                        super.receiveEntity(entity);
                    }
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        double simTime = simulator.getSimulatorTime();
        simulator.step(); // warmup
        simulator.step(); // generate 2 entities (batch size = 2)
        assertTrue(simulator.getSimulatorTime() > simTime);
        assertEquals(1, createBlock[0].getNumberCreationEvents());
        assertEquals(2, createBlock[0].getNumberGeneratedEntities());
        assertEquals(2, createBlock[0].getCountStatistic().getCount());
        simTime = simulator.getSimulatorTime();
        simulator.step(); // generate 2 entities (batch size = 2)
        assertTrue(simulator.getSimulatorTime() > simTime);
        assertEquals(2, createBlock[0].getNumberCreationEvents());
        assertEquals(4, createBlock[0].getNumberGeneratedEntities());
        assertEquals(4, createBlock[0].getCountStatistic().getCount());

        // startTime distribution cannot be changed after entities have been created
        Try.testFail(
                () -> createBlock[0].setStartTimeDist(
                        new DistContinuousSimulationTime.TimeDouble(new DistExponential(model.getDefaultStream(), 2.0))),
                IllegalStateException.class);
        simulator.cleanUp();
    }

    /**
     * Test errors in the create flow block.
     */
    @Test
    public void testCreateErrors()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c2 = new Create<Double>("c2", this.simulator);

                // generator does not have an interval distribution, nor an entity supplier
                Try.testFail(() -> c2.generate(), NullPointerException.class);

                // batch size distribution should not be null
                Try.testFail(() -> c2.setBatchSizeDist(null), NullPointerException.class);

                // batch size should be >= 0
                Try.testFail(() -> c2.setBatchSize(-1), IllegalArgumentException.class);

                // generator does not have a generator
                c2.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(
                        new DistExponential(getSimulator().getModel().getDefaultStream(), 2.5)));
                Try.testFail(() -> c2.generate(), NullPointerException.class);

                // Create block should not receive any entities
                Try.testFail(() -> c2.receiveEntity(new Entity<Double>("e", 1.0)), SimRuntimeException.class);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.cleanUp();
    }

    /**
     * Test maximum number of create events.
     */
    @Test
    public void testMaxNumberCreateEvents()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c3 = new Create<Double>("c3", this.simulator);
                createBlock[0] = c3;
                var stream = this.simulator.getModel().getDefaultStream();
                c3.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c3.setMaxNumberCreationEvents(10);
                c3.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 5.0)));
                c3.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        assertEquals(10, createBlock[0].getNumberCreationEvents());
        assertEquals(20, counter.get());
        assertEquals(20, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test maximum number of created entities.
     */
    @Test
    public void testMaxNumberCreatedEntities()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c4 = new Create<Double>("c4", this.simulator);
                createBlock[0] = c4;
                var stream = this.simulator.getModel().getDefaultStream();
                c4.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c4.setMaxNumberGeneratedEntities(15);
                c4.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 5.0)));
                c4.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        assertEquals(15, counter.get());
        assertEquals(15, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time.
     */
    @Test
    public void testEndTimeExclusive()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c5 = new Create<Double>("c5", this.simulator);
                createBlock[0] = c5;
                var stream = this.simulator.getModel().getDefaultStream();
                c5.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c5.setEndTime(33.0);
                c5.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c5.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // Generation from t=0 till t=33 with step 4 = 9 events
        assertEquals(9, createBlock[0].getNumberCreationEvents());
        assertEquals(18, counter.get());
        assertEquals(18, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time.
     */
    @Test
    public void testEndTimeExclusiveWithStartup()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c6 = new Create<Double>("c6", this.simulator);
                createBlock[0] = c6;
                var stream = this.simulator.getModel().getDefaultStream();
                c6.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c6.setStartTimeDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c6.setEndTime(33.0);
                c6.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c6.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // Generation from t=4 till t=33 with step 4 = 8 events
        assertEquals(8, createBlock[0].getNumberCreationEvents());
        assertEquals(16, counter.get());
        assertEquals(16, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time, with an exact match on the end time (should be executed).
     */
    @Test
    public void testEndTimeInclusive()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c7 = new Create<Double>("c7", this.simulator);
                createBlock[0] = c7;
                var stream = this.simulator.getModel().getDefaultStream();
                c7.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c7.setEndTime(32.0);
                c7.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c7.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // Generation from t=0 till t=32 with step 4 = 9 events
        assertEquals(9, createBlock[0].getNumberCreationEvents());
        assertEquals(18, counter.get());
        assertEquals(18, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time, with an interval change.
     */
    @Test
    public void testIntervalChangeAfterEndTime()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c8 = new Create<Double>("c8", this.simulator);
                createBlock[0] = c8;
                var stream = this.simulator.getModel().getDefaultStream();
                c8.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c8.setEndTime(32.0);
                c8.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c8.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.scheduleEventAbs(17.0, () -> simulator.stop());
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        createBlock[0].setEndTime(15.0);
        // t = 17, now beyond end time -- setIntervalDist should still have a nextCreateEvent
        assertNotNull(createBlock[0].getNextCreateEvent());
        createBlock[0]
                .setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(model.getDefaultStream(), 2.0)));
        assertNotNull(createBlock[0].getNextCreateEvent());
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // but not generate any extra entities. Expected number = {0, 4, 8, 12, 16} = 5
        assertEquals(5, createBlock[0].getNumberCreationEvents());
        assertEquals(10, counter.get());
        assertEquals(10, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time, with an interval change.
     */
    @Test
    public void testIntervalChange()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c9 = new Create<Double>("c9", this.simulator);
                createBlock[0] = c9;
                var stream = this.simulator.getModel().getDefaultStream();
                c9.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c9.setEndTime(33.0);
                c9.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 4.0)));
                c9.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.scheduleEventAbs(17.0, () -> createBlock[0]
                .setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(model.getDefaultStream(), 6.0))));
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // Generation from t=0 till t=17 with step 4 [0, 4, 8, 12, 16] = 5 events.
        // From t=17 to t=33 with step 6 [23, 29] = 2 events. Total = 7 events.
        assertEquals(7, createBlock[0].getNumberCreationEvents());
        assertEquals(14, counter.get());
        assertEquals(14, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with end time, with an interval change that goes from a large interarrival time to a smaller interarrival time. This
     * tests whether the cancellation of the big gap is done properly.
     */
    @Test
    public void testIntervalChangeDecrease()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c10 = new Create<Double>("c10", this.simulator);
                createBlock[0] = c10;
                var stream = this.simulator.getModel().getDefaultStream();
                c10.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c10.setEndTime(49.0);
                c10.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 90.0)));
                c10.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.scheduleEventAbs(20.0, () ->
        {
            createBlock[0].setIntervalDist(
                    new DistContinuousSimulationTime.TimeDouble(new DistConstant(model.getDefaultStream(), 5.0)));
        });
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        // Generation from t=0 till t=20 with step 4 = 1 event at t = 0.
        // From t=20 to t=49 with step 5 [25, 30, 35, 40, 45] = 5 events. Total = 6 events.
        assertEquals(6, createBlock[0].getNumberCreationEvents());
        assertEquals(12, counter.get());
        assertEquals(12, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test with an end time that shifts forward, to see if premature generation cancellation does not happen. We start with an
     * end time at t=19 and generation every 5 time units. at t=18, we shift the end time forward till t=49. In total, 10x2
     * entities should be generated. See https://github.com/averbraeck/dsol/issues/51.
     */
    @Test
    public void testShiftEndTimeForward()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c11 = new Create<Double>("c11", this.simulator);
                createBlock[0] = c11;
                var stream = this.simulator.getModel().getDefaultStream();
                c11.setBatchSizeDist(new DistDiscreteConstant(stream, 2));
                c11.setEndTime(19.0);
                c11.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 5.0)));
                c11.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.scheduleEventAbs(18.0, () ->
        { createBlock[0].setEndTime(49.0); });
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        assertEquals(10, createBlock[0].getNumberCreationEvents());
        assertEquals(20, counter.get());
        assertEquals(20, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Test that generation after the end time is not resumed when a new IntervalDist is used.
     */
    @Test
    public void testChangeIntervalAfterEndTime()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Create<Double>[] createBlock = new Create[1];
        final AtomicInteger counter = new AtomicInteger(0);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var c12 = new Create<Double>("c12", this.simulator);
                createBlock[0] = c12;
                var stream = this.simulator.getModel().getDefaultStream();
                c12.setBatchSize(2);
                c12.setEndTime(49.0);
                c12.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 5.0)));
                c12.setEntitySupplier(() ->
                {
                    counter.incrementAndGet();
                    return new Entity<Double>("entity", this.simulator.getSimulatorTime());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.scheduleEventAbs(75.0, () ->
        {
            createBlock[0].setIntervalDist(
                    new DistContinuousSimulationTime.TimeDouble(new DistConstant(model.getDefaultStream(), 5.0)));
        });
        simulator.start();
        while (simulator.isStartingOrRunning())
        {
            sleep(10);
        }
        assertEquals(10, createBlock[0].getNumberCreationEvents());
        assertEquals(20, counter.get());
        assertEquals(20, createBlock[0].getNumberGeneratedEntities());
        simulator.cleanUp();
    }

    /**
     * Sleep for a certain amount of ms.
     * @param ms the time to sleep in ms
     */
    protected void sleep(final int ms)
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
}
