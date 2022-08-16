package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;

/**
 * Swing InputField for Double. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldDouble extends InputFieldString
{
    /**
     * Create a double field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterDouble; the parameter
     */
    public InputFieldDouble(final JPanel panel, final InputParameterDouble parameter)
    {
        super(panel, parameter);
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterDouble getParameter()
    {
        return (InputParameterDouble) super.getParameter();
    }

    /**
     * Return the numeric value of the field.
     * @return the double value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public double getDoubleValue() throws InputParameterException
    {
        return getDoubleValue(this.textField.getText(), this.parameter.getShortName());
    }

    /**
     * Return the numeric value of the field.
     * @param s String; the String to test
     * @param shortName String; the name of the field to test
     * @return the double value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public static double getDoubleValue(final String s, final String shortName) throws InputParameterException
    {
        try
        {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException exception)
        {
            throw new InputParameterException(
                    "Field " + shortName + " does not contain a valid double value -- value = '" + s + "'");
        }
    }
}
