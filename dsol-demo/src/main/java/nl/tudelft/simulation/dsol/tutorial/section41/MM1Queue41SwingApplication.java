package nl.tudelft.simulation.dsol.tutorial.section41;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
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
public class MM1Queue41SwingApplication extends DSOLApplication
{
    /** the model. */
    private MM1Queue41Model model;

    /**
     * @param panel DSOLPanel; the panel
     * @param model MM1Queue41Model; the model
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     */
    public MM1Queue41SwingApplication(final MM1Queue41Panel panel, final MM1Queue41Model model,
            final DEVSSimulator<Double> simulator)
    {
        super(panel, "MM1Queue41SwingApplication");
        this.model = model;
        try
        {
            simulator.scheduleEventAbs(1000.0, this, this, "terminate", null);
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
        DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("MM1Queue41SwingApplication");
        MM1Queue41Model model = new MM1Queue41Model(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        simulator.initialize(model, replication);
        DEVSControlPanel.TimeDouble controlPanel = new DEVSControlPanel.TimeDouble(model, simulator);
        new MM1Queue41SwingApplication(new MM1Queue41Panel(controlPanel, model), model, simulator);
    }

    /** stop the simulation. */
    public void terminate()
    {
        System.out.println("average queue length = " + this.model.qN.getWeightedSampleMean());
        System.out.println("average queue wait   = " + this.model.dN.getSampleMean());
        System.out.println("average utilization  = " + this.model.uN.getWeightedSampleMean());
    }

}
