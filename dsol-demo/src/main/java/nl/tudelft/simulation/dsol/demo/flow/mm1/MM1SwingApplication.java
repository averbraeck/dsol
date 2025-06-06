package nl.tudelft.simulation.dsol.demo.flow.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;
import nl.tudelft.simulation.language.DsolException;

/**
 * M/M/1 queuing model with animation and graphs.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MM1SwingApplication extends DsolApplication
{
    /** the model. */
    private MM1Model model;

    /**
     * @param panel the panel
     * @param model the model
     * @param simulator the simulator
     */
    public MM1SwingApplication(final MM1Panel panel, final MM1Model model, final DevsSimulator<Double> simulator)
    {
        super(panel, "MM1SwingApplication");
        this.model = model;
        try
        {
            simulator.scheduleEventAbs(1000.0, this, "terminate", null);
        }
        catch (SimRuntimeException exception)
        {
            simulator.getLogger().always().error(exception, "<init>");
        }
    }

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param args arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws DsolException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DsolException
    {
        CategoryLogger.setAllLogLevel(Level.TRACE);
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("MM1SwingApplication");
        MM1Model model = new MM1Model(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        new TabbedParameterDialog(model.getInputParameterMap());
        simulator.initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, simulator);
        new MM1SwingApplication(new MM1Panel(controlPanel, model), model, simulator);
    }

    /** stop the simulation. */
    public void terminate()
    {
        System.out.println("average queue length = " + this.model.qN.getWeightedSampleMean());
        System.out.println("average queue wait   = " + this.model.dN.getSampleMean());
        System.out.println("average utilization  = " + this.model.uN.getWeightedSampleMean());
    }

}
