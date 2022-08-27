package nl.tudelft.simulation.examples.dsol.terminal;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulator;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;

/**
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ConsoleRunnerTerminal implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20220110L;

    /**
     * Construct the terminal experiment.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws InputParameterException on error
     */
    private ConsoleRunnerTerminal() throws SimRuntimeException, RemoteException, NamingException, InputParameterException
    {
        long seed = 127;
        int rep = 1;
        int numQC = 5;
        int numAGV = 42;
        double runtime = 40 * 60;
        DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("ConsoleRunnerTerminal");
        Terminal model = new Terminal(simulator, rep);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, runtime);
        model.getStreams().put("default", new MersenneTwister(seed++));
        InputParameterMap parameters = model.getInputParameterMap();
        ((InputParameterInteger) parameters.get("numQC")).setIntValue(numQC);
        ((InputParameterInteger) parameters.get("numAGV")).setIntValue(numAGV);
        simulator.initialize(model, replication);
        simulator.scheduleEventAbs(runtime - 0.00001, this, this, "terminate", new Object[] {simulator, numQC, numAGV, rep});
        model.addListener(this, Terminal.READY_EVENT);
        simulator.start();
    }

    /**
     * @param simulator DEVSSimulator&lt;Double&gt;; the simulator
     * @param numQC int; num QC
     * @param numAGV int; num AGV
     * @param rep int; replication number
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     */
    public synchronized void terminate(final DEVSSimulator<Double> simulator, final int numQC, final int numAGV,
            final int rep) throws SimRuntimeException, RemoteException
    {
        simulator.stop();
        System.out.println(numQC + "\t" + numAGV + "\t" + rep + "\tNaN\tNaN");
        System.exit(0);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void notify(final EventInterface event) throws RemoteException
    {
        if (event.getType().equals(Terminal.READY_EVENT))
        {
            Terminal.Output output = (Terminal.Output) event.getContent();
            System.out.println(output.getNumQC() + "\t" + output.getNumAGV() + "\t" + output.getRep() + "\t"
                    + output.getDelayHours() + "\t" + output.getCosts());
            System.exit(0);
        }
    }

    /**
     * @param args String[]; args
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws InputParameterException on error
     */
    public static void main(final String[] args)
            throws SimRuntimeException, RemoteException, NamingException, InputParameterException
    {
        new ConsoleRunnerTerminal();
    }
}
