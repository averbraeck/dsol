package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * The Delay object is a station which delays an entity by some time units. When an entity arrives at a delay object, dsol
 * delays the entity by the resulting time period. During the time delay, the entity is held in the delay object.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Delay<T extends Number & Comparable<T>> extends Station<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** delayDistribution which is the distribution defining the delay. */
    private DistContinuousSimulationTime<T> delayDistribution;

    /**
     * Constructor for Delay.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; is the simulator
     * @param delayDistribution DistContinuousSimulationTime&lt;R&gt;; is the delayDistribution
     */
    public Delay(final Serializable id, final DEVSSimulatorInterface<T> simulator,
            final DistContinuousSimulationTime<T> delayDistribution)
    {
        super(id, simulator);
        this.delayDistribution = delayDistribution;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void receiveObject(final Object object)
    {
        super.receiveObject(object);
        try
        {
            this.simulator.scheduleEventRel(this.delayDistribution.draw(), this, this, "releaseObject", new Object[] {object});
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "receiveObject");
        }
    }

}
