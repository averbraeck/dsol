package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.djutils.exceptions.Throw;

/**
 * InputParameterSelectionList contains a list of values to select from. An example would be a list of countries (String), a
 * list of states (String), or a list of logarithmic speeds for a simulation model (double). The InputParameterSelectionList
 * extends AbstractInputParameter&lt;T&gt; and not AbstractInputParameter&lt;List&lt;T&gt;&gt; because the value it can return
 * is the item in the list and not the list itself.<br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <T> the type of parameter stored in the list
 */
public class InputParameterSelectionList<T> extends AbstractInputParameter<T, T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The options of the selection list. */
    private List<T> options;

    /**
     * Construct a new InputParameterSelectionList.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterSelectionList
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param options List&lt;T&gt;; the list of selection options
     * @param defaultValue T; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException in case the default value is not part of the list
     */
    public InputParameterSelectionList(final String key, final String shortName, final String description,
            final List<T> options, final T defaultValue, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, defaultValue, displayPriority);
        this.options = new ArrayList<>(options);
        if (!options.contains(defaultValue))
        {
            throw new InputParameterException(
                    "Default value " + defaultValue + " not part of selectionList options for key " + getKey());
        }
    }

    /**
     * Construct a new InputParameterSelectionList.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterSelectionList
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param options T[]; the array of selection options
     * @param defaultValue T; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException in case the default value is not part of the list
     */
    public InputParameterSelectionList(final String key, final String shortName, final String description, final T[] options,
            final T defaultValue, final double displayPriority) throws InputParameterException
    {
        this(key, shortName, description, Arrays.asList(options), defaultValue, displayPriority);
    }

    /** {@inheritDoc} */
    @Override
    public T getCalculatedValue()
    {
        return getValue();
    }

    /**
     * @return the options for the selection
     */
    public List<T> getOptions()
    {
        return this.options;
    }

    /**
     * Return the index of the current value.
     * @return int; the index of the current value, or -1 if the list does not contain the value
     */
    public int getIndex()
    {
        return getIndex(getValue());
    }

    /**
     * Return the index of the given value in the options.
     * @param value T; the value to calculate the index for
     * @return int; the index of the given value, or -1 if the list does not contain the value
     */
    public int getIndex(final T value)
    {
        return this.options.indexOf(value);
    }

    /**
     * Set a new value by providing one of the list options.
     * @param newValue T; the new value (must be one of the list options)
     * @throws InputParameterException when the new value is not part of selectionList options, or when the list is read-only
     */
    public void setListValue(final T newValue) throws InputParameterException
    {
        if (getIndex(newValue) == -1)
        {
            throw new InputParameterException("value " + newValue + " not part of selectionList options for key " + getKey());
        }
        super.setValue(newValue);
    }

    /**
     * Return the index of the given value in the options.
     * @param index int; the index of the new value
     * @throws InputParameterException when index out of bounds
     */
    public void setIndex(final int index) throws InputParameterException
    {
        Throw.when(index < 0 || index >= this.options.size(), InputParameterException.class,
                "InputParameterSelectionList.setIndex() - index out of bounds");
        setValue(this.options.get(index));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterSelectionList<T> clone()
    {
        InputParameterSelectionList<T> ipsl = (InputParameterSelectionList<T>) super.clone();
        List<T> clonedList = new ArrayList<>();
        for (T item : this.options)
        {
            // needed because the ArrayList.clone() returns a shallow copy
            if (item instanceof InputParameter<?, ?>)
            {
                clonedList.add((T) ((InputParameter<?, ?>) item).clone());
            }
            else
            {
                clonedList.add(item); // shallow; we cannot see the clone() method for other objects...
            }
        }
        try
        {
            boolean readOnly = ipsl.isReadOnly();
            ipsl.setReadOnly(false);
            ipsl.setValue(getValue());
            ipsl.setReadOnly(readOnly);
        }
        catch (InputParameterException exception)
        {
            throw new RuntimeException(exception);
        }

        return ipsl;
    }
}
