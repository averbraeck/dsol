package nl.tudelft.simulation.dsol.swing.gui.test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsxSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Simple M/M/1 queuing model, which can be changed into a X/X/c model by changing the parameters.
 * <p>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1Model extends AbstractDSOLModel<Double, DevsxSimulatorInterface<Double>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** resource capacity. */
    private int capacity = 1;

    /** busy units. */
    private int busy = 0;

    /** the stream. */
    private StreamInterface stream = new MersenneTwister(12L);

    /** inter-arrival time. */
    private DistContinuousSimulationTime<Double> interarrivalTime =
            new DistContinuousSimulationTime.TimeDouble(new DistExponential(this.stream, 1.0));

    /** processing time. */
    private DistContinuousSimulationTime<Double> processingTime =
            new DistContinuousSimulationTime.TimeDouble(new DistTriangular(this.stream, 0.8, 0.9, 1.1));

    /** queue of waiting entities. */
    private List<QueueEntry<Entity>> queue = new ArrayList<QueueEntry<Entity>>();

    /** entity counter for id. */
    private int entityCounter = 0;

    /** statistics for the utilization. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> persistentUtilization;

    /** statistics for the queue length. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimPersistent<Double> persistentQueueLength;

    /** statistics for the time in queue. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimTally<Double> tallyTimeInQueue;

    /** statistics for the time in system. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    SimTally<Double> tallyTimeInSystem;

    /**
     * @param simulator DEVSSimulator&lt;Double&gt;;
     */
    public MM1Model(final DevsxSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        try
        {
            this.persistentUtilization = new SimPersistent<Double>("utilization", this.simulator);
            this.persistentQueueLength = new SimPersistent<Double>("queue length", this.simulator);
            this.tallyTimeInQueue = new SimTally<Double>("time in queue", this.simulator);
            this.tallyTimeInSystem = new SimTally<Double>("time in system", this.simulator);
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }

        generate();
    }

    /**
     * Generate new items. Reschedules itself.
     * @throws SimRuntimeException on simulation error
     */
    protected void generate() throws SimRuntimeException
    {
        Entity entity = new Entity(this.entityCounter++, this.simulator.getSimulatorTime());
        System.out.println("Generated: " + entity);
        CategoryLogger.always().info("Generated: " + entity);
        synchronized (this.queue)
        {
            if (this.capacity - this.busy >= 1)
            {
                // process
                startProcess(entity);
            }
            else
            {
                // queue
                this.persistentQueueLength.register(getSimulator().getSimulatorTime(), this.queue.size());
                this.queue.add(new QueueEntry<Entity>(entity, this.simulator.getSimulatorTime()));
                System.out.println("In Queue: " + entity);
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
        this.persistentUtilization.register(getSimulator().getSimulatorTime(), this.busy);
        this.busy++;
        this.persistentUtilization.register(getSimulator().getSimulatorTime(), this.busy);
        this.simulator.scheduleEventRel(this.processingTime.draw(), this, "endProcess", new Object[] {entity});
        this.tallyTimeInQueue.register(this.simulator.getSimulatorTime() - entity.getCreateTime());
    }

    /**
     * @param entity Entity; entity to stop processing
     * @throws SimRuntimeException on simulation error
     */
    protected void endProcess(final Entity entity) throws SimRuntimeException
    {
        System.out.println("End Process: " + entity);
        this.persistentUtilization.register(getSimulator().getSimulatorTime(), this.busy);
        this.busy--;
        this.persistentUtilization.register(getSimulator().getSimulatorTime(), this.busy);
        if (!this.queue.isEmpty())
        {
            this.persistentQueueLength.register(getSimulator().getSimulatorTime(), this.queue.size());
            startProcess(this.queue.remove(0).getEntity());
        }
        this.tallyTimeInSystem.register(this.simulator.getSimulatorTime() - entity.getCreateTime());
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
