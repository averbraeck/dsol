package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.ArrayList;
import java.util.List;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;

/**
 * The Delay object is a flow object that delays an entity for a given time. After the time delay, the entity is released to the
 * next flow object. During the time delay, the entity is held in the delay object.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Delay<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the distribution defining the delay. */
    private DistContinuousSimulationTime<T> delayDistribution;

    /** list of the current entities in delay. */
    private final List<Entity<T>> delayedEntityList = new ArrayList<>();

    /** persistent statistic for the number of delayed entities. */
    private SimPersistent<T> numberDelayedStatistic = null;

    /** NUMBER_DELAYED_EVENT is fired after receiving and after releasing an entity. */
    public static final EventType NUMBER_DELAYED_EVENT = new EventType(new MetaData("NUMBER_DELAYED_EVENT",
            "Number of delayed entities)", new ObjectDescriptor("numberDelayed", "number of delayed entities", Integer.class)));

    /**
     * Construct a new Delay flow block.
     * @param id String; the id of the Delay flow object
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     */
    public Delay(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Provide the Delay block with a new delay distribution.
     * @param delayDistribution the new delay distribution
     * @return the Delay instance for method chaining
     */
    public Delay<T> setDelayDistribution(final DistContinuousSimulationTime<T> delayDistribution)
    {
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
        return this;
    }

    /**
     * Turn on the default statistics for this flow block.
     * @return the Delay instance for method chaining
     */
    public Delay<T> setDefaultStatistics()
    {
        super.setDefaultFlowObjectStatistics();
        this.numberDelayedStatistic = new SimPersistent<>(getId() + " number of entities in delay block",
                getSimulator().getModel(), this, Delay.NUMBER_DELAYED_EVENT);
        this.numberDelayedStatistic.initialize();
        fireTimedEvent(NUMBER_DELAYED_EVENT, getDelayedEntityList().size(), getSimulator().getSimulatorTime());
        return this;
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        Throw.whenNull(this.delayDistribution, "received entity, but delayDistribution is null");
        super.receiveEntity(entity);
        this.delayedEntityList.add(entity);
        fireTimedEvent(NUMBER_DELAYED_EVENT, getDelayedEntityList().size(), getSimulator().getSimulatorTime());
        getSimulator().scheduleEventRel(this.delayDistribution.draw(), this, "releaseEntity", new Object[] {entity});
    }

    @Override
    protected synchronized void releaseEntity(final Entity<T> entity)
    {
        this.delayedEntityList.remove(entity);
        fireTimedEvent(NUMBER_DELAYED_EVENT, getDelayedEntityList().size(), getSimulator().getSimulatorTime());
        super.releaseEntity(entity);
    }

    /**
     * Return the current delay distribution.
     * @return the current delay distribution, can be null if not yet set.
     */
    public DistContinuousSimulationTime<T> getDelayDistribution()
    {
        return this.delayDistribution;
    }

    /**
     * Return the list of entities that are currently in the Delay block.
     * @return the list of delayed entities
     */
    public List<Entity<T>> getDelayedEntityList()
    {
        return this.delayedEntityList;
    }

    /**
     * Return the number of entities that are currently in the Delay block.
     * @return the number of entities that are currently in the Delay block
     */
    public int getNumberDelayedEntities()
    {
        return this.delayedEntityList.size();
    }

    /**
     * Return whether statistics are turned on for this Delay block.
     * @return whether statistics are turned on for this Delay block.
     */
    public boolean hasDefaultStatistics()
    {
        return this.numberDelayedStatistic != null;
    }

    /**
     * Return the statistic for the number of concurrent delayed entities.
     * @return the statistic for the number of concurrent delayed entities, can be null if no statistics are calculated
     */
    public SimPersistent<T> getNumberDelayedStatistic()
    {
        return this.numberDelayedStatistic;
    }

}
