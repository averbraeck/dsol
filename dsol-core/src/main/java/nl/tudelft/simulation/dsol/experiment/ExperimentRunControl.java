package nl.tudelft.simulation.dsol.experiment;

import org.djutils.exceptions.Throw;

/**
 * ExperimentRunControl.java.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type to be able to implement a comparator on the simulation time.
 */
public class ExperimentRunControl<T extends Number & Comparable<T>> extends RunControl<T>
{
    /** */
    private static final long serialVersionUID = 20210410L;

    /** The number of replications to execute. */
    private final int numberOfReplications;

    /**
     * Construct an object with off-line run control information for an experiment.
     * @param id String; the id of the run control that will be used as the id for the replication; should be unique within the
     *            experiment.
     * @param startTime T; the start time of the simulation.
     * @param warmupPeriod T; the warmup period, included in the runlength (!)
     * @param runLength T; the total length of the run, including the warm-up period.
     * @param numberOfReplications int; the number of replications to execute
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when the warmup
     *             time is longer than or equal to the runlength, or when number of replications is zero or negative
     */
    public ExperimentRunControl(final String id, final T startTime, final T warmupPeriod, final T runLength,
            final int numberOfReplications)
    {
        super(id, startTime, warmupPeriod, runLength);
        Throw.when(numberOfReplications <= 0, IllegalArgumentException.class,
                "number of replications can not be zero or negative");
        this.numberOfReplications = numberOfReplications;
    }

    /**
     * Return the total number of replications to execute.
     * @return int; the total number of replications to execute
     */
    public int getNumberOfReplications()
    {
        return this.numberOfReplications;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.numberOfReplications;
        return result;
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        ExperimentRunControl<?> other = (ExperimentRunControl<?>) obj;
        if (this.numberOfReplications != other.numberOfReplications)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ExperimentRunControl " + this.getId() + ", NumberOfReplications=" + this.numberOfReplications;
    }
}
