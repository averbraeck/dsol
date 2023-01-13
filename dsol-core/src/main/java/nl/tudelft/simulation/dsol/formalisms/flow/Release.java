package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The release station releases a given quantity of a claimed resource.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Release<T extends Number & Comparable<T>> extends Station<T>
{
    /** */
    private static final long serialVersionUID = 20151028L;

    /** resource refers to the resource released. */
    private Resource<T> resource;

    /** amount defines the amount to be released. */
    private double amount = 1.0;

    /**
     * Constructor for Release.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; on which is scheduled
     * @param resource Resource&lt;A,R,T&gt;; which is released
     */
    public Release(final Serializable id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource)
    {
        this(id, simulator, resource, 1.0);
    }

    /**
     * Constructor for Release.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; on which is scheduled
     * @param resource Resource&lt;A,R,T&gt;; which is released
     * @param amount double; of resource which is released
     */
    public Release(final Serializable id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource,
            final double amount)
    {
        super(id, simulator);
        this.resource = resource;
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void receiveObject(final Object object)
    {
        super.receiveObject(object);
        try
        {
            this.resource.releaseCapacity(this.amount);
            this.releaseObject(object);
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "receiveObject");
        }
    }

}
