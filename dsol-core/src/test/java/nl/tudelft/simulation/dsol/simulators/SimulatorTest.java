package nl.tudelft.simulation.dsol.simulators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.Sleep;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;

/**
 * SimulatorTest tests the simulator.addScheduledMethodOnInitialize method.
 * <p>
 * Copyright (c) 2021-2021 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SimulatorTest
{
    /**
     * Test the simulator.addScheduledMethodOnInitialize method.
     */
    @Test
    public void testScheduledInitMethods()
    {
        new TestScheduledInitMethods();
    }

    /** */
    static class TestScheduledInitMethods
    {
        /** */
        TestScheduledInitMethods()
        {
            DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("sim");
            TestModel model = new TestModel(simulator);
            ReplicationInterface<Double> replication = new SingleReplication<Double>("rep", 0.0, 0.0, 100.0);
            simulator.initialize(model, replication);
            simulator.start();
            while (simulator.isStartingOrRunning())
            {
                Sleep.sleep(10);
            }
            assertEquals("The value after running should be 103 -- adding 1, 2, and 10 x 10", 103, model.getValue());
        }
    }

    /** */
    static class TestModel extends AbstractDSOLModel<Double, DEVSSimulator<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** var for testing. */
        private int value = 0;

        /**
         * @param simulator the simulator
         */
        TestModel(final DEVSSimulator<Double> simulator)
        {
            super(simulator);
            try
            {
                // this should fail
                getSimulator().scheduleEventAbs(5.0, this, this, "loop", new Object[] {});
                fail("call to ScheduleMethod in constructor of model should fail");
            }
            catch (Exception e)
            {
                // expected
            }
            // use scheduled initialization instead
            simulator.addScheduledMethodOnInitialize(this, this, "add", new Object[] {1});
            simulator.addScheduledMethodOnInitialize(this, this, "add", new Object[] {2});
            simulator.addScheduledMethodOnInitialize(this, this, "schedule", new Object[] {});

        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            try
            {
                // this should succeed
                getSimulator().scheduleEventAbs(10.0, this, this, "test", new Object[] {});
            }
            catch (Exception e)
            {
                fail("call to ScheduleMethod in constructModel should succeed");
            }
        }

        /** */
        void test()
        {
            // do nothing
        }

        /**
         * @param i int to add
         */
        void add(final int i)
        {
            this.value += i;
        }

        /** */
        void schedule()
        {
            getSimulator().scheduleEventRel(10.0, this, this, "loop", new Object[] {});
        }

        /** */
        void loop()
        {
            this.value += 10;
            schedule();
        }

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "SimModel";
        }

        /**
         * @return value
         */
        public final int getValue()
        {
            return this.value;
        }

    }
}
