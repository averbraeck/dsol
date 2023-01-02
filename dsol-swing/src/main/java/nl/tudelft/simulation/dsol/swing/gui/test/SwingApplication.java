package nl.tudelft.simulation.dsol.swing.gui.test;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.control.DEVSControlPanel;

/**
 * SwingApplication.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SwingApplication extends DSOLApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param panel the panel with the controls and tabbed pane for content
     */
    public SwingApplication(final DSOLPanel panel)
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
        DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("simulator");
        MyModel model = new MyModel(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        DEVSControlPanel.TimeDouble controlPanel = new DEVSControlPanel.TimeDouble(model, simulator);
        DSOLPanel panel = new DSOLPanel(controlPanel);
        panel.addConsoleLogger(Level.INFO);
        new SwingApplication(panel);
    }

    /** */
    static class MyModel extends AbstractDSOLModel<Double, DEVSSimulator<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param simulator the simulator
         */
        MyModel(final DEVSSimulator<Double> simulator)
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
