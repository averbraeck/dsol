package nl.tudelft.simulation.dsol.tutorial.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.logger.CategoryLogger;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Experiment;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MM1ExperimentApplication
{
    /** */
    private DevsSimulator<Double> simulator;

    /** */
    private MM1Model model;

    /**
     * Construct a console application.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    protected MM1ExperimentApplication() throws SimRuntimeException, RemoteException, NamingException
    {
        this.simulator = new DevsSimulator<Double>("MM1ExperimentApplication");
        this.model = new MM1Model(this.simulator);
        Experiment<Double, SimulatorInterface<Double>> experiment =
                new Experiment<>("mm1", this.simulator, this.model, 0.0, 0.0, 1000.0, 10);
        experiment.start();
    }

    /**
     * @param args String[]; can be left empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException
    {
        CategoryLogger.setAllLogLevel(Level.WARNING);
        new MM1ExperimentApplication();
    }

}
