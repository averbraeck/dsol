package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Throw;

/**
 * InputParameterSelectionMap contains a list of key values to select from, each leading to another value to be selected as the
 * value. An example would be a list of human readable ISO-3166 2-letter country codes to choose from (String), whereas the
 * ISO3166 numeric code would be stored as an int. The InputParameterSelectionMap extends AbstractInputParameter&lt;T&gt; and
 * not AbstractInputParameter&lt;Map&lt;K,&nbsp;T&gt;&gt; because the value it can return is the value-item in the map and not
 * the map itself.<br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <K> the key value to select the values in the map
 * @param <T> the type of parameter stored in the map
 */
public class InputParameterSelectionMap<K, T> extends AbstractInputParameter<T, T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** The options of the selection list. */
    private SortedMap<K, T> options;

    /**
     * Construct a new InputParameterSelectionMap.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterSelectionMap
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param options SortedMap&lt;K,T&gt;; the list of selection options
     * @param defaultValue T; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException in case the default value is not part of the list
     */
    public InputParameterSelectionMap(final String key, final String shortName, final String description,
            final SortedMap<K, T> options, final T defaultValue, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, defaultValue, displayPriority);
        this.options = new TreeMap<>(options);
        if (!options.containsValue(defaultValue))
        {
            throw new InputParameterException(
                    "Default value " + defaultValue + " not part of selectionMap options for key " + getKey());
        }
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
    public SortedMap<K, T> getOptions()
    {
        return this.options;
    }

    /**
     * Return the index of the current value.
     * @return K; the key in the options belonging to the current value, or null if the list does not contain the value
     */
    public K getKeyforValue()
    {
        return getKeyforValue(getValue());
    }

    /**
     * Return the index of the given value in the options.
     * @param value T; the value to calculate the index for
     * @return K; the key in the options belonging to the given value, or null if the list does not contain the value
     */
    public K getKeyforValue(final T value)
    {
        for (K key : this.options.keySet())
        {
            if (this.options.get(key).equals(value))
            {
                return key;
            }
        }
        return null;
    }

    /**
     * Set a new value by providing one of the map value options.
     * @param newValue T; the new value (must be one of the map value options)
     * @throws InputParameterException when the new value is not part of selectionMap options, or when the map is read-only
     */
    public void setMapValue(final T newValue) throws InputParameterException
    {
        if (getKeyforValue(newValue) == null)
        {
            throw new InputParameterException("Value " + newValue + " not part of selectionMap options");
        }
        super.setValue(newValue);
    }

    /**
     * Change the value of the input parameter, as an object when the generics are not known (e.g., in a user interface).
     * @param objectValue Object; the new value for the input parameter
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setObjectValue(final Object objectValue) throws InputParameterException
    {
        @SuppressWarnings("unchecked")
        T newValue = (T) objectValue;
        if (getKeyforValue(newValue) == null)
        {
            throw new InputParameterException("Value " + newValue + " not part of selectionMap options");
        }
        super.setValue(newValue);
    }

    /**
     * Return the index of the given value in the options.
     * @param key K; the index of the new value
     * @throws InputParameterException when index out of bounds
     */
    public void setKeyforValue(final K key) throws InputParameterException
    {
        Throw.when(key == null || !this.options.containsKey(key), InputParameterException.class,
                "InputParameterSelectionMap.setKeyforValue() - key not part of options");
        setValue(this.options.get(key));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterSelectionMap<K, T> clone()
    {
        InputParameterSelectionMap<K, T> ipsm = (InputParameterSelectionMap<K, T>) super.clone();
        SortedMap<K, T> clonedMap = new TreeMap<>(this.options.comparator());
        for (K key : this.options.keySet())
        {
            T item = this.options.get(key);
            // needed because the ArrayList.clone() returns a shallow copy
            if (item instanceof InputParameter<?, ?>)
            {
                clonedMap.put(key, (T) ((InputParameter<?, ?>) item).clone());
            }
            else
            {
                clonedMap.put(key, item); // shallow; we cannot see the clone() method for other objects...
            }
        }
        try
        {
            boolean readOnly = ipsm.isReadOnly();
            ipsm.setReadOnly(false);
            ipsm.setValue(getValue());
            ipsm.setReadOnly(readOnly);
        }
        catch (InputParameterException exception)
        {
            throw new RuntimeException(exception);
        }

        return ipsm;
    }
}
