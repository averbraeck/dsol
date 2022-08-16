package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * The InputParameterTypedMap is a hierarchical input parameter that can return a variable of a certain type. An example is a
 * scalar where a double value and a unit have to be inputed. The InputParameterTypedMap is then supposed to be able to return
 * the scalar based on the two sub-values. The same holds, e.g., for a continuous distribution function. The input parameter for
 * a continuous distribution should be able to return the chosen distribution, but internally it consists of a selection list or
 * selection map for the distribution, followed by one or more parameters for the chosen distribution, which are stored in an
 * InputParameterMap. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <T> the type that this map should be able to construct or return
 */
public abstract class AbstractInputParameterTypedMap<T> extends AbstractInputParameterMap<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the value based on the input. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected T typedValue;

    /** the default typed value. */
    private final T defaultTypedValue;

    /**
     * Construct a new InputParameterTypedMap.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterMap
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param defaultTypedValue T; the default value in the corresponding type
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public AbstractInputParameterTypedMap(final String key, final String shortName, final String description,
            final T defaultTypedValue, final double displayPriority)
    {
        super(key, shortName, description, displayPriority);
        this.defaultTypedValue = defaultTypedValue;
    }

    /**
     * Calculate the calculated value for this parameter entry.
     * @throws InputParameterException when the typed value based on the parameter map cannot be constructed
     */
    public abstract void setCalculatedValue() throws InputParameterException;

    /** {@inheritDoc} */
    @Override
    public T getCalculatedValue() throws InputParameterException
    {
        Throw.when(this.typedValue == null, InputParameterException.class,
                "Value for parameter " + getShortName() + " has not yet been calculated");
        return this.typedValue;
    }

    /**
     * Return the default value in the corresponding type.
     * @return T; defaultTypedValue the default typed value
     */
    public T getDefaultTypedValue()
    {
        return this.defaultTypedValue;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractInputParameterTypedMap<T> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractInputParameterTypedMap<T> clonedMap = (AbstractInputParameterTypedMap<T>) super.clone();
        clonedMap.typedValue = this.typedValue;
        return clonedMap;
    }

}
