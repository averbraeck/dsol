package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Delay object is a flow object that delays an entity for a given time. After the time delay, the entity is released to the
 * next flow object. During the time delay, the entity is held in the delay object.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Delay<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the distribution defining the delay. */
    private DistContinuousSimulationTime<T> delayDistribution;

    /**
     * Construct a new Delay flow block.
     * @param id String; the id of the Delay flow object
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     * @param delayDistribution DistContinuousSimulationTime&lt;T&gt;; the delay distribution
     */
    public Delay(final String id, final DevsSimulatorInterface<T> simulator,
            final DistContinuousSimulationTime<T> delayDistribution)
    {
        super(id, simulator);
        Throw.whenNull(delayDistribution, "delayDistribution cannot be null");
        this.delayDistribution = delayDistribution;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        super.receiveEntity(entity);
        try
        {
            this.simulator.scheduleEventRel(this.delayDistribution.draw(), this, "releaseEntity", new Object[] {entity});
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "receiveObject");
        }
    }

}
