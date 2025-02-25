package nl.tudelft.simulation.dsol.experiment;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * StreamInformation contains information about Random Streams that exists before the model has been constructed.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class StreamInformation implements Serializable
{
    /** */
    private static final long serialVersionUID = 20210408L;

    /** streams used in the replication. */
    private Map<String, StreamInterface> streams = new LinkedHashMap<String, StreamInterface>();

    /**
     * Construct a StreamInformation object that can be used to pass information about streams to to a model.
     */
    public StreamInformation()
    {
        this(new MersenneTwister(10L));
    }

    /**
     * Construct a StreamInformation object that can be used to pass information about streams to to a model.
     * @param defaultStream StreamInterface; the default stream that can be retrieved with the name "default"
     * @throws NullPointerException when defaultStream is null
     */
    public StreamInformation(final StreamInterface defaultStream)
    {
        Throw.whenNull(defaultStream, "defaultStream cannot be null");
        this.streams.put("default", defaultStream);
    }

    /**
     * Add a new stream, based on a stream id, possibly overwriting a previous existing stream with the same name. No warning
     * will be given if previous information is overwritten.
     * @param streamId String; the id of the stream to be added
     * @param stream StreamInterface; the stream
     * @throws NullPointerException when streamId is null or stream is null
     */
    public void addStream(final String streamId, final StreamInterface stream)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        Throw.whenNull(stream, "stream cannot be null");
        this.streams.put(streamId, stream);
    }

    /**
     * Return the streams of this model, mapping stream ids to streams.
     * @return Map&lt;String, StreamInterface&gt;; the stored streams
     */
    public Map<String, StreamInterface> getStreams()
    {
        return this.streams;
    }

    /**
     * Return a specific stream, based on a stream id, or null when no stream with that id is present.
     * @param streamId String; the id of the stream to be retrieved
     * @return StreamInterface; the stream, or null when no stream with that id is present
     * @throws NullPointerException when streamId is null
     */
    public StreamInterface getStream(final String streamId)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        return this.streams.get(streamId);
    }

}
