package nl.tudelft.simulation.dsol.model.inputparameters;

import java.time.LocalDateTime;

/**
 * InputParameterLocalDateTime stores a parameter that contains a date and time, e.g. 2020-11-12T12:34. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterLocalDateTime extends AbstractInputParameter<LocalDateTime, LocalDateTime>
{
    /**
     * Construct a new InputParameterLong.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterLong
     * @param shortName concise description of the input parameter
     * @param description long description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterLocalDateTime(final String key, final String shortName, final String description,
            final LocalDateTime defaultValue, final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
    }

    @Override
    public LocalDateTime getCalculatedValue() throws InputParameterException
    {
        return getValue();
    }

    @Override
    public InputParameterLocalDateTime clone()
    {
        return (InputParameterLocalDateTime) super.clone();
    }

}
