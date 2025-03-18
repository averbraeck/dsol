package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * FlowObjectTest tests the generic properties of the abstract FlowObject class.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FlowObjectTest extends FlowTest
{
    /**
     * Test the assignments in the FlowObject.
     */
    @Test
    public void testFlowObjectAsssignments()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractDsolModel<Double, DevsSimulatorInterface<Double>>(simulator)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                var delay = new Delay<Double>("delay", this.simulator);
                assertNull(delay.getAttribute("x"));
                assertNull(delay.getAttribute("y", Integer.class));
                assertNull(delay.getCountReceivedStatistic());
                assertNull(delay.getCountReleasedStatistic());
                assertNull(delay.getDelayDistribution());
                assertNotNull(delay.getDelayedEntityList());
                assertEquals(0, delay.getDelayedEntityList().size());
                assertNull(delay.getDestination());
                assertNull(delay.getNumberAttribute("n"));
                assertEquals(0, delay.getNumberEntitiesInMinusOut());
                assertNull(delay.getStringAttribute("s"));

                // create a flow
                var delay2 = new Delay<Double>("delay2", this.simulator);
                delay.setDestination(delay2);
                delay2.setDelayFunction((e) -> 1.0);

                // see where null is allowed
                Try.testFail(() -> delay.executeFunction(null), NullPointerException.class);
                delay.setReceiveFunction(null);
                delay.setReleaseFunction(null);

                // Set initial attributes with a function and test them
                delay.executeFunction(() ->
                {
                    delay.setAttribute("x", 10.0);
                    delay.setNumberAttribute("y", 12);
                    delay.setStringAttribute("s", "string");
                });
                assertNotNull(delay.getAttribute("x"));
                assertNotNull(delay.getAttribute("y", Integer.class));
                assertNotNull(delay.getNumberAttribute("x"));
                assertNotNull(delay.getStringAttribute("s"));
                assertEquals(10.0, delay.getAttribute("x"));
                assertEquals(10.0, delay.getAttribute("x", Double.class));
                assertEquals(12, delay.getNumberAttribute("y"));
                assertEquals("string", delay.getStringAttribute("s"));
                assertEquals("12", delay.getStringAttribute("y"));

                // create entity and check attributes
                var entity = new Entity<Double>("e1", 0.0);
                assertNull(entity.getTimestamp("t"));
                assertNull(entity.getAttribute("x"));
                assertNull(entity.getAttribute("y", Integer.class));
                assertNull(entity.getNumberAttribute("n"));
                assertNull(entity.getStringAttribute("s"));

                // set delay function to 0.0
                delay.setDelayFunction((e) -> 0.0);

                // set attributes upon receiving an entity
                delay.setReceiveFunction((e) ->
                {
                    delay.setAttribute("x", 20.0);
                    delay.setNumberAttribute("y", delay.getNumberAttribute("y").intValue() + 1);
                    delay.setStringAttribute("s", "other");
                    e.setTimestamp("time", 0.0);
                    e.setNumberAttribute("ex", delay.getNumberAttribute("x"));
                    e.setNumberAttribute("ey", delay.getNumberAttribute("y"));
                    e.setStringAttribute("es", delay.getStringAttribute("s"));
                });
                delay.receiveEntity(entity);
                assertEquals(1, delay.getDelayedEntityList().size());
                assertEquals(1, delay.getNumberEntitiesInMinusOut());
                assertEquals(20.0, delay.getAttribute("x"));
                assertEquals(20.0, delay.getAttribute("x", Double.class));
                assertEquals(13, delay.getNumberAttribute("y"));
                assertEquals("other", delay.getStringAttribute("s"));
                assertEquals("13", delay.getStringAttribute("y"));
                assertEquals(20.0, entity.getAttribute("ex"));
                assertEquals(20.0, entity.getAttribute("ex", Double.class));
                assertEquals(13, entity.getNumberAttribute("ey"));
                assertEquals("other", entity.getStringAttribute("es"));
                assertEquals("13", entity.getStringAttribute("ey"));
                assertEquals(0.0, entity.getTimestamp("time"));

                // set attributes upon releasing an entity
                delay.setReleaseFunction((e) ->
                {
                    delay.setStringAttribute("s", "last");
                    e.setNumberAttribute("ex", e.getNumberAttribute("ex").doubleValue() * 2.0);
                    e.setNumberAttribute("ey", e.getNumberAttribute("ey").intValue() + 1);
                    e.setStringAttribute("es", e.getStringAttribute("es") + " value");
                });
                delay.releaseEntity(entity);
                assertEquals(0, delay.getDelayedEntityList().size());
                assertEquals(0, delay.getNumberEntitiesInMinusOut());
                assertEquals(20.0, delay.getAttribute("x"));
                assertEquals(20.0, delay.getAttribute("x", Double.class));
                assertEquals(13, delay.getNumberAttribute("y"));
                assertEquals("last", delay.getStringAttribute("s"));
                assertEquals("13", delay.getStringAttribute("y"));
                assertEquals(40.0, entity.getAttribute("ex"));
                assertEquals(40.0, entity.getAttribute("ex", Double.class));
                assertEquals(14, entity.getNumberAttribute("ey"));
                assertEquals("other value", entity.getStringAttribute("es"));
                assertEquals("14", entity.getStringAttribute("ey"));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }
}
