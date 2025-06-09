package nl.tudelft.simulation.dsol.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The counterTest test the counter.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SimCounterTest extends LocalEventProducer
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
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new DummyModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 10.0);
        simulator.initialize(model, replication);

        String key = "counter";
        String description = "counter description";
        SimCounter<Double> counter = new SimCounter<Double>(key, description, model);
        assertTrue(counter.toString().startsWith("Counter"));
        assertTrue(counter.toString().contains(description));
        assertEquals(counter.getDescription(), description);
        assertEquals(counter.getKey(), key);

        assertEquals(0L, counter.getN());
        assertEquals(0L, counter.getCount());

        counter.initialize();

        counter.addListener(new EventListener()
        {
            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void notify(final Event event)
            {
                assertTrue(event.getType().equals(SimCounter.TIMED_OBSERVATION_ADDED_EVENT));
                assertTrue(event.getContent().getClass().equals(Long.class));
            }
        }, SimCounter.TIMED_OBSERVATION_ADDED_EVENT);

        EventType et = new EventType("observation",
                new MetaData("observation", "observation", new ObjectDescriptor("value", "value", Long.class)));
        long value = 0;
        for (int i = 0; i < 100; i++)
        {
            counter.notify(new TimedEvent<Double>(et, Long.valueOf(2 * i), 0.1 * i));
            value = value + 2 * i;
        }
        assertTrue(counter.getN() == 100);
        assertTrue(counter.getCount() == value);
    }
}
