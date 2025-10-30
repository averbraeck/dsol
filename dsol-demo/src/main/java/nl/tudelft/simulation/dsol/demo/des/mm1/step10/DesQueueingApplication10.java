package nl.tudelft.simulation.dsol.demo.des.mm1.step10;

import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.swing.gui.DsolApplication;
import nl.tudelft.simulation.dsol.swing.gui.control.DevsControlPanel;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;

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
class DesQueueingApplication10
{
    /** Store the input parameter map. */
    private InputParameterMap inputParameterMap = null;

    /**
     * Build the UI, and rebuild it on reset.
     */
    public void build()
    {
        var simulator = new DevsSimulator<Double>("MM1.Simulator");
        var model = new DesQueueingModel10(simulator);
        model.setResetApplicationExecutable(() -> build());
        var replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
        if (this.inputParameterMap == null)
        {
            new TabbedParameterDialog(model.getInputParameterMap());
            this.inputParameterMap = model.getInputParameterMap();
        }
        else
        {
            model.setInputParameterMap(this.inputParameterMap);
        }
        model.getSimulator().initialize(model, replication);
        DevsControlPanel.TimeDouble controlPanel = new DevsControlPanel.TimeDouble(model, model.getSimulator());
        new DsolApplication(new DesQueueingPanel(controlPanel), "MM1 queuing model");
    }

    /**
     * Main program to start the discrete-event queueing application.
     * @param args not used
     */
    public static void main(final String[] args)
    {
        var app = new DesQueueingApplication10();
        app.build();
    }

}
