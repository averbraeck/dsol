package nl.tudelft.simulation.dsol.experiment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * StreamSeedInformation stores information about the streams, but also about the way the seeds have to be updated for each
 * replication. The StreamSeedUpdater class uses this StreamSeedInformation class to make the seed updates.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StreamSeedInformation extends StreamInformation
{
    /** */
    private static final long serialVersionUID = 20210408L;

    /** the mapping of replication numbers to seeds for each of the streams, identified by id. */
    private Map<String, Map<Integer, Long>> streamSeedMap = new LinkedHashMap<>();

    /**
     * Construct a StreamInformation object that can be used to pass information about streams to to a model.
     */
    public StreamSeedInformation()
    {
        super();
    }

    /**
     * Construct a StreamInformation object that can be used to pass information about streams to to a model.
     * @param defaultStream StreamInterface; the default stream that can be retrieved with the name "default"
     * @throws NullPointerException when defaultStream is null
     */
    public StreamSeedInformation(final StreamInterface defaultStream)
    {
        super(defaultStream);
    }

    /**
     * Add a new seed map for a stream, based on a stream id, possibly overwriting a previous existing seed map with the same
     * name. No warning will be given is previous information is overwritten. A safe copy is made of the seed map.
     * @param streamId String; the id of the stream to be added
     * @param seedMap Map&lt;Integer, Long&gt;; the map of replication number to seed value for that replication
     * @throws NullPointerException when streamId is null or seedMap is null
     * @throws IllegalArgumentException when streamId is not present in the stream map
     */
    public void putSeedMap(final String streamId, final Map<Integer, Long> seedMap)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        Throw.whenNull(seedMap, "seedMap cannot be null");
        Throw.when(getStream(streamId) == null, IllegalArgumentException.class, "streamId not an existing stream");
        this.streamSeedMap.put(streamId, new LinkedHashMap<>(seedMap));
    }

    /**
     * Add a new seed map for a stream, based on a stream id, possibly overwriting a previous existing seed map with the same
     * name. No warning will be given is previous information is overwritten. Data is provided as a long array.
     * @param streamId String; the id of the stream to be added
     * @param seedArray long[]; an array of seed values for that replication using the replication number as index
     * @throws NullPointerException when streamId is null or seedArray is null
     * @throws IllegalArgumentException when streamId is not present in the stream map
     */
    public void putSeedArray(final String streamId, final long[] seedArray)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        Throw.whenNull(seedArray, "seedArray cannot be null");
        Throw.when(getStream(streamId) == null, IllegalArgumentException.class, "streamId not an existing stream");
        Map<Integer, Long> seedMap = new LinkedHashMap<>();
        for (int i = 0; i < seedArray.length; i++)
        {
            seedMap.put(i, seedArray[i]);
        }
        this.streamSeedMap.put(streamId, seedMap);
    }

    /**
     * Add a new seed map for a stream, based on a stream id, possibly overwriting a previous existing seed map with the same
     * name. No warning will be given is previous information is overwritten. Data is provided as a List of longs.
     * @param streamId String; the id of the stream to be added
     * @param seedList List&lt;Long&gt;; a list of seed values for that replication using the replication number as index
     * @throws NullPointerException when streamId is null or seedList is null
     * @throws IllegalArgumentException when streamId is not present in the stream map
     */
    public void putSeedList(final String streamId, final List<Long> seedList)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        Throw.whenNull(seedList, "seedList cannot be null");
        Throw.when(getStream(streamId) == null, IllegalArgumentException.class, "streamId not an existing stream");
        Map<Integer, Long> seedMap = new LinkedHashMap<>();
        for (int i = 0; i < seedList.size(); i++)
        {
            seedMap.put(i, seedList.get(i));
        }
        this.streamSeedMap.put(streamId, seedMap);
    }

    /**
     * Return the available seed maps for all stored streams, mapping stream ids to seed maps.
     * @return Map&lt;String, Map&lt;Integer, Long&gt;&gt;; the stored seed maps
     */
    public Map<String, Map<Integer, Long>> getStreamSeedMap()
    {
        return this.streamSeedMap;
    }

    /**
     * Return a specific seed map of a stream, based on a stream id, or null when the seed map is not present.
     * @param streamId String; the stream id of the seed map to be retrieved
     * @return Map&lt;Integer, Long&gt;; the seed map, or null when the seed map is not present
     * @throws NullPointerException when streamId is null
     */
    public Map<Integer, Long> getSeedMap(final String streamId)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        return this.streamSeedMap.get(streamId);
    }

}
