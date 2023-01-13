package nl.tudelft.simulation.dsol.tutorial.section41;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.simulators.DevsxSimulator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DEVSControlPanel;
import nl.tudelft.simulation.language.DSOLException;

/**
 * M/M/1 Swing application that shows the events that are fired by the Simulator in the Console.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1Queue41SwingApplicationEvents extends DSOLApplication
{
    /** the model. */
    private MM1Queue41Model model;

    /** logger. */
    private SimLogger logger;

    /**
     * @param panel DSOLPanel; the panel
     * @param model MM1Queue41Model; the model
     * @param devsSimulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     */
    public MM1Queue41SwingApplicationEvents(final MM1Queue41Panel panel, final MM1Queue41Model model,
            final DevsxSimulator<Double> devsSimulator)
    {
        super(panel, "MM1Queue41SwingApplicationEvents");
        this.model = model;
        this.logger = devsSimulator.getLogger();
        try
        {
            devsSimulator.scheduleEventAbs(1000.0, this, "terminate", null);
        }
        catch (SimRuntimeException exception)
        {
            this.logger.always().error(exception, "<init>");
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
        DevsxSimulator<Double> devsSimulator = new DevsxSimulator<Double>("MM1Queue41SwingApplicationEvents");
        MM1Queue41Model model = new MM1Queue41Model(devsSimulator);
        new SimulatorEventLogger(devsSimulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        devsSimulator.initialize(model, replication);
        DEVSControlPanel.TimeDouble controlPanel = new DEVSControlPanel.TimeDouble(model, devsSimulator);
        new MM1Queue41SwingApplicationEvents(new MM1Queue41Panel(controlPanel, model), model, devsSimulator);
    }

    /** stop the simulation. */
    public void terminate()
    {
        System.out.println("average queue length = " + this.model.qN.getWeightedSampleMean());
        System.out.println("average queue wait   = " + this.model.dN.getSampleMean());
        System.out.println("average utilization  = " + this.model.uN.getWeightedSampleMean());
    }

    /**
     * Class to catch the events from the Simulator to check that they are right.
     */
    protected static class SimulatorEventLogger implements EventListener
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** */
        private final DevsxSimulator<Double> devsSimulator;

        /**
         * @param devsSimulator DEVSSimulator&lt;Double&gt;; the simulator to provide the events
         */
        SimulatorEventLogger(final DevsxSimulator<Double> devsSimulator)
        {
            this.devsSimulator = devsSimulator;
            devsSimulator.addListener(this, ReplicationInterface.START_REPLICATION_EVENT);
            devsSimulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.START_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.STOP_EVENT);
            devsSimulator.addListener(this, ReplicationInterface.WARMUP_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.TIME_CHANGED_EVENT);
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final Event event) throws RemoteException
        {
            this.devsSimulator.getLogger().always().info(event.getType().toString());
        }

    }
}
