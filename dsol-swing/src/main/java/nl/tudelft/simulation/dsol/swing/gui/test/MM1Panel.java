package nl.tudelft.simulation.dsol.swing.gui.test;

import java.rmi.RemoteException;

import org.djutils.stats.summarizers.event.StatisticsEvents;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.table.PersistentTableModel;
import nl.tudelft.simulation.dsol.statistics.table.TallyTableModel;
import nl.tudelft.simulation.dsol.swing.charts.boxAndWhisker.BoxAndWhiskerChart;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DEVSControlPanel;
import nl.tudelft.simulation.dsol.swing.statistics.StatisticsTable;

/**
 * MM1Panel panel for test model.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1Panel extends DSOLPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param controlPanel DEVSControlPanel.TimeDouble; the control panel
     * @throws RemoteException when communications to a remote machine fails
     */
    public MM1Panel(final DEVSControlPanel.TimeDouble controlPanel) throws RemoteException
    {
        super(controlPanel);
        addTabs();
        enableSimulationControlButtons();
    }

    /**
     * add a number of charts for the demo.
     */
    protected void addTabs()
    {
        TablePanel charts = new TablePanel(4, 4);
        getTabbedPane().addTab("statistics", charts);
        getTabbedPane().setSelectedIndex(0);
        MM1Model model = (MM1Model) getModel();

        try
        {
            // time in queue

            XYChart dNVal = new XYChart(getSimulator(), "time in queue (dN)").setLabelXAxis("time (s)").setLabelYAxis("dN");
            dNVal.add("dN value", model.tallyTimeInQueue, StatisticsEvents.OBSERVATION_ADDED_EVENT);
            charts.setCell(dNVal.getSwingPanel(), 0, 0);

            XYChart dN = new XYChart(getSimulator(), "avg time in queue").setLabelXAxis("time (s)").setLabelYAxis("avg dN");
            dN.add("dN mean", model.tallyTimeInQueue, StatisticsEvents.SAMPLE_MEAN_EVENT);
            charts.setCell(dN.getSwingPanel(), 1, 0);

            BoxAndWhiskerChart bwdN = new BoxAndWhiskerChart(getSimulator(), "dN boxplot");
            bwdN.add(model.tallyTimeInQueue);
            charts.setCell(bwdN.getSwingPanel(), 2, 0);

            StatisticsTable dNTable = new StatisticsTable(new TallyTableModel(model.tallyTimeInQueue));
            charts.setCell(dNTable.getSwingPanel(), 3, 0);

            // queue length

            XYChart qNVal = new XYChart(getSimulator(), "queue length (qN)").setLabelXAxis("time (s)").setLabelYAxis("qN");
            qNVal.add("qN value", model.persistentQueueLength, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            charts.setCell(qNVal.getSwingPanel(), 0, 1);

            XYChart qN = new XYChart(getSimulator(), "avg queue length").setLabelXAxis("time (s)").setLabelYAxis("avg qN");
            qN.add("qN mean", model.persistentQueueLength, StatisticsEvents.TIMED_WEIGHTED_SAMPLE_MEAN_EVENT);
            charts.setCell(qN.getSwingPanel(), 1, 1);

            BoxAndWhiskerChart bwqN = new BoxAndWhiskerChart(getSimulator(), "qN boxplot");
            bwqN.add(model.persistentQueueLength);
            charts.setCell(bwqN.getSwingPanel(), 2, 1);

            StatisticsTable qNTable = new StatisticsTable(new PersistentTableModel(model.persistentQueueLength));
            charts.setCell(qNTable.getSwingPanel(), 3, 1);

            // utilization

            XYChart utilization = new XYChart(getSimulator(), "utilization").setLabelXAxis("time (s)").setLabelYAxis("uN");
            utilization.add("utilization", model.persistentUtilization, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            charts.setCell(utilization.getSwingPanel(), 0, 2);

            XYChart meanUtilization =
                    new XYChart(getSimulator(), "avg utilization (uN)").setLabelXAxis("time (s)").setLabelYAxis("avg uN");
            meanUtilization.add("mean utilization", model.persistentUtilization,
                    StatisticsEvents.TIMED_WEIGHTED_SAMPLE_MEAN_EVENT);
            charts.setCell(meanUtilization.getSwingPanel(), 1, 2);

            BoxAndWhiskerChart bwuN = new BoxAndWhiskerChart(getSimulator(), "uN boxplot");
            bwuN.add(model.persistentUtilization);
            charts.setCell(bwuN.getSwingPanel(), 2, 2);

            StatisticsTable uNTable = new StatisticsTable(new PersistentTableModel(model.persistentUtilization));
            charts.setCell(uNTable.getSwingPanel(), 3, 2);

            // time in system

            XYChart tNVal = new XYChart(getSimulator(), "time in system (tN)").setLabelXAxis("time (s)").setLabelYAxis("tN");
            tNVal.add("tN value", model.tallyTimeInSystem, StatisticsEvents.OBSERVATION_ADDED_EVENT);
            charts.setCell(tNVal.getSwingPanel(), 0, 3);

            XYChart tN = new XYChart(getSimulator(), "avg time in system").setLabelXAxis("time (s)").setLabelYAxis("avg tN");
            tN.add("tN mean", model.tallyTimeInSystem, StatisticsEvents.SAMPLE_MEAN_EVENT);
            charts.setCell(tN.getSwingPanel(), 1, 3);

            BoxAndWhiskerChart bwtN = new BoxAndWhiskerChart(getSimulator(), "tN boxplot");
            bwtN.add(model.tallyTimeInSystem);
            charts.setCell(bwtN.getSwingPanel(), 2, 3);

            StatisticsTable tNTable = new StatisticsTable(new TallyTableModel(model.tallyTimeInSystem));
            charts.setCell(tNTable.getSwingPanel(), 3, 3);
        }
        catch (RemoteException exception)
        {
            model.getSimulator().getLogger().always().error(exception);
        }

        ConsoleLogger logConsole = new ConsoleLogger(Level.INFO);
        getTabbedPane().addTab("logger", logConsole);
        getTabbedPane().addTab("console", new ConsoleOutput());
    }

}
