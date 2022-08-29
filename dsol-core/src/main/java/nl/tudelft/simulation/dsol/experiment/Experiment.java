package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.ref.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.stats.summarizers.Tally;

import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;
import nl.tudelft.simulation.dsol.statistics.StatisticsInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The Experiment specifies the parameters for a number of simulation replications, and can execute a series of replications.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author Alexander Verbraeck relative types are the same.
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <S> the simulator to use
 */
public class Experiment<T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends EventProducer
        implements EventListenerInterface, RunControlInterface<T>, Contextualized
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** START_EXPERIMENT_EVENT is fired when the experiment starts. */
    public static final EventType START_EXPERIMENT_EVENT =
            new EventType(new MetaData("START_EXPERIMENT_EVENT", "Start of experiment"));

    /** END_EXPERIMENT_EVENT is fired when the experiment is ended. */
    public static final EventType END_EXPERIMENT_EVENT =
            new EventType(new MetaData("END_EXPERIMENT_EVENT", "End of experiment"));

    /** The started replications of this experiment. */
    private List<ExperimentReplication<T, S>> startedReplications = new ArrayList<>();

    /** The simulator that will execute the replications. */
    private final S simulator;

    /** The model that has to be executed. */
    private final DSOLModel<T, ? extends S> model;

    /** The current replication. */
    private int currentReplicationNumber = -1;

    /** the run control for the replication. */
    private final ExperimentRunControl<T> runControl;

    /** The Experiment context. */
    private ContextInterface context;

    /** The class that updates the seeds of the streams between replications. */
    private StreamUpdater streamUpdater = new SimpleStreamUpdater();

    /** The worker thread to carry out the experiment. */
    private ExperimentThread experimentThread;

    /** is the simulation experiment running? */
    private boolean running = false;

    /**
     * The summary statistics over multiple replications. The table maps the name of the statistic to a map of fields to tallies
     * that contain the statistics of the tallied values. Suppose we run a model with 10 replications, which has a tally named
     * "waiting time". Then there will be an entry in this table called "waiting time" as well. This "waiting time" maps to
     * several sub-maps, such as "N", "Population mean", "Population variance", "Min", "Max", etc. Each of these is a Tally for
     * which the final values of the replications for that value have been tallied. The "Population Mean" for "waiting time" in
     * this example therefore contains the average of the 10 average waiting times that have been alculated in the 10
     * replications.
     */
    private SortedMap<String, SortedMap<String, Tally>> summaryStatistics = new TreeMap<>();

    /**
     * Construct a new Experiment.
     * @param id String; the id of the experiment
     * @param simulator S; the simulator
     * @param model DSOLModel&lt;T, S&gt;; the model to experiment with
     * @param startTime T; the start time as a time object.
     * @param warmupPeriod R; the warmup period, included in the runlength (!)
     * @param runLength R; the total length of the run, including the warm-up period.
     * @param numberOfReplications int; the number of replications to execute
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     * @throws IllegalArgumentException when warmup period is negative, or run length is zero or negative, or when the warmup
     *             time is longer than or equal to the runlength, or the number of replications is zero or negative
     */
    public Experiment(final String id, final S simulator, final DSOLModel<T, ? extends S> model, final T startTime,
            final T warmupPeriod, final T runLength, final int numberOfReplications)
    {
        this(simulator, model, new ExperimentRunControl<T>(id, startTime, warmupPeriod, runLength, numberOfReplications));
    }

    /**
     * Construct a new Experiment, using a RunControl to store the run control information.
     * @param simulator S; the simulator
     * @param model DSOLModel&lt;T, S&gt;; the model to experiment with
     * @param runControl ExperimentRunControl; the run control information
     * @throws NullPointerException when id, startTime, warmupPeriod or runLength is null
     */
    public Experiment(final S simulator, final DSOLModel<T, ? extends S> model, final ExperimentRunControl<T> runControl)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(runControl, "runControl cannot be null");
        this.runControl = runControl;
        this.simulator = simulator;
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.runControl.getId();
    }

    /**
     * Return the simulator.
     * @return S; the simulator
     */
    public S getSimulator()
    {
        return this.simulator;
    }

    /**
     * Return the model.
     * @return DSOLModel; the model
     */
    public DSOLModel<T, ? extends S> getModel()
    {
        return this.model;
    }

    /**
     * Return the list of started replications. Not all replications might have finished yet.
     * @return List&lt;Replication&lt;T, S&gt;&gt;; the list of started replications
     */
    public List<? extends ExperimentReplication<T, S>> getStartedReplications()
    {
        return this.startedReplications;
    }

    /**
     * Start the extire experiment on a simulator, and execute ehe replications one by one.
     * @throws RemoteException on network error if started by RMI
     * @throws IllegalArgumentException when there are no more replications to run, or when the simulator is already running
     */
    public synchronized void start() throws RemoteException
    {
        Throw.when(this.currentReplicationNumber >= getNumberOfReplications() - 1, IllegalArgumentException.class,
                "Experiment: No more replications");
        Throw.when(this.simulator.isStartingOrRunning(), IllegalArgumentException.class,
                "Simulator for experiment running -- Experiment cannot be started");
        this.fireEvent(Experiment.START_EXPERIMENT_EVENT, null);
        this.experimentThread = new ExperimentThread(this);
        this.running = true;
        this.experimentThread.start();
    }

    /**
     * Start the next replication from the list of replications, or fire END_EXPERIMENT_EVENT when there are no more
     * non-executed replications.
     * @throws RemoteException on network error if started by RMI
     */
    protected void startNextReplication() throws RemoteException
    {
        Throw.when(this.currentReplicationNumber >= getNumberOfReplications() - 1, IllegalArgumentException.class,
                "Trying to run replication beyond given number");
        this.currentReplicationNumber++;
        ExperimentReplication<T, S> replication = makeExperimentReplication();
        this.startedReplications.add(replication);
        this.streamUpdater.updateSeeds(this.model.getStreams(), this.currentReplicationNumber);
        this.simulator.initialize(getModel(), replication);
        this.simulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT, ReferenceType.STRONG);
        this.simulator.start();
    }

    /**
     * Fire the end Experiment event.
     */
    protected void endExperiment()
    {
        this.fireEvent(Experiment.END_EXPERIMENT_EVENT, null);
        this.running = false;
    }

    /**
     * Create a new replication for an experiment. This method can be overridden in the inner classes.
     * @return ExperimentReplication; a new replication for an experiment
     */
    protected ExperimentReplication<T, S> makeExperimentReplication()
    {
        return new ExperimentReplication<T, S>("Replication " + this.currentReplicationNumber, getStartSimTime(),
                getWarmupPeriod(), getRunLength(), this);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(ReplicationInterface.END_REPLICATION_EVENT))
        {
            endReplication();
            this.experimentThread.interrupt();
        }
    }

    /**
     * Reset the experiment so it can be run again.
     */
    public void reset()
    {
        this.currentReplicationNumber = -1;
        for (ExperimentReplication<T, S> replication : this.startedReplications)
        {
            replication.removeFromContext();
        }
        this.startedReplications.clear();
        this.summaryStatistics = new TreeMap<>();
    }

    /**
     * Create or update summary statistics for the experiment based on the statistics of the just completed replication.
     */
    protected void endReplication()
    {
        for (StatisticsInterface<T> stat : this.model.getOutputStatistics())
        {
            if (stat instanceof SimCounter)
            {
                SimCounter<T> counter = (SimCounter<T>) stat;
                addSummaryStatistic(counter.getDescription(), "N", counter.getN());
                addSummaryStatistic(counter.getDescription(), "Count", counter.getCount());
            }
            else if (stat instanceof SimTally)
            {
                SimTally<T> tally = (SimTally<T>) stat;
                addSummaryStatistic(tally.getDescription(), "N", tally.getN());
                addSummaryStatistic(tally.getDescription(), "Max", tally.getMax());
                addSummaryStatistic(tally.getDescription(), "Min", tally.getMin());
                addSummaryStatistic(tally.getDescription(), "PopulationExcessKurtosis", tally.getPopulationExcessKurtosis());
                addSummaryStatistic(tally.getDescription(), "PopulationKurtosis", tally.getPopulationKurtosis());
                addSummaryStatistic(tally.getDescription(), "PopulationMean", tally.getPopulationMean());
                addSummaryStatistic(tally.getDescription(), "PopulationSkewness", tally.getPopulationSkewness());
                addSummaryStatistic(tally.getDescription(), "PopulationStDev", tally.getPopulationStDev());
                addSummaryStatistic(tally.getDescription(), "PopulationVariance", tally.getPopulationVariance());
                addSummaryStatistic(tally.getDescription(), "SampleExcessKurtosis", tally.getSampleExcessKurtosis());
                addSummaryStatistic(tally.getDescription(), "SampleKurtosis", tally.getSampleKurtosis());
                addSummaryStatistic(tally.getDescription(), "SampleMean", tally.getSampleMean());
                addSummaryStatistic(tally.getDescription(), "SampleSkewness", tally.getSampleSkewness());
                addSummaryStatistic(tally.getDescription(), "SampleStDev", tally.getSampleStDev());
                addSummaryStatistic(tally.getDescription(), "SampleVariance", tally.getSampleVariance());
                addSummaryStatistic(tally.getDescription(), "Sum", tally.getSum());
            }
            else if (stat instanceof SimPersistent) // includes Utilization
            {
                SimPersistent<T> persistent = (SimPersistent<T>) stat;
                // note that the last value has to be stored for the end simulation time, otherwise we have a 'gap' at the end
                persistent.endObservations(this.simulator.getSimulatorTime());
                addSummaryStatistic(persistent.getDescription(), "N", persistent.getN());
                addSummaryStatistic(persistent.getDescription(), "Max", persistent.getMax());
                addSummaryStatistic(persistent.getDescription(), "Min", persistent.getMin());
                addSummaryStatistic(persistent.getDescription(), "WeightedPopulationMean",
                        persistent.getWeightedPopulationMean());
                addSummaryStatistic(persistent.getDescription(), "WeightedPopulationStDev",
                        persistent.getWeightedPopulationStDev());
                addSummaryStatistic(persistent.getDescription(), "WeightedPopulationVariance",
                        persistent.getWeightedPopulationVariance());
                addSummaryStatistic(persistent.getDescription(), "WeightedSampleMean", persistent.getWeightedSampleMean());
                addSummaryStatistic(persistent.getDescription(), "WeightedSampleStDev", persistent.getWeightedSampleStDev());
                addSummaryStatistic(persistent.getDescription(), "WeightedSampleVariance",
                        persistent.getWeightedSampleVariance());
                addSummaryStatistic(persistent.getDescription(), "WeightedSum", persistent.getWeightedSum());
            }
            else
            {
                CategoryLogger.always().warn("Unknown statistic for summary statistics: " + stat.getClass().getSimpleName());
            }
        }
    }

    /**
     * Tally a value in a summary statistic over multiple replications.
     * @param statistic String; the name of the statistic
     * @param field String; the name of the field for the summary statistic
     * @param value double; the value to tally by the summary statistic
     */
    protected void addSummaryStatistic(final String statistic, final String field, final double value)
    {
        SortedMap<String, Tally> fieldMap = this.summaryStatistics.get(statistic);
        if (fieldMap == null)
        {
            fieldMap = new TreeMap<>();
            this.summaryStatistics.put(statistic, fieldMap);
        }
        Tally summaryTally = fieldMap.get(field);
        if (summaryTally == null)
        {
            summaryTally = new Tally(field);
            fieldMap.put(field, summaryTally);
        }
        if (!Double.isNaN(value))
        {
            summaryTally.register(value);
        }
    }

    /**
     * The summary statistics over multiple replications. The table maps the name of the statistic to a map of fields to tallies
     * that contain the statistics of the tallied values. Suppose we run a model with 10 replications, which has a tally named
     * "waiting time". Then there will be an entry in this table called "waiting time" as well. This "waiting time" maps to
     * several sub-maps, such as "N", "Population mean", "Population variance", "Min", "Max", etc. Each of these is a Tally for
     * which the final values of the replications for that value have been tallied. The "Population Mean" for "waiting time" in
     * this example therefore contains the average of the 10 average waiting times that have been alculated in the 10
     * replications.
     * @return summaryStatistics SortedMap&lt;String, SortedMap&lt;String, Tally&gt;&gt;; the summary statistics
     */
    public SortedMap<String, SortedMap<String, Tally>> getSummaryStatistics()
    {
        return this.summaryStatistics;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.runControl.getId();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return this.runControl.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description)
    {
        this.runControl.setDescription(description);
    }

    /**
     * Return the current (running or finished) replication.
     * @return int; the current replication (still running or finished in case of last replication)
     */
    public int getCurrentReplication()
    {
        return this.currentReplicationNumber;
    }

    /** {@inheritDoc} */
    @Override
    public ContextInterface getContext()
    {
        try
        {
            if (this.context == null)
            {
                ContextInterface rootContext = InitialEventContext.instantiate("root");
                this.context = ContextUtil.lookupOrCreateSubContext(rootContext, this.runControl.getId());
            }
            return this.context;
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException("Cannot destroy context for replication. Error is: " + exception.getMessage());
        }
    }

    /**
     * Remove the entire experiment tree from the context.
     */
    public void removeFromContext()
    {
        try
        {
            if (this.context != null)
            {
                ContextInterface rootContext = InitialEventContext.instantiate("root");
                ContextUtil.destroySubContext(rootContext, this.runControl.getId());
            }
        }
        catch (RemoteException | NamingException exception)
        {
            throw new IllegalArgumentException("Cannot destroy context for replication. Error is: " + exception.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public T getStartSimTime()
    {
        return this.runControl.getStartSimTime();
    }

    /** {@inheritDoc} */
    @Override
    public T getEndSimTime()
    {
        return this.runControl.getEndSimTime();
    }

    /** {@inheritDoc} */
    @Override
    public T getWarmupSimTime()
    {
        return this.runControl.getWarmupSimTime();
    }

    /**
     * Return the current stream updater.
     * @return streamUpdater StreamUpdater; the current stream updater
     */
    public StreamUpdater getStreamUpdater()
    {
        return this.streamUpdater;
    }

    /**
     * Set a new StreamUpdater to update the random seeds between replications.
     * @param streamUpdater StreamUpdater; the new stream updater
     */
    public void setStreamUpdater(final StreamUpdater streamUpdater)
    {
        this.streamUpdater = streamUpdater;
    }

    /**
     * Return the current replication number, which is -1 if the experiment has not yet started.
     * @return int; the current replication number
     */
    public int getCurrentReplicationNumber()
    {
        return this.currentReplicationNumber;
    }

    /**
     * Return the total number of replications to execute.
     * @return int; the total number of replications to execute
     */
    public int getNumberOfReplications()
    {
        return this.runControl.getNumberOfReplications();
    }

    /**
     * Return whether the experiment is running or not.
     * @return boolean; whether the experiment is running or not
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Experiment[" + getDescription() + " ; simulator=" + this.simulator.getClass().getTypeName() + "]";
    }

    /* ********************************************************************************************************* */
    /* ************************************** EXPERIMENT RUNNER CLASS ****************************************** */
    /* ********************************************************************************************************* */

    /** The ExperimentRunner job. */
    protected static class ExperimentThread extends Thread
    {
        /** the experiment. */
        private final Experiment<?, ?> experiment;

        /**
         * Construct the ExperimentRunner with a pointer to the Experiment.
         * @param experiment Experiment; the experiment
         */
        public ExperimentThread(final Experiment<?, ?> experiment)
        {
            this.experiment = experiment;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            synchronized (this)
            {
                while (this.experiment.getCurrentReplicationNumber() < this.experiment.getNumberOfReplications() - 1)
                {
                    try
                    {
                        this.experiment.startNextReplication();
                        wait(); // wait for END_REPLICATION event
                    }
                    catch (RemoteException e)
                    {
                        CategoryLogger.always().error(e);
                        break;
                    }
                    catch (InterruptedException ie)
                    {
                        Thread.interrupted(); // clear the interrupted flag
                        // start next replication.
                    }
                }
                this.experiment.endExperiment();
            }
        }

    }
}
