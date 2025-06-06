package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterDistDiscrete provides a choice for a discrete distribution.<br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterDistDiscrete extends AbstractInputParameter<DistDiscrete, DistDiscrete>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the random number stream to use for the distribution. */
    private StreamInterface stream;

    /**
     * Construct a new InputParameterDistDiscrete.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterDistDiscrete
     * @param shortName concise description of the input parameter
     * @param description long description of the input parameter (may use HTML markup)
     * @param stream the random number stream to use for the chosen distribution
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws InputParameterException in case the default value is not part of the list
     * @throws NullPointerException when key, shortName, defaultValue, description, or stream is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterDistDiscrete(final String key, final String shortName, final String description,
            final StreamInterface stream, final DistDiscrete defaultValue, final double displayPriority)
            throws InputParameterException
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(stream, "stream cannot be null");
        this.stream = stream;
    }

    @Override
    public DistDiscrete getCalculatedValue() throws InputParameterException
    {
        return getValue();
    }

    /**
     * @return stream
     */
    public StreamInterface getStream()
    {
        return this.stream;
    }

    /**
     * @param stream set stream
     * @throws NullPointerException when stream is null
     */
    public void setStream(final StreamInterface stream)
    {
        Throw.whenNull(stream, "stream cannot be null");
        this.stream = stream;
        getDefaultValue().setStream(stream);
        getValue().setStream(stream);
    }

    /**
     * Set the value of the distribution.
     * @param dist the distribution to set the value to
     * @throws NullPointerException when dist is null
     * @throws InputParameterException when this InputParameter is read-only, or dist is not valid
     */
    public void setDistValue(final DistDiscrete dist) throws InputParameterException
    {
        Throw.whenNull(dist, "dist cannot be null");
        setValue(dist);
    }

    @Override
    public InputParameterDistDiscrete clone()
    {
        return (InputParameterDistDiscrete) super.clone();
    }
}
