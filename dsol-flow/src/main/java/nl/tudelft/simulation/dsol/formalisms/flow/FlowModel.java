package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.Map;

import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * FlowModel.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @param <S> the simulator type to use
 */
public interface FlowModel<T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends DsolModel<T, S>
{
    /**
     * Return the map with block ids and simulation blocks in this flow model.
     * @return the map with block ids and simulation blocks in this flow model
     */
    Map<String, Block<T>> getBlockMap();

    /**
     * Add a new block to the model. Adding blocks is done in the constructor of the Block object itself.
     * @param block the new block to add to the model
     */
    void addBlock(final Block<T> block);

    /** The block naming type enum: id-based or automatic. */
    public enum BlockNamingType
    {
        /** Naming using the ids of the blocks, which have to be unique. */
        BY_ID,

        /** Naming using an automatically allocated id. */
        AUTOMATIC;
    }
}
