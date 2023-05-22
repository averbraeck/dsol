package nl.tudelft.simulation.examples.dsol.mm1queue;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;

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
public class MM1QueueSwingApplication extends DsolApplication
{
    /**
     * @param title String; the title
     * @param panel DsolPanel; the panel
     */
    public MM1QueueSwingApplication(final String title, final DsolPanel panel)
    {
        super(panel, title);
        panel.enableSimulationControlButtons();
    }

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param args String[]; arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws InterruptedException on error
     * @throws InputParameterException on parameter error
     */
    public static void main(final String[] args)
            throws SimRuntimeException, RemoteException, NamingException, InterruptedException, InputParameterException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("MM1QueueSwingApplication");
        MM1QueueModel model = new MM1QueueModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        new TabbedParameterDialog(model.getInputParameterMap());
        new MM1QueueSwingApplication("MM1 Queue model", new MM1QueuePanel(model, simulator));
    }

    /**
     * <p>
     * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
     * BSD-style license. See <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank"> DSOL License</a>.
     * <br>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    protected static class MM1QueuePanel extends DsolPanel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param model MM1QueueModel; the model
         * @param simulator DevsSimulator&lt;Double&gt;; the simulator
         * @throws RemoteException on error
         */
        MM1QueuePanel(final MM1QueueModel model, final DevsSimulator<Double> simulator) throws RemoteException
        {
            super(new DevsControlPanel.TimeDouble(model, simulator));
            addTabs(model);
            addConsoleLogger(Level.INFO);
            addConsoleOutput();
            addInputParametersTab();
        }

        /**
         * add a number of charts for the demo.
         * @param model MM1QueueModel; the model from which to take the statistics
         */
        public void addTabs(final MM1QueueModel model)
        {
            TablePanel charts = new TablePanel(2, 1);
            getTabbedPane().addTab("statistics", charts);
            getTabbedPane().setSelectedIndex(0);
            charts.setCell(model.serviceTimeChart.getSwingPanel(), 0, 0);
            charts.setCell(model.serviceTimeBWChart.getSwingPanel(), 1, 0);
        }
    }
}
