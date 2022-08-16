package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * InputParameterLong.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterLong extends AbstractInputParameter<Long, Long>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Format string to display the value of the input parameter. */
    private String format = "%d";

    /** The minimum value of the input parameter. */
    private long minimumValue = -Long.MAX_VALUE;

    /** The maximum value of the input parameter. */
    private long maximumValue = Long.MAX_VALUE;

    /**
     * Construct a new InputParameterLong.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterLong
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param defaultValue long; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterLong(final String key, final String shortName, final String description, final long defaultValue,
            final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
    }

    /**
     * Construct a new InputParameterLong.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterLong
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param defaultValue long; the default value of this input parameter
     * @param minimumValue long; the lowest value allowed as input
     * @param maximumValue long; the highest value allowed as input
     * @param format String; the format to use in displaying the long
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, or format is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterLong(final String key, final String shortName, final String description, final long defaultValue,
            final long minimumValue, final long maximumValue, final String format, final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(format, "format cannot be null");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.format = format;
    }

    /** {@inheritDoc} */
    @Override
    public Long getCalculatedValue()
    {
        return getValue();
    }

    /**
     * Check and set the typed value, and call super.setValue to make the actual allocation.
     * @param newValue long; the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setLongValue(final long newValue) throws InputParameterException
    {
        if (this.minimumValue > newValue || this.maximumValue < newValue)
        {
            throw new InputParameterException("new value for InputParameterLong with key " + getKey() + " with value "
                    + newValue + " is out of valid range [" + this.minimumValue + ".." + this.maximumValue + "]");
        }
        super.setValue(newValue);
    }

    /**
     * @return format
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * @param format String; set format
     * @throws NullPointerException when format is null
     */
    public void setFormat(final String format)
    {
        Throw.whenNull(format, "format cannot be null");
        this.format = format;
    }

    /**
     * @return minimumValue
     */
    public Long getMinimumValue()
    {
        return this.minimumValue;
    }

    /**
     * @param minimumValue long; set minimumValue
     */
    public void setMinimumValue(final long minimumValue)
    {
        this.minimumValue = minimumValue;
    }

    /**
     * @return maximumValue
     */
    public Long getMaximumValue()
    {
        return this.maximumValue;
    }

    /**
     * @param maximumValue long; set maximumValue
     */
    public void setMaximumValue(final long maximumValue)
    {
        this.maximumValue = maximumValue;
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterLong clone()
    {
        return (InputParameterLong) super.clone();
    }

}
