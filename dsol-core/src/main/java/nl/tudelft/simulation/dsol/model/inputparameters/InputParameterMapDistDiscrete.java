package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterMapDistDiscrete is a InputParameterMap with a stream, a getDist() and a setDist() method. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class InputParameterMapDistDiscrete extends InputParameterMap
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the random number stream to use for the distribution. */
    private StreamInterface stream;

    /** the distribution based on the input. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistDiscrete dist;

    /**
     * Construct a new InputParameterMap.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterMap
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterMapDistDiscrete(final String key, final String shortName, final String description,
            final double displayPriority)
    {
        super(key, shortName, description, displayPriority);
    }

    /**
     * Calculate the distribution for this parameter entry.
     * @throws InputParameterException when the distribution based on the parameter map cannot be constructed
     */
    public abstract void setDist() throws InputParameterException;

    /**
     * Return the previously calculated typed value based on the components.
     * @return T; the previously calculated typed value based on the components
     * @throws InputParameterException when the value has not been calculated
     */
    public DistDiscrete getDist() throws InputParameterException
    {
        Throw.when(this.dist == null, InputParameterException.class,
                "Value for parameter " + getShortName() + " has not yet been calculated");
        return this.dist;
    }

    /**
     * @return stream
     */
    public StreamInterface getStream()
    {
        return this.stream;
    }

    /**
     * @param stream StreamInterface; set stream
     */
    public void setStream(final StreamInterface stream)
    {
        this.stream = stream;
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterMapDistDiscrete clone()
    {
        InputParameterMapDistDiscrete clonedMap = (InputParameterMapDistDiscrete) super.clone();
        clonedMap.dist = this.dist;
        return clonedMap;
    }

}
