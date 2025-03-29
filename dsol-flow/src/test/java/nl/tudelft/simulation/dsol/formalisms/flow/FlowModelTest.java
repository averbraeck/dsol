package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowModel.BlockNamingType;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;

/**
 * FlowModelTest.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FlowModelTest extends FlowTest
{

    /** Test the FlowModel with automatic naming. */
    @Test
    public void testFlowModelAutomaticId()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var model = new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(simulator, BlockNamingType.AUTOMATIC)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                assertEquals(BlockNamingType.AUTOMATIC, getBlockNamingType());
                assertEquals(0, getBlockMap().size());
                var create1 = new Create<>("create", this.simulator);
                assertEquals(1, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("Create_1"));
                assertEquals(create1, getBlockMap().get("Create_1"));
                var create2 = new Create<>("create", this.simulator);
                assertEquals(2, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("Create_2"));
                assertEquals(create2, getBlockMap().get("Create_2"));
                var entity = new Entity<Double>("entity", this.simulator);
                assertEquals(3, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("Entity_1"));
                assertEquals(entity, getBlockMap().get("Entity_1"));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the FlowModel with id-based naming. */
    @Test
    public void testFlowModelById()
    {
        var simulator = new DevsSimulator<Double>("sim");
        var streamInfo = new StreamInformation(new MersenneTwister(10L));
        var model = new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(simulator, streamInfo, BlockNamingType.BY_ID)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
                assertEquals(BlockNamingType.BY_ID, getBlockNamingType());
                assertEquals(0, getBlockMap().size());
                var create1 = new Create<>("create1", this.simulator);
                assertEquals(1, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("create1"));
                assertEquals(create1, getBlockMap().get("create1"));
                var create2 = new Create<>("create2", this.simulator);
                assertEquals(2, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("create2"));
                assertEquals(create2, getBlockMap().get("create2"));
                Try.testFail(() -> new Create<>("create2", this.simulator));
                var entity = new Entity<Double>("entity345", this.simulator);
                assertEquals(3, getBlockMap().size());
                assertTrue(getBlockMap().containsKey("entity345"));
                assertEquals(entity, getBlockMap().get("entity345"));
            }
        };
        simulator.initialize(model, new SingleReplication<Double>("rep", 0.0, 0.0, 100.0));
        cleanUp(simulator);
    }

    /** Test the FlowModel for constructor errors. */
    @Test
    public void testFlowModelConstructorError()
    {
        var simulator = new DevsSimulator<Double>("sim");
        Try.testFail(() -> new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(simulator, null)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
            }
        });

        var streamInfo = new StreamInformation(new MersenneTwister(10L));
        Try.testFail(() -> new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(simulator, streamInfo, null)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
            }
        });

        Try.testFail(() -> new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(null, BlockNamingType.AUTOMATIC)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void constructModel() throws SimRuntimeException
            {
            }
        });

        Try.testFail(
                () -> new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(null, streamInfo, BlockNamingType.AUTOMATIC)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                    }
                });

        Try.testFail(
                () -> new AbstractFlowModel<Double, DevsSimulatorInterface<Double>>(simulator, null, BlockNamingType.AUTOMATIC)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                    }
                });
    }
}
