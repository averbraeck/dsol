package nl.tudelft.simulation.dsol.model.inputparameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.djunits.unit.Unit;
import org.djunits.value.vdouble.scalar.base.DoubleScalar;
import org.djutils.exceptions.Throw;
import org.djutils.reflection.ClassUtil;

/**
 * InputParameterDoubleScalar: double parameter with a unit. The number and the value are stored in an InputParameterMap as two
 * input variables of type InputFieldDouble (name: value), and InputFieldUnit (name: unit). <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <U> the unit type
 * @param <T> the scalar type
 */
public class InputParameterDoubleScalar<U extends Unit<U>, T extends DoubleScalar<U, T>>
        extends AbstractInputParameterTypedMap<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The minimum value of the input parameter (SI units). */
    private double minimumValueSI = -Double.MAX_VALUE;

    /** The maximum value of the input parameter (SI units). */
    private double maximumValueSI = Double.MAX_VALUE;

    /** Is the minimum value included or excluded in the allowed interval? */
    private boolean minIncluded = true;

    /** Is the maximum value included or excluded in the allowed interval? */
    private boolean maxIncluded = true;

    /**
     * Construct a new InputParameterDoubleScalar.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterDoubleUnit
     * @param shortName concise description of the input parameter
     * @param description double description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    public InputParameterDoubleScalar(final String key, final String shortName, final String description, final T defaultValue,
            final double displayPriority) throws InputParameterException
    {
        this(key, shortName, description, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f",
                displayPriority);
    }

    /**
     * Construct a new InputParameterDoubleScalar.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterDoubleUnit
     * @param shortName concise description of the input parameter
     * @param description double description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param minimumValue the lowest value allowed as input
     * @param maximumValue the highest value allowed as input
     * @param minIncluded is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded is the maximum value included or excluded in the allowed interval?
     * @param format the format to use in displaying the double
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, format, minimumValue, maximumValue, or
     *             defaultValue is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterDoubleScalar(final String key, final String shortName, final String description, final T defaultValue,
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
     * Construct a new InputParameterDoubleScalar.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterDoubleUnit
     * @param shortName concise description of the input parameter
     * @param description double description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param minimumValueSI the lowest value allowed as input (in SI units)
     * @param maximumValueSI the highest value allowed as input (in SI units)
     * @param minIncluded is the minimum value included or excluded in the allowed interval?
     * @param maxIncluded is the maximum value included or excluded in the allowed interval?
     * @param format the format to use in displaying the double
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, format, or defaultValue is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public InputParameterDoubleScalar(final String key, final String shortName, final String description, final T defaultValue,
            final double minimumValueSI, final double maximumValueSI, final boolean minIncluded, final boolean maxIncluded,
            final String format, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, defaultValue, displayPriority);
        Throw.whenNull(format, "format cannot be null");
        Throw.whenNull(defaultValue, "defaultValue cannot be null");
        add(new InputParameterDouble("value", "value", "double value in the given unit", defaultValue.getInUnit(),
                -Double.MAX_VALUE, Double.MAX_VALUE, false, false, format, 1.0));
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
     * @return the double sub-parameter
     * @throws RuntimeException when parameter map has been corrupted and no value parameter can be found
     */
    public InputParameterDouble getDoubleParameter()
    {
        try
        {
            return (InputParameterDouble) get("value");
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
     *             or when the scalar does not have a constructor Scalar(double, unit)
     */
    @Override
    public void setCalculatedValue() throws InputParameterException
    {
        double doubleValue = getDoubleParameter().getValue();
        U unit = getUnitParameter().getValue();
        T newValue;
        try
        {
            @SuppressWarnings("unchecked")
            Constructor<T> constructor = (Constructor<T>) ClassUtil.resolveConstructor(getDefaultTypedValue().getClass(),
                    new Object[] {doubleValue, unit});
            newValue = constructor.newInstance(doubleValue, unit);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException
                | InvocationTargetException exception)
        {
            throw new InputParameterException(exception);
        }

        if (this.minimumValueSI > newValue.si || this.maximumValueSI < newValue.si
                || (this.minimumValueSI == newValue.si && !this.minIncluded)
                || (this.maximumValueSI == newValue.si && !this.maxIncluded) || Double.isNaN(newValue.si))
        {
            throw new InputParameterException(
                    "new value for InputParameterDoubleUnit with key " + getKey() + " with value " + newValue
                            + " is out of valid range [" + this.minimumValueSI + ".." + this.maximumValueSI + "] (SI units)");
        }

        this.typedValue = newValue;
    }

    /**
     * @return minimumValue (SI units)
     */
    public Double getMinimumValueSI()
    {
        return this.minimumValueSI;
    }

    /**
     * @param minimumValueSI set minimumValue (SI units)
     */
    public void setMinimumValueSI(final double minimumValueSI)
    {
        this.minimumValueSI = minimumValueSI;
    }

    /**
     * @return maximumValue (SI units)
     */
    public Double getMaximumValueSI()
    {
        return this.maximumValueSI;
    }

    /**
     * @param maximumValueSI set maximumValue (SI units)
     */
    public void setMaximumValueSI(final double maximumValueSI)
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

    @SuppressWarnings("unchecked")
    @Override
    public InputParameterDoubleScalar<U, T> clone()
    {
        InputParameterDoubleScalar<U, T> clonedMap = (InputParameterDoubleScalar<U, T>) super.clone();
        clonedMap.minimumValueSI = this.minimumValueSI;
        clonedMap.maximumValueSI = this.maximumValueSI;
        clonedMap.minIncluded = this.minIncluded;
        clonedMap.maxIncluded = this.maxIncluded;
        return clonedMap;
    }

}
