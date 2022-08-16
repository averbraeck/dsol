package nl.tudelft.simulation.dsol.experiment;

import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * StreamSeedUpdater updates the streams based on a stored map of replication numbers to seed numbers.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StreamSeedUpdater implements StreamUpdater
{
    /** */
    private static final long serialVersionUID = 20210408L;

    /** the mapping of replication numbers to seeds for each of the streams, identified by id. */
    private final Map<String, Map<Integer, Long>> streamSeedMap = new LinkedHashMap<>();

    /** the fallback stream updater in case the stream or the replication is not in the seed map. */
    private StreamUpdater fallbackStreamUpdater;

    /**
     * Construct a new StreamSeedUpdater object an initialize it with the seed map.
     * @param streamSeedMap Map&lt;String, Map&lt;Integer, Long&gt;&gt;; the stored seed maps
     */
    public StreamSeedUpdater(final Map<String, Map<Integer, Long>> streamSeedMap)
    {
        this.streamSeedMap.putAll(streamSeedMap);
        this.fallbackStreamUpdater = new SimpleStreamUpdater();
    }

    /** {@inheritDoc} */
    @Override
    public void updateSeed(final String streamId, final StreamInterface stream, final int replicationNumber)
    {
        if (this.streamSeedMap.containsKey(streamId) && this.streamSeedMap.get(streamId).containsKey(replicationNumber))
        {
            stream.setSeed(this.streamSeedMap.get(streamId).get(replicationNumber));
        }
        else
        {
            // streamId not found in seed map -- fall back to other method
            this.fallbackStreamUpdater.updateSeed(streamId, stream, replicationNumber);
        }
    }

    /**
     * Return the fallback stream updater in case the stream or the replication is not in the seed map.
     * @return fallbackStreamUpdater StreamUpdater; the fallback stream updater in case the stream or the replication is not in
     *         the seed map.
     */
    public StreamUpdater getFallbackStreamUpdater()
    {
        return this.fallbackStreamUpdater;
    }

    /**
     * Set a new fallback stream updater in case the stream or the replication is not in the seed map.
     * @param fallbackStreamUpdater StreamUpdater; the new fallback stream updater in case the stream or the replication is not
     *            in the seed map.
     * @throws NullPointerException when fallbackStreamUpdater is null
     */
    public void setFallbackStreamUpdater(final StreamUpdater fallbackStreamUpdater)
    {
        Throw.whenNull(fallbackStreamUpdater, "fallbackStreamUpdater cannot be null");
        this.fallbackStreamUpdater = fallbackStreamUpdater;
    }

    /**
     * Return the available seed maps for all stored streams, mapping stream ids to seed maps.
     * @return Map&lt;String, Map&lt;Integer, Long&gt;&gt;; the stored seed maps
     */
    public Map<String, Map<Integer, Long>> getStreamSeedMap()
    {
        return this.streamSeedMap;
    }

}
