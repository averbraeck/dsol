package nl.tudelft.simulation.dsol.experiment;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * The base class for a single replication of an Experiment.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 *            relative types are the same.
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 */
public abstract class AbstractReplication<
        T extends Number & Comparable<T>> implements ReplicationInterface<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20210404L;

    /** the run control for the replication. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public RunControlInterface<T> runControl;

    /** the context root of this replication. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected ContextInterface context;

    /**
     * Construct a stand-alone replication. Checking the validity of the arguments is left to the RunControl object.
     * @param id String; the id of the replication; should be unique within the experiment.
     * @param startTime T; the start time as a time object.
     * @param warmupPeriod T; the warmup period, included in the runlength (!)
     * @param runLength T; the total length of the run, including the warm-up period.
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when a context for
     *             the replication cannot be created, or when the warmup time is longer than or equal to the runlength
     */
    public AbstractReplication(final String id, final T startTime, final T warmupPeriod, final T runLength)
    {
        this(new RunControl<>(id, startTime, warmupPeriod, runLength));
    }

    /**
     * Construct a stand-alone replication using a RunControl to store the run information.
     * @param runControl RunControlInterface; the run control for the replication
     * @throws NullPointerException when runControl is null
     */
    public AbstractReplication(final RunControlInterface<T> runControl)
    {
        Throw.whenNull(runControl, "runControl should not be null");
        this.runControl = runControl;
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getContext()
    {
        return this.context;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Replication " + this.getDescription();
    }
}
