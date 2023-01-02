package nl.tudelft.simulation.dsol.experiment;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;

import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * The interface for a replication. Several types of replications exist, such as the SingleReplication and the
 * ExperimentReplication.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a> relative types are the same.
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 */
public interface ReplicationInterface<T extends Number & Comparable<T>> extends RunControlInterface<T>, Contextualized
{
    /** START_REPLICATION_EVENT is fired when a replication is started. */
    EventType START_REPLICATION_EVENT = new EventType(new MetaData("START_REPLICATION_EVENT", "Replication started"));

    /** END_REPLICATION_EVENT is fired when a replication is finished. */
    EventType END_REPLICATION_EVENT = new EventType(new MetaData("END_REPLICATION_EVENT", "Replication ended"));

    /** WARMUP_EVENT is fired when the warmup period is over, and statistics have to be reset. */
    EventType WARMUP_EVENT = new EventType(new MetaData("WARMUP_EVENT", "warmup time"));

    /**
     * Return the RunControl object.
     * @return RunControlInterface the RunControl object
     */
    RunControlInterface<T> getRunControl();

    /** {@inheritDoc} */
    @Override
    default String getId()
    {
        return getRunControl().getId();
    }

    /** {@inheritDoc} */
    @Override
    default void setDescription(final String description)
    {
        getRunControl().setDescription(description);
    }

    /** {@inheritDoc} */
    @Override
    default String getDescription()
    {
        return getRunControl().getDescription();
    }

    /** {@inheritDoc} */
    @Override
    default T getStartSimTime()
    {
        return getRunControl().getStartSimTime();
    }

    /** {@inheritDoc} */
    @Override
    default T getEndSimTime()
    {
        return getRunControl().getEndSimTime();
    }

    /** {@inheritDoc} */
    @Override
    default T getWarmupSimTime()
    {
        return getRunControl().getWarmupSimTime();
    }

}
