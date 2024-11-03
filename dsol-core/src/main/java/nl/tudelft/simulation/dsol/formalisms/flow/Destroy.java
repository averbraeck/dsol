package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Destroy flow block where entities will be destroyed from the model.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author Peter Jacobs
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type itself to be able to implement a comparator on the simulation time.
 */
public class Destroy<T extends Number & Comparable<T>> extends FlowObject<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** TIME_IN_SYSTEM_EVENT is fired when an entity leaves the system. */
    public static final EventType TIME_IN_SYSTEM_EVENT = new EventType(new MetaData("TIME_IN_SYSTEM_EVENT", "Time in system",
            new ObjectDescriptor("Time in system", "time in system", Double.class)));

    /**
     * Construct a Destroy flow block.
     * @param id String; the id of the Destroy flow block
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator
     */
    public Destroy(final String id, final DevsSimulatorInterface<T> simulator)
    {
        super(id, simulator);
    }

    @Override
    public synchronized void receiveEntity(final Entity<T> entity)
    {
        super.receiveEntity(entity);
        fireTimedEvent(TIME_IN_SYSTEM_EVENT,
                getSimulator().getSimulatorTime().doubleValue() - entity.getCreationTime().doubleValue(),
                getSimulator().getSimulatorTime());
    }

}
