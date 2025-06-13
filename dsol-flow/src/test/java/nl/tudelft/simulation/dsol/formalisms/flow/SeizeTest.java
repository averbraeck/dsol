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
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;

/**
 * SeizeTest.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SeizeTest extends FlowTest
{

    /**
     * Test the general aspects of the Seize.DoubleCapacity block.
     */
    @Test
    public void testSeizeDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 2.0);
                var seize = new Seize.DoubleCapacity<Double>("seize", this.simulator, resource);
                assertEquals("seize", seize.getId());
                assertEquals(this.simulator, seize.getSimulator());
                assertEquals(resource, seize.getResource());
                assertNotNull(seize.getStorage());
                assertEquals(0, seize.getStorage().size());
                assertEquals(1.0, seize.getFixedCapacityClaim());

                assertFalse(seize.hasDefaultStatistics());
                assertNull(seize.getNumberStoredStatistic());
                assertNull(seize.getStorageTimeStatistic());
                assertNull(seize.getCountReceivedStatistic());
                assertNull(seize.getCountReleasedStatistic());
                assertNull(seize.getResource().getUtilizationStatistic());
                assertNull(seize.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNull(seize.getResource().getRequestQueue().getTimeInQueueStatistic());

                seize.setDefaultStatistics();
                assertTrue(seize.hasDefaultStatistics());
                assertNotNull(seize.getNumberStoredStatistic());
                assertNotNull(seize.getStorageTimeStatistic());
                assertNotNull(seize.getCountReceivedStatistic());
                assertNotNull(seize.getCountReleasedStatistic());
                assertNotNull(seize.getResource().getUtilizationStatistic());
                assertNotNull(seize.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNotNull(seize.getResource().getRequestQueue().getTimeInQueueStatistic());

                var nss = seize.getNumberStoredStatistic();
                seize.setDefaultStatistics();
                assertEquals(nss, seize.getNumberStoredStatistic(), "Do not initialize statistics twice");

                seize.setFixedCapacityClaim(4.0);
                assertEquals(4.0, seize.getFixedCapacityClaim());
                seize.setFlexibleCapacityClaim((entity) -> 4.0);
                assertTrue(Double.isNaN(seize.getFixedCapacityClaim()));
                seize.setFixedCapacityClaim(2.0);
                assertEquals(2.0, seize.getFixedCapacityClaim());
                Try.testFail(() -> seize.setFixedCapacityClaim(-1.0));
                Try.testFail(() -> seize.setFlexibleCapacityClaim(null));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test the general aspects of the Seize.IntegerCapacity block.
     */
    @Test
    public void testSeizeInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 2);
                var seize = new Seize.IntegerCapacity<Double>("seize", this.simulator, resource);
                assertEquals("seize", seize.getId());
                assertEquals(this.simulator, seize.getSimulator());
                assertEquals(resource, seize.getResource());
                assertNotNull(seize.getStorage());
                assertEquals(0, seize.getStorage().size());
                assertEquals(1, seize.getFixedCapacityClaim());

                assertFalse(seize.hasDefaultStatistics());
                assertNull(seize.getNumberStoredStatistic());
                assertNull(seize.getStorageTimeStatistic());
                assertNull(seize.getCountReceivedStatistic());
                assertNull(seize.getCountReleasedStatistic());
                assertNull(seize.getResource().getUtilizationStatistic());
                assertNull(seize.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNull(seize.getResource().getRequestQueue().getTimeInQueueStatistic());

                seize.setDefaultStatistics();
                assertTrue(seize.hasDefaultStatistics());
                assertNotNull(seize.getNumberStoredStatistic());
                assertNotNull(seize.getStorageTimeStatistic());
                assertNotNull(seize.getCountReceivedStatistic());
                assertNotNull(seize.getCountReleasedStatistic());
                assertNotNull(seize.getResource().getUtilizationStatistic());
                assertNotNull(seize.getResource().getRequestQueue().getQueueLengthStatistic());
                assertNotNull(seize.getResource().getRequestQueue().getTimeInQueueStatistic());

                seize.setFixedCapacityClaim(4);
                assertEquals(4, seize.getFixedCapacityClaim());
                seize.setFlexibleCapacityClaim((entity) -> 4);
                assertEquals(-1, seize.getFixedCapacityClaim());
                seize.setFixedCapacityClaim(2);
                assertEquals(2, seize.getFixedCapacityClaim());
                Try.testFail(() -> seize.setFixedCapacityClaim(-1));
                Try.testFail(() -> seize.setFlexibleCapacityClaim(null));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test the receive part of the Seize.DoubleCapacity block.
     */
    @Test
    public void testSeizeReceiveDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

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
    public void testSeizeReceiveInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

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
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

}
