package nl.tudelft.simulation.dsol.swing.gui.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;

/**
 * SwingApplication.java.
 * <p>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SwingApplication extends DsolApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param panel the panel with the controls and tabbed pane for content
     */
    public SwingApplication(final DsolPanel panel)
    {
        super(panel, "Swing test appication");
        panel.enableSimulationControlButtons();
    }

    /**
     * @param args String[]; not used
     * @throws NamingException on error with the context
     * @throws RemoteException on error with remote components
     */
    public static void main(final String[] args) throws NamingException, RemoteException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("simulator");
        MyModel model = new MyModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, simulator);
        DsolPanel panel = new DsolPanel(controlPanel);
        panel.addConsoleLogger(Level.INFO);
        new SwingApplication(panel);
    }

    /** */
    static class MyModel extends AbstractDsolModel<Double, DevsSimulator<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param simulator the simulator
         */
        MyModel(final DevsSimulator<Double> simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            //
        }
    }
}
