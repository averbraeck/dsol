package nl.tudelft.simulation.dsol.formalisms.flow;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Duplicate flow block makes a number of copies of incoming entities and sends them to a destination.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public class Duplicate<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** DuplicateDestination which is the duplicate definition. */
    private FlowObject<T> duplicateDestination;

    /** numberCopies refers to the number of duplicates. */
    private int numberCopies;

    /**
     * Create a new Duplicate flow block that makes 1 copy.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param duplicateDestination FlowObject&lt;T&gt;; the duplicate destination
     */
    public Duplicate(final String id, final DevsSimulatorInterface<T> simulator, final FlowObject<T> duplicateDestination)
    {
        this(id, simulator, duplicateDestination, 1);
    }

    /**
     * Create a new Duplicate flock block that makes numberCopies copies.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; on which is scheduled
     * @param duplicateDestination FlowObject&lt;T&gt;; which is the duplicate definition
     * @param numberCopies int; the number of copies
     */
    public Duplicate(final String id, final DevsSimulatorInterface<T> simulator, final FlowObject<T> duplicateDestination,
            final int numberCopies)
    {
        super(id, simulator);
        this.duplicateDestination = duplicateDestination;
        this.numberCopies = numberCopies;
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        super.receiveEntity(entity);
        this.releaseEntity(entity);
        for (int i = 0; i < this.numberCopies; i++)
        {
            Entity<T> clone = entity.clone();
            this.fireTimedEvent(FlowObject.RELEASE_EVENT, clone, getSimulator().getSimulatorTime());
            this.duplicateDestination.receiveEntity(clone);
        }
    }

}
