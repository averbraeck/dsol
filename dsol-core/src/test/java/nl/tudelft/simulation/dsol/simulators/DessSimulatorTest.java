package nl.tudelft.simulation.dsol.simulators;

import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.junit.jupiter.api.Test;

import net.jodah.concurrentunit.Waiter;

/**
 * The DESSSSimulatorTest test the DEVS Simulator.
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
public class DessSimulatorTest implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the Waiter from ConcurrentUnit that catches AssertionErrors in other threads. */
    private Waiter waiter;

    /**
     * DessSimulatorTest.
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     * @throws RemoteException on error
     */
    @Test
    public void testDessSimulator() throws TimeoutException, InterruptedException, RemoteException
    {
//        this.waiter = new Waiter();
//        DessSimulatorInterface<Double> dessSimulator = new DessSimulator<Double>("DessSimulatorTest", 0.1);
//        dessSimulator.addListener(this, Replication.END_REPLICATION_EVENT);
//        ExperimentalFrame experimentalFrame =
//                ExperimentUtilDouble.createExperimentalFrame(dessSimulator, new TestModel(dessSimulator));
//        experimentalFrame.start();
//        this.waiter.await(1000);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        this.waiter.resume();
    }

}
