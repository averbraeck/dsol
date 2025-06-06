package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * InputParameterBoolean.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterString extends AbstractInputParameter<String, String>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new InputParameterString.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterString
     * @param shortName concise description of the input parameter
     * @param description long description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, or defaultValue is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterString(final String key, final String shortName, final String description, final String defaultValue,
            final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(defaultValue, "defaultValue cannot be null");
    }

    @Override
    public String getCalculatedValue()
    {
        return getValue();
    }

    /**
     * Check and set the typed value, and call super.setValue to make the actual allocation.
     * @param newValue the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setStringValue(final String newValue) throws InputParameterException
    {
        super.setValue(newValue);
    }

    @Override
    public InputParameterString clone()
    {
        return (InputParameterString) super.clone();
    }
}
