package nl.tudelft.simulation.dsol.experiment;

import java.util.Objects;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * RunControl is a data object that contains off-line run control information. It can be fed to an Experiment or a Replication
 * to set the run control parameters for a simulation run.
 * <p>
 * Copyright (c) 2021-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a> relative types are the same.
 * @param <T> the simulation time type to be able to implement a comparator on the simulation time.
 */
public class RunControl<T extends Number & Comparable<T>> implements Treatment<T>
{
    /** */
    private static final long serialVersionUID = 20210409L;

    /** the id of the replication. */
    private final String id;

    /** the description of the replication (if not set, the id will be used). */
    private String description;

    /** the start time of the simulation. */
    private final T startTime;

    /** the end time of the simulation. */
    private final T endTime;

    /** the warmup time of the simulation (included in the total run length). */
    private final T warmupTime;

    /**
     * Construct an object with off-line run control information.
     * @param id String; the id of the run control that will be used as the id for the replication; should be unique within the
     *            experiment.
     * @param startTime T; the start time of the simulation
     * @param warmupPeriod T; the warmup period, included in the runlength (!)
     * @param runLength T; the total length of the run, including the warm-up period
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when the warmup
     *             time is longer than or equal to the runlength
     */
    public RunControl(final String id, final T startTime, final T warmupPeriod, final T runLength)
    {
        Throw.whenNull(id, "id should not be null");
        Throw.whenNull(startTime, "startTime should not be null");
        Throw.whenNull(warmupPeriod, "warmupPeriod should not be null");
        Throw.whenNull(runLength, "runLength should not be null");
        Throw.when(warmupPeriod.doubleValue() < 0.0, IllegalArgumentException.class, "warmup period should not be negative");
        Throw.when(runLength.doubleValue() <= 0.0, IllegalArgumentException.class, "run length should not be zero or negative");
        Throw.when(warmupPeriod.compareTo(runLength) >= 0, IllegalArgumentException.class,
                "the warmup time is longer than or equal to the runlength");

        this.id = id;
        this.description = id;
        this.startTime = startTime;
        this.endTime = SimTime.plus(startTime, runLength);
        this.warmupTime = SimTime.plus(startTime, warmupPeriod);
    }

    /** {@inheritDoc} */
    @Override
    public RunControl<T> getRunControl()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description)
    {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public T getStartTime()
    {
        return this.startTime;
    }

    /** {@inheritDoc} */
    @Override
    public T getEndTime()
    {
        return this.endTime;
    }

    /** {@inheritDoc} */
    @Override
    public T getWarmupTime()
    {
        return this.warmupTime;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.endTime, this.id, this.startTime, this.warmupTime);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RunControl<?> other = (RunControl<?>) obj;
        return Objects.equals(this.endTime, other.endTime) && Objects.equals(this.id, other.id)
                && Objects.equals(this.startTime, other.startTime) && Objects.equals(this.warmupTime, other.warmupTime);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "RunControl [id=" + this.id + ", startTime=" + this.startTime + ", warmupTime=" + this.warmupTime + ", endTime="
                + this.endTime + "]";
    }

}
