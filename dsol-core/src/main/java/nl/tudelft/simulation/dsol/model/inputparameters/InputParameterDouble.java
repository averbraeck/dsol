package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * InputParameterDouble.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterDouble extends AbstractInputParameter<Double, Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** Format string to display the value of the input parameter. */
    private String format = "%f";

    /** The minimum value of the input parameter. */
    private double minimumValue = -Double.MAX_VALUE;

    /** The maximum value of the input parameter. */
    private double maximumValue = Double.MAX_VALUE;

    /** Is the minimum value included or excluded in the allowed interval? */
    private boolean minIncluded = true;

    /** Is the maximum value included or excluded in the allowed interval? */
    private boolean maxIncluded = true;

    /**
     * Construct a new InputParameterDouble.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterDouble
     * @param shortName String; concise description of the input parameter
     * @param description String; double description of the input parameter (may use HTML markup)
     * @param defaultValue double; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterDouble(final String key, final String shortName, final String description, final double defaultValue,
            final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
    }

    /**
     * Construct a new InputParameterDouble.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterDouble
     * @param shortName String; concise description of the input parameter
     * @param description String; double description of the input parameter (may use HTML markup)
     * @param defaultValue double; the default value of this input parameter
     * @param minimumValue double; the lowest value allowed as input
     * @param maximumValue double; the highest value allowed as input
     * @param minIncluded boolean; is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded boolean; is the maximum value included or excluded in the allowed interval?
     * @param format String; the format to use in displaying the double
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, or format is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterDouble(final String key, final String shortName, final String description, final double defaultValue,
            final double minimumValue, final double maximumValue, final boolean minIncluded, final boolean maxIncluded,
            final String format, final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(format, "format cannot be null");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
        this.format = format;
    }

    /** {@inheritDoc} */
    @Override
    public Double getCalculatedValue()
    {
        return getValue();
    }

    /**
     * Check and set the typed value, and call super.setValue to make the actual allocation.
     * @param newValue double; the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setDoubleValue(final double newValue) throws InputParameterException
    {
        if (this.minimumValue > newValue || this.maximumValue < newValue || (this.minimumValue == newValue && !this.minIncluded)
                || (this.maximumValue == newValue && !this.maxIncluded) || Double.isNaN(newValue))
        {
            throw new InputParameterException("new value for InputParameterDouble with key " + getKey() + " with value "
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
     */
    public void setFormat(final String format)
    {
        Throw.whenNull(format, "format cannot be null");
        this.format = format;
    }

    /**
     * @return minimumValue
     */
    public Double getMinimumValue()
    {
        return this.minimumValue;
    }

    /**
     * @param minimumValue double; set minimumValue
     */
    public void setMinimumValue(final double minimumValue)
    {
        this.minimumValue = minimumValue;
    }

    /**
     * @return maximumValue
     */
    public Double getMaximumValue()
    {
        return this.maximumValue;
    }

    /**
     * @param maximumValue double; set maximumValue
     */
    public void setMaximumValue(final double maximumValue)
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
     * @param minIncluded boolean; set minIncluded
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
     * @param maxIncluded boolean; set maxIncluded
     */
    public void setMaxIncluded(final boolean maxIncluded)
    {
        this.maxIncluded = maxIncluded;
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterDouble clone()
    {
        return (InputParameterDouble) super.clone();
    }

}
