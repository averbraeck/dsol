package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.unit.Unit;

/**
 * InputParameterUnit: parameter to select a unit. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <U> the unit type
 */
public class InputParameterUnit<U extends Unit<U>> extends InputParameterSelectionMap<String, U>
{
    /**
     * Construct a new InputParameterUnit.
     * @param key unique (within the parent's input parameter map) name of the new InputParameterUnit
     * @param shortName concise description of the input parameter
     * @param description double description of the input parameter (may use HTML markup)
     * @param defaultValue the default value of this input parameter
     * @param displayPriority sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     */
    public InputParameterUnit(final String key, final String shortName, final String description, final U defaultValue,
            final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, makeUnitMap(defaultValue), defaultValue, displayPriority);
    }

    /**
     * Make the map of allowed units based on the default value given.
     * @param defaultUnit the unit to derive the alternatives for
     * @return a map of options for this unit
     * @throws InputParameterException when unit for the default value cannot be found in the unit definition
     * @param <U> the unit type
     */
    private static <U extends Unit<U>> SortedMap<String, U> makeUnitMap(final U defaultUnit) throws InputParameterException
    {
        SortedMap<String, U> options = new TreeMap<>();
        Collection<U> unitSet = defaultUnit.getQuantity().getUnitsById().values();
        for (U unit : unitSet)
        {
            options.put(unit.getName(), unit);
        }
        return options;
    }

    @Override
    public InputParameterUnit<U> clone()
    {
        return (InputParameterUnit<U>) super.clone();
    }

}
