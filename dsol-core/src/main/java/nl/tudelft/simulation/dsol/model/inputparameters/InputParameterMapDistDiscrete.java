package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterMapDistDiscrete is a InputParameterMap with a stream, a getDist() and a setDist() method. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class InputParameterMapDistDiscrete extends InputParameterMap
{
    /** the random number stream to use for the distribution. */
    private StreamInterface stream;

    /** the distribution based on the input. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DistDiscrete dist;

    /**
     * Construct a new InputParameterMap.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterMap
     * @param shortName concise description of the input parameter
     * @param description long description of the input parameter (may use HTML markup)
     * @param displayPriority sorting order when properties are displayed to the user
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
     * @return the previously calculated typed value based on the components
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
     * @param stream set stream
     */
    public void setStream(final StreamInterface stream)
    {
        this.stream = stream;
    }

    @Override
    public InputParameterMapDistDiscrete clone()
    {
        InputParameterMapDistDiscrete clonedMap = (InputParameterMapDistDiscrete) super.clone();
        clonedMap.dist = this.dist;
        return clonedMap;
    }

}
