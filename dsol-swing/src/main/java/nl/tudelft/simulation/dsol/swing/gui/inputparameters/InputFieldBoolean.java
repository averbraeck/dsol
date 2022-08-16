package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterBoolean;

/**
 * Swing InputField for Boolean, using a tickbox. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldBoolean extends AbstractInputField
{
    /** combo box for the user interface. */
    private JCheckBox checkField;

    /**
     * Create a boolean field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterBoolean; the parameter
     */
    public InputFieldBoolean(final JPanel panel, final InputParameterBoolean parameter)
    {
        super(parameter);
        this.checkField = new JCheckBox(parameter.getShortName());
        this.checkField.setSelected(parameter.getDefaultValue());
        JLabel explanation = new JLabel(parameter.getDescription());
        panel.add(new JLabel(""));
        panel.add(this.checkField);
        panel.add(explanation);
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterBoolean getParameter()
    {
        return (InputParameterBoolean) super.getParameter();
    }

    /** @return the boolean value of the selected field in the gui. */
    public boolean getValue()
    {
        return this.checkField.isSelected();
    }

}
