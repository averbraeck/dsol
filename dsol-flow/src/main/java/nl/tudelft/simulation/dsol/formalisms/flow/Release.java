package nl.tudelft.simulation.dsol.formalisms.flow;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Release flow object releases a given quantity of a claimed resource.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Release<T extends Number & Comparable<T>> extends FlowBlock<T, Release<T>>
{
    /** */
    private static final long serialVersionUID = 20151028L;

    /** resource refers to the resource released. */
    private Resource<T, ?> resource;

    /** amount defines the amount to be released. */
    private double amount = 1.0;

    /**
     * Construct a Release flow object to release seized resource units.
     * @param id the id of the FlowObject
     * @param simulator on which is scheduled
     * @param resource which is released
     */
    public Release(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T, ?> resource)
    {
        this(id, simulator, resource, 1.0);
    }

    /**
     * Constructor for Release.
     * @param id the id of the FlowObject
     * @param simulator on which is scheduled
     * @param resource which is released
     * @param amount of resource which is released
     */
    public Release(final String id, final DevsSimulatorInterface<T> simulator, final Resource<T, ?> resource,
            final double amount)
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
            ((Resource.DoubleCapacity<T>) this.resource).releaseCapacity(this.amount);
            this.releaseEntity(entity);
        }
        catch (Exception exception)
        {
            getSimulator().getLogger().always().warn(exception, "receiveEntity");
        }
    }

}
