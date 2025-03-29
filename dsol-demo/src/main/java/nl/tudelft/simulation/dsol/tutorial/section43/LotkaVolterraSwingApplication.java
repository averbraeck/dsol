package nl.tudelft.simulation.dsol.tutorial.section43;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DessSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.GenericControlPanel;

/**
 * The interactive model for the Lotka-Volterra differential equation.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class LotkaVolterraSwingApplication extends DsolApplication
{
    /**
     * @param title the title
     * @param panel the panel
     */
    public LotkaVolterraSwingApplication(final String title, final DsolPanel panel)
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
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException
    {
        DessSimulator<Double> simulator = new DessSimulator<Double>("LotkaVolterraSwingApplication", 0.01);
        PredatorPreyModel model = new PredatorPreyModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);

        GenericControlPanel.TimeDouble controlPanel = new GenericControlPanel.TimeDouble(model, simulator);
        new LotkaVolterraSwingApplication("DESS model", new LotkaVolterraPanel(model, controlPanel));
    }

    /** The panel. */
    private static class LotkaVolterraPanel extends DsolPanel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param model the model
         * @param controlPanel the control panel for the interactive simulation
         * @throws RemoteException on error
         */
        LotkaVolterraPanel(final PredatorPreyModel model, final GenericControlPanel.TimeDouble controlPanel)
                throws RemoteException
        {
            super(controlPanel);

            // add a chart for the demo.
            TablePanel charts = new TablePanel(1, 1);
            getTabbedPane().addTab("statistics", charts);
            getTabbedPane().setSelectedIndex(0);
            charts.setCell(model.getChart().getSwingPanel(), 0, 0);
        }
    }
}
