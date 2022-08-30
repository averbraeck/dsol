package nl.tudelft.simulation.dsol.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.TimedEventType;
import org.junit.Test;

import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * The counterTest test the counter.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SimCounterTest extends EventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Test the counter.
     * @throws RemoteException on remote error (should not happen)
     * @throws NamingException on statistics registration error
     */
    @Test
    public void test() throws RemoteException, NamingException
    {
        DEVSSimulatorInterface<Double> simulator = new DEVSSimulator<Double>("sim");
        DSOLModel<Double, DEVSSimulatorInterface<Double>> model = new DummyModel(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 10.0);
        simulator.initialize(model, replication);

        String description = "counter description";
        SimCounter<Double> counter = new SimCounter<Double>(description, simulator);
        assertEquals(counter.toString(), description);
        assertEquals(counter.getDescription(), description);

        assertEquals(0L, counter.getN());
        assertEquals(0L, counter.getCount());

        counter.initialize();

        counter.addListener(new EventListenerInterface()
        {
            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void notify(final EventInterface event)
            {
                assertTrue(event.getType().equals(SimCounter.TIMED_OBSERVATION_ADDED_EVENT));
                assertTrue(event.getContent().getClass().equals(Long.class));
            }
        }, SimCounter.TIMED_OBSERVATION_ADDED_EVENT);

        TimedEventType et = new TimedEventType("observation");
        long value = 0;
        for (int i = 0; i < 100; i++)
        {
            counter.notify(new TimedEvent<Double>(et, "CounterTest", Long.valueOf(2 * i), 0.1 * i));
            value = value + 2 * i;
        }
        assertTrue(counter.getN() == 100);
        assertTrue(counter.getCount() == value);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "CounterTest";
    }
}
