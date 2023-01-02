package nl.tudelft.simulation.dsol.model;

import java.util.ArrayList;
import java.util.List;

import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.inputparameters.AbstractInputParameter;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.StatisticsInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;

/**
 * AbstractDSOLModel, an abstract helper class to easily construct a DSOLModel. The model automatically acts as an
 * EventProducer, so events can be sent to statistics gathering classes. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <S> the simulator type to use
 */
public abstract class AbstractDSOLModel<T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends LocalEventProducer
        implements DSOLModel<T, S>
{
    /** */
    private static final long serialVersionUID = 20181117L;

    /** the simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected S simulator;

    /** the input parameters. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected InputParameterMap inputParameterMap = new InputParameterMap("model", "Model parameters", "Model parameters", 1.0);

    /** the output statistics. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected List<StatisticsInterface<T>> outputStatistics = new ArrayList<>();

    /** streams used in the replication. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected StreamInformation streamInformation;

    /**
     * Construct a DSOL model and set the simulator.
     * @param simulator S; the simulator to use for this model
     * @throws NullPointerException when simulator is null
     */
    public AbstractDSOLModel(final S simulator)
    {
        this(simulator, new StreamInformation(new MersenneTwister(10L)));
    }

    /**
     * Construct a DSOL model and set the simulator as well as the initial streams, so they can be used in the constructor of
     * the model.
     * @param simulator S; the simulator to use for this model
     * @param streamInformation StreamInformation; the streams that have been prepared in a StreamInformation class
     * @throws NullPointerException when simulator or streamInformation is null
     */
    public AbstractDSOLModel(final S simulator, final StreamInformation streamInformation)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        this.simulator = simulator;
        setStreamInformation(streamInformation);
    }

    /** {@inheritDoc} */
    @Override
    public void setStreamInformation(final StreamInformation streamInformation)
    {
        Throw.whenNull(streamInformation, "streamInformation cannot be null");
        this.streamInformation = streamInformation;
    }

    /** {@inheritDoc} */
    @Override
    public StreamInformation getStreamInformation()
    {
        return this.streamInformation;
    }

    /** {@inheritDoc} */
    @Override
    public S getSimulator()
    {
        return this.simulator;
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterMap getInputParameterMap()
    {
        return this.inputParameterMap;
    }

    /**
     * Add an input parameter to the list of input parameters.
     * @param inputParameter AbstractInputParameter&lt;?,?&gt;; the input parameter to add
     * @throws InputParameterException in case an input parameter with the same key already exists
     */
    public void addInputParameter(final AbstractInputParameter<?, ?> inputParameter) throws InputParameterException
    {
        this.inputParameterMap.add(inputParameter);
    }

    /**
     * Retrieve the value of an input parameter from the map of input parameters, based on a key. The key can use the 'dot
     * notation' to access values in sub-maps of input parameters.
     * @param key String; the key of the input parameter to retrieve
     * @return the value belonging to the key, or null if the key could not be found
     * @throws InputParameterException in case the input parameter with this key does not exist
     */
    public Object getInputParameter(final String key) throws InputParameterException
    {
        return this.inputParameterMap.get(key).getCalculatedValue();
    }

    /** {@inheritDoc} */
    @Override
    public List<StatisticsInterface<T>> getOutputStatistics()
    {
        return this.outputStatistics;
    }

}
