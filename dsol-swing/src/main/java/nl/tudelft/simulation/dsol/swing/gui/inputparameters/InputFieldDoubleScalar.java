package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.djunits.unit.Unit;
import org.djunits.value.vdouble.scalar.base.DoubleScalar;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDoubleScalar;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;

/**
 * Swing InputField for Doubles with a unit. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://github.com/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <U> the unit type
 * @param <T> the scalar type
 */
public class InputFieldDoubleScalar<U extends Unit<U>, T extends DoubleScalar<U, T>> extends AbstractInputField
{
    /** field for the double value. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JTextField doubleField;

    /** combo box for the unit. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JComboBox<String> unitField;

    /**
     * Create a double field with a unit on the screen.
     * @param panel panel to add the field to
     * @param parameter the parameter
     */
    public InputFieldDoubleScalar(final JPanel panel, final InputParameterDoubleScalar<U, T> parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        this.doubleField = new JTextField(20);
        this.doubleField
                .setText("" + parameter.getDefaultTypedValue().getInUnit(parameter.getDefaultTypedValue().getDisplayUnit()));
        JLabel explanation = new JLabel(parameter.getDescription());

        String[] selections = new String[parameter.getUnitParameter().getOptions().size()];
        int defaultIndex = 0;
        int i = 0;
        for (String option : parameter.getUnitParameter().getOptions().keySet())
        {
            selections[i] = option.toString();
            U value = parameter.getUnitParameter().getOptions().get(option);
            if (value.equals(parameter.getUnitParameter().getDefaultValue()))
            { defaultIndex = i; }
            i++;
        }
        this.unitField = new JComboBox<>(selections);
        this.unitField.setSelectedIndex(defaultIndex);

        panel.add(label);
        JPanel scalarPanel = new JPanel();
        scalarPanel.setLayout(new GridLayout(1, 2, 5, 0));
        scalarPanel.add(this.doubleField);
        scalarPanel.add(this.unitField);
        panel.add(scalarPanel);
        panel.add(explanation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InputParameterDoubleScalar<U, T> getParameter()
    {
        return (InputParameterDoubleScalar<U, T>) super.getParameter();
    }

    /**
     * Return the numeric value of the field.
     * @return the double value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public T getDoubleScalarValue() throws InputParameterException
    {
        double doubleValue = getDoubleValue(this.doubleField.getText(), this.parameter.getShortName());
        getParameter().getDoubleParameter().setDoubleValue(doubleValue);
        U unit = getParameter().getUnitParameter().getOptions().get(this.unitField.getSelectedItem().toString());
        getParameter().getUnitParameter().setMapValue(unit);
        getParameter().setCalculatedValue();
        return getParameter().getCalculatedValue();
    }

    /**
     * Return the double part of the entered value.
     * @return the double part of the entered value
     * @throws InputParameterException on invalid input
     */
    public double getDoubleValue() throws InputParameterException
    {
        return getDoubleValue(this.doubleField.getText(), this.parameter.getShortName());
    }

    /**
     * Return the unit part of the entered value.
     * @return the unit part of the entered value
     * @throws InputParameterException on invalid input
     */
    public U getUnit() throws InputParameterException
    {
        return getParameter().getUnitParameter().getOptions().get(this.unitField.getSelectedItem().toString());
    }

    /**
     * Return the numeric value of the field.
     * @param s the String to test
     * @param shortName the name of the field to test
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
