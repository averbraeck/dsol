package nl.tudelft.simulation.dsol.demo.des.mm1.withgui;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;

/**
 * Discrete event queueing application.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
class DesQueueingApplication10 extends DsolApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the discrete event queueing application with a graphics control panel.
     * @param panel the control panel for the GUI
     */
    public DesQueueingApplication10(final DesQueueingPanel panel)
    {
        super(panel, "MM1 queuing model");
    }

    /**
     * Main program to start the discrete-event queueing application.
     * @param args not used
     * @throws RemoteException on network error when there are remote components
     */
    public static void main(final String[] args) throws RemoteException
    {
        var simulator = new DevsSimulator<Double>("MM1.Simulator");
        var model = new DesQueueingModel10(simulator);
        var replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, simulator);
        new DesQueueingApplication10(new DesQueueingPanel(controlPanel));
    }

}
