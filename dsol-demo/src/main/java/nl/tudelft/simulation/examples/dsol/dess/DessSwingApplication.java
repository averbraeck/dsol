package nl.tudelft.simulation.examples.dsol.dess;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DessSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;

/**
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DessSwingApplication extends DsolApplication
{
    /**
     * @param title String; the title
     * @param panel DsolPanel&lt;Double,Double,Double&gt;; the panel
     */
    public DessSwingApplication(final String title, final DsolPanel panel)
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
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException
    {
        DessSimulator<Double> simulator = new DessSimulator<Double>("DESSSwingApplication", 0.1);
        DessModel model = new DessModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        new DessSwingApplication("DESS model", new DessPanel(model, simulator));
    }

}
