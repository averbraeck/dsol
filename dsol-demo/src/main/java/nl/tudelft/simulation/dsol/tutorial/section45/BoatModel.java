package nl.tudelft.simulation.dsol.tutorial.section45;

import java.io.Serializable;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;

/**
 * A BoatModel.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class BoatModel extends AbstractDSOLModel<Double, DEVSSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new BoatModel.
     * @param simulator DEVSSimulator<Double>; the simulator
     */
    public BoatModel(final DEVSSimulator<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        Port port = new Port(this.simulator);

        // We schedule boat creation
        this.scheduleBoatArrival(0, port);
        this.scheduleBoatArrival(1, port);
        this.scheduleBoatArrival(15, port);
    }

    /**
     * schedules the creation of a boat.
     * @param time double; the time when the boat should arrive
     * @param port Port; the port
     * @throws SimRuntimeException on simulation exception
     */
    private void scheduleBoatArrival(final double time, final Port port) throws SimRuntimeException
    {
        this.simulator.scheduleEventAbs(time, this, Boat.class, "<init>", new Object[] {this.simulator, port});
    }

    /**
     * command line executes the model.
     * @param args String[]; the arguments to the command line
     * @throws NamingException on Context error
     * @throws SimRuntimeException on simulation model construction error
     */
    public static void main(final String[] args) throws NamingException, SimRuntimeException
    {
        DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("BoatModel");
        DSOLModel<Double, DEVSSimulator<Double>> model = new BoatModel(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        simulator.initialize(model, replication);
        simulator.start();
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "BoatModel";
    }
}
