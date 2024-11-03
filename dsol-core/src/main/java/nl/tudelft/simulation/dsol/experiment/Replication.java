package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * The base class for a single replication of an Experiment.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a> relative types are the same.
 * @param <T> the time type
 */
public abstract class Replication<T extends Number & Comparable<T>> implements Contextualized, Treatment<T>, Serializable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20210404L;

    /** START_REPLICATION_EVENT is fired when a replication is started. */
    public static final EventType START_REPLICATION_EVENT =
            new EventType(new MetaData("START_REPLICATION_EVENT", "Replication started"));

    /** END_REPLICATION_EVENT is fired when a replication is finished. */
    public static final EventType END_REPLICATION_EVENT =
            new EventType(new MetaData("END_REPLICATION_EVENT", "Replication ended"));

    /** WARMUP_EVENT is fired when the warmup period is over, and statistics have to be reset. */
    public static final EventType WARMUP_EVENT = new EventType(new MetaData("WARMUP_EVENT", "warmup time"));

    /** the run control for the replication. */
    private RunControl<T> runControl;

    /** the context root of this replication. */
    private ContextInterface context;

    /**
     * Construct a stand-alone replication. Checking the validity of the arguments is left to the RunControl object.
     * @param id String; the id of the replication; should be unique within the experiment.
     * @param startTime T; the start time of the simulation.
     * @param warmupPeriod T; the warmup period, included in the runlength (!)
     * @param runLength T; the total length of the run, including the warm-up period.
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when a context for
     *             the replication cannot be created, or when the warmup time is longer than or equal to the runlength
     */
    public Replication(final String id, final T startTime, final T warmupPeriod, final T runLength)
    {
        this(new RunControl<T>(id, startTime, warmupPeriod, runLength));
    }

    /**
     * Construct a stand-alone replication using a RunControl to store the run information.
     * @param runControl RunControlInterface; the run control for the replication
     * @throws NullPointerException when runControl is null
     */
    public Replication(final RunControl<T> runControl)
    {
        Throw.whenNull(runControl, "runControl should not be null");
        this.runControl = runControl;
    }

    @Override
    public RunControl<T> getRunControl()
    {
        return this.runControl;
    }

    /**
     * Set the context; method is protected so only subclasses can use this setter.
     * @param context ContextInterface; set new context
     */
    protected void setContext(final ContextInterface context)
    {
        this.context = context;
    }

    @Override
    public ContextInterface getContext()
    {
        return this.context;
    }

}
