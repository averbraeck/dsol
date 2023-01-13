package nl.tudelft.simulation.examples.dsol.timesharedcomputer;

import org.djutils.event.EventType;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.flow.Station;
import nl.tudelft.simulation.dsol.formalisms.flow.StationInterface;
import nl.tudelft.simulation.dsol.simulators.DevsxSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * The Terminal as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4..
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Terminal extends Station<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** SERVICE_TIME is fired on job completion. */
    public static final EventType SERVICE_TIME = new EventType("SERVICE_TIME");

    /** the thinkDelay. */
    private DistContinuous thinkDelay = null;

    /** the jobSize. */
    private DistContinuous jobSize = null;

    /**
     * constructs a new Terminal.
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     * @param cpu StationInterface; the destination
     * @param thinkDelay DistContinuous; the delay
     * @param jobSize DistContinuous; in time
     */
    public Terminal(final DevsxSimulatorInterface<Double> simulator, final StationInterface cpu,
            final DistContinuous thinkDelay, final DistContinuous jobSize)
    {
        super("Terminal", simulator);
        this.thinkDelay = thinkDelay;
        this.jobSize = jobSize;
        this.setDestination(cpu);
        this.releaseObject(null);
    }

    /** {@inheritDoc} */
    @Override
    public void receiveObject(final Object object)
    {
        this.fireTimedEvent(SERVICE_TIME, this.simulator.getSimulatorTime() - ((Job) object).getCreationTime(),
                this.simulator.getSimulatorTime());
        try
        {
            Object[] args = {object};
            this.simulator.scheduleEventAbs(this.simulator.getSimulatorTime() + this.thinkDelay.draw(), this, "releaseObject",
                    args);
        }
        catch (SimRuntimeException exception)
        {
            getSimulator().getLogger().always().error(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void releaseObject(final Object object)
    {
        Job job = new Job(this.jobSize, this, this.simulator.getSimulatorTime());
        this.fireTimedEvent(StationInterface.RELEASE_EVENT, 1, this.simulator.getSimulatorTime());
        super.destination.receiveObject(job);
    }
}
