package nl.tudelft.simulation.dsol.experiment;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * A single replication that is executed outside of an Experiment.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type, e.g., Double, Long, Duration
 */
public class SingleReplication<T extends Number & Comparable<T>> extends Replication<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20210404L;

    /**
     * construct a stand-alone replication.
     * @param id String; the id of the replication; should be unique within the experiment.
     * @param startTime T; the start time of the simulation.
     * @param warmupPeriod R; the warmup period, included in the runlength (!)
     * @param runLength R; the total length of the run, including the warm-up period.
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when the warmup
     *             time is longer than or equal to the runlength, or when a context for the replication cannot be created
     */
    public SingleReplication(final String id, final T startTime, final T warmupPeriod, final T runLength)
    {
        this(new RunControl<T>(id, startTime, warmupPeriod, runLength));
    }

    /**
     * Construct a stand-alone replication using a RunControl to store the run information.
     * @param runControl RunControlInterface; the run control for the replication
     * @throws NullPointerException when runControl is null
     */
    public SingleReplication(final RunControl<T> runControl)
    {
        super(runControl);
        setContext();
    }

    /**
     * Set the context for this replication.
     * @throws IllegalArgumentException in case a context for the replication cannot be created
     */
    protected void setContext()
    {
        try
        {
            ContextInterface rootContext = InitialEventContext.instantiate("root");
            setContext(ContextUtil.lookupOrCreateSubContext(rootContext, getId()));
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException(
                    "Cannot lookup or create context for experiment. Error is: " + exception.getMessage());
        }
    }

    /**
     * Remove the context for this replication.
     */
    public void removeFromContext()
    {
        try
        {
            if (getContext() != null)
            {
                ContextInterface rootContext = InitialEventContext.instantiate("root");
                ContextUtil.destroySubContext(rootContext, getId());
                setContext(null); // to avoid removing twice
            }
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException("Cannot destroy context for replication. Error is: " + exception.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return "SingleReplication [id=" + getId() + ", startTime=" + getStartTime() + ", warmupTime=" + getWarmupTime()
                + ", endTime=" + getEndTime() + "]";
    }
}
