package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterSelectionMap;

/**
 * Swing InputField for a selection with a map, using a ComboBox. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <K> the type of key to the map, of which the toString() will be shown in the combobox
 * @param <T> the type of field that the selection map contains
 */
public class InputFieldSelectionMap<K, T> extends AbstractInputField
{
    /** combo box for the user interface. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JComboBox<String> selectField;

    /** mapping from String value to original class. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<String, T> selectionMap = new LinkedHashMap<>();

    /**
     * Create a string field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterSelectionMap&lt;K,T&gt;; the parameter
     */
    public InputFieldSelectionMap(final JPanel panel, final InputParameterSelectionMap<K, T> parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        String[] selections = new String[parameter.getOptions().size()];
        int defaultIndex = 0;
        int i = 0;
        for (K option : parameter.getOptions().keySet())
        {
            selections[i] = option.toString();
            T value = parameter.getOptions().get(option);
            this.selectionMap.put(selections[i], value);
            if (value.equals(parameter.getDefaultValue()))
            {
                defaultIndex = i;
            }
            i++;
        }
        this.selectField = new JComboBox<>(selections);
        this.selectField.setSelectedIndex(defaultIndex);
        JLabel explanation = new JLabel(parameter.getDescription());
        panel.add(label);
        panel.add(this.selectField);
        panel.add(explanation);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterSelectionMap<K, T> getParameter()
    {
        return (InputParameterSelectionMap<K, T>) super.getParameter();
    }

    /** @return the mapped value of the field in the gui, selected by the key's toString() value. */
    public T getValue()
    {
        return this.selectionMap.get(this.selectField.getSelectedItem().toString());
    }

}
