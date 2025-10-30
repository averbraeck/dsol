package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;

/**
 * ReleaseTest tests the Release flow block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ReleaseTest extends FlowTest
{

    /**
     * Test the general aspects of the Release.DoubleCapacity block.
     */
    @Test
    public void testReleaseDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 2.0);
                var release = new Release.DoubleCapacity<Double>("release", this.simulator, resource);
                assertEquals("release", release.getId());
                assertEquals(this.simulator, release.getSimulator());
                assertEquals(resource, release.getResource());
                assertTrue(release.getBlockNumber() > 0);
                assertEquals(1.0, release.getFixedCapacityRelease());

                assertFalse(release.hasDefaultStatistics());
                assertNull(release.getCountReceivedStatistic());
                assertNull(release.getCountReleasedStatistic());
                assertNull(release.getResource().getUtilizationStatistic());
                assertNull(release.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNull(release.getResource().getRequestQueue().getTimeInQueueStatistic());

                release.setDefaultStatistics();
                assertTrue(release.hasDefaultStatistics());
                assertNotNull(release.getCountReceivedStatistic());
                assertNotNull(release.getCountReleasedStatistic());
                assertNotNull(release.getResource().getUtilizationStatistic());
                assertNotNull(release.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNotNull(release.getResource().getRequestQueue().getTimeInQueueStatistic());

                var rru = release.getResource().getUtilizationStatistic();
                release.setDefaultStatistics();
                assertEquals(rru, release.getResource().getUtilizationStatistic(), "Do not initialize statistics twice");

                release.setFixedCapacityRelease(4.0);
                assertEquals(4.0, release.getFixedCapacityRelease());
                release.setFlexibleCapacityRelease((entity) -> 4.0);
                assertTrue(Double.isNaN(release.getFixedCapacityRelease()));
                release.setFixedCapacityRelease(2.0);
                assertEquals(2.0, release.getFixedCapacityRelease());
                UnitTest.testFail(() -> release.setFixedCapacityRelease(-1.0));
                UnitTest.testFail(() -> release.setFlexibleCapacityRelease(null));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test the general aspects of the Release.IntegerCapacity block.
     */
    @Test
    public void testReleaseInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 2);
                var release = new Release.IntegerCapacity<Double>("release", this.simulator, resource);
                assertEquals("release", release.getId());
                assertEquals(this.simulator, release.getSimulator());
                assertEquals(resource, release.getResource());
                assertEquals(1, release.getFixedCapacityRelease());

                assertFalse(release.hasDefaultStatistics());
                assertNull(release.getCountReceivedStatistic());
                assertNull(release.getCountReleasedStatistic());
                assertNull(release.getResource().getUtilizationStatistic());
                assertNull(release.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNull(release.getResource().getRequestQueue().getTimeInQueueStatistic());

                release.setDefaultStatistics();
                assertTrue(release.hasDefaultStatistics());
                assertNotNull(release.getCountReceivedStatistic());
                assertNotNull(release.getCountReleasedStatistic());
                assertNotNull(release.getResource().getUtilizationStatistic());
                assertNotNull(release.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNotNull(release.getResource().getRequestQueue().getTimeInQueueStatistic());

                release.setFixedCapacityRelease(4);
                assertEquals(4, release.getFixedCapacityRelease());
                release.setFlexibleCapacityRelease((entity) -> 4);
                assertEquals(-1, release.getFixedCapacityRelease());
                release.setFixedCapacityRelease(2);
                assertEquals(2, release.getFixedCapacityRelease());
                UnitTest.testFail(() -> release.setFixedCapacityRelease(-1));
                UnitTest.testFail(() -> release.setFlexibleCapacityRelease(null));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test the receive part of the Release.DoubleCapacity block.
     */
    @Test
    public void testReleaseReceiveDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 2.0);
                var seize = new Seize.DoubleCapacity<Double>("seize", this.simulator, resource);
                seize.setFixedCapacityClaim(1.0);
                seize.setDefaultStatistics();
                var delay = new Delay<Double>("delay", this.simulator);
                delay.setDelayFunction((entity) -> 2.0);
                delay.setDefaultStatistics();
                seize.setDestination(delay);
                var release = new Release.DoubleCapacity<Double>("release", this.simulator, resource);
                release.setFixedCapacityRelease(1.0);
                release.setDefaultStatistics();
                delay.setDestination(release);

                var e1 = new Entity<Double>("e1", this.simulator);
                var e2 = new Entity<Double>("e2", this.simulator);
                var e3 = new Entity<Double>("e3", this.simulator);
                var e4 = new Entity<Double>("e4", this.simulator);

                this.simulator.scheduleEventRel(0.0, () ->
                {
                    seize.receiveEntity(e1);
                    seize.receiveEntity(e2);
                    seize.receiveEntity(e3);
                    seize.receiveEntity(e4);
                });

                this.simulator.scheduleEventRel(1.0, () ->
                {
                    assertEquals(2, seize.getStorage().size());
                    assertEquals(2, delay.getDelayedEntityList().size());
                    assertEquals(2.0, resource.getClaimedCapacity());
                    assertEquals(0.0, resource.getAvailableCapacity());
                    assertEquals(2, resource.getRequestQueue().getNumberRequestsInQueue());
                    assertEquals(0, release.getCountReceivedStatistic().getCount());
                    assertEquals(0, release.getCountReleasedStatistic().getCount());
                });

                this.simulator.scheduleEventRel(3.0, () ->
                {
                    assertEquals(2, release.getCountReceivedStatistic().getCount());
                    assertEquals(2, release.getCountReleasedStatistic().getCount());
                });

                this.simulator.scheduleEventRel(5.0, () ->
                {
                    assertEquals(4, release.getCountReceivedStatistic().getCount());
                    assertEquals(4, release.getCountReleasedStatistic().getCount());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

    /**
     * Test the receive part of the Seize.IntegerCapacity block.
     */
    @Test
    public void testReleaseReceiveInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 2);
                var seize = new Seize.IntegerCapacity<Double>("seize", this.simulator, resource);
                seize.setFixedCapacityClaim(1);
                seize.setDefaultStatistics();
                var delay = new Delay<Double>("delay", this.simulator);
                delay.setDelayFunction((entity) -> 2.0);
                delay.setDefaultStatistics();
                seize.setDestination(delay);
                var release = new Release.IntegerCapacity<Double>("release", this.simulator, resource);
                release.setFixedCapacityRelease(1);
                release.setDefaultStatistics();
                delay.setDestination(release);

                var e1 = new Entity<Double>("e1", this.simulator);
                var e2 = new Entity<Double>("e2", this.simulator);
                var e3 = new Entity<Double>("e3", this.simulator);
                var e4 = new Entity<Double>("e4", this.simulator);

                this.simulator.scheduleEventRel(0.0, () ->
                {
                    seize.receiveEntity(e1);
                    seize.receiveEntity(e2);
                    seize.receiveEntity(e3);
                    seize.receiveEntity(e4);
                });

                this.simulator.scheduleEventRel(1.0, () ->
                {
                    assertEquals(2, seize.getStorage().size());
                    assertEquals(2, delay.getDelayedEntityList().size());
                    assertEquals(2, resource.getClaimedCapacity());
                    assertEquals(0, resource.getAvailableCapacity());
                    assertEquals(2, resource.getRequestQueue().getNumberRequestsInQueue());
                    assertEquals(0, release.getCountReceivedStatistic().getCount());
                    assertEquals(0, release.getCountReleasedStatistic().getCount());
                });

                this.simulator.scheduleEventRel(3.0, () ->
                {
                    assertEquals(2, release.getCountReceivedStatistic().getCount());
                    assertEquals(2, release.getCountReleasedStatistic().getCount());
                });

                this.simulator.scheduleEventRel(5.0, () ->
                {
                    assertEquals(4, release.getCountReceivedStatistic().getCount());
                    assertEquals(4, release.getCountReleasedStatistic().getCount());
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

}
