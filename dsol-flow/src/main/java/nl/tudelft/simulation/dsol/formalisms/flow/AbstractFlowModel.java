package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.DsolRuntimeException;

/**
 * AbstractFlowModel offers a base for a block-based model that registers the blocks it is using.
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
public abstract class AbstractFlowModel<T extends Number & Comparable<T>, S extends SimulatorInterface<T>>
        extends AbstractDsolModel<T, S> implements FlowModel<T, S>
{
    /** */
    private static final long serialVersionUID = 20250323L;

    /** counter of the blocks when automatic naming is used. */
    protected Map<String, AtomicInteger> countMap = new HashMap<>();

    /** the blocks in the model. */
    protected Map<String, Block<T>> blockMap = new HashMap<>();

    /** the naming regime: id-based or automatic. */
    protected BlockNamingType blockNamingType = BlockNamingType.AUTOMATIC;

    /**
     * Construct a DSOL model and set the simulator.
     * @param simulator S; the simulator to use for this model
     * @param blockNamingType the naming regime: BY_ID or AUTOMATIC
     * @throws NullPointerException when simulator or blockNamingType is null
     */
    public AbstractFlowModel(final S simulator, final BlockNamingType blockNamingType)
    {
        super(simulator);
        Throw.whenNull(blockNamingType, "blockNamingType cannot be null");
        this.blockNamingType = blockNamingType;
    }

    /**
     * Construct a DSOL model and set the simulator as well as the initial streams, so they can be used in the constructor of
     * the model.
     * @param simulator S; the simulator to use for this model
     * @param streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     * @param blockNamingType the naming regime: BY_ID or AUTOMATIC
     * @throws NullPointerException when simulator or streamInformation or blockNamingType is null
     */
    public AbstractFlowModel(final S simulator, final StreamInformation streamInformation,
            final BlockNamingType blockNamingType)
    {
        super(simulator, streamInformation);
        Throw.whenNull(blockNamingType, "blockNamingType cannot be null");
        this.blockNamingType = blockNamingType;
    }

    /**
     * Return the naming regime: id-based or automatic.
     * @return the naming regime: BY_ID or AUTOMATIC
     */
    public BlockNamingType getBlockNamingType()
    {
        return this.blockNamingType;
    }

    /**
     * Return the map with block ids and simulation blocks in this flow model.
     * @return the map with block ids and simulation blocks in this flow model
     */
    @Override
    public Map<String, Block<T>> getBlockMap()
    {
        return this.blockMap;
    }

    /**
     * Add a new block to the model. Adding blocks is done in the constructor of the Block object itself.
     * @param block the new block to add to the model
     */
    @Override
    public void addBlock(final Block<T> block)
    {
        String id;
        if (this.blockNamingType.equals(BlockNamingType.BY_ID))
        {
            id = block.getId();
            Throw.when(this.blockMap.containsKey(id), DsolRuntimeException.class, "block id " + id + " already registered");
        }
        else
        {
            id = block.getClass().getSimpleName();
            if (!this.countMap.containsKey(id))
            {
                this.countMap.put(id, new AtomicInteger(0));
            }
            int nr = this.countMap.get(id).incrementAndGet();
            id += "_" + nr;
        }
        this.blockMap.put(id, block);
    }

}
