package nl.tudelft.simulation.dsol.demo.des.experiment;

import java.rmi.RemoteException;
import java.util.SortedMap;
import java.util.function.Predicate;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.stats.summarizers.Tally;

import nl.tudelft.simulation.dsol.experiment.Experiment;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.SimTally;

/**
 * Discrete event queueing application with a stopping criterion.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
class DesExperimentStoppingApp implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the model. */
    private DesExperimentModel model;

    /** the experiment. */
    private Experiment<Double, DevsSimulatorInterface<Double>> experiment;

    /**
     * Constructor for the discrete event queueing application.
     * @throws RemoteException when one or more remote components cannot be reached
     */
    DesExperimentStoppingApp() throws RemoteException
    {
        var simulator = new DevsSimulator<Double>("MM1.Simulator");
        this.model = new DesExperimentModel(simulator);
        this.experiment = new Experiment<>("mm1", simulator, this.model, 0.0, 0.0, 1.0E6, 10);
        this.experiment.setStoppingCondition(new Predicate<DesExperimentModel>()
        {
            @Override
            public boolean test(final DesExperimentModel model)
            {
                return model.getTallyTimeInSystem().getN() >= 1000;
            }
        });
        this.experiment.addListener(this, Replication.END_REPLICATION_EVENT);
        this.experiment.addListener(this, Experiment.END_EXPERIMENT_EVENT);
        this.experiment.start();
    }

    /**
     * Main program to start the discrete-event queueing application.
     * @param args not used
     * @throws RemoteException when one or more remote components cannot be reached
     */
    public static void main(final String[] args) throws RemoteException
    {
        new DesExperimentStoppingApp();
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Replication.END_REPLICATION_EVENT))
        {
            reportStats();
        }
        else if (event.getType().equals(Experiment.END_EXPERIMENT_EVENT))
        {
            reportFinalStats();
        }
    }

    /**
     * Report statistics at the end of a replication.
     */
    protected void reportStats()
    {
        this.model.getPersistentUtilization().endObservations(this.model.getSimulator().getReplication().getRunLength());
        this.model.getPersistentQueueLength().endObservations(this.model.getSimulator().getReplication().getRunLength());

        System.out.println("\nStatistics of replication : " + this.model.getSimulator().getReplication().getId());
        System.out.println(SimTally.reportHeader());
        System.out.println(this.model.getTallyTimeInQueue().reportLine());
        System.out.println(this.model.getTallyTimeInSystem().reportLine());
        System.out.println(SimTally.reportFooter());

        System.out.println(SimPersistent.reportHeader());
        System.out.println(this.model.getPersistentQueueLength().reportLine());
        System.out.println(this.model.getPersistentUtilization().reportLine());
        System.out.println(SimPersistent.reportFooter());
    }

    /**
     * Report the final statistics
     */
    protected void reportFinalStats()
    {
        System.out.println("\nFinal statistics:");
        SortedMap<String, SortedMap<String, Tally>> stats = this.experiment.getSummaryStatistics();
        for (String statMapKey : stats.keySet())
        {
            System.out.println("\nSummary statistic for: " + statMapKey);
            System.out.println(Tally.reportHeader());
            SortedMap<String, Tally> statMap = stats.get(statMapKey);
            for (String statKey : statMap.keySet())
            {
                Tally stat = statMap.get(statKey);
                System.out.println(stat.reportLine());
            }
            System.out.println(Tally.reportFooter());
        }
    }
}
