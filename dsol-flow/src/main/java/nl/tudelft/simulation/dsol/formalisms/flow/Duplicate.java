package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.function.Consumer;

import org.djutils.event.EventType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;

/**
 * The Duplicate flow block makes a number of copies of incoming entities and sends them to a destination.
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
public class Duplicate<T extends Number & Comparable<T>> extends FlowBlock<T, Duplicate<T>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The destination of the duplicates. */
    private FlowBlock<T, ?> duplicateDestination;

    /** The distribution of the number of duplicates to generate. */
    private DistDiscrete numberCopiesDist = null;

    /** the function to apply just before releasing a duplicate entity. */
    private Consumer<Entity<T>> duplicateReleaseFunction = null;

    /** a potential count statistic for the number of released duplicate entities; when null, no statistic is calculated. */
    private SimCounter<T> countDuplicateReleasedStatistic;

    /** DUPLICATE_RELEASE_EVENT is fired whenever a duplicate entity leaves the flow object. */
    public static final EventType DUPLICATE_RELEASE_EVENT = new EventType(new MetaData("DUPLICATE_RELEASE_EVENT",
            "Duplicate entity released",
            new ObjectDescriptor("releasedDuplicateEntity", "number of released duplicate entities (1)", Integer.class)));

    /**
     * Create a new Duplicate flow block.
     * @param id the id of the FlowObject
     * @param simulator on which is scheduled
     */
    public Duplicate(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    /**
     * Set the destination of the duplicate entity / entities. The destination can be null, in that case the duplicates are
     * destroyed after creation.
     * @param duplicateDestination the new destination of the duplicate entities
     * @return this object for method chaining
     */
    public Duplicate<T> setDuplicateDestination(final FlowBlock<T, ?> duplicateDestination)
    {
        this.duplicateDestination = duplicateDestination;
        return this;
    }

    /**
     * Set the distribution of the number of copies to generate.
     * @param numberCopiesDist set numberCopiesDist
     * @return this object for method chaining
     */
    public Duplicate<T> setNumberCopiesDist(final DistDiscrete numberCopiesDist)
    {
        Throw.whenNull(numberCopiesDist, "distribution for number copies cannot be null");
        this.numberCopiesDist = numberCopiesDist;
        return this;
    }

    /**
     * Set a fixed number of copies to generate.
     * @param numberCopies set the fixed number of copies to generate
     * @return this object for method chaining
     */
    public Duplicate<T> setNumberCopies(final int numberCopies)
    {
        Throw.when(numberCopies < 0, IllegalArgumentException.class, "number of copies cannot be negative");
        this.numberCopiesDist = new DistDiscreteConstant(getSimulator().getModel().getDefaultStream(), numberCopies);
        return this;
    }

    /**
     * Set the function to apply just before releasing a duplicate entity.
     * @param duplicateReleaseFunction the function to apply just before releasing a duplicate entity, can be null to remove an
     *            existing duplicate release function
     * @return this object for method chaining
     */
    public Duplicate<T> setDuplicateReleaseFunction(final Consumer<Entity<T>> duplicateReleaseFunction)
    {
        this.duplicateReleaseFunction = duplicateReleaseFunction;
        return this;
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        Throw.whenNull(this.numberCopiesDist, "distribution for number copies cannot be null when running");
        super.receiveEntity(entity);
        this.releaseEntity(entity);
        long nr = this.numberCopiesDist.draw();
        Throw.when(nr < 0, IllegalArgumentException.class, "number of copies cannot be negative");
        for (int i = 0; i < nr; i++)
        {
            Entity<T> clone = entity.clone();
            this.fireTimedEvent(DUPLICATE_RELEASE_EVENT, 1, getSimulator().getSimulatorTime());
            if (this.duplicateReleaseFunction != null)
                this.duplicateReleaseFunction.accept(clone);
            if (this.duplicateDestination != null)
                this.duplicateDestination.receiveEntity(clone);
        }
    }

    /**
     * Turn on the default statistics for this flow block.
     * @return the Duplicate instance for method chaining
     */
    public Duplicate<T> setDefaultStatistics()
    {
        if (!hasDefaultStatistics())
        {
            super.setDefaultFlowBlockStatistics();
            this.countDuplicateReleasedStatistic = new SimCounter<>(getId() + " # of released duplicate entities",
                    getSimulator().getModel(), this, DUPLICATE_RELEASE_EVENT);
            this.countDuplicateReleasedStatistic.initialize();
        }
        return this;
    }

    /**
     * Return whether statistics are turned on for this Duplicate block.
     * @return whether statistics are turned on for this Duplicate block.
     */
    public boolean hasDefaultStatistics()
    {
        return this.countDuplicateReleasedStatistic != null;
    }

    /**
     * Return the count statistic for the number of released objects
     * @return the count statistic for the number of released objects, can be null if no statistics are calculated.
     */
    public SimCounter<T> getCountDuplicateReleasedStatistic()
    {
        return this.countDuplicateReleasedStatistic;
    }

    /**
     * Return the destination flow block where the duplicates go.
     * @return the destination flow block where the duplicates go, can be null if not yet set
     */
    public FlowBlock<T, ?> getDuplicateDestination()
    {
        return this.duplicateDestination;
    }

    /**
     * Return the distribution of the number of copies.
     * @return the distribution of the number of copies, can be null if not yet set
     */
    public DistDiscrete getNumberCopiesDist()
    {
        return this.numberCopiesDist;
    }

}
