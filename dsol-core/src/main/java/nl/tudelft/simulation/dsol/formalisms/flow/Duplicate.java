package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.rmi.MarshalledObject;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * A duplicate station duplicates incoming objects and sends them to their alternative destination.
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
public class Duplicate<T extends Number & Comparable<T>> extends Station<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** DuplicateDestination which is the duplicate definition. */
    private Station<T> duplicateDestination;

    /** numberCopies refers to the number of duplicates. */
    private int numberCopies;

    /**
     * Creates a new Duplicate that makes 1 copy.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; on which is scheduled
     * @param duplicateDestination Station&lt;A,R,T&gt;; the duplicate destination
     */
    public Duplicate(final Serializable id, final DevsSimulatorInterface<T> simulator,
            final Station<T> duplicateDestination)
    {
        this(id, simulator, duplicateDestination, 1);
    }

    /**
     * Create a new Duplicate that makes numberCopies copies.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; on which is scheduled
     * @param duplicateDestination Station&lt;A,R,T&gt;; which is the duplicate definition
     * @param numberCopies int; the number of copies
     */
    public Duplicate(final Serializable id, final DevsSimulatorInterface<T> simulator,
            final Station<T> duplicateDestination, final int numberCopies)
    {
        super(id, simulator);
        this.duplicateDestination = duplicateDestination;
        this.numberCopies = numberCopies;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void receiveObject(final Object object)
    {
        super.receiveObject(object);
        try
        {
            this.releaseObject(object);
            if (object instanceof Serializable)
            {
                for (int i = 0; i < this.numberCopies; i++)
                {
                    Object clone = new MarshalledObject<Object>(object).get();
                    this.fireTimedEvent(Station.RELEASE_EVENT, 1, getSimulator().getSimulatorTime());
                    this.duplicateDestination.receiveObject(clone);
                }
            }
            else
            {
                throw new Exception(
                        "cannot duplicate object: " + object.getClass() + " does not implement java.io.Serializable");
            }
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "receiveMethod");
        }
    }

}
