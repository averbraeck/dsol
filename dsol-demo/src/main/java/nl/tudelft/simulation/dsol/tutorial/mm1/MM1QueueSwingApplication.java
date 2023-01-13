package nl.tudelft.simulation.dsol.tutorial.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DEVSControlPanel;
import nl.tudelft.simulation.language.DSOLException;

/**
 * M/M/1 queuing model with animation and graphs.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1QueueSwingApplication extends DSOLApplication
{
    /** the model. */
    private MM1QueueModel model;

    /**
     * @param panel DSOLPanel; the panel
     * @param model MM1Queue41Model; the model
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     */
    public MM1QueueSwingApplication(final MM1QueuePanel panel, final MM1QueueModel model,
            final DevsSimulator<Double> simulator)
    {
        super(panel, "MM1Queue41SwingApplication");
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
     * @param args String[]; arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws DSOLException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DSOLException
    {
        CategoryLogger.setAllLogLevel(Level.TRACE);
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("MM1Queue41SwingApplication");
        MM1QueueModel model = new MM1QueueModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        DEVSControlPanel.TimeDouble controlPanel = new DEVSControlPanel.TimeDouble(model, simulator);
        new MM1QueueSwingApplication(new MM1QueuePanel(controlPanel, model), model, simulator);
    }

    /** stop the simulation. */
    public void terminate()
    {
        System.out.println("average queue length = " + this.model.qN.getWeightedSampleMean());
        System.out.println("average queue wait   = " + this.model.dN.getSampleMean());
        System.out.println("average utilization  = " + this.model.uN.getWeightedSampleMean());
    }

}
