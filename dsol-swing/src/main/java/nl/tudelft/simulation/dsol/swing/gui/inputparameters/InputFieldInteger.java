package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;

/**
 * Swing InputField for Integer. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldInteger extends InputFieldString
{
    /**
     * Create a integer field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterInteger; the parameter
     */
    public InputFieldInteger(final JPanel panel, final InputParameterInteger parameter)
    {
        super(panel, parameter);
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterInteger getParameter()
    {
        return (InputParameterInteger) super.getParameter();
    }

    /**
     * Return the numeric value of the field.
     * @return the integer value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public int getIntValue() throws InputParameterException
    {
        return getIntValue(this.textField.getText(), this.parameter.getShortName());
    }

    /**
     * Return the numeric value of the field.
     * @param s String; the String to test
     * @param shortName String; the name of the field to test
     * @return the integer value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public static int getIntValue(final String s, final String shortName) throws InputParameterException
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException exception)
        {
            throw new InputParameterException(
                    "Field " + shortName + " does not contain a valid integer value -- value = '" + s + "'");
        }
    }
}
