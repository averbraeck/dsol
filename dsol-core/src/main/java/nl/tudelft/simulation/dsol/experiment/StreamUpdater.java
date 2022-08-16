package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;
import java.util.Map;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The StreamUpdater interface describes how to update the seed values for the next replication.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface StreamUpdater extends Serializable
{
    /**
     * Update all seeds for the given replication number. The method should be fully reproducible, and can be based on the
     * previous seed values, possibly the String representation, and the replication number.
     * @param streams Map&lt;String, StreamIterface&gt;; the map of the streams for the replication
     * @param replicationNumber int; the replication number for which to set the seed values
     */
    default void updateSeeds(Map<String, StreamInterface> streams, int replicationNumber)
    {
        for (Map.Entry<String, StreamInterface> entry : streams.entrySet())
        {
            updateSeed(entry.getKey(), entry.getValue(), replicationNumber);
        }
    }

    /**
     * Update one seed for the given streamId and replication number. The method should be fully reproducible, and can be based
     * on the previous seed value of the stream, possibly the String representation, and the replication number.
     * @param streamId String; the id of the stream to update
     * @param stream StreamInterface; the stream to update for this replication
     * @param replicationNumber int; the replication number for which to set the seed value
     */
    void updateSeed(String streamId, StreamInterface stream, int replicationNumber);

}
