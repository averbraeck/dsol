package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.swing.JPanel;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterLocalDateTime;

/**
 * Swing InputField for LocalDateTime. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldLocalDateTime extends InputFieldString
{
    /**
     * Create an ISO date-time field on the screen.
     * @param panel panel to add the field to
     * @param parameter the parameter
     */
    public InputFieldLocalDateTime(final JPanel panel, final InputParameterLocalDateTime parameter)
    {
        super(panel, parameter);
    }

    @Override
    public InputParameterLocalDateTime getParameter()
    {
        return (InputParameterLocalDateTime) super.getParameter();
    }

    /**
     * Return the DateTime value of the field.
     * @return the DateTime value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public LocalDateTime getDateTimeValue() throws InputParameterException
    {
        return getDateTimeValue(this.textField.getText(), this.parameter.getShortName());
    }

    /**
     * Return the DateTime value of the field.
     * @param s the String to test
     * @param shortName the name of the field to test
     * @return the DateTime value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public static LocalDateTime getDateTimeValue(final String s, final String shortName) throws InputParameterException
    {
        try
        {
            return LocalDateTime.parse(s);
        }
        catch (DateTimeParseException exception)
        {
            throw new InputParameterException(
                    "Field " + shortName + " does not contain a valid ISO DateTime value -- value = '" + s + "'");
        }
    }
}
