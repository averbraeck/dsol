package nl.tudelft.simulation.dsol.demo.event.mm1;

import java.util.ArrayList;
import java.util.List;

import org.djutils.logger.CategoryLogger;

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
 * Simple M/M/1 queuing model, which can be changed into a X/X/c model by changing the parameters.
 * <p>
 * Copyright (c) 2020- 2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1Model extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** resource capacity. */
    private int capacity = 1;

    /** busy units. */
    private int busy = 0;

    /** batch size. */
    private int batchSize = 1;

    /** inter-arrival time. */
    private DistContinuous interarrivalTime;

    /** processing time. */
    private DistContinuous processingTime;

    /** queue of waiting entities. */
    private List<QueueEntry<Entity>> queue = new ArrayList<QueueEntry<Entity>>();

    /** entity counter for id. */
    private int entityCounter = 0;

    /** statistics for the utilization. */
    SimPersistent<Double> persistentUtilization;

    /** statistics for the queue length. */
    SimPersistent<Double> persistentQueueLength;

    /** statistics for the time in queue. */
    SimTally<Double> tallyTimeInQueue;

    /** statistics for the time in system. */
    SimTally<Double> tallyTimeInSystem;

    /**
     * @param simulator DevsSimulator&lt;Double&gt;; the simulator
     * @throws InputParameterException on parameter error
     */
    public MM1Model(final DevsSimulatorInterface<Double> simulator) throws InputParameterException
    {
        super(simulator);
        InputParameterMap generatorMap = new InputParameterMap("generator", "Generator", "Generator", 1.0);
        generatorMap.add(new InputParameterDouble("intervalTime", "Average interval time", "Average interval time", 1.0, 1.0));
        generatorMap.add(new InputParameterDouble("startTime", "Generator start time", "Generator start time", 0.0, 2.0));
        generatorMap.add(new InputParameterInteger("batchSize", "Batch size", "batch size", 1, 3.0));
        this.inputParameterMap.add(generatorMap);
        InputParameterMap resourceMap = new InputParameterMap("resource", "Resource", "Resource", 2.0);
        resourceMap.add(new InputParameterInteger("capacity", "Resource capacity", "Resource capacity", 1, 1.0));
        resourceMap.add(new InputParameterDouble("serviceTime", "Average service time", "Average service time", 0.9, 2.0));
        this.inputParameterMap.add(resourceMap);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        this.persistentUtilization = new SimPersistent<Double>("utilization", this);
        this.persistentUtilization.register(0.0, 0.0);
        this.persistentQueueLength = new SimPersistent<Double>("queue length", this);
        this.persistentQueueLength.register(0.0, 0.0);
        this.tallyTimeInQueue = new SimTally<Double>("time in queue", this);
        this.tallyTimeInSystem = new SimTally<Double>("time in system", this);

        try
        {
            this.capacity = (Integer) getInputParameter("resource.capacity");
            this.batchSize = (Integer) getInputParameter("generator.batchSize");
            double startTime = (Double) getInputParameter("generator.startTime");
            double iat = (Double) getInputParameter("generator.intervalTime");
            this.interarrivalTime = new DistExponential(getDefaultStream(), iat);
            double st = (Double) getInputParameter("resource.serviceTime");
            this.processingTime = new DistExponential(getDefaultStream(), st);
            getSimulator().scheduleEventRel(startTime, this, "generate", null);
        }
        catch (InputParameterException e)
        {
            throw new SimRuntimeException(e);
        }
    }

    /**
     * Generate new items. Reschedules itself.
     * @throws SimRuntimeException on simulation error
     */
    protected void generate() throws SimRuntimeException
    {
        double time = this.simulator.getSimulatorTime();
        for (int i = 0; i < this.batchSize; i++)
        {
            Entity entity = new Entity(this.entityCounter++, time);
            System.out.println("Generated: " + entity);
            CategoryLogger.always().info("Generated: " + entity);
            synchronized (this.queue)
            {
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
                    System.out.println("In Queue: " + entity);
                }
            }
        }
        this.simulator.scheduleEventRel(this.interarrivalTime.draw(), this, "generate", null);
    }

    /**
     * @param entity Entity; entity to process
     * @throws SimRuntimeException on simulation error
     */
    protected void startProcess(final Entity entity) throws SimRuntimeException
    {
        System.out.println("Start Process: " + entity);
        double time = getSimulator().getSimulatorTime();
        this.busy++;
        this.persistentUtilization.register(time, this.busy);
        this.simulator.scheduleEventRel(this.processingTime.draw(), this, "endProcess", new Object[] {entity});
    }

    /**
     * @param entity Entity; entity to stop processing
     * @throws SimRuntimeException on simulation error
     */
    protected void endProcess(final Entity entity) throws SimRuntimeException
    {
        System.out.println("End Process: " + entity);
        double time = getSimulator().getSimulatorTime();
        this.busy--;
        this.persistentUtilization.register(time, this.busy);
        if (!this.queue.isEmpty())
        {
            QueueEntry<Entity> queueEntry = this.queue.remove(0);
            this.persistentQueueLength.register(time, this.queue.size());
            this.tallyTimeInQueue.register(time - queueEntry.getQueueInTime());
            startProcess(queueEntry.getEntity());
        }
        this.tallyTimeInSystem.register(time - entity.getCreateTime());
    }

    /******************************************************************************************************/
    protected class Entity
    {
        /** time of creation for statistics. */
        private final double createTime;

        /** id number. */
        private final int id;

        /**
         * @param id int; entity id number
         * @param createTime double; time of creation for statistics
         */
        public Entity(final int id, final double createTime)
        {
            super();
            this.id = id;
            this.createTime = createTime;
        }

        /**
         * @return time of creation for statistics
         */
        public double getCreateTime()
        {
            return this.createTime;
        }

        /**
         * @return entity id number
         */
        public int getId()
        {
            return this.id;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Entity [createTime=" + this.createTime + ", id=" + this.id + "]";
        }
    }

    /******************************************************************************************************/

    /**
     * Queue entry of an entity of type E.
     * @param <E> the entity type in the queue
     */
    protected class QueueEntry<E>
    {
        /** time of queue entry for statistics. */
        private final double queueInTime;

        /** entity in the queue. */
        private final E entity;

        /**
         * @param entity E; the entity to insert in the queue
         * @param queueInTime double; the time it gets into the queue
         */
        public QueueEntry(final E entity, final double queueInTime)
        {
            super();
            this.entity = entity;
            this.queueInTime = queueInTime;
        }

        /**
         * @return queueInTime
         */
        public double getQueueInTime()
        {
            return this.queueInTime;
        }

        /**
         * @return entity
         */
        public E getEntity()
        {
            return this.entity;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "QueueEntry [queueInTime=" + this.queueInTime + ", entity=" + this.entity + "]";
        }
    }

}
