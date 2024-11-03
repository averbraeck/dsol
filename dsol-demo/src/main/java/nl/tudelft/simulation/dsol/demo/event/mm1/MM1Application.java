package nl.tudelft.simulation.dsol.demo.event.mm1;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.inputparameters.TabbedParameterDialog;

/**
 * MM1Application.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1Application
{
    /**
     * M/M/1 queueing application.
     * @throws InputParameterException on parameter error
     */
    protected MM1Application() throws InputParameterException
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<>("MM1.Simulator");
        DsolModel<Double, DevsSimulatorInterface<Double>> model = new MM1Model(simulator);
        Replication<Double> replication = new SingleReplication<>("rep1", 0.0, 0.0, 1000.0);
        new TabbedParameterDialog(model.getInputParameterMap());
        simulator.initialize(model, replication);
        simulator.start();
    }

    /**
     * Start the simulation experiment with the model.
     * @param args String[]; the arguments (not used, should be empty)
     * @throws InputParameterException on parameter error
     */
    public static void main(final String[] args) throws InputParameterException
    {
        new MM1Application();
    }

}
