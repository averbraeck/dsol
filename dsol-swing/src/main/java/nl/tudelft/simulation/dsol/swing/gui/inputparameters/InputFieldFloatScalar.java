package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.djunits.unit.Unit;
import org.djunits.value.vfloat.scalar.base.AbstractFloatScalar;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterFloatScalar;

/**
 * Swing InputField for Floats with a unit. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 * @param <U> the unit type
 * @param <T> the scalar type
 */
public class InputFieldFloatScalar<U extends Unit<U>, T extends AbstractFloatScalar<U, T>> extends AbstractInputField
{
    /** field for the float value. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JTextField floatField;

    /** combo box for the unit. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JComboBox<String> unitField;

    /**
     * Create a float field with a unit on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterFloatScalar&lt;U,T&gt;; the parameter
     */
    public InputFieldFloatScalar(final JPanel panel, final InputParameterFloatScalar<U, T> parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        this.floatField = new JTextField(20);
        this.floatField
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
            {
                defaultIndex = i;
            }
            i++;
        }
        this.unitField = new JComboBox<>(selections);
        this.unitField.setSelectedIndex(defaultIndex);

        panel.add(label);
        JPanel scalarPanel = new JPanel();
        scalarPanel.setLayout(new GridLayout(1, 2, 5, 0));
        scalarPanel.add(this.floatField);
        scalarPanel.add(this.unitField);
        panel.add(scalarPanel);
        panel.add(explanation);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public InputParameterFloatScalar<U, T> getParameter()
    {
        return (InputParameterFloatScalar<U, T>) super.getParameter();
    }

    /**
     * Return the numeric value of the field.
     * @return the float value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public T getFloatScalarValue() throws InputParameterException
    {
        float floatValue = getFloatValue(this.floatField.getText(), this.parameter.getShortName());
        getParameter().getFloatParameter().setFloatValue(floatValue);
        U unit = getParameter().getUnitParameter().getOptions().get(this.unitField.getSelectedItem().toString());
        getParameter().getUnitParameter().setMapValue(unit);
        getParameter().setCalculatedValue();
        return getParameter().getCalculatedValue();
    }

    /**
     * Return the float part of the entered value.
     * @return float; the float part of the entered value
     * @throws InputParameterException on invalid input
     */
    public float getFloatValue() throws InputParameterException
    {
        return getFloatValue(this.floatField.getText(), this.parameter.getShortName());
    }

    /**
     * Return the unit part of the entered value.
     * @return U; the unit part of the entered value
     * @throws InputParameterException on invalid input
     */
    public U getUnit() throws InputParameterException
    {
        return getParameter().getUnitParameter().getOptions().get(this.unitField.getSelectedItem().toString());
    }

    /**
     * Return the numeric value of the field.
     * @param s String; the String to test
     * @param shortName String; the name of the field to test
     * @return the float value of the field in the gui.
     * @throws InputParameterException on invalid input
     */
    public static float getFloatValue(final String s, final String shortName) throws InputParameterException
    {
        try
        {
            return Float.parseFloat(s);
        }
        catch (NumberFormatException exception)
        {
            throw new InputParameterException(
                    "Field " + shortName + " does not contain a valid float value -- value = '" + s + "'");
        }
    }
}
