package nl.tudelft.simulation.dsol.tutorial.section25;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

/**
 * A Simple console app to run the Customer-Order model.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public final class CustomerOrderApp implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Create the simulation.
     * @throws NamingException on Context error
     */
    private CustomerOrderApp() throws NamingException
    {
        DevsSimulator<Double> simulator = new DevsSimulator<Double>("CustomerOrderApp");
        CustomerOrderModel model = new CustomerOrderModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.addListener(this, Replication.END_REPLICATION_EVENT);
        simulator.start();
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Replication.END_REPLICATION_EVENT))
        {
            System.exit(0);
        }
    }

    /**
     * executes the model.
     * @param args empty
     * @throws NamingException on Context error
     */
    public static void main(final String[] args) throws NamingException
    {
        new CustomerOrderApp();
    }

}
