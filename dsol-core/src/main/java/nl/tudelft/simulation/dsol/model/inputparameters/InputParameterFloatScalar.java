package nl.tudelft.simulation.dsol.model.inputparameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.djunits.unit.Unit;
import org.djunits.value.vfloat.scalar.base.AbstractFloatScalar;
import org.djutils.exceptions.Throw;
import org.djutils.reflection.ClassUtil;

/**
 * InputParameterFloatScalar: float parameter with a unit. The number and the value are stored in an InputParameterMap as two
 * input variables of type InputFieldFloat (name: value), and InputFieldUnit (name: unit). <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <U> the unit type
 * @param <T> the scalar type
 */
public class InputParameterFloatScalar<U extends Unit<U>, T extends AbstractFloatScalar<U, T>>
        extends AbstractInputParameterTypedMap<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The minimum value of the input parameter (SI units). */
    private float minimumValueSI = -Float.MAX_VALUE;

    /** The maximum value of the input parameter (SI units). */
    private float maximumValueSI = Float.MAX_VALUE;

    /** Is the minimum value included or excluded in the allowed interval? */
    private boolean minIncluded = true;

    /** Is the maximum value included or excluded in the allowed interval? */
    private boolean maxIncluded = true;

    /**
     * Construct a new InputParameterFloatScalar.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterFloatUnit
     * @param shortName String; concise description of the input parameter
     * @param description String; float description of the input parameter (may use HTML markup)
     * @param defaultValue T; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    public InputParameterFloatScalar(final String key, final String shortName, final String description, final T defaultValue,
            final double displayPriority) throws InputParameterException
    {
        this(key, shortName, description, defaultValue, -Float.MAX_VALUE, Float.MAX_VALUE, false, false, "%d", displayPriority);
    }

    /**
     * Construct a new InputParameterFloatScalar.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterFloatUnit
     * @param shortName String; concise description of the input parameter
     * @param description String; float description of the input parameter (may use HTML markup)
     * @param defaultValue T; the default value of this input parameter
     * @param minimumValue T; the lowest value allowed as input
     * @param maximumValue T; the highest value allowed as input
     * @param minIncluded boolean; is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded boolean; is the maximum value included or excluded in the allowed interval?
     * @param format String; the format to use in displaying the float
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, format, minimumValue, maximumValue, or
     *             defaultValue is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterFloatScalar(final String key, final String shortName, final String description, final T defaultValue,
            final T minimumValue, final T maximumValue, final boolean minIncluded, final boolean maxIncluded,
            final String format, final double displayPriority) throws InputParameterException
    {
        this(key, shortName, description, defaultValue, minimumValue.si, maximumValue.si, minIncluded, maxIncluded, format,
                displayPriority);
        Throw.whenNull(format, "format cannot be null");
        Throw.whenNull(defaultValue, "defaultValue cannot be null");
        Throw.whenNull(minimumValue, "minimumValue cannot be null");
        Throw.whenNull(maximumValue, "maximumValue cannot be null");
    }

    /**
     * Construct a new InputParameterFloatScalar.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterFloatUnit
     * @param shortName String; concise description of the input parameter
     * @param description String; float description of the input parameter (may use HTML markup)
     * @param defaultValue T; the default value of this input parameter
     * @param minimumValueSI float; the lowest value allowed as input (in SI units)
     * @param maximumValueSI float; the highest value allowed as input (in SI units)
     * @param minIncluded boolean; is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded boolean; is the maximum value included or excluded in the allowed interval?
     * @param format String; the format to use in displaying the float
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, format, or defaultValue is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterFloatScalar(final String key, final String shortName, final String description, final T defaultValue,
            final float minimumValueSI, final float maximumValueSI, final boolean minIncluded, final boolean maxIncluded,
            final String format, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(format, "format cannot be null");
        Throw.whenNull(defaultValue, "defaultValue cannot be null");
        add(new InputParameterFloat("value", "value", "float value in the given unit", defaultValue.getInUnit(),
                -Float.MAX_VALUE, Float.MAX_VALUE, false, false, format, 1.0));
        add(new InputParameterUnit<U>("unit", "unit", "unit for the value", defaultValue.getDisplayUnit(), 2.0));
        this.minimumValueSI = minimumValueSI;
        this.maximumValueSI = maximumValueSI;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
    }

    /**
     * @return the unit sub-parameter
     * @throws RuntimeException when parameter map has been corrupted and no unit parameter can be found
     */
    @SuppressWarnings("unchecked")
    public InputParameterUnit<U> getUnitParameter()
    {
        try
        {
            return (InputParameterUnit<U>) get("unit");
        }
        catch (InputParameterException exception)
        {
            throw new RuntimeException(
                    "Parameter map has been corrupted and no unit parameter can be found for field " + getShortName(),
                    exception);
        }
    }

    /**
     * @return the float sub-parameter
     * @throws RuntimeException when parameter map has been corrupted and no value parameter can be found
     */
    public InputParameterFloat getFloatParameter()
    {
        try
        {
            return (InputParameterFloat) get("value");
        }
        catch (InputParameterException exception)
        {
            throw new RuntimeException(
                    "Parameter map has been corrupted and no value parameter can be found for field " + getShortName(),
                    exception);
        }
    }

    /**
     * Construct the scalar value with reflection, and set it as the current value. The unit is specified as Unit&lt;?&gt;
     * because the input using fields cannot guarantee conformance to the right unit type.
     * @throws InputParameterException when an operation is attempted that is not compatible with the indicated input parameter,
     *             or when the scalar does not have a constructor Scalar(float, unit)
     */
    @Override
    public void setCalculatedValue() throws InputParameterException
    {
        float floatValue = ((InputParameterFloat) get("value")).getValue();
        @SuppressWarnings("unchecked")
        U unit = ((InputParameterUnit<U>) get("unit")).getValue();
        T newValue;
        try
        {
            @SuppressWarnings("unchecked")
            Constructor<T> constructor = (Constructor<T>) ClassUtil.resolveConstructor(getDefaultTypedValue().getClass(),
                    new Object[] {floatValue, unit});
            newValue = constructor.newInstance(floatValue, unit);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException exception)
        {
            throw new InputParameterException(exception);
        }

        if (this.minimumValueSI > newValue.si || this.maximumValueSI < newValue.si
                || (this.minimumValueSI == newValue.si && !this.minIncluded)
                || (this.maximumValueSI == newValue.si && !this.maxIncluded) || Float.isNaN(newValue.si))
        {
            throw new InputParameterException(
                    "new value for InputParameterFloatUnit with key " + getKey() + " with value " + newValue
                            + " is out of valid range [" + this.minimumValueSI + ".." + this.maximumValueSI + "] (SI units)");
        }

        this.typedValue = newValue;
    }

    /**
     * @return minimumValue (SI units)
     */
    public Float getMinimumValueSI()
    {
        return this.minimumValueSI;
    }

    /**
     * @param minimumValueSI float; set minimumValue (SI units)
     */
    public void setMinimumValueSI(final float minimumValueSI)
    {
        this.minimumValueSI = minimumValueSI;
    }

    /**
     * @return maximumValue (SI units)
     */
    public Float getMaximumValueSI()
    {
        return this.maximumValueSI;
    }

    /**
     * @param maximumValueSI float; set maximumValue (SI units)
     */
    public void setMaximumValueSI(final float maximumValueSI)
    {
        this.maximumValueSI = maximumValueSI;
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
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterFloatScalar<U, T> clone()
    {
        InputParameterFloatScalar<U, T> clonedMap = (InputParameterFloatScalar<U, T>) super.clone();
        clonedMap.minimumValueSI = this.minimumValueSI;
        clonedMap.maximumValueSI = this.maximumValueSI;
        clonedMap.minIncluded = this.minIncluded;
        clonedMap.maxIncluded = this.maxIncluded;
        return clonedMap;
    }

}
