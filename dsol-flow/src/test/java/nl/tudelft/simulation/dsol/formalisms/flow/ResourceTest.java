package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.formalisms.flow.Resource.ReleaseType;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;

/**
 * ResourceTest.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ResourceTest extends FlowTest
{

    /** Test the double-capacity resource and queue. */
    @Test
    public void testResourceDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 1.0);
                var entity = new Entity<>("entity:1", this.simulator);
                assertEquals("resource", resource.getId());
                assertEquals(this.simulator, resource.getSimulator());
                assertEquals(1.0, resource.getCapacity());
                assertEquals(0.0, resource.getClaimedCapacity());
                assertEquals(1.0, resource.getAvailableCapacity());
                assertFalse(resource.hasDefaultStatistics());
                assertNull(resource.getUtilizationStatistic());
                resource.setCapacity(2.0);
                assertEquals(2.0, resource.getCapacity());
                assertEquals(0.0, resource.getClaimedCapacity());
                assertEquals(2.0, resource.getAvailableCapacity());
                assertEquals(ReleaseType.FIRST_ONLY, resource.getReleaseType());
                resource.setReleaseType(ReleaseType.ENTIRE_QUEUE);
                assertEquals(ReleaseType.ENTIRE_QUEUE, resource.getReleaseType());
                resource.setReleaseType(ReleaseType.FIRST_ONLY);
                Try.testFail(() -> resource.requestCapacity(entity, -1.0, null));
                Try.testFail(() -> resource.releaseCapacity(entity, -1.0));

                assertNotNull(resource.getRequestQueue());
                Queue<Double> queue = resource.getRequestQueue();
                assertEquals("resource_queue", queue.getId());
                assertEquals(this.simulator, queue.getSimulator());
                assertFalse(queue.hasDefaultStatistics());
                assertEquals(0, queue.getNumberRequestsInQueue());
                assertNull(queue.getQueueLengthStatistic());
                assertNull(queue.getTimeInQueueStatistic());
                Try.testFail(() -> queue.peek());
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the double-capacity resource and queue. */
    @Test
    public void testResourceDoubleRequestRelease()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 1.0);
                var entity = new Entity<>("entity:1", this.simulator);
                resource.setDefaultStatistics();
                assertTrue(resource.hasDefaultStatistics());
                assertNotNull(resource.getUtilizationStatistic());
                assertTrue(Double.isNaN(resource.getUtilizationStatistic().getWeightedPopulationMean()));
                Queue<Double> queue = resource.getRequestQueue();
                assertTrue(queue.hasDefaultStatistics());
                assertNotNull(queue.getQueueLengthStatistic());
                assertTrue(Double.isNaN(queue.getQueueLengthStatistic().getWeightedPopulationMean()));
                assertNotNull(queue.getTimeInQueueStatistic());
                assertTrue(Double.isNaN(queue.getTimeInQueueStatistic().getPopulationMean()));
                var requestor1 = new CapacityRequestor.DoubleCapacity<Double>()
                {
                    public double receivedCapacity = Double.NaN;

                    public double time = Double.NaN;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                            final Resource.DoubleCapacity<Double> resource)
                    {
                        this.receivedCapacity = requestedCapacity;
                        this.time = resource.getSimulator().getSimulatorTime();
                    }
                };
                var requestor2 = new CapacityRequestor.DoubleCapacity<Double>()
                {
                    public double receivedCapacity = Double.NaN;

                    public double time = Double.NaN;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                            final Resource.DoubleCapacity<Double> resource)
                    {
                        this.receivedCapacity = requestedCapacity;
                        this.time = resource.getSimulator().getSimulatorTime();
                    }
                };
                this.simulator.scheduleEventRel(0.0, () -> resource.requestCapacity(entity, 1.0, requestor1));
                this.simulator.scheduleEventRel(1.0, () ->
                {
                    assertEquals(1.0, resource.getCapacity());
                    assertEquals(0.0, resource.getAvailableCapacity());
                    assertEquals(1.0, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(0.0, requestor1.time);
                    assertEquals(1.0, requestor1.receivedCapacity);
                });
                this.simulator.scheduleEventRel(2.0, () ->
                {
                    resource.releaseCapacity(entity, 1.0);
                    assertEquals(1.0, resource.getCapacity());
                    assertEquals(1.0, resource.getAvailableCapacity());
                    assertEquals(0.0, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(1.0, resource.getUtilizationStatistic().getWeightedPopulationMean());
                });
                this.simulator.scheduleEventRel(4.0, () -> resource.requestCapacity(entity, 0.5, requestor1));
                this.simulator.scheduleEventRel(4.0, () -> resource.requestCapacity(entity, 1.0, requestor2));
                this.simulator.scheduleEventRel(6.0, () ->
                {
                    assertEquals(1.0, resource.getCapacity());
                    assertEquals(0.5, resource.getAvailableCapacity());
                    assertEquals(0.5, resource.getClaimedCapacity());
                    assertEquals(1, queue.getNumberRequestsInQueue());
                    assertNotNull(queue.peek());
                    assertEquals(4.0, requestor1.time);
                    assertEquals(0.5, requestor1.receivedCapacity);
                    assertTrue(Double.isNaN(requestor2.receivedCapacity));
                    assertTrue(Double.isNaN(requestor2.time));
                });
                this.simulator.scheduleEventRel(8.0, () ->
                {
                    resource.releaseCapacity(entity, 0.5);
                    assertEquals(1.0, resource.getCapacity());
                    assertEquals(0.0, resource.getAvailableCapacity());
                    assertEquals(1.0, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(8.0, requestor2.time);
                    assertEquals(1.0, requestor2.receivedCapacity);
                    assertEquals(4.0, queue.getTimeInQueueStatistic().getPopulationMean(), 1E-6);
                    assertEquals(0.5, queue.getQueueLengthStatistic().getWeightedPopulationMean(), 1E-6);
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

    /**
     * Test different queue sorting regimes. The following requests are created for the queue:
     * 
     * <pre>
     * Id Time Priority Amount
     * 0   0     1        1
     * 1   0     1        1
     * 2   0     2        1
     * 3   0     2        1
     * 4   1     1        1
     * 5   1     1        1
     * 6   1     2        1
     * 7   1     2        1
     * </pre>
     * 
     * The handling should be as follows:
     * 
     * <pre>
     * FCFS:  0 1 2 3 4 5 6 7
     * LCFS:  7 6 5 4 3 2 1 0
     * PFCFC: 2 3 6 7 0 1 4 5
     * PLCFS: 7 6 3 2 5 4 1 0
     * </pre>
     */
    @Test
    public void testQueueSorting()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public void constructModel() throws SimRuntimeException
            {
                var requestor = new CapacityRequestor.DoubleCapacity<Double>()
                {
                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                            final Resource.DoubleCapacity<Double> resource)
                    {
                        // do nothing
                    }
                };

                var fcfs = new Queue<Double>("fcfs", this.simulator).setComparator(new Queue.FcfsRequestComparator<>());
                var lcfs = new Queue<Double>("lcfs", this.simulator).setComparator(new Queue.LcfsRequestComparator<>());
                var fcfsp =
                        new Queue<Double>("fcfsp", this.simulator).setComparator(new Queue.FcfsPriorityRequestComparator<>());
                var lcfsp =
                        new Queue<Double>("lcfsp", this.simulator).setComparator(new Queue.LcfsPriorityRequestComparator<>());
                var qa = new Queue[] {fcfs, lcfs, fcfsp, lcfsp};

                for (double t : new double[] {0.0, 1.0})
                {
                    for (Queue<Double> queue : qa)
                    {
                        this.simulator.scheduleEventRel(t, () ->
                        {
                            queue.add(null, 1.0, requestor, 1);
                            queue.add(null, 1.0, requestor, 1);
                            queue.add(null, 1.0, requestor, 2);
                            queue.add(null, 1.0, requestor, 2);
                        });
                    }
                }

                this.simulator.scheduleEventRel(2.0, () ->
                {
                    var list = new ArrayList<Integer>();
                    for (var r : fcfs)
                        list.add((int) r.getId());
                    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7), list);

                    list.clear();
                    for (var r : lcfs)
                        list.add((int) r.getId());
                    assertEquals(List.of(7, 6, 5, 4, 3, 2, 1, 0), list);

                    list.clear();
                    for (var r : fcfsp)
                        list.add((int) r.getId());
                    assertEquals(List.of(2, 3, 6, 7, 0, 1, 4, 5), list);

                    list.clear();
                    for (var r : lcfsp)
                        list.add((int) r.getId());
                    assertEquals(List.of(7, 6, 3, 2, 5, 4, 1, 0), list);
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

    /** Test the floating-point capacity resource and queue with FIRST_ONLY versus ENTIRE_QUEUE regime. */
    @Test
    public void testResourceDoubleRegime()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var requestor = new CapacityRequestor.DoubleCapacity<Double>()
                {
                    public int count = 0;

                    public double received = 0.0;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                            final Resource.DoubleCapacity<Double> resource)
                    {
                        this.count++;
                        this.received += requestedCapacity;
                    }
                };

                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 0.0);
                var entity = new Entity<>("entity:1", this.simulator);
                resource.setReleaseType(ReleaseType.FIRST_ONLY);
                resource.setDefaultStatistics();
                resource.requestCapacity(entity, 0.1, requestor);
                resource.requestCapacity(entity, 0.2, requestor);
                resource.requestCapacity(entity, 1.0, requestor);
                resource.requestCapacity(entity, 0.3, requestor);
                resource.requestCapacity(entity, 0.1, requestor);
                resource.requestCapacity(entity, 0.5, requestor);
                assertEquals(0, requestor.count);
                assertEquals(0.0, requestor.received);
                resource.setCapacity(1.0);
                assertEquals(2, requestor.count);
                assertEquals(0.3, requestor.received, 1E-6);
                resource.setCapacity(10.0);
                resource.releaseCapacity(entity, 2.2);
                assertEquals(6, requestor.count);

                resource.setCapacity(0.0);
                resource.setReleaseType(ReleaseType.ENTIRE_QUEUE);
                requestor.count = 0;
                requestor.received = 0.0;
                resource.requestCapacity(entity, 0.1, requestor);
                resource.requestCapacity(entity, 0.2, requestor);
                resource.requestCapacity(entity, 1.0, requestor);
                resource.requestCapacity(entity, 0.3, requestor);
                resource.requestCapacity(entity, 0.1, requestor);
                resource.requestCapacity(entity, 0.5, requestor);
                assertEquals(0, requestor.count);
                assertEquals(0.0, requestor.received);
                resource.setCapacity(1.0);
                assertEquals(4, requestor.count);
                assertEquals(0.7, requestor.received, 1E-6);
                resource.setCapacity(10.0);
                resource.releaseCapacity(entity, 2.2);
                assertEquals(6, requestor.count);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the integer-capacity resource and queue. */
    @Test
    public void testResourceInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 1);
                var entity = new Entity<>("entity:1", this.simulator);
                assertEquals("resource", resource.getId());
                assertEquals(this.simulator, resource.getSimulator());
                assertEquals(1, resource.getCapacity());
                assertEquals(0, resource.getClaimedCapacity());
                assertEquals(1, resource.getAvailableCapacity());
                assertFalse(resource.hasDefaultStatistics());
                assertNull(resource.getUtilizationStatistic());
                resource.setCapacity(2);
                assertEquals(2, resource.getCapacity());
                assertEquals(0, resource.getClaimedCapacity());
                assertEquals(2, resource.getAvailableCapacity());
                assertEquals(ReleaseType.FIRST_ONLY, resource.getReleaseType());
                resource.setReleaseType(ReleaseType.ENTIRE_QUEUE);
                assertEquals(ReleaseType.ENTIRE_QUEUE, resource.getReleaseType());
                resource.setReleaseType(ReleaseType.FIRST_ONLY);
                Try.testFail(() -> resource.requestCapacity(entity, -1, null));
                Try.testFail(() -> resource.releaseCapacity(entity, -1));

                assertNotNull(resource.getRequestQueue());
                Queue<Double> queue = resource.getRequestQueue();
                assertEquals("resource_queue", queue.getId());
                assertEquals(this.simulator, queue.getSimulator());
                assertFalse(queue.hasDefaultStatistics());
                assertEquals(0, queue.getNumberRequestsInQueue());
                assertNull(queue.getQueueLengthStatistic());
                assertNull(queue.getTimeInQueueStatistic());
                Try.testFail(() -> queue.peek());
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the integer-capacity resource and queue. */
    @Test
    public void testResourceIntegerRequestRelease()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 1);
                var entity = new Entity<>("entity:1", this.simulator);
                resource.setDefaultStatistics();
                assertTrue(resource.hasDefaultStatistics());
                assertNotNull(resource.getUtilizationStatistic());
                assertTrue(Double.isNaN(resource.getUtilizationStatistic().getWeightedPopulationMean()));
                Queue<Double> queue = resource.getRequestQueue();
                assertTrue(queue.hasDefaultStatistics());
                assertNotNull(queue.getQueueLengthStatistic());
                assertTrue(Double.isNaN(queue.getQueueLengthStatistic().getWeightedPopulationMean()));
                assertNotNull(queue.getTimeInQueueStatistic());
                assertTrue(Double.isNaN(queue.getTimeInQueueStatistic().getPopulationMean()));
                var requestor1 = new CapacityRequestor.IntegerCapacity<Double>()
                {
                    public int receivedCapacity = -1;

                    public double time = Double.NaN;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final int requestedCapacity,
                            final Resource.IntegerCapacity<Double> resource)
                    {
                        this.receivedCapacity = requestedCapacity;
                        this.time = resource.getSimulator().getSimulatorTime();
                    }
                };
                var requestor2 = new CapacityRequestor.IntegerCapacity<Double>()
                {
                    public int receivedCapacity = -1;

                    public double time = Double.NaN;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final int requestedCapacity,
                            final Resource.IntegerCapacity<Double> resource)
                    {
                        this.receivedCapacity = requestedCapacity;
                        this.time = resource.getSimulator().getSimulatorTime();
                    }
                };
                this.simulator.scheduleEventRel(0.0, () -> resource.requestCapacity(entity, 1, requestor1));
                this.simulator.scheduleEventRel(1.0, () ->
                {
                    assertEquals(1, resource.getCapacity());
                    assertEquals(0, resource.getAvailableCapacity());
                    assertEquals(1, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(0.0, requestor1.time);
                    assertEquals(1, requestor1.receivedCapacity);
                });
                this.simulator.scheduleEventRel(2.0, () ->
                {
                    resource.releaseCapacity(entity, 1);
                    assertEquals(1, resource.getCapacity());
                    assertEquals(1, resource.getAvailableCapacity());
                    assertEquals(0, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(1.0, resource.getUtilizationStatistic().getWeightedPopulationMean());
                });
                this.simulator.scheduleEventRel(4.0, () ->
                {
                    resource.setCapacity(2);
                    resource.requestCapacity(entity, 1, requestor1);
                });
                this.simulator.scheduleEventRel(4.0, () -> resource.requestCapacity(entity, 2, requestor2));
                this.simulator.scheduleEventRel(6.0, () ->
                {
                    assertEquals(2, resource.getCapacity());
                    assertEquals(1, resource.getAvailableCapacity());
                    assertEquals(1, resource.getClaimedCapacity());
                    assertEquals(1, queue.getNumberRequestsInQueue());
                    assertNotNull(queue.peek());
                    assertEquals(4.0, requestor1.time);
                    assertEquals(1, requestor1.receivedCapacity);
                    assertEquals(-1, requestor2.receivedCapacity);
                    assertTrue(Double.isNaN(requestor2.time));
                });
                this.simulator.scheduleEventRel(8.0, () ->
                {
                    resource.releaseCapacity(entity, 1);
                    assertEquals(2, resource.getCapacity());
                    assertEquals(0, resource.getAvailableCapacity());
                    assertEquals(2, resource.getClaimedCapacity());
                    assertEquals(0, queue.getNumberRequestsInQueue());
                    assertEquals(8.0, requestor2.time);
                    assertEquals(2, requestor2.receivedCapacity);
                    assertEquals(4.0, queue.getTimeInQueueStatistic().getPopulationMean(), 1E-6);
                    assertEquals(0.5, queue.getQueueLengthStatistic().getWeightedPopulationMean(), 1E-6);
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        wait(simulator, 500);
        cleanUp(simulator);
    }

    /** Test the integer-capacity resource and queue with FIRST_ONLY versus ENTIRE_QUEUE regime. */
    @Test
    public void testResourceIntegerRegime()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var requestor = new CapacityRequestor.IntegerCapacity<Double>()
                {
                    public int count = 0;

                    public int received = 0;

                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final int requestedCapacity,
                            final Resource.IntegerCapacity<Double> resource)
                    {
                        this.count++;
                        this.received += requestedCapacity;
                    }
                };

                var resource = new Resource.IntegerCapacity<Double>("resource", this.simulator, 0);
                var entity = new Entity<>("entity:1", this.simulator);
                resource.setReleaseType(ReleaseType.FIRST_ONLY);
                resource.setDefaultStatistics();
                resource.requestCapacity(entity, 1, requestor);
                resource.requestCapacity(entity, 2, requestor);
                resource.requestCapacity(entity, 10, requestor);
                resource.requestCapacity(entity, 3, requestor);
                resource.requestCapacity(entity, 1, requestor);
                resource.requestCapacity(entity, 5, requestor);
                assertEquals(0, requestor.count);
                assertEquals(0, requestor.received);
                resource.setCapacity(10);
                assertEquals(2, requestor.count);
                assertEquals(3, requestor.received);
                resource.setCapacity(100);
                resource.releaseCapacity(entity, 22);
                assertEquals(6, requestor.count);

                resource.setCapacity(0);
                resource.setReleaseType(ReleaseType.ENTIRE_QUEUE);
                requestor.count = 0;
                requestor.received = 0;
                resource.requestCapacity(entity, 1, requestor);
                resource.requestCapacity(entity, 2, requestor);
                resource.requestCapacity(entity, 10, requestor);
                resource.requestCapacity(entity, 3, requestor);
                resource.requestCapacity(entity, 1, requestor);
                resource.requestCapacity(entity, 5, requestor);
                assertEquals(0, requestor.count);
                assertEquals(0, requestor.received);
                resource.setCapacity(10);
                assertEquals(4, requestor.count);
                assertEquals(7, requestor.received);
                resource.setCapacity(100);
                resource.releaseCapacity(entity, 22);
                assertEquals(6, requestor.count);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test capacity request for Double.
     */
    @Test
    public void testCapacityRequestDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var entity = new Entity<Double>("e1", simulator);
        var id = 12L;
        var queueEntryTime = 0.5;
        var amount = 1.0;
        var priority = 4;
        var requestor = new CapacityRequestor.DoubleCapacity<Double>()
        {
            @Override
            public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                    final Resource.DoubleCapacity<Double> resource)
            {
            }
        };
        var c1 = new CapacityRequest.DoubleCapacity<Double>(id, entity, requestor, amount, priority, queueEntryTime);
        assertEquals(id, c1.getId());
        assertEquals(entity, c1.getEntity());
        assertEquals(requestor, c1.getRequestor());
        assertEquals(amount, c1.getAmount());
        assertEquals(priority, c1.getPriority());
        assertEquals(queueEntryTime, c1.getQueueEntryTime());
        var c2 = new CapacityRequest.DoubleCapacity<Double>(id, entity, requestor, amount, priority, queueEntryTime);
        assertEquals(c1, c2);
        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c1.toString(), c2.toString());
        assertNotEquals(c1, new Object());
        assertNotEquals(c1, null);
        assertNotEquals(c1,
                new CapacityRequest.DoubleCapacity<Double>(8L, entity, requestor, amount, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.DoubleCapacity<Double>(id, new Entity<Double>("e2", simulator), requestor,
                amount, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.DoubleCapacity<Double>(id, entity, requestor, 18.2, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.DoubleCapacity<Double>(id, entity, requestor, amount, 0, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.DoubleCapacity<Double>(id, entity, requestor, amount, priority, 17.2));
    }

    /**
     * Test capacity request for Integer.
     */
    @Test
    public void testCapacityRequestInteger()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var entity = new Entity<Double>("e1", simulator);
        var id = 12L;
        var queueEntryTime = 0.5;
        var amount = 1;
        var priority = 4;
        var requestor = new CapacityRequestor.IntegerCapacity<Double>()
        {
            @Override
            public void receiveRequestedCapacity(final Entity<Double> entity, final int requestedCapacity,
                    final Resource.IntegerCapacity<Double> resource)
            {
            }
        };
        var c1 = new CapacityRequest.IntegerCapacity<Double>(id, entity, requestor, amount, priority, queueEntryTime);
        assertEquals(id, c1.getId());
        assertEquals(entity, c1.getEntity());
        assertEquals(requestor, c1.getRequestor());
        assertEquals(amount, c1.getAmount());
        assertEquals(priority, c1.getPriority());
        assertEquals(queueEntryTime, c1.getQueueEntryTime());
        var c2 = new CapacityRequest.IntegerCapacity<Double>(id, entity, requestor, amount, priority, queueEntryTime);
        assertEquals(c1, c2);
        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c1.toString(), c2.toString());
        assertNotEquals(c1, new Object());
        assertNotEquals(c1, null);
        assertNotEquals(c1,
                new CapacityRequest.IntegerCapacity<Double>(8L, entity, requestor, amount, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.IntegerCapacity<Double>(id, new Entity<Double>("e2", simulator), requestor,
                amount, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.IntegerCapacity<Double>(id, entity, requestor, 18, priority, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.IntegerCapacity<Double>(id, entity, requestor, amount, 0, queueEntryTime));
        assertNotEquals(c1, new CapacityRequest.IntegerCapacity<Double>(id, entity, requestor, amount, priority, 17.2));
    }

    /** Test the double-capacity resource and queue for multiple releases and errors. */
    @Test
    public void testMultipleReleaseDouble()
    {
        var simulator = new DevsSimulator<Double>("sim");
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var resource = new Resource.DoubleCapacity<Double>("resource", this.simulator, 1.0);
                var entity1 = new Entity<>("entity:1", this.simulator);
                var entity2 = new Entity<>("entity:2", this.simulator);
                var requestor = new CapacityRequestor.DoubleCapacity<Double>()
                {
                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final double requestedCapacity,
                            final Resource.DoubleCapacity<Double> resource)
                    {
                        assertEquals(0.5, requestedCapacity);
                        assertEquals(entity1, entity);
                    }
                };
                assertEquals("resource", resource.getId());
                assertEquals(this.simulator, resource.getSimulator());
                assertEquals(1.0, resource.getCapacity());
                assertEquals(0.0, resource.getClaimedCapacity());
                assertEquals(1.0, resource.getAvailableCapacity());
                
                resource.requestCapacity(entity1, 1.0, requestor);
                assertEquals(1.0, resource.getCapacity());
                assertEquals(1.0, resource.getClaimedCapacity());
                assertEquals(0.0, resource.getAvailableCapacity());
                
                resource.releaseCapacity(entity1, 0.5);
                assertEquals(1.0, resource.getCapacity());
                assertEquals(0.5, resource.getClaimedCapacity());
                assertEquals(0.5, resource.getAvailableCapacity());

                Try.testFail(() -> resource.releaseCapacity(entity1, 5.0));
                Try.testFail(() -> resource.releaseCapacity(null, 0.1));
                Try.testFail(() -> resource.releaseCapacity(entity2, 0.5));

                resource.releaseCapacity(entity1, 0.5);
                assertEquals(1.0, resource.getCapacity());
                assertEquals(0.0, resource.getClaimedCapacity());
                assertEquals(1.0, resource.getAvailableCapacity());

                Try.testFail(() -> resource.releaseCapacity(entity1, 0.5));
                Try.testFail(() -> resource.releaseCapacity(entity2, 0.5));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the integer-capacity resource and queue for multiple releases and errors. */
    @Test
    public void testMultipleReleaseInteger()
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
                var entity1 = new Entity<>("entity:1", this.simulator);
                var entity2 = new Entity<>("entity:2", this.simulator);
                var requestor = new CapacityRequestor.IntegerCapacity<Double>()
                {
                    @Override
                    public void receiveRequestedCapacity(final Entity<Double> entity, final int requestedCapacity,
                            final Resource.IntegerCapacity<Double> resource)
                    {
                        assertEquals(1, requestedCapacity);
                        assertEquals(entity1, entity);
                    }
                };
                assertEquals("resource", resource.getId());
                assertEquals(this.simulator, resource.getSimulator());
                assertEquals(2, resource.getCapacity());
                assertEquals(0, resource.getClaimedCapacity());
                assertEquals(2, resource.getAvailableCapacity());
                
                resource.requestCapacity(entity1, 2, requestor);
                assertEquals(2, resource.getCapacity());
                assertEquals(2, resource.getClaimedCapacity());
                assertEquals(0, resource.getAvailableCapacity());
                
                resource.releaseCapacity(entity1, 1);
                assertEquals(2, resource.getCapacity());
                assertEquals(1, resource.getClaimedCapacity());
                assertEquals(1, resource.getAvailableCapacity());
                
                Try.testFail(() -> resource.releaseCapacity(entity1, 5));
                Try.testFail(() -> resource.releaseCapacity(null, 1));
                Try.testFail(() -> resource.releaseCapacity(entity2, 1));
               
                resource.releaseCapacity(entity1, 1);
                assertEquals(2, resource.getCapacity());
                assertEquals(0, resource.getClaimedCapacity());
                assertEquals(2, resource.getAvailableCapacity());

                Try.testFail(() -> resource.releaseCapacity(entity1, 1));
                Try.testFail(() -> resource.releaseCapacity(entity2, 1));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

}
