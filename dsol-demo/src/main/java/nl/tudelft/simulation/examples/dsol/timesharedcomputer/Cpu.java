package nl.tudelft.simulation.examples.dsol.timesharedcomputer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.formalisms.flow.Entity;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowObject;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The CPU example as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4. .
 * <p>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Cpu extends FlowObject<Double> implements Locatable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** UTILIZATION_EVENT are fired on utilization. */
    public static final EventType UTILIZATION_EVENT = new EventType(new MetaData("UTILIZATION_EVENT",
            "Utilization change", new ObjectDescriptor("utilization", "Current utilization", Double.class)));

    /** QUEUE_LENGTH_EVENT is fired on changes in the Queue length. */
    public static final EventType QUEUE_LENGTH_EVENT = new EventType(new MetaData("QUEUE_LENGTH_EVENT",
            "Queue length change", new ObjectDescriptor("queueLength", "New queue length", Integer.class)));

    /** QUANTUM is the QUANTUM of the CPU. */
    public static final double QUANTUM = 0.1;

    /** SWAP is the swap of this cpu. */
    public static final double SWAP = 0.015;

    /** IDLE defines the IDLE state. */
    public static final boolean IDLE = true;

    /** BUSY defines the BUSY state. */
    public static final boolean BUSY = false;

    /** status of the CPU. */
    private boolean status = IDLE;

    /** queue is the queue of waiting jobs. */
    private List<Object> queue = Collections.synchronizedList(new ArrayList<Object>());

    /** the location. */
    private OrientedPoint3d location = new OrientedPoint3d(-90, 0, 0);

    /**
     * constructs a new CPU.
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; a devs simulator
     */
    public Cpu(final DevsSimulatorInterface<Double> simulator)
    {
        super("CPU", simulator);
        this.fireTimedEvent(UTILIZATION_EVENT, 0.0, simulator.getSimulatorTime());
    }

    /**
     * returns the queue.
     * @return List the queue
     */
    public List<Object> getQueue()
    {
        return this.queue;
    }

    /** {@inheritDoc} */
    @Override
    public void receiveEntity(final Entity<Double> entity)
    {
        this.queue.add(entity);
        this.fireTimedEvent(QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
        if (this.status == IDLE)
        {
            try
            {
                this.next();
            }
            catch (SimRuntimeException exception)
            {
                getSimulator().getLogger().always().error(exception);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected synchronized void releaseEntity(final Entity<Double> entity)
    {
        this.status = IDLE;
        this.fireTimedEvent(UTILIZATION_EVENT, 0.0, getSimulator().getSimulatorTime());
        ((Job) entity).getOwner().receiveEntity(entity);
        try
        {
            this.next();
        }
        catch (SimRuntimeException exception)
        {
            getSimulator().getLogger().always().error(exception);
        }
    }

    /**
     * services the next job.
     * @throws SimRuntimeException on simulation failure
     */
    private void next() throws SimRuntimeException
    {
        if (this.queue.size() > 0)
        {
            this.status = BUSY;
            this.fireTimedEvent(UTILIZATION_EVENT, 1.0, getSimulator().getSimulatorTime());
            Job job = (Job) this.queue.remove(0);
            this.fireTimedEvent(QUEUE_LENGTH_EVENT, this.queue.size(), getSimulator().getSimulatorTime());
            if (job.getServiceTime() > QUANTUM)
            {
                job.setServiceTime(job.getServiceTime() - QUANTUM);
                Object[] args = {job};
                getSimulator().scheduleEventAbs(getSimulator().getSimulatorTime() + QUANTUM + SWAP, this, "receiveObject", args);
                getSimulator().scheduleEventAbs(getSimulator().getSimulatorTime() + QUANTUM + SWAP, this, "next", null);
            }
            else
            {
                Object[] args = {job};
                getSimulator().scheduleEventAbs(getSimulator().getSimulatorTime() + job.getServiceTime() + SWAP, this, "releaseObject",
                        args);
            }
        }
        else
        {
            this.status = IDLE;
            this.fireTimedEvent(UTILIZATION_EVENT, 0.0, getSimulator().getSimulatorTime());
        }
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation()
    {
        return this.location;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(0, 0, 0);
    }
}
