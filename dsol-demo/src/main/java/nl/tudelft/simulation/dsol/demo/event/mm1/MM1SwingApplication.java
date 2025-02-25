package nl.tudelft.simulation.dsol.demo.event.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;

/**
 * MM1SwingApplication is a test GUI application.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MM1SwingApplication extends DsolApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param panel the simulation panel
     */
    public MM1SwingApplication(final MM1Panel panel)
    {
        super(panel, "MM1 Queueing model");
    }

    /**
     * @param args String[]; the arguments for the program (should be empty)
     * @throws SimRuntimeException on simulation error
     * @throws RemoteException on remote error
     * @throws NamingException on naming/animation error
     * @throws InputParameterException on parameter error
     */
    public static void main(final String[] args)
            throws SimRuntimeException, RemoteException, NamingException, InputParameterException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("MM1SwingApplication.Simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new MM1Model(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        new TabbedParameterDialog(model.getInputParameterMap());
        simulator.initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, simulator);
        new MM1SwingApplication(new MM1Panel(controlPanel));
    }

}
