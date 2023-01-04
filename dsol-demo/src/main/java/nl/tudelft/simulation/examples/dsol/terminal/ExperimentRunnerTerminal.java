package nl.tudelft.simulation.examples.dsol.terminal;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;

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
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ExperimentRunnerTerminal implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** number of running simulations. */
    private int numruns = 0;

    /** number of completed simulations. */
    private int completed = 0;

    /** number of runs. */
    private static final int REPS = 100;

    /** number of runs. */
    private static final int RUNS = 3 * 4 * REPS;

    /**
     * Construct the terminal experiment.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws InputParameterException on error
     */
    private ExperimentRunnerTerminal() throws SimRuntimeException, RemoteException, NamingException, InputParameterException
    {
        long seed = 1;
        int maxConcurrent = 8;
        for (int numQC = 4; numQC <= 6; numQC++)
        {
            for (int numAGV = 25; numAGV <= 28; numAGV++)
            {
                for (int rep = 1; rep <= REPS; rep++)
                {
                    while (this.numruns > maxConcurrent)
                    {
                        try
                        {
                            Thread.sleep(1);
                        }
                        catch (InterruptedException exception)
                        {
                            //
                        }
                    }

                    double runtime = 40 * 60;
                    DEVSSimulator<Double> simulator = new DEVSSimulator<Double>("ExperimentRunnerTerminal");
                    Terminal model = new Terminal(simulator, rep);
                    ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, runtime);
                    model.getStreams().put("default", new MersenneTwister(seed++));
                    InputParameterMap parameters = model.getInputParameterMap();
                    ((InputParameterInteger) parameters.get("numQC")).setIntValue(numQC);
                    ((InputParameterInteger) parameters.get("numAGV")).setIntValue(numAGV);
                    simulator.initialize(model, replication);
                    model.addListener(this, Terminal.READY_EVENT);
                    this.numruns++;
                    simulator.start();
                    simulator.scheduleEventAbs(runtime - 0.00001, this, "terminate", new Object[] {simulator, numQC, numAGV, rep, model});
                }
            }
        }
    }

    /**
     * @param simulator DEVSSimulator&lt;Double&gt;; the simulator
     * @param numQC int; num QC
     * @param numAGV int; num AGV
     * @param rep int; replication number
     * @param model Terminal; the model
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     */
    public synchronized void terminate(final DEVSSimulator<Double> simulator, final int numQC, final int numAGV,
            final int rep, final Terminal model) throws SimRuntimeException, RemoteException
    {
        simulator.stop();
        System.out.println(numQC + "\t" + numAGV + "\t" + rep + "\tNaN\tNaN\t40\t" + model.getShip().getContainers());
        this.numruns--;
        this.completed++;
        if (this.completed == RUNS)
        {
            System.exit(0);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Terminal.READY_EVENT))
        {
            Terminal.Output output = (Terminal.Output) event.getContent();
            System.out.println(output.getNumQC() + "\t" + output.getNumAGV() + "\t" + output.getRep() + "\t"
                    + output.getDelayHours() + "\t" + output.getCosts() + "\t" + output.getReady() + "\t3000");
            this.numruns--;
            this.completed++;
            if (this.completed == RUNS)
            {
                System.exit(0);
            }
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
        new ExperimentRunnerTerminal();
    }

}
