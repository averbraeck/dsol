package nl.tudelft.simulation.dsol.demo.des.mm1.step02;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
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
class DesQueueingModel2
{
    /** the simulator. */
    private final DevsSimulatorInterface<Double> simulator;

    /** the queue in which entities might be placed. */
    private List<Entity> queue = new ArrayList<>();;

    /** the entity counter to provide a unique number. */
    private int entityCounter = 0;

    /** the capacity of the server. */
    private int capacity = 1;

    /** the number of busy units. */
    private int busy = 0;

    /** the distribution of the interarrival time between entities. */
    private final DistContinuous interArrivalTime;

    /**
     * Constructor for the model. This provides the distribution functions and the simulator to the model.
     * @param simulator the simulator on which to schedule new events
     * @param interArrivalTime the distribution of the interarrival time between entities
     */
    protected DesQueueingModel2(final DevsSimulatorInterface<Double> simulator, final DistContinuous interArrivalTime)
    {
        this.simulator = simulator;
        this.interArrivalTime = interArrivalTime;
    }

    /**
     * Generate an entity.
     */
    protected void generate()
    {
        double time = this.simulator.getSimulatorTime();
        Entity entity = new Entity(this.entityCounter++, time);
        if (this.capacity - this.busy >= 1)
        {
            // process
            startProcess(entity);
        }
        else
        {
            // queue
            this.queue.add(entity);
        }
        this.simulator.scheduleEventRel(this.interArrivalTime.draw(), this, "generate", null);
    }

    /**
     * Start the processing of an entity by the server.
     * @param entity the entity to be processed
     */
    protected void startProcess(final Entity entity)
    {
        // to be implemented
    }
}
