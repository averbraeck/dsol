package nl.tudelft.simulation.dsol.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.stats.ConfidenceInterval;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The SimTallyTest tests the SimTally.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class SimTallyTest extends LocalEventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /** update event. */
    private static final EventType UPDATE_EVENT =
            new EventType("update", new MetaData("update", "update", new ObjectDescriptor("value", "value", Double.class)));

    /**
     * Test the tally.
     * @throws RemoteException on remote error (should not happen)
     * @throws NamingException on failure registering the replication or statistic in the Context
     */
    @Test
    public void testTallTimeDouble() throws RemoteException, NamingException
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new DummyModel(simulator);
        SingleReplication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 10.0);
        simulator.initialize(model, replication);

        String key = "tally";
        String description = "THIS TALLY IS TESTED";
        SimTally<Double> tally = new SimTally<Double>(key, description, model);

        // check uninitialized tally
        checkBefore(tally);

        // now we initialize the tally
        tally.initialize();

        // check the initialized tally
        checkInitialized(tally);

        // fire evets to fill the tally
        fireEvents(tally);

        // Now we check the tally
        check(tally);

        // remove the tally from he statistics
        replication.removeFromContext();
    }

    /**
     * Test the tally.
     * @throws RemoteException on remote error (should not happen)
     * @throws NamingException on failure registering the replication or statistic in the Context
     */
    @Test
    public void testTallyEventProduceTimeDouble() throws RemoteException, NamingException
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new DummyModel(simulator);
        SingleReplication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 10.0);
        simulator.initialize(model, replication);

        String key = "tally";
        String description = "THIS TALLY IS TESTED";
        SimTally<Double> tally = new SimTally<Double>(key, description, model, this, UPDATE_EVENT);

        // check uninitialized tally
        checkBefore(tally);

        // now we initialize the tally
        tally.initialize();

        // check the initialized tally
        checkInitialized(tally);

        // fire evets to fill the tally
        fireEvents(tally);

        // Now we check the tally
        check(tally);

        // remove the tally from he statistics
        replication.removeFromContext();
    }

    /**
     * Check the uninitialized tally.
     * @param tally the tally to test
     */
    private void checkBefore(final SimTally<Double> tally)
    {
        // check the description
        assertEquals("THIS TALLY IS TESTED", tally.getDescription());
        assertEquals("tally", tally.getKey());

        // now we check the initial values
        assertTrue(Double.isNaN(tally.getMin()));
        assertTrue(Double.isNaN(tally.getMax()));
        assertTrue(Double.isNaN(tally.getSampleMean()));
        assertTrue(Double.isNaN(tally.getSampleVariance()));
        assertTrue(Double.isNaN(tally.getSampleStDev()));
        assertEquals(0.0, tally.getSum(), 1E-6);
        assertEquals(0L, tally.getN());
        assertNull(tally.getConfidenceInterval(0.95));
        assertNull(tally.getConfidenceInterval(0.95, ConfidenceInterval.LEFT_SIDE_CONFIDENCE));
    }

    /**
     * Check the initialized tally.
     * @param tally the tally to test
     */
    private void checkInitialized(final SimTally<Double> tally)
    {
        assertTrue(Double.isNaN(tally.getMin()));
        assertTrue(Double.isNaN(tally.getMax()));
        assertTrue(Double.isNaN(tally.getSampleMean()));
        assertTrue(Double.isNaN(tally.getSampleVariance()));
        assertTrue(Double.isNaN(tally.getSampleStDev()));
        assertEquals(0.0, tally.getSum(), 1E-6);
        assertEquals(0L, tally.getN());
        assertNull(tally.getConfidenceInterval(0.95));
        assertNull(tally.getConfidenceInterval(0.95, ConfidenceInterval.LEFT_SIDE_CONFIDENCE));
    }

    /**
     * Fire events to the initialized tally.
     * @param tally the tally to test
     */
    private void fireEvents(final SimTally<Double> tally)
    {
        // We first fire a wrong event
        try
        {
            tally.notify(new TimedEvent<String>(UPDATE_EVENT, "ERROR", "ERROR"));
            fail("tally should react on timed event.value !instanceOf Double");
        }
        catch (Exception exception)
        {
            assertNotNull(exception);
        }

        // Now we fire some events
        try
        {
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.0, 0.1));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.1, 0.2));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.2, 0.3));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.3, 0.4));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.4, 0.5));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.5, 0.6));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.6, 0.7));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.7, 0.8));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.8, 0.9));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 1.9, 1.0));
            tally.notify(new TimedEvent<Double>(UPDATE_EVENT, 2.0, 1.1));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }

    /**
     * Check the filled tally.
     * @param tally the tally to test
     */
    private void check(final SimTally<Double> tally)
    {
        assertEquals(2.0, tally.getMax(), 1E-6);
        assertEquals(1.0, tally.getMin(), 1E-6);
        assertEquals(11L, tally.getN());
        assertEquals(16.5, tally.getSum(), 1E-6);
        assertEquals(1.5, tally.getSampleMean(), 1E-6);
        assertEquals(0.11, tally.getSampleVariance(), 1E-6);
        assertEquals(Math.sqrt(0.11), tally.getSampleStDev(), 1E-6);
        assertEquals(1.304, tally.getConfidenceInterval(0.05)[0], 1E-3);

        // we check the input of the confidence interval
        try
        {
            assertNull(tally.getConfidenceInterval(-0.95));
            fail("should have reacted on wrong confidence levels");
        }
        catch (Exception exception)
        {
            assertTrue(exception.getClass().equals(IllegalArgumentException.class));
        }
        try
        {
            assertNull(tally.getConfidenceInterval(1.14));
            fail("should have reacted on wrong confidence levels");
        }
        catch (Exception exception)
        {
            assertTrue(exception.getClass().equals(IllegalArgumentException.class));
        }

        assertEquals(1.5, tally.getSampleMean(), 1E-6);

        // Let's compute the standard deviation
        double variance = 0;
        for (int i = 0; i < 11; i++)
        {
            variance = Math.pow(1.5 - (1.0 + i / 10.0), 2) + variance;
        }
        variance = variance / 10.0;
        double stDev = Math.sqrt(variance);

        assertEquals(variance, tally.getSampleVariance(), 1E-6);
        assertEquals(stDev, tally.getSampleStDev(), 1E-6);
    }

}
