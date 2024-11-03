package nl.tudelft.simulation.dsol.demo.flow.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.logger.SimLogger;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;
import nl.tudelft.simulation.language.DsolException;

/**
 * M/M/1 Swing application that shows the events that are fired by the Simulator in the Console.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1SwingApplicationEvents extends DsolApplication
{
    /** the model. */
    private MM1Model model;

    /** logger. */
    private SimLogger logger;

    /**
     * @param panel DsolPanel; the panel
     * @param model MM1Model; the model
     * @param devsSimulator DevsSimulatorInterface&lt;Double&gt;; the simulator
     */
    public MM1SwingApplicationEvents(final MM1Panel panel, final MM1Model model,
            final DevsSimulator<Double> devsSimulator)
    {
        super(panel, "MM1SwingApplicationEvents");
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
     * @throws DsolException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DsolException
    {
        CategoryLogger.setAllLogLevel(Level.TRACE);
        DevsSimulator<Double> devsSimulator = new DevsSimulator<Double>("MM1SwingApplicationEvents");
        MM1Model model = new MM1Model(devsSimulator);
        new SimulatorEventLogger(devsSimulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        new TabbedParameterDialog(model.getInputParameterMap());
        devsSimulator.initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, devsSimulator);
        new MM1SwingApplicationEvents(new MM1Panel(controlPanel, model), model, devsSimulator);
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
        private final DevsSimulator<Double> devsSimulator;

        /**
         * @param devsSimulator DevsSimulator&lt;Double&gt;; the simulator to provide the events
         */
        SimulatorEventLogger(final DevsSimulator<Double> devsSimulator)
        {
            this.devsSimulator = devsSimulator;
            devsSimulator.addListener(this, Replication.START_REPLICATION_EVENT);
            devsSimulator.addListener(this, Replication.END_REPLICATION_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.START_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.STOP_EVENT);
            devsSimulator.addListener(this, Replication.WARMUP_EVENT);
            devsSimulator.addListener(this, SimulatorInterface.TIME_CHANGED_EVENT);
        }

        @Override
        public void notify(final Event event) throws RemoteException
        {
            this.devsSimulator.getLogger().always().info(event.getType().toString());
        }

    }
}
