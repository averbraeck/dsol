package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;

/**
 * Swing InputField for String. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldString extends AbstractInputField
{
    /** field for the user interface. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JTextField textField;

    /**
     * Create a string field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameter&lt;?,?&gt;; the parameter
     */
    public InputFieldString(final JPanel panel, final InputParameter<?, ?> parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        this.textField = new JTextField(20);
        this.textField.setText(parameter.getDefaultValue().toString());
        JLabel explanation = new JLabel(parameter.getDescription());
        panel.add(label);
        panel.add(this.textField);
        panel.add(explanation);
    }

    /** @return the string value of the field in the gui. */
    public String getStringValue()
    {
        return this.textField.getText();
    }

}
