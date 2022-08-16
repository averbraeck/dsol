package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.SortedMap;

/**
 * The InputParameterMap contains a number of InputParameters, each of which can also be an InputParameterMap again. The
 * InputParameterMap provides functions to add and remove sub-parameters, to retrieve sub-parameters based on their key, and to
 * return a sorted set of InputParameters based on their displayValue. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterMap extends AbstractInputParameterMap<SortedMap<String, InputParameter<?, ?>>>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new InputParameterMap.
     * @param key String; unique (within the parent's input parameter map) name of the new InputParameterMap
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     */
    public InputParameterMap(final String key, final String shortName, final String description, final double displayPriority)
    {
        super(key, shortName, description, displayPriority);
        setReadOnly(true);
    }

    /** {@inheritDoc} */
    @Override
    public SortedMap<String, InputParameter<?, ?>> getCalculatedValue()
    {
        return getValue();
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterMap clone()
    {
        return (InputParameterMap) super.clone();
    }

}
