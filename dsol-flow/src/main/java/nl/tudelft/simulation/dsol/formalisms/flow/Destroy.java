package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimTally;

/**
 * The Destroy flow block where entities will be destroyed from the model.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author Peter Jacobs
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type itself to be able to implement a comparator on the simulation time.
 */
public class Destroy<T extends Number & Comparable<T>> extends FlowBlock<T, Destroy<T>>
{
    /** TIME_IN_SYSTEM_EVENT is fired when an entity leaves the system. */
    public static final EventType TIME_IN_SYSTEM_EVENT = new EventType(new MetaData("TIME_IN_SYSTEM_EVENT", "Time in system",
            new ObjectDescriptor("Time in system", "time in system", Double.class)));

    /** Tally statistic for the time-in-sysem of the destroyed entities. */
    private SimTally<T> timeInSystemStatistic = null;

    /**
     * Construct a Destroy flow block.
     * @param id the id of the Destroy flow block
     * @param simulator the simulator
     */
    public Destroy(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        super.receiveEntity(entity);
        fireTimedEvent(TIME_IN_SYSTEM_EVENT,
                getSimulator().getSimulatorTime().doubleValue() - entity.getCreationTime().doubleValue(),
                getSimulator().getSimulatorTime());
    }

    /**
     * Turn on the default statistics for this flow block.
     * @return the Destroy instance for method chaining
     */
    public Destroy<T> setDefaultStatistics()
    {
        if (!hasDefaultStatistics())
        {
            super.setDefaultFlowBlockStatistics();
            this.timeInSystemStatistic = new SimTally<>("Destroy.TimeInSystem:" + getBlockNumber(),
                    getId() + " entity time in system", getSimulator().getModel(), this, TIME_IN_SYSTEM_EVENT);
            this.timeInSystemStatistic.initialize();
        }
        return this;
    }

    /**
     * Return whether statistics are turned on for this Destroy block.
     * @return whether statistics are turned on for this Destroy block.
     */
    public boolean hasDefaultStatistics()
    {
        return this.timeInSystemStatistic != null;
    }

    /**
     * Return the statistic for the time-in-sysem of the destroyed entities.
     * @return the statistic for the time-in-sysem of the destroyed entities, can be null if no statistics are calculated
     */
    public SimTally<T> getTimeInSystemStatistic()
    {
        return this.timeInSystemStatistic;
    }

}
