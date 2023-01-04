package nl.tudelft.simulation.dsol.simulators;

import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.junit.Test;

import net.jodah.concurrentunit.Waiter;

/**
 * The DEVSSimulatorTest test the DEVS Simulator.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class DEVSSimulatorTest implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the Waiter from ConcurrentUnit that catches AssertionErrors in other threads. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Waiter waiter;

    /**
     * DEVSSimulatorTest.
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     * @throws RemoteException on error
     */
    @Test
    public void testDEVSSimulator() throws TimeoutException, InterruptedException, RemoteException
    {
//        this.waiter = new Waiter();
//        DEVSSimulatorInterface<Double> devsSimulator = new DEVSSimulator<Double>("DEVSSimulatorTest");
//        devsSimulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
//        ExperimentalFrame experimentalFrame =
//                ExperimentUtilDouble.createExperimentalFrame(devsSimulator, new DEVSTestModel(devsSimulator));
//        experimentalFrame.start();
//        this.waiter.await(5000);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        this.waiter.resume();
    }

}
