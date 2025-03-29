package nl.tudelft.simulation.dsol.tutorial.section42;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.stats.summarizers.event.StatisticsEvents;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Warehouse42SwingApplication extends DsolApplication
{
    /**
     * @param title the title
     * @param panel the panel
     */
    public Warehouse42SwingApplication(final String title, final DsolPanel panel)
    {
        super(panel, title);
        panel.enableSimulationControlButtons();
    }

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param args arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws InterruptedException on error
     * @throws InputParameterException on parameter error
     */
    public static void main(final String[] args)
            throws SimRuntimeException, RemoteException, NamingException, InterruptedException, InputParameterException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("Warehouse42SwingApplication");
        Warehouse42Model model = new Warehouse42Model(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 5 * 24.0);
        simulator.initialize(model, replication);
        new TabbedParameterDialog(model.getInputParameterMap());
        new Warehouse42SwingApplication("MM1 Queue model", new Warehouse42Panel(model, simulator));
    }

    /**
     * <p>
     * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
     * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank"> DSOL
     * License</a>. <br>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    protected static class Warehouse42Panel extends DsolPanel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param model the model
         * @param simulator the simulator
         * @throws RemoteException on error
         */
        Warehouse42Panel(final Warehouse42Model model, final DevsSimulator<Double> simulator) throws RemoteException
        {
            super(new DevsControlPanel.TimeDouble(model, simulator));
            addTabs(model);
        }

        /**
         * add a number of charts for the demo.
         * @param model the model from which to take the statistics
         */
        public void addTabs(final Warehouse42Model model)
        {
            TablePanel charts = new TablePanel(2, 1);
            getTabbedPane().addTab("statistics", charts);
            getTabbedPane().setSelectedIndex(0);
            addConsoleLogger(Level.INFO);
            addConsoleOutput();

            try
            {
                XYChart chart = new XYChart(getSimulator(), "Inventory Levels");
                chart.add("inventory", model.inventory, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
                chart.add("backlog", model.backlog, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT);
                charts.setCell(chart.getSwingPanel(), 0, 0);

                XYChart orderChart =
                        new XYChart(getSimulator(), "Ordering costs").setLabelXAxis("time (s)").setLabelYAxis("cost");
                orderChart.add("ordering costs", model.orderingCosts, StatisticsEvents.OBSERVATION_ADDED_EVENT);
                charts.setCell(orderChart.getSwingPanel(), 1, 0);
            }
            catch (RemoteException exception)
            {
                getSimulator().getLogger().always().error(exception);
            }
        }
    }
}
