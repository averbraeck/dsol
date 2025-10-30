package nl.tudelft.simulation.dsol.demo.des.mm1.step08;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistExponential;

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
class DesQueueingModel8 extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** the queue in which entities might be placed. */
    private List<QueueEntry<Entity>> queue = new ArrayList<>();;

    /** the entity counter to provide a unique number. */
    private int entityCounter = 0;

    /** the capacity of the server. */
    private int capacity = 1;

    /** the capacity of the server. */
    private int batchSize = 1;

    /** the number of busy units. */
    private int busy = 0;

    /** the distribution of the interarrival time between entities. */
    private DistContinuous interArrivalTime;

    /** the distribution of the processing time of entities. */
    private DistContinuous processingTime;

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
     */
    public DesQueueingModel8(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
        try
        {
            InputParameterMap generatorMap = new InputParameterMap("generator", "Generator", "Generator", 1.0);
            generatorMap
                    .add(new InputParameterDouble("intervalTime", "Average interval time", "Average interval time", 1.0, 1.0));
            generatorMap.add(new InputParameterDouble("startTime", "Generator start time", "Generator start time", 0.0, 2.0));
            generatorMap.add(new InputParameterInteger("batchSize", "Batch size", "batch size", 1, 3.0));
            this.inputParameterMap.add(generatorMap);
            InputParameterMap resourceMap = new InputParameterMap("resource", "Resource", "Resource", 2.0);
            resourceMap.add(new InputParameterInteger("capacity", "Resource capacity", "Resource capacity", 1, 1.0));
            resourceMap.add(new InputParameterDouble("serviceTime", "Average service time", "Average service time", 0.9, 2.0));
            this.inputParameterMap.add(resourceMap);
        }
        catch (InputParameterException e)
        {
            throw new SimRuntimeException("Error defining parameters for the model", e);
        }
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        double startTime = 0.0;
        try
        {
            this.capacity = (Integer) getInputParameter("resource.capacity");
            this.batchSize = (Integer) getInputParameter("generator.batchSize");
            startTime = (Double) getInputParameter("generator.startTime");
            double iat = (Double) getInputParameter("generator.intervalTime");
            this.interArrivalTime = new DistExponential(getDefaultStream(), iat);
            double st = (Double) getInputParameter("resource.serviceTime");
            this.processingTime = new DistExponential(getDefaultStream(), st);
        }
        catch (InputParameterException e)
        {
            throw new SimRuntimeException("Error retrieving parameters for the model", e);
        }

        this.tallyTimeInQueue = new SimTally<>("tQ", "Time in queue", this);
        this.tallyTimeInSystem = new SimTally<>("tS", "Time in system", this);
        this.persistentQueueLength = new SimPersistent<>("lQ", "Queue length", this);
        this.persistentUtilization = new SimPersistent<>("Ut", "Server utilization", this);

        this.simulator.scheduleEventRel(startTime, () -> generate());
    }

    /**
     * Generate one or more entities, based on batchSize.
     */
    protected void generate()
    {
        double time = getSimulator().getSimulatorTime();
        for (int i = 0; i < this.batchSize; i++)
        {
            Entity entity = new Entity(this.entityCounter++, time);
            System.out.println(String.format("Time: %.3f. Generated entity %d", time, entity.getId()));
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
                System.out.println(String.format("Time: %.3f. Entity %d entered the queue. Length: %d", time, entity.getId(),
                        this.queue.size()));
            }
        }
        this.simulator.scheduleEventRel(this.interArrivalTime.draw(), () -> generate());
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
        this.simulator.scheduleEventRel(this.processingTime.draw(), () -> endProcess(entity));
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
        System.out.println(String.format("Time: %.3f. Entity %d left the system. Time in system: %.3f", time, entity.getId(),
                time - entity.getCreateTime()));
    }

}
