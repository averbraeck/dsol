package nl.tudelft.simulation.dsol.tutorial.section25;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;

import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;

/**
 * A Simple console app to run the Customer-Order model.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public final class CustomerOrderApp implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Create the simulation.
     * @throws NamingException on Context error
     */
    private CustomerOrderApp() throws NamingException
    {
        DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("CustomerOrderApp");
        CustomerOrderModel model = new CustomerOrderModel(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
        simulator.start();
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(ReplicationInterface.END_REPLICATION_EVENT))
        {
            System.exit(0);
        }
    }

    /**
     * executes the model.
     * @param args String[]; empty
     * @throws NamingException on Context error
     */
    public static void main(final String[] args) throws NamingException
    {
        new CustomerOrderApp();
    }

}
