package nl.tudelft.simulation.dsol.demo.flow.mm1;

import java.rmi.RemoteException;

import org.djutils.stats.summarizers.event.StatisticsEvents;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.statistics.table.PersistentTableModel;
import nl.tudelft.simulation.dsol.statistics.table.TallyTableModel;
import nl.tudelft.simulation.dsol.swing.charts.boxwhisker.BoxAndWhiskerChart;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.statistics.StatisticsTable;
import nl.tudelft.simulation.language.DsolException;

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
public class MM1Panel extends DsolPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param controlPanel DevsControlPanel; the control panel
     * @param model MM1Model; the model
     * @throws DsolException on error
     * @throws RemoteException on error
     */
    public MM1Panel(final DevsControlPanel.TimeDouble controlPanel, final MM1Model model)
            throws RemoteException, DsolException
    {
        super(controlPanel);
        addTabs(model);
        enableSimulationControlButtons();
    }

    /**
     * add a number of charts for the demo.
     * @param model MM1Model; the model from which to take the statistics
     */
    public void addTabs(final MM1Model model)
    {
        TablePanel charts = new TablePanel(4, 3);
        getTabbedPane().addTab("statistics", charts);
        getTabbedPane().setSelectedIndex(0);
        addConsoleLogger(Level.TRACE);
        addConsoleOutput();

        try
        {
            // dN

            XYChart dNVal = new XYChart(getSimulator(), "time in queue (dN)").setLabelXAxis("time (s)").setLabelYAxis("dN");
            dNVal.add("dN value", model.dN, StatisticsEvents.OBSERVATION_ADDED_EVENT);
            charts.setCell(dNVal.getSwingPanel(), 0, 0);

            XYChart dN = new XYChart(getSimulator(), "avg time in queue").setLabelXAxis("time (s)").setLabelYAxis("avg dN");
            dN.add("dN mean", model.dN, StatisticsEvents.SAMPLE_MEAN_EVENT);
            charts.setCell(dN.getSwingPanel(), 1, 0);

            BoxAndWhiskerChart bwdN = new BoxAndWhiskerChart(getSimulator(), "dN boxplot");
            bwdN.add(model.dN);
            charts.setCell(bwdN.getSwingPanel(), 2, 0);

            StatisticsTable dNTable = new StatisticsTable(new TallyTableModel(model.dN));
            charts.setCell(dNTable.getSwingPanel(), 3, 0);

            // qN

            XYChart qNVal = new XYChart(getSimulator(), "queue length (qN)").setLabelXAxis("time (s)").setLabelYAxis("qN");
            qNVal.add("qN value", model.qN, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            charts.setCell(qNVal.getSwingPanel(), 0, 1);

            XYChart qN = new XYChart(getSimulator(), "avg queue length").setLabelXAxis("time (s)").setLabelYAxis("avg qN");
            qN.add("qN mean", model.qN, StatisticsEvents.TIMED_WEIGHTED_SAMPLE_MEAN_EVENT);
            charts.setCell(qN.getSwingPanel(), 1, 1);

            BoxAndWhiskerChart bwqN = new BoxAndWhiskerChart(getSimulator(), "qN boxplot");
            bwqN.add(model.qN);
            charts.setCell(bwqN.getSwingPanel(), 2, 1);

            StatisticsTable qNTable = new StatisticsTable(new PersistentTableModel(model.qN));
            charts.setCell(qNTable.getSwingPanel(), 3, 1);

            // uN

            XYChart utilization = new XYChart(getSimulator(), "utilization").setLabelXAxis("time (s)").setLabelYAxis("uN");
            utilization.add("utilization", model.uN, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
            charts.setCell(utilization.getSwingPanel(), 0, 2);

            XYChart meanUtilization =
                    new XYChart(getSimulator(), "avg utilization (uN)").setLabelXAxis("time (s)").setLabelYAxis("avg uN");
            meanUtilization.add("mean utilization", model.uN, StatisticsEvents.TIMED_WEIGHTED_SAMPLE_MEAN_EVENT);
            charts.setCell(meanUtilization.getSwingPanel(), 1, 2);

            BoxAndWhiskerChart bwuN = new BoxAndWhiskerChart(getSimulator(), "uN boxplot");
            bwuN.add(model.uN);
            charts.setCell(bwuN.getSwingPanel(), 2, 2);

            StatisticsTable uNTable = new StatisticsTable(new PersistentTableModel(model.uN));
            charts.setCell(uNTable.getSwingPanel(), 3, 2);
        }
        catch (RemoteException exception)
        {
            model.getSimulator().getLogger().always().error(exception);
        }
    }

}
