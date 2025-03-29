package nl.tudelft.simulation.dsol.formalisms.flow;

import org.djutils.base.Identifiable;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * Block is the abstract parent class for flow blocks such as Create, Seize and Delay, as well as non-flock blocks such as
 * Resource and Queue.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class Block<T extends Number & Comparable<T>> extends LocalEventProducer implements Identifiable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The simulator on which behavior is scheduled. */
    private DevsSimulatorInterface<T> simulator;

    /** The id of the Block. */
    private final String id;

    /**
     * Construct a new simulation block.
     * @param id the id of the block
     * @param simulator is the simulator on which behavior is scheduled
     */
    public Block(final String id, final DevsSimulatorInterface<T> simulator)
    {
        Throw.whenNull(id, "id cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        this.id = id;
        this.simulator = simulator;
        
        if (simulator.getModel() instanceof FlowModel<T, ?> flowModel)
        {
            flowModel.addBlock(this);
        }
    }

    /**
     * Return the simulator.
     * @return the simulator
     */
    public DevsSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

}
