package nl.tudelft.simulation.dsol.formalisms.flow.statistics;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.reference.ReferenceType;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.formalisms.flow.FlowObject;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;

/**
 * A Utilization statistic for the flow components.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
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

    /**
     * constructs a new Utilization.
     * @param description String; the description of this utilization
     * @param model DsolModel&lt;T, SimulatorInterface&lt;T&gt;&gt;; the model
     * @param target FlowObject&lt;T&gt;; the target
     */
    public Utilization(final String description, final DsolModel<T, ? extends SimulatorInterface<T>> model,
            final FlowObject<T, ?> target)
    {
        super(description, model);
        try
        {
            target.addListener(this, FlowObject.RECEIVE_EVENT, ReferenceType.STRONG);
            target.addListener(this, FlowObject.RELEASE_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, Replication.WARMUP_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, Replication.END_REPLICATION_EVENT, ReferenceType.STRONG);
            // object is already bound, because SimPersistend (super) bound the statistic to the Context
        }
        catch (RemoteException exception)
        {
            getSimulator().getLogger().always().warn(exception, "<init>");
        }
    }

    @Override
    public void notify(final Event event)
    {
        if (event.getType().equals(Replication.WARMUP_EVENT))
        {
            this.initialized = true;
            try
            {
                getSimulator().removeListener(this, Replication.WARMUP_EVENT);
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
                super.endObservations(getSimulator().getSimulatorTime());
            }
            else
            {
                super.notify(event);
            }
        }
    }
}
