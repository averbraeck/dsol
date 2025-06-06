package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * InputParameterFloat.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterFloat extends AbstractInputParameter<Float, Float>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Format string to display the value of the input parameter. */
    private String format = "%d";

    /** The minimum value of the input parameter. */
    private float minimumValue = -Float.MAX_VALUE;

    /** The maximum value of the input parameter. */
    private float maximumValue = Float.MAX_VALUE;

    /** Is the minimum value included or excluded in the allowed interval? */
    private boolean minIncluded = true;

    /** Is the maximum value included or excluded in the allowed interval? */
    private boolean maxIncluded = true;

    /**
     * Construct a new InputParameterFloat.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterFloat
     * @param shortName concise description of the input parameter
     * @param description float description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterFloat(final String key, final String shortName, final String description, final float defaultValue,
            final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
    }

    /**
     * Construct a new InputParameterFloat.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterFloat
     * @param shortName concise description of the input parameter
     * @param description float description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param minimumValue the lowest value allowed as input
     * @param maximumValue the highest value allowed as input
     * @param minIncluded is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded is the maximum value included or excluded in the allowed interval?
     * @param format the format to use in displaying the float
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, or format is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterFloat(final String key, final String shortName, final String description, final float defaultValue,
            final float minimumValue, final float maximumValue, final boolean minIncluded, final boolean maxIncluded,
            final String format, final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(format, "format cannot be null");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.format = format;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
    }

    @Override
    public Float getCalculatedValue()
    {
        return getValue();
    }

    /**
     * Check and set the typed value, and call super.setValue to make the actual allocation.
     * @param newValue the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setFloatValue(final float newValue) throws InputParameterException
    {
        if (this.minimumValue > newValue || this.maximumValue < newValue || (this.minimumValue == newValue && !this.minIncluded)
                || (this.maximumValue == newValue && !this.maxIncluded) || Float.isNaN(newValue))
        {
            throw new InputParameterException("new value for InputParameterFloat with key " + getKey() + " with value "
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
     * @param format set format
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
    public Float getMinimumValue()
    {
        return this.minimumValue;
    }

    /**
     * @param minimumValue set minimumValue
     */
    public void setMinimumValue(final float minimumValue)
    {
        this.minimumValue = minimumValue;
    }

    /**
     * @return maximumValue
     */
    public Float getMaximumValue()
    {
        return this.maximumValue;
    }

    /**
     * @param maximumValue set maximumValue
     */
    public void setMaximumValue(final float maximumValue)
    {
        this.maximumValue = maximumValue;
    }

    /**
     * @return minIncluded
     */
    public boolean isMinIncluded()
    {
        return this.minIncluded;
    }

    /**
     * @param minIncluded set minIncluded
     */
    public void setMinIncluded(final boolean minIncluded)
    {
        this.minIncluded = minIncluded;
    }

    /**
     * @return maxIncluded
     */
    public boolean isMaxIncluded()
    {
        return this.maxIncluded;
    }

    /**
     * @param maxIncluded set maxIncluded
     */
    public void setMaxIncluded(final boolean maxIncluded)
    {
        this.maxIncluded = maxIncluded;
    }

    @Override
    public InputParameterFloat clone()
    {
        return (InputParameterFloat) super.clone();
    }

}
