package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The interface for a run control as used in the Experiment and in the Replication.
 * <p>
 * Copyright (c) 2021-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type to be able to implement a comparator on the simulation time.
 */
public interface RunControlInterface<T extends Number & Comparable<T>> extends Serializable
{
    /**
     * Return the id of this replication.
     * @return String; the id of this replication
     */
    String getId();

    /**
     * Sets the description of this replication.
     * @param description String; the description of this replication
     */
    void setDescription(String description);

    /**
     * Return the description of this replication.
     * @return String; the description of this replication
     */
    String getDescription();

    /**
     * Return the run length of this replication in relative units.
     * @return R; the runLength.
     */
    default T getRunLength()
    {
        return SimTime.minus(getEndSimTime(), getStartSimTime());
    }

    /**
     * Return the warmup period of this replication in relative units.
     * @return R; the warmup period.
     */
    default T getWarmupPeriod()
    {
        return SimTime.minus(getWarmupSimTime(), getStartSimTime());
    }

    /**
     * Return the absolute start time of the simulation.
     * @return A; the absolute start time of the simulation
     */
    default T getStartTime()
    {
        return getStartSimTime();
    }

    /**
     * Return the absolute end time of the simulation.
     * @return A; the absolute end time of the simulation
     */
    default T getEndTime()
    {
        return getEndSimTime();
    }

    /**
     * Return the absolute moment when the warmup event will take place.
     * @return A; the absolute moment when the warmup event will take place
     */
    default T getWarmupTime()
    {
        return getWarmupSimTime();
    }

    /**
     * Return the absolute start time of the simulation as a SimTime object.
     * @return T; the absolute start time of the simulation as a SimTime object
     */
    T getStartSimTime();

    /**
     * Return the absolute end time of the simulation as a SimTime object.
     * @return T; the absolute end time of the simulation as a SimTime object
     */
    T getEndSimTime();

    /**
     * Return the absolute warmup time of the simulation as a SimTime object.
     * @return T; the absolute warmup time of the simulation as a SimTime object
     */
    T getWarmupSimTime();
}
