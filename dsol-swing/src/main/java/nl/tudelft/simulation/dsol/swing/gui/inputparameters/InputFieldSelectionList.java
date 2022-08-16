package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterSelectionList;

/**
 * Swing InputField for a selection, using a ComboBox. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <T> the type of field that the selection list contains
 */
public class InputFieldSelectionList<T> extends AbstractInputField
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
     * @param parameter InputParameterSelectionList&lt;T&gt;; the parameter
     */
    public InputFieldSelectionList(final JPanel panel, final InputParameterSelectionList<T> parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        String[] selections = new String[parameter.getOptions().size()];
        for (int i = 0; i < selections.length; i++)
        {
            selections[i] = parameter.getOptions().get(i).toString();
            this.selectionMap.put(selections[i], parameter.getOptions().get(i));
        }
        this.selectField = new JComboBox<>(selections);
        for (int i = 0; i < selections.length; i++)
        {
            if (selections[i].equals(parameter.getDefaultValue().toString()))
            {
                this.selectField.setSelectedIndex(i);
                break;
            }
        }
        JLabel explanation = new JLabel(parameter.getDescription());
        panel.add(label);
        panel.add(this.selectField);
        panel.add(explanation);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterSelectionList<T> getParameter()
    {
        return (InputParameterSelectionList<T>) super.getParameter();
    }

    /** @return the value of the field in the gui. */
    public T getValue()
    {
        return this.selectionMap.get(this.selectField.getSelectedItem().toString());
    }

    /** @return the index of the field in the gui. */
    public int getIndex()
    {
        return this.selectField.getSelectedIndex();
    }

}
