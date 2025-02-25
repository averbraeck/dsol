package nl.tudelft.simulation.examples.dsol.terminal;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The 'active' container object.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Container implements IntResourceRequestorInterface<Double>
{
    /** the simulator. */
    private final DevsSimulatorInterface<Double> simulator;

    /** the container number. */
    private final int containerNumber;

    /** the QC resources. */
    private final QuayCrane qc;

    /** the AGV resources. */
    private final Agv agv;

    /** the ship. */
    private final Ship ship;

    /** phase. */
    private int phase = 0;

    /**
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator
     * @param containerNumber int; the container number
     * @param qc QC; the QC resources
     * @param agv AGV; the AGV resources
     * @param ship Ship; the ship
     */
    public Container(final DevsSimulatorInterface<Double> simulator, final int containerNumber, final QuayCrane qc, final Agv agv,
            final Ship ship)
    {
        this.simulator = simulator;
        this.containerNumber = containerNumber;
        this.qc = qc;
        this.agv = agv;
        this.ship = ship;
        synchronized (ship)
        {
            try
            {
                if (Terminal.DEBUG)
                {
                    System.out.println(
                            "T = " + this.simulator.getSimulatorTime() + ", Claim AGV for container " + this.containerNumber);
                }
                this.simulator.scheduleEventAbs(39.0 * 60.0, this, "checkPhase", null);
                this.agv.requestCapacity(1, this);
                this.phase++;
            }
            catch (SimRuntimeException | RemoteException e)
            {
                this.simulator.getLogger().always().error(e);
            }
        }
    }

    @Override
    public synchronized void receiveRequestedResource(final long requestedCapacity,
            final IntResource<Double> resource) throws RemoteException
    {
        try
        {
            if (resource instanceof Agv)
            {
                this.phase++;
                this.simulator.scheduleEventRel(this.agv.drawDelay(), this, "agvReady", null);
            }

            if (resource instanceof QuayCrane)
            {
                if (Terminal.DEBUG)
                {
                    System.out.println(
                            "T = " + this.simulator.getSimulatorTime() + ", Claim QC for container " + this.containerNumber);
                }
                this.phase++;
                this.simulator.scheduleEventRel(this.qc.drawDelay(), this, "qcReady", null);
            }
        }
        catch (SimRuntimeException e)
        {
            this.simulator.getLogger().always().error(e);
        }
    }

    /** */
    protected synchronized void agvReady()
    {
        try
        {
            this.phase++;
            if (Terminal.DEBUG)
            {
                System.out.println(
                        "T = " + this.simulator.getSimulatorTime() + ", AGV ready for container " + this.containerNumber);
            }
            this.agv.releaseCapacity(1);
            this.qc.requestCapacity(1, this);
        }
        catch (SimRuntimeException | RemoteException e)
        {
            this.simulator.getLogger().always().error(e);
        }
    }

    /** */
    protected synchronized void qcReady()
    {
        try
        {
            if (Terminal.DEBUG)
            {
                System.out.println(
                        "T = " + this.simulator.getSimulatorTime() + ", QC ready for container " + this.containerNumber);
            }
            this.qc.releaseCapacity(1);
            this.phase++;
            this.ship.incContainers();
        }
        catch (RemoteException e)
        {
            this.simulator.getLogger().always().error(e);
        }
    }

    /** */
    protected void checkPhase()
    {
        if (this.phase != 5)
        {
            System.out.println("Container " + this.containerNumber + " was stuck in phase " + this.phase);
        }
    }
}
