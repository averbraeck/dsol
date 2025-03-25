package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import nl.tudelft.simulation.dsol.simulators.ErrorStrategy;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DuplicateTest tests the Duplicate flow block.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DuplicateTest extends FlowTest
{
    /** */
    private DuplicateTest()
    {
        // do not instantiate
    }

    /**
     * Test the construction of the Duplicate flow block.
     */
    @Test
    public void testDuplicateMethods()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Duplicate<Double>[] duplicateBlock = new Duplicate[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var d1 = new Duplicate<Double>("d1", this.simulator);
                duplicateBlock[0] = d1;
                assertEquals("d1", d1.getId());
                assertEquals(this.simulator, d1.getSimulator());
                assertNull(d1.getDestination());
                assertNull(d1.getDuplicateDestination());
                assertNull(d1.getCountReceivedStatistic());
                assertNull(d1.getCountReleasedStatistic());
                assertNull(d1.getNumberCopiesDist());
                assertEquals(0, d1.getNumberEntitiesInMinusOut());
                assertFalse(d1.hasDefaultStatistics());
                assertNull(d1.getCountDuplicateReleasedStatistic());
                assertNotNull(d1.toString());

                // set duplicate distribution
                StreamInterface stream = new MersenneTwister(12L);
                var duplicateDist = new DistDiscreteUniform(stream, 1, 4);
                d1.setNumberCopiesDist(duplicateDist);
                assertEquals(duplicateDist, d1.getNumberCopiesDist());

                // set duplicates
                d1.setNumberCopies(2);
                assertEquals(DistDiscreteConstant.class, d1.getNumberCopiesDist().getClass());

                // turn on statistics
                d1.setDefaultStatistics();
                assertNotNull(d1.getCountReceivedStatistic());
                assertNotNull(d1.getCountReleasedStatistic());
                assertNotNull(d1.getCountDuplicateReleasedStatistic());
                assertEquals(0, d1.getCountReceivedStatistic().getCount());
                assertEquals(0, d1.getCountReleasedStatistic().getCount());
                assertEquals(0, d1.getCountDuplicateReleasedStatistic().getCount());
                assertTrue(d1.hasDefaultStatistics());
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test errors in the duplicate flow block.
     */
    @Test
    public void testDuplicateErrors()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                Try.testFail(() -> new Duplicate<Double>(null, this.simulator), NullPointerException.class);
                Try.testFail(() -> new Duplicate<Double>("d", null), NullPointerException.class);

                var d2 = new Duplicate<Double>("d2", this.simulator);

                // duplicate block does not have a distribution for the number of copies
                Try.testFail(() -> d2.receiveEntity(new Entity<Double>("e", this.simulator)), NullPointerException.class);

                // number of copies distribution should not be null
                Try.testFail(() -> d2.setNumberCopiesDist(null), NullPointerException.class);

                // number of copies distribution should not be negative
                Try.testFail(() -> d2.setNumberCopies(-2), IllegalArgumentException.class);

                // number of copies distribution should not have a distribution with negative numbers
                StreamInterface stream = getSimulator().getModel().getDefaultStream();
                d2.setNumberCopiesDist(new DistDiscreteConstant(stream, -2));
                Try.testFail(() -> d2.receiveEntity(new Entity<Double>("e", this.simulator)), IllegalArgumentException.class);
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /**
     * Test duplicate of 1 entity.
     */
    @Test
    public void testDuplicate1Entity()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Entity<Double>[] entities = new Entity[2];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                StreamInterface stream = new MersenneTwister(12L);
                var destroy1 = new Destroy<Double>("regular", this.simulator);
                var destroy2 = new Destroy<Double>("copies", this.simulator);
                destroy1.setReceiveFunction((entity) -> entities[0] = entity);
                destroy2.setReceiveFunction((entity) -> entities[1] = entity);

                var duplicate = new Duplicate<Double>("d3", this.simulator);
                duplicate.setNumberCopies(1);
                duplicate.setDefaultStatistics();
                duplicate.setDestination(destroy1);
                duplicate.setDuplicateDestination(destroy2);
                duplicate.setDuplicateReleaseFunction((entity) -> entity.setAttribute("extra", 1));

                var generator = new Create<Double>("c3", this.simulator);
                generator.setBatchSize(1);
                generator.setDestination(duplicate);
                generator.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 10.0)));
                generator.setStartTime(5.0);
                generator.setEntitySupplier(() -> new Entity<>("e", this.simulator));
                generator.setReleaseFunction((entity) ->
                {
                    entity.setAttribute("dist", new DistExponential(stream, 2.0));
                    entity.setTimestamp("t", getSimulator().getSimulatorTime());
                    entity.setNumberAttribute("n", 10);
                    entity.setStringAttribute("s", "Hello World");
                });
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 10.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        waitForCompletion(simulator, 1000);

        assertEquals("e", entities[1].getId());
        assertEquals(entities[0].getId(), entities[1].getId());
        assertEquals(entities[0].getCreationTime(), entities[1].getCreationTime());
        assertEquals(10, entities[1].getNumberAttribute("n"));
        assertEquals(entities[0].getNumberAttribute("n"), entities[1].getNumberAttribute("n"));
        assertEquals("Hello World", entities[1].getAttribute("s"));
        assertEquals(entities[0].getAttribute("s"), entities[1].getAttribute("s"));
        assertEquals(DistExponential.class, entities[1].getAttribute("dist").getClass());
        assertNotEquals(System.identityHashCode(entities[0].getAttribute("dist")),
                System.identityHashCode(entities[1].getAttribute("dist")));
        assertEquals(System.identityHashCode(entities[0].getAttribute("dist", DistContinuous.class).getStream()),
                System.identityHashCode(entities[1].getAttribute("dist", DistContinuous.class).getStream()));
        assertNull(entities[0].getAttribute("extra"));
        assertNotNull(entities[1].getAttribute("extra"));
        assertEquals(1, entities[1].getNumberAttribute("extra"));
        cleanUp(simulator);
    }

    /**
     * Test duplicate of 100 entities.
     */
    @Test
    public void testDuplicate100Entities()
    {
        var simulator = new DevsSimulator<Double>("sim");
        @SuppressWarnings("unchecked")
        final Duplicate<Double>[] duplicateBlock = new Duplicate[1];
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                StreamInterface stream = new MersenneTwister(12L);
                var destroy1 = new Destroy<Double>("regular", this.simulator);
                // the duplicates will be immediately destroyed, but should be counted.

                var duplicate = new Duplicate<Double>("d3", this.simulator);
                duplicateBlock[0] = duplicate;
                duplicate.setNumberCopies(10);
                duplicate.setDefaultStatistics();
                duplicate.setDestination(destroy1);

                var generator = new Create<Double>("c3", this.simulator);
                generator.setBatchSize(1);
                generator.setDestination(duplicate);
                generator.setIntervalDist(new DistContinuousSimulationTime.TimeDouble(new DistConstant(stream, 10.0)));
                generator.setStartTime(5.0);
                generator.setEntitySupplier(() -> new Entity<>("e", this.simulator));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        simulator.setErrorStrategy(ErrorStrategy.WARN_AND_THROW);
        simulator.start();
        waitForCompletion(simulator, 1000);

        assertEquals(10, duplicateBlock[0].getCountReceivedStatistic().getCount());
        assertEquals(10, duplicateBlock[0].getCountReleasedStatistic().getCount());
        assertEquals(100, duplicateBlock[0].getCountDuplicateReleasedStatistic().getCount());

        cleanUp(simulator);
    }

}
