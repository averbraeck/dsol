package nl.tudelft.simulation.dsol.formalisms.flow;

import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Release flow object releases a given quantity of a claimed resource.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Release<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    private static final long serialVersionUID = 20151028L;

    /** resource refers to the resource released. */
    private Resource<T> resource;

    /** amount defines the amount to be released. */
    private double amount = 1.0;

    /**
     * Construct a Release flow object to release seized resource units.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param resource Resource&lt;T&gt;; which is released
     */
    public Release(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource)
    {
        this(id, simulator, resource, 1.0);
    }

    /**
     * Constructor for Release.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param resource Resource&lt;T&gt;; which is released
     * @param amount double; of resource which is released
     */
    public Release(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T> resource, final double amount)
    {
        super(id, simulator);
        this.resource = resource;
        this.amount = amount;
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        super.receiveEntity(entity);
        try
        {
            this.resource.releaseCapacity(this.amount);
            this.releaseEntity(entity);
        }
        catch (Exception exception)
        {
            getSimulator().getLogger().always().warn(exception, "receiveEntity");
        }
    }

}
