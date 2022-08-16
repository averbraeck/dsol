package nl.tudelft.simulation.dsol.model.inputparameters;

/**
 * InputParameterBoolean.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterBoolean extends AbstractInputParameter<Boolean, Boolean>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new InputParameterBoolean.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterBoolean
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param defaultValue boolean; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterBoolean(final String key, final String shortName, final String description, final boolean defaultValue,
            final double displayPriority)
    {
        super(key, shortName, description, defaultValue, displayPriority);
    }

    /** {@inheritDoc} */
    @Override
    public Boolean getCalculatedValue()
    {
        return getValue();
    }

    /**
     * Check and set the typed value, and call super.setValue to make the actual allocation.
     * @param newValue boolean; the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setBooleanValue(final boolean newValue) throws InputParameterException
    {
        super.setValue(newValue);
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterBoolean clone()
    {
        return (InputParameterBoolean) super.clone();
    }
}
