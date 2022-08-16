package nl.tudelft.simulation.dsol.formalisms.process;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.formalisms.ResourceRequestorInterface;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.interpreter.process.InterpretableProcess;
import nl.tudelft.simulation.dsol.simtime.SimTime;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * The Process for the process interaction formalism with hold(), suspend(), resume() and cancel() methods.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 */
public abstract class Process<T extends Number & Comparable<T>> extends InterpretableProcess
        implements ResourceRequestorInterface<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** The simulator to schedule on. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DEVSSimulatorInterface<T> simulator = null;

    /**
     * the simEvent which is used to schedule the resume.
     */
    private SimEventInterface<T> simEvent = null;

    /**
     * constructs a new Process and IMMEDIATELY STARTS ITS PROCESS METHOD.
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; the simulator to schedule on
     */
    public Process(final DEVSSimulatorInterface<T> simulator)
    {
        this(simulator, true);
    }

    /**
     * Constructs a new <code>Process</code>.
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; the simulator to schedule on
     * @param start boolean; whether to immediately start this process
     */
    public Process(final DEVSSimulatorInterface<T> simulator, final boolean start)
    {
        super();
        this.simulator = simulator;
        if (start)
        {
            try
            {
                T simTime = this.simulator.getSimulatorTime();
                this.simEvent = new SimEvent<T>(simTime, this, this, "resume", null);
                this.simulator.scheduleEvent(this.simEvent);
            }
            catch (Exception exception)
            {
                this.simulator.getLogger().always().error(exception, "<init>");
            }
        }
    }

    /**
     * processes the process.
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on simulation failures.
     */
    public abstract void process() throws RemoteException, SimRuntimeException;

    /**
     * holds the process for a duration.
     * @param duration T; the duration
     * @throws SimRuntimeException on negative duration
     * @throws RemoteException on network failure
     */
    protected void hold(final T duration) throws SimRuntimeException, RemoteException
    {
        // First we schedule the resume operation
        this.simEvent = new SimEvent<T>(SimTime.plus(this.simulator.getSimulatorTime(), duration), this, this, "resume", null);
        this.simulator.scheduleEvent(this.simEvent);
        // Now we suspend
        this.suspend();
    }

    /**
     * cancels this process entirely. After the process.cancel() is invoked a process can no longer be resumed.
     */
    public void cancel()
    {
        super.cancelProcess();
        if (this.simEvent != null)
        {
            try
            {
                this.simulator.cancelEvent(this.simEvent);
            }
            catch (Exception exception)
            {
                this.simulator.getLogger().always().warn(exception, "cancel");
            }
        }
    }

    /**
     * resumes this process.
     */
    public void resume()
    {
        this.simEvent = null;
        super.resumeProcess();
    }

    /**
     * suspends this process.
     */
    public void suspend()
    {
        super.suspendProcess();
    }

    /** {@inheritDoc} */
    @Override
    public void receiveRequestedResource(final double requestedCapacity, final Resource<T> resource)
    {
        this.resume();
    }
}
