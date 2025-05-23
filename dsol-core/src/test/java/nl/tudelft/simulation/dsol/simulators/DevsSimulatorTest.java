package nl.tudelft.simulation.dsol.simulators;

import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.junit.jupiter.api.Test;

import net.jodah.concurrentunit.Waiter;

/**
 * The DevsSimulatorTest test the DEVS Simulator.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class DevsSimulatorTest implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the Waiter from ConcurrentUnit that catches AssertionErrors in other threads. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Waiter waiter;

    /**
     * DevsSimulatorTest.
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     * @throws RemoteException on error
     */
    @Test
    public void testDevsSimulator() throws TimeoutException, InterruptedException, RemoteException
    {
        // this.waiter = new Waiter();
        // DevsSimulatorInterface<Double> devsSimulator = new DevsSimulator<Double>("DevsSimulatorTest");
        // devsSimulator.addListener(this, Replication.END_REPLICATION_EVENT);
        // ExperimentalFrame experimentalFrame =
        // ExperimentUtilDouble.createExperimentalFrame(devsSimulator, new DEVSTestModel(devsSimulator));
        // experimentalFrame.start();
        // this.waiter.await(5000);
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        this.waiter.resume();
    }

}
