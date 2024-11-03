package nl.tudelft.simulation.dsol.experiment;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * A single replication belonging to an Experiment.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a> relative types are the same.
 * @param <T> the time type
 * @param <S> the simulator type
 */
public class ExperimentReplication<T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends Replication<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20210404L;

    /** the experiment to which this replication belongs. */
    private final Experiment<T, S> experiment;

    /** the replication number within the experiment. */
    final int replicationNr;

    /**
     * Construct a replication to be used in an experiment.
     * @param id String; the id of the replication; should be unique within the experiment.
     * @param startTime T; the start time of the simulation.
     * @param warmupPeriod T; the warmup period, included in the runlength (!)
     * @param runLength T; the total length of the run, including the warm-up period
     * @param experiment Experiment; the experiment to which this replication belongs
     * @param replicationNr int; the replication number within the experiment
     * @throws NullPointerException when id, startTime, warmupPeriod, runLength or experiment is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when the warmup
     *             time is longer than or equal to the runlength, or when a context for the replication cannot be created
     */
    public ExperimentReplication(final String id, final T startTime, final T warmupPeriod, final T runLength,
            final Experiment<T, S> experiment, final int replicationNr)
    {
        this(new RunControl<T>(id, startTime, warmupPeriod, runLength), experiment, replicationNr);
    }

    /**
     * Construct a replication to be used in an experiment, using a RunControl to store the run information.
     * @param runControl RunControlInterface; the run control for the replication
     * @param experiment Experiment; the experiment to which this replication belongs
     * @param replicationNr int; the replication number within the experiment
     * @throws NullPointerException when runControl or experiment is null
     */
    public ExperimentReplication(final RunControl<T> runControl, final Experiment<T, S> experiment, final int replicationNr)
    {
        super(runControl);
        Throw.whenNull(experiment, "experiment cannot be null");
        this.experiment = experiment;
        this.replicationNr = replicationNr;
        setContext();
    }

    @Override
    public String getId()
    {
        return super.getId() + "." + this.replicationNr;
    }

    /**
     * Set the context for this replication.
     * @throws IllegalArgumentException in case a context for the replication cannot be created
     */
    protected void setContext()
    {
        try
        {
            ContextInterface rootContext = this.experiment.getContext();
            setContext(ContextUtil.lookupOrCreateSubContext(rootContext, getId()));
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException(
                    "Cannot lookup or create context for replication. Error is: " + exception.getMessage());
        }
    }

    /**
     * Remove the entire experiment tree from the context.
     */
    public void removeFromContext()
    {
        try
        {
            if (getContext() != null)
            {
                ContextInterface rootContext = this.experiment.getContext();
                ContextUtil.destroySubContext(rootContext, getId());
                setContext(null);
            }
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException("Cannot destroy context for replication. Error is: " + exception.getMessage());
        }
    }

    /**
     * Return the experiment to which this replication belongs.
     * @return experiment Experiment; experiment to which this replication belongs.
     */
    public Experiment<T, S> getExperiment()
    {
        return this.experiment;
    }

    /**
     * Return the replication number within the experiment.
     * @return replicationNr int; the replication number within the experiment
     */
    public int getReplicationNr()
    {
        return this.replicationNr;
    }

    @Override
    public String toString()
    {
        return "ExperimentReplication [id=" + getId() + ", startTime=" + getStartTime() + ", warmupTime=" + getWarmupTime()
                + ", endTime=" + getEndTime() + ", replicationNr=" + this.replicationNr + "]";
    }

}
