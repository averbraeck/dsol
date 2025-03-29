package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * EntityTest tests the methods of the Entity class.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EntityTest extends FlowTest
{

    /**
     * Test the construction and cloning of an Entity.
     */
    @Test
    public void testEntityMethods()
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = makeModelDouble(simulator);
        SingleReplication<Double> replication = new SingleReplication<Double>("replication", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.runUpTo(2.0);
        wait(simulator, 500);

        var e1 = new Entity<Double>("e1", simulator);
        assertEquals("e1", e1.getId());
        assertEquals(2.0, e1.getCreationTime());
        assertNull(e1.getAttribute("x"));
        assertNull(e1.getAttribute("x", Double.class));
        assertNull(e1.getNumberAttribute("y"));
        assertNull(e1.getStringAttribute("s"));
        assertNull(e1.getTimestamp("t"));
        assertNotNull(e1.toString());

        // check deep copy
        e1.setTimestamp("t", 2.0);
        e1.setStringAttribute("s", "hello");
        e1.setNumberAttribute("L", 2L);
        StreamInterface stream = new MersenneTwister(10L);
        e1.setAttribute("d", new DistExponential(stream, 4.0));
        Entity<Double> e2 = e1.clone();
        assertEquals("e1", e2.getId());
        assertEquals(2.0, e2.getCreationTime());
        assertEquals(2.0, e2.getTimestamp("t"));
        assertEquals("hello", e2.getStringAttribute("s"));
        assertEquals(2L, e2.getNumberAttribute("L").longValue());
        DistContinuous d1 = e1.getAttribute("d", DistContinuous.class);
        DistContinuous d2 = e2.getAttribute("d", DistContinuous.class);
        assertEquals(d1.toString(), d2.toString());
        assertEquals(d1.getStream(), d2.getStream());
        assertNotEquals(d1.draw(), d2.draw()); // the STREAM is not cloned (!)

        cleanUp(simulator);
    }
}
