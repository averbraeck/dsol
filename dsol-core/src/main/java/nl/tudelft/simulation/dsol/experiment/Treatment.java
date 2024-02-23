package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The Treatment is the interface that indicates that you can retrieve the simulation start time, end time, and warmup period,
 * as well as an id and description of the simulation run or replication. <br>
 * <br>
 * Copyright (c) 2023-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type to be able to implement a comparator on the simulation time.
 */
public interface Treatment<T extends Number & Comparable<T>> extends Serializable
{
    /**
     * Return the RunControl object that contains the treatment information.
     * @return RunControl; the RunControl object that contains the treatment information
     */
    RunControl<T> getRunControl();

    /**
     * Return the id of this replication.
     * @return String; the id of this replication
     */
    default String getId()
    {
        return getRunControl().getId();
    }

    /**
     * Sets the description of this replication.
     * @param description String; the description of this replication
     */
    default void setDescription(final String description)
    {
        getRunControl().setDescription(description);
    }

    /**
     * Return the description of this replication.
     * @return String; the description of this replication
     */
    default String getDescription()
    {
        return getRunControl().getDescription();
    }

    /**
     * Return the run length of this replication in relative units.
     * @return T; the runLength.
     */
    default T getRunLength()
    {
        return SimTime.minus(getEndTime(), getStartTime());
    }

    /**
     * Return the warmup period of this replication in relative units.
     * @return T; the warmup period.
     */
    default T getWarmupPeriod()
    {
        return SimTime.minus(getWarmupTime(), getStartTime());
    }

    /**
     * Return the absolute start time of the simulation.
     * @return T; the absolute start time of the simulation
     */
    default T getStartTime()
    {
        return getRunControl().getStartTime();
    }

    /**
     * Return the absolute end time of the simulation.
     * @return T; the absolute end time of the simulation
     */
    default T getEndTime()
    {
        return getRunControl().getEndTime();
    }

    /**
     * Return the absolute moment when the warmup event will take place.
     * @return T; the absolute moment when the warmup event will take place
     */
    default T getWarmupTime()
    {
        return getRunControl().getWarmupTime();
    }

}
