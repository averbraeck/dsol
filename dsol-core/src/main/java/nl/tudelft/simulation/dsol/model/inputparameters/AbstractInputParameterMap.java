package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The InputParameterMap contains a number of InputParameters, each of which can also be an InputParameterMap again. The
 * InputParameterMap provides functions to add and remove sub-parameters, to retrieve sub-parameters based on their key, and to
 * return a sorted set of InputParameters based on their displayValue. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <CT> The calculated type
 */
public abstract class AbstractInputParameterMap<CT> extends AbstractInputParameter<SortedMap<String, InputParameter<?, ?>>, CT>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new AbstractInputParameterMap.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterMap
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public AbstractInputParameterMap(final String key, final String shortName, final String description,
            final double displayPriority)
    {
        super(key, shortName, description, new TreeMap<String, InputParameter<?, ?>>(), displayPriority);
        setReadOnly(true);
    }

    /**
     * Add an input parameter to this map of input parameters.
     * @param inputParameter AbstractInputParameter&lt;?,?&gt;; the input parameter to add
     * @throws InputParameterException in case an input parameter with the same key already exists
     */
    public void add(final AbstractInputParameter<?, ?> inputParameter) throws InputParameterException
    {
        if (getValue().containsKey(inputParameter.getKey()))
        {
            throw new InputParameterException(
                    "Duplicate key " + inputParameter.getKey() + " for InputParameterMap " + getKey());
        }
        getValue().put(inputParameter.getKey(), inputParameter);
        inputParameter.setParent(this);
    }

    /**
     * Removes an input parameter from this map of input parameters. The input parameter can point to deeper maps using the
     * dot-notation. E.g., when an InputParameterMap exists in this map with name 'server1', and the server1 map has an input
     * parameter called 'iat', we can remove iat by calling remove("server1.iat");
     * @param key String; the key of the input parameter to remove
     * @throws InputParameterException in case the input parameter with this key does not exist
     */
    public void remove(final String key) throws InputParameterException
    {
        String[] keys = key.split("\\.");
        if (!getValue().containsKey(keys[0]))
        {
            throw new InputParameterException("Key " + key + " does not exist in InputParameterMap " + getKey());
        }
        if (keys.length == 1)
        {
            getValue().remove(key);
            return;
        }
        InputParameter<?, ?> parent = getValue().get(keys[0]);
        if (!(parent instanceof AbstractInputParameterMap))
        {
            throw new InputParameterException(
                    "Key " + key + " in InputParameterMap " + getKey() + " is not a leaf, but also not an InputParameterMap");
        }
        ((AbstractInputParameterMap<?>) parent).remove(key.substring(key.indexOf('.') + 1));
    }

    /**
     * Gets an input parameter from this map of input parameters. The input parameter can point to deeper maps using the
     * dot-notation. E.g., when an InputParameterMap exists in this map with name 'server1', and the server1 map has an input
     * parameter called 'iat', we can get the InputParameter iat by calling get("server1.iat");
     * @param key String; the key of the input parameter to retrieve
     * @return the input parameter belonging to the key
     * @throws InputParameterException in case the input parameter with this key does not exist
     */
    public InputParameter<?, ?> get(final String key) throws InputParameterException
    {
        String[] keys = key.split("\\.");
        if (!getValue().containsKey(keys[0]))
        {
            throw new InputParameterException("Key " + key + " does not exist in InputParameterMap " + getKey());
        }
        if (keys.length == 1)
        {
            return getValue().get(key);
        }
        InputParameter<?, ?> parent = getValue().get(keys[0]);
        if (!(parent instanceof AbstractInputParameterMap))
        {
            throw new InputParameterException(
                    "Key " + key + " in InputParameterMap " + getKey() + " is not a leaf, but also not an InputParameterMap");
        }
        return ((AbstractInputParameterMap<?>) parent).get(key.substring(key.indexOf('.') + 1));
    }

    /** {@inheritDoc} */
    @Override
    public AbstractInputParameterMap<?> clone()
    {
        AbstractInputParameterMap<?> ipm = (AbstractInputParameterMap<?>) super.clone();
        SortedMap<String, InputParameter<?, ?>> clonedMap = new TreeMap<>();
        for (String key : getValue().keySet())
        {
            // needed because the TreeMap.clone() returns a shallow copy
            clonedMap.put(key, getValue().get(key).clone());
        }
        try
        {
            boolean readOnly = ipm.isReadOnly();
            ipm.setReadOnly(false);
            ipm.setValue(clonedMap);
            ipm.setReadOnly(readOnly);
        }
        catch (InputParameterException exception)
        {
            throw new RuntimeException(exception);
        }
        return ipm;
    }

    /**
     * Return a sorted set of sub-parameters of this InputParameterMap.
     * @return SortedSet&lt;InputParameter&lt;?&gt;&gt;; a sorted set of sub-parameters of this InputParameterMap
     */
    public SortedSet<InputParameter<?, ?>> getSortedSet()
    {
        SortedSet<InputParameter<?, ?>> set = new TreeSet<>(new InputParameterComparator());
        set.addAll(getValue().values());
        return set;
    }

    /**
     * Return a formatted human readable list of keys and values, indented with spaces corresponding to the depth.
     * @param map AbstractInputParameterMap&lt;?&gt;; the map to display
     * @param depth int; the depth of the tree
     * @return String; a formatted human readable list of keys and values, indented by depth
     */
    protected String printValues(final AbstractInputParameterMap<?> map, final int depth)
    {
        StringBuffer out = new StringBuffer();
        for (InputParameter<?, ?> param : map.getSortedSet())
        {
            out.append("                    ".substring(0, depth));
            if (param instanceof AbstractInputParameterTypedMap)
            {
                out.append(param.getKey());
                out.append(" = ");
                try
                {
                    out.append(((AbstractInputParameterTypedMap<?>) param).getCalculatedValue().toString());
                }
                catch (InputParameterException exception)
                {
                    out.append("!!ERROR!!");
                }
                out.append("\n");
            }
            else if (param instanceof AbstractInputParameterMap)
            {
                out.append("MAP: ");
                out.append(param.getKey());
                out.append("\n");
                out.append(printValues((AbstractInputParameterMap<?>) param, depth + 2));
            }
            else
            {
                out.append(param.getKey());
                out.append(" = ");
                out.append(param.getValue());
                out.append("\n");
            }
        }
        return out.toString();
    }

    /**
     * Return a formatted human readable list of keys and values.
     * @return String; a formatted human readable list of keys and values
     */
    public String printValues()
    {
        return printValues(this, 0);
    }

    /**
     * InputParameterComparator provides the comparator based on the displayPriority of the entries. In case they are the same,
     * the entries are ordered by the String value of the key. <br>
     * <br>
     * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    class InputParameterComparator implements Comparator<InputParameter<?, ?>>
    {
        /** {@inheritDoc} */
        @Override
        public int compare(final InputParameter<?, ?> o1, final InputParameter<?, ?> o2)
        {
            if (o1.getDisplayPriority() != o2.getDisplayPriority())
            {
                return o1.getDisplayPriority() < o2.getDisplayPriority() ? -1 : 1;
            }
            return o1.getKey().compareTo(o2.getKey());
        }
    }
}
