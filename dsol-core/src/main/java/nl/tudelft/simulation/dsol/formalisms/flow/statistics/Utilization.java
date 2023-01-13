package nl.tudelft.simulation.dsol.formalisms.flow.statistics;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.reference.ReferenceType;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.formalisms.flow.Station;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;

/**
 * A Utilization statistic for the flow components.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the absolute simulation time to use in the warmup event
 */
public class Utilization<T extends Number & Comparable<T>> extends SimPersistent<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** initialzed the tally. */
    private boolean initialized = false;

    /** the simulator. */
    private SimulatorInterface<T> simulator = null;

    /**
     * constructs a new Utilization.
     * @param description String; the description of this utilization
     * @param simulator SimulatorInterface&lt;T&gt;; the simulator
     * @param target Station&lt;T&gt;; the target
     * @throws RemoteException on network error for one of the listeners
     */
    public Utilization(final String description, final SimulatorInterface<T> simulator, final Station<T> target)
            throws RemoteException
    {
        super(description, simulator);
        this.simulator = simulator;
        target.addListener(this, Station.RECEIVE_EVENT, ReferenceType.STRONG);
        target.addListener(this, Station.RELEASE_EVENT, ReferenceType.STRONG);
        this.simulator.addListener(this, Replication.WARMUP_EVENT, ReferenceType.STRONG);
        this.simulator.addListener(this, Replication.END_REPLICATION_EVENT, ReferenceType.STRONG);
        // object is already bound, because SimPersistend (super) bound the statistic to the Context
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event)
    {
        if (event.getType().equals(Replication.WARMUP_EVENT))
        {
            this.initialized = true;
            try
            {
                this.simulator.removeListener(this, Replication.WARMUP_EVENT);
            }
            catch (RemoteException exception)
            {
                CategoryLogger.always().warn(exception);
            }
            super.initialize();
            return;
        }
        else if (this.initialized)
        {
            if (event.getType().equals(Replication.END_REPLICATION_EVENT))
            {
                super.endObservations(this.simulator.getSimulatorTime());
            }
            else
            {
                super.notify(event);
            }
        }
    }
}
