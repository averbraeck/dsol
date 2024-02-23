package nl.tudelft.simulation.dsol.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimulationStatistic;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The model interface defines the model object. Since version 2.1.0 of DSOL, the DsolModel now knows its simulator and can
 * return it to anyone interested. Through the Simulator, the Replication can be requested and through that the Experiment and
 * the Treatment under which the simulation is running.
 * <p>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <T> the time type
 * @param <S> the simulator to use
 */
public interface DsolModel<T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends Serializable
{
    /**
     * construct a model on a simulator.
     * @throws SimRuntimeException on model construction failure
     */
    void constructModel() throws SimRuntimeException;

    /**
     * Return the simulator for this model.
     * @return the simulator for the model
     */
    S getSimulator();

    /**
     * Get the input parameters for this model.
     * @return List&lt;InputParameter&gt; the input parameters for this model
     */
    InputParameterMap getInputParameterMap();

    /**
     * Get the output statistics for this model.
     * @return List&lt;StatisticsInterface&gt; the output statistics for this model
     */
    List<SimulationStatistic<T>> getOutputStatistics();

    /**
     * Set the initial streams of the model based on a StreamInformation object. This method can be called right after the
     * construction of the model object, or just before the model is constructed. <br>
     * <u>Note 1:</u> If a model is run as part of an Experiment, the seeds of the random streams will be reset just before the
     * call to constructModel(), so <b>do not call this method from constructModel()</b>, as it will reset the seeds to their
     * initial values, and undo the seed management of the Experiment.<br>
     * <u>Note 2:</u> The original streams are copied into the model, so they are not cloned (as the streams do not implement
     * cloneable, and as they have inner state that needs to be preserved). So be careful with manipulating the streams in the
     * streamInformation object afterward.
     * @param streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     * @throws NullPointerException when streamInformation is null
     */
    void setStreamInformation(StreamInformation streamInformation);

    /**
     * Return the available streams of the model stored in a StreamInformation object.
     * @return streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     */
    StreamInformation getStreamInformation();

    /**
     * Return the streams of this model, mapping stream ids to streams.
     * @return Map&lt;String, StreamInterface&gt;; the streams of this model
     */
    default Map<String, StreamInterface> getStreams()
    {
        return getStreamInformation().getStreams();
    }

    /**
     * Return a specific stream of this model, based on a stream id, or null when the stream could not be found.
     * @param streamId String; the id of the stream to be retrieved
     * @return StreamInterface the stream, or null when the stream could not be found
     * @throws NullPointerException when streamId is null
     */
    default StreamInterface getStream(final String streamId)
    {
        Throw.whenNull(streamId, "streamId cannot be null");
        synchronized (getStreamInformation())
        {
            return getStreams().get(streamId);
        }
    }

    /**
     * Return the default streams of this model.
     * @return StreamInterface; the default stream of this model
     */
    default StreamInterface getDefaultStream()
    {
        return getStreamInformation().getStream("default");
    }

    /**
     * Reset the streams to their original seed values.
     */
    default void resetStreams()
    {
        synchronized (getStreamInformation())
        {
            for (StreamInterface stream : getStreams().values())
            {
                stream.reset();
            }
        }
    }

}
