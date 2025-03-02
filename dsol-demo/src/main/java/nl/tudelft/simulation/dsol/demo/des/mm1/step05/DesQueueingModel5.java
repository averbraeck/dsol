package nl.tudelft.simulation.dsol.demo.des.mm1.step05;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * Model with a generator that generates entities.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
class DesQueueingModel5 extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the queue in which entities might be placed. */
    private List<QueueEntry<Entity>> queue = new ArrayList<>();;

    /** the entity counter to provide a unique number. */
    private int entityCounter = 0;

    /** the capacity of the server. */
    private int capacity = 1;

    /** the number of busy units. */
    private int busy = 0;

    /** the distribution of the interarrival time between entities. */
    private final DistContinuous interArrivalTime;

    /** the distribution of the processing time of entities. */
    private final DistContinuous processingTime;

    /** statistic to store the time entities spend in the queue. */
    private SimTally<Double> tallyTimeInQueue;

    /** statistic to store the time entities spend in the system. */
    private SimTally<Double> tallyTimeInSystem;

    /** statistic to store the utilization of the server. */
    private SimPersistent<Double> persistentUtilization;

    /** statistic to store the length of the queue. */
    private SimPersistent<Double> persistentQueueLength;

    /**
     * Constructor for the model. This provides the distribution functions and the simulator to the model.
     * @param simulator the simulator on which to schedule new events
     * @param interArrivalTime the distribution of the interarrival time between entities
     * @param processingTime the distribution of the processing time of entities
     */
    public DesQueueingModel5(final DevsSimulatorInterface<Double> simulator, final DistContinuous interArrivalTime,
            final DistContinuous processingTime)
    {
        super(simulator);
        this.interArrivalTime = interArrivalTime;
        this.processingTime = processingTime;
    }

    @Override
    public void constructModel()
    {
        this.tallyTimeInQueue = new SimTally<>("Time in queue", this);
        this.tallyTimeInSystem = new SimTally<>("Time in system", this);
        this.persistentQueueLength = new SimPersistent<>("Queue length", this);
        this.persistentUtilization = new SimPersistent<>("Server utilization", this);

        this.simulator.scheduleEventRel(this.interArrivalTime.draw(), this, "generate", null);
    }

    /**
     * Generate an entity.
     */
    protected void generate()
    {
        double time = getSimulator().getSimulatorTime();
        Entity entity = new Entity(this.entityCounter++, time);
        if (this.capacity - this.busy >= 1)
        {
            // process
            this.tallyTimeInQueue.register(0.0); // no waiting
            startProcess(entity);
        }
        else
        {
            // queue
            this.queue.add(new QueueEntry<Entity>(entity, time));
            this.persistentQueueLength.register(time, this.queue.size());
        }
        this.simulator.scheduleEventRel(this.interArrivalTime.draw(), this, "generate", null);
    }

    /**
     * Start the processing of an entity by the server.
     * @param entity the entity to be processed
     */
    protected void startProcess(final Entity entity)
    {
        double time = getSimulator().getSimulatorTime();
        this.busy++;
        this.persistentUtilization.register(time, this.busy);
        this.simulator.scheduleEventRel(this.processingTime.draw(), this, "endProcess", new Object[] {entity});
    }

    /**
     * End the processing of an entity by the server.
     * @param entity the entity that is being processed, and should leave the server
     */
    protected void endProcess(final Entity entity)
    {
        double time = getSimulator().getSimulatorTime();
        this.busy--;
        this.persistentUtilization.register(time, this.busy);
        if (!this.queue.isEmpty())
        {
            QueueEntry<Entity> nextQueueEntry = this.queue.remove(0);
            this.persistentQueueLength.register(time, this.queue.size());
            this.tallyTimeInQueue.register(time - nextQueueEntry.getQueueInTime());
            startProcess(nextQueueEntry.getEntity());
        }
        this.tallyTimeInSystem.register(time - entity.getCreateTime());
    }

}
