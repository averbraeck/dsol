package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * InputPort class. The input port can function as an input port for a Parallel DEVS Atomic Model as well as for a Parallel
 * Hierarchical DEVS Coupled Model. A boolean in the class indicates whether it behaves as a port for an atomic model or a
 * coupled model. For a coupled model, the input message is passed on to the external input couplings (EIC), for an atomic
 * model, the external event handler is called (or the confluent event handler in case of a conflict).
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <TYPE> The type of messages the input port accepts.
 */
public class InputPort<T extends Number & Comparable<T>, TYPE> implements InputPortInterface<T, TYPE>
{
    /** The model to which the port links. */
    private AbstractDEVSModel<T> model;

    /** Is the model atomic or not? */
    private boolean atomic;

    /**
     * Constructor for the input port where the model is a coupled model.
     * @param coupledModel CoupledModel&lt;A,R,T&gt;; the coupled model to which the port is added.
     */
    public InputPort(final CoupledModel<T> coupledModel)
    {
        this.model = coupledModel;
        this.atomic = false;
    }

    /**
     * Constructor for the input port where the model is an atomic model.
     * @param atomicModel AtomicModel&lt;A,R,T&gt;; the atomic model to which the port is added.
     */
    public InputPort(final AtomicModel<T> atomicModel)
    {
        this.model = atomicModel;
        this.atomic = true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized void receive(final TYPE value, final T time) throws RemoteException, SimRuntimeException
    {
        if (this.atomic)
        {
            // ATOMIC MODEL
            AtomicModel<T> atomicModel = (AtomicModel<T>) this.model;
            while (atomicModel.activePort != null)
            {
                this.model.getSimulator().getLogger().filter(Cat.DSOL)
                        .trace("receive: Waiting for event treatement // Another input is being processed");
                try
                {
                    Thread.sleep(1); // added because of infinite loop
                }
                catch (InterruptedException exception)
                {
                    // do nothing -- just wait till activePort != null
                }
            }

            if (atomicModel.activePort == null)
            {
                atomicModel.activePort = this;
                boolean passivity = true;
                SimEvent<T> nextEventCopy = null;
                this.model.getSimulator().getLogger().filter(Cat.DSOL).debug("receive: TIME IS {}",
                        this.model.getSimulator().getSimulatorTime());

                // Original: if (elapsedTime(time) - 0.000001 > timeAdvance())
                int etminta = DoubleCompare.compare(atomicModel.elapsedTime(time).doubleValue(),
                        atomicModel.timeAdvance().doubleValue());
                if (etminta == 1)
                {
                    this.model.getSimulator().getLogger().always().error("receive: {} - {}", atomicModel.elapsedTime(time),
                            atomicModel.timeAdvance());
                    this.model.getSimulator().getLogger().always()
                            .error("receive - IMPOSSIBLE !!! TIME SYNCHRONIZATION PROBLEM {}", atomicModel.toString());
                    System.err.println("IMPOSSIBLE !!! TIME SYNCHRONIZATION PROBLEM " + atomicModel.toString());
                }
                else
                {
                    if (etminta == 0 && atomicModel.elapsedTime(time).doubleValue() > 0.0) // 22-10-2009
                    {
                        atomicModel.setConflict(true);
                        passivity = false;
                        nextEventCopy = atomicModel.getNextEvent();
                    }
                    else
                    {
                        atomicModel.setConflict(false);
                        if (atomicModel.timeAdvance().doubleValue() != Double.POSITIVE_INFINITY)
                        {
                            passivity = false;
                            nextEventCopy = atomicModel.getNextEvent();
                        }
                        else
                        {
                            passivity = true;
                        }
                    }
                }
                if (atomicModel.isConflict())
                {
                    atomicModel.deltaConfluent(
                            SimTime.minus(this.model.getSimulator().getSimulatorTime(), atomicModel.getTimeLastEvent()), value);
                }
                else
                {
                    atomicModel.deltaExternalEventHandler(
                            SimTime.minus(this.model.getSimulator().getSimulatorTime(), atomicModel.getTimeLastEvent()), value);
                }
                if (!passivity)
                {
                    this.model.getSimulator().cancelEvent(nextEventCopy);
                }
            }
            atomicModel.activePort = null;
        }

        else

        {
            // COUPLED MODEL
            CoupledModel<T> coupledModel = (CoupledModel<T>) this.model;
            for (EIC<T, ?> o : coupledModel.externalInputCouplingSet)
            {
                if (o.getFromPort() == this)
                {
                    try
                    {
                        ((EIC<T, TYPE>) o).getToPort().receive(value, time);
                    }
                    catch (SimRuntimeException e)
                    {
                        this.model.getSimulator().getLogger().always().error(e);
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractDEVSModel<T> getModel()
    {
        return this.model;
    }
}
