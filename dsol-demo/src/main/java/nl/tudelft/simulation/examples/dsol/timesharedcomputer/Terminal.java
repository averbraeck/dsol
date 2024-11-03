package nl.tudelft.simulation.examples.dsol.timesharedcomputer;

import org.djutils.event.EventType;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowObject;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The Terminal as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4..
 * <p>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Terminal extends FlowObject<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** SERVICE_TIME is fired on job completion. */
    public static final EventType SERVICE_TIME = new EventType("SERVICE_TIME");

    /** the thinkDelay. */
    private DistContinuous thinkDelay = null;

    /** the jobSize. */
    private DistContinuous jobSize = null;

    /** the job counter for the job number. */
    private int jobCounter = 0;

    /**
     * constructs a new Terminal.
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator
     * @param cpu Station; the destination
     * @param thinkDelay DistContinuous; the delay
     * @param jobSize DistContinuous; in time
     */
    public Terminal(final DevsSimulatorInterface<Double> simulator, final FlowObject<Double> cpu, final DistContinuous thinkDelay,
            final DistContinuous jobSize)
    {
        super("Terminal", simulator);
        this.thinkDelay = thinkDelay;
        this.jobSize = jobSize;
        setDestination(cpu);
        this.releaseEntity(null);
    }

    @Override
    public void receiveEntity(final Entity<Double> entity)
    {
        this.fireTimedEvent(SERVICE_TIME, getSimulator().getSimulatorTime() - ((Job) entity).getCreationTime(),
                getSimulator().getSimulatorTime());
        try
        {
            Object[] args = {entity};
            getSimulator().scheduleEventAbs(getSimulator().getSimulatorTime() + this.thinkDelay.draw(), this, "releaseObject",
                    args);
        }
        catch (SimRuntimeException exception)
        {
            getSimulator().getLogger().always().error(exception);
        }
    }

    @Override
    public synchronized void releaseEntity(final Entity<Double> entity)
    {
        String id = "job:" + this.jobCounter++;
        Job job = new Job(id, this.jobSize, this, getSimulator().getSimulatorTime());
        this.fireTimedEvent(FlowObject.RELEASE_EVENT, 1, getSimulator().getSimulatorTime());
        getDestination().receiveEntity(job);
    }
}
