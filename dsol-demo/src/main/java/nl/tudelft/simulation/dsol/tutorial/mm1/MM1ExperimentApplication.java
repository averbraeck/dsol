package nl.tudelft.simulation.dsol.tutorial.mm1;

import java.rmi.RemoteException;
import java.util.SortedMap;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.logger.CategoryLogger;
import org.djutils.stats.summarizers.Tally;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Experiment;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1ExperimentApplication implements EventListener
{
    /** */
    private static final long serialVersionUID = 20230114L;

    /** */
    private DevsSimulator<Double> simulator;

    /** */
    private MM1Model model;

    /** */
    private Experiment<Double, SimulatorInterface<Double>> experiment;

    /**
     * Construct a console application.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    protected MM1ExperimentApplication() throws SimRuntimeException, RemoteException, NamingException
    {
        this.simulator = new DevsSimulator<Double>("MM1ExperimentApplication");
        this.model = new MM1Model(this.simulator);
        this.experiment = new Experiment<>("mm1", this.simulator, this.model, 0.0, 0.0, 1000.0, 10);
        this.experiment.addListener(this, Experiment.END_EXPERIMENT_EVENT);
        this.experiment.addListener(this, Replication.END_REPLICATION_EVENT);
        this.experiment.start();
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Replication.END_REPLICATION_EVENT))
        {
            reportReplicationStatistics();
        }
        if (event.getType().equals(Experiment.END_EXPERIMENT_EVENT))
        {
            reportFinalStatistics();
        }
    }

    /**
     * Report statistics at the end of the run.
     */
    protected void reportReplicationStatistics()
    {
        System.out.println("Statistics replication:");
        System.out.println("average queue length = " + this.model.qN.getWeightedSampleMean());
        System.out.println("average queue wait   = " + this.model.dN.getSampleMean());
        System.out.println("average utilization  = " + this.model.uN.getWeightedSampleMean());
        System.out.println();
    }

    /**
     * Report statistics at the end of the run.
     */
    protected void reportFinalStatistics()
    {
        System.out.println("Final statistics:");
        SortedMap<String, SortedMap<String, Tally>> stats = this.experiment.getSummaryStatistics();
        for (String statMapKey : stats.keySet())
        {
            System.out.println("\nSummary statistic for: " + statMapKey);
            System.out
                    .println("-".repeat(113) + String.format("%n| %-48.48s | %6.6s | %10.10s | %10.10s | %10.10s | %10.10s |%n",
                            "Tally name", "n", "mean", "st.dev", "minimum", "maximum") + "-".repeat(113));
            SortedMap<String, Tally> statMap = stats.get(statMapKey);
            for (String statKey : statMap.keySet())
            {
                Tally stat = statMap.get(statKey);
                System.out.println(stat.reportLine());
            }
            System.out.println("-".repeat(113));
        }

        System.exit(0);
    }

    /**
     * @param args String[]; can be left empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException
    {
        CategoryLogger.setAllLogLevel(Level.WARNING);
        new MM1ExperimentApplication();
    }

}
