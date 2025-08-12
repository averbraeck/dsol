package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The Treatment is the interface that indicates that you can retrieve the simulation start time, end time, and warmup period,
 * as well as an id and description of the simulation run or replication. <br>
 * <br>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type to be able to implement a comparator on the simulation time.
 */
public interface Treatment<T extends Number & Comparable<T>> extends Serializable
{
    /**
     * Return the id of this replication.
     * @return the id of this replication
     */
    String getId();

    /**
     * Sets the description of this replication.
     * @param description the description of this replication
     */
    void setDescription(final String description);

    /**
     * Return the description of this replication.
     * @return the description of this replication
     */
    String getDescription();

    /**
     * Return the run length of this replication in relative units.
     * @return the runLength.
     */
    default T getRunLength()
    {
        return SimTime.minus(getEndTime(), getStartTime());
    }

    /**
     * Return the warmup period of this replication in relative units.
     * @return the warmup period.
     */
    default T getWarmupPeriod()
    {
        return SimTime.minus(getWarmupTime(), getStartTime());
    }

    /**
     * Return the absolute start time of the simulation.
     * @return the absolute start time of the simulation
     */
    T getStartTime();

    /**
     * Return the absolute end time of the simulation.
     * @return the absolute end time of the simulation
     */
    T getEndTime();

    /**
     * Return the absolute moment when the warmup event will take place.
     * @return the absolute moment when the warmup event will take place
     */
    T getWarmupTime();

}
