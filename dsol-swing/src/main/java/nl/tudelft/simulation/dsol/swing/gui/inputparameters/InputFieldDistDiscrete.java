package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDistDiscreteSelection;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterFloat;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterLong;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMapDistDiscrete;

/**
 * Swing InputField for a selection of a continuous distribution, using a ComboBox. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputFieldDistDiscrete extends AbstractInputField implements ItemListener
{
    /** combo box for the user interface. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JComboBox<String> distComboBox;

    /** fields for the parameters. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<String, JTextField[]> textFields = new LinkedHashMap<>();

    /** mapping from String value to original class. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<String, InputParameterMapDistDiscrete> selectionMap = new LinkedHashMap<>();

    /** cards for the distributions. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected JPanel distPanel;

    /**
     * Create a string field on the screen.
     * @param panel JPanel; panel to add the field to
     * @param parameter InputParameterDistDiscreteSelection; the parameter
     */
    public InputFieldDistDiscrete(final JPanel panel, final InputParameterDistDiscreteSelection parameter)
    {
        super(parameter);
        JLabel label = new JLabel(parameter.getShortName());
        panel.add(label);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel comboBoxPane = new JPanel(); // use FlowLayout
        String[] selections = new String[parameter.getOptions().size()];
        int defaultIndex = 0;
        int i = 0;
        for (String option : parameter.getOptions().keySet())
        {
            selections[i] = option.toString();
            InputParameterMapDistDiscrete value = parameter.getOptions().get(option);
            this.selectionMap.put(selections[i], value);
            if (value.equals(parameter.getDefaultValue()))
            {
                defaultIndex = i;
            }
            i++;
        }
        this.distComboBox = new JComboBox<>(selections);
        this.distComboBox.setSelectedIndex(defaultIndex);
        this.distComboBox.addItemListener(this);
        comboBoxPane.add(this.distComboBox);

        CardLayout cardLayout = new CardLayout();
        this.distPanel = new JPanel(cardLayout);
        for (String option : parameter.getOptions().keySet())
        {
            JPanel distParamPanel = new JPanel();
            BoxLayout boxLayout = new BoxLayout(distParamPanel, BoxLayout.Y_AXIS);
            distParamPanel.setLayout(boxLayout);
            InputParameterMapDistDiscrete value = parameter.getOptions().get(option);
            JTextField[] paramFields = new JTextField[value.getSortedSet().size()];
            int index = 0;
            for (InputParameter<?, ?> param : value.getSortedSet())
            {
                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new GridLayout(1, 2, 5, 0));
                itemPanel.add(new JLabel(param.getShortName()));
                paramFields[index] = new JTextField(param.getDefaultValue().toString(), 20);
                itemPanel.add(paramFields[index]);
                distParamPanel.add(itemPanel);
                index++;
            }
            this.textFields.put(option.toString(), paramFields);
            distParamPanel.add(Box.createVerticalGlue());
            this.distPanel.add(distParamPanel, option.toString());
        }

        container.add(this.distComboBox);
        container.add(this.distPanel);
        panel.add(container);

        cardLayout.show(this.distPanel, selections[defaultIndex]);

        JLabel explanation = new JLabel(parameter.getDescription());
        panel.add(explanation);
    }

    /** {@inheritDoc} */
    @Override
    public void itemStateChanged(final ItemEvent event)
    {
        CardLayout cardLayout = (CardLayout) (this.distPanel.getLayout());
        cardLayout.show(this.distPanel, (String) event.getItem());
    }

    /** {@inheritDoc} */
    @Override
    public InputParameterDistDiscreteSelection getParameter()
    {
        return (InputParameterDistDiscreteSelection) super.getParameter();
    }

    /**
     * @throws InputParameterException when parameter not correctly entered
     */
    public void setDistParameterValues() throws InputParameterException
    {
        String selectedOption = this.distComboBox.getSelectedItem().toString();
        getParameter().setMapValue(this.selectionMap.get(selectedOption));
        InputParameterMapDistDiscrete ipMap = this.selectionMap.get(selectedOption);
        JTextField[] paramFields = this.textFields.get(selectedOption);
        int index = 0;
        for (InputParameter<?, ?> param : ipMap.getSortedSet())
        {
            String sValue = paramFields[index].getText();
            if (param instanceof InputParameterDouble)
            {
                InputParameterDouble dParam = (InputParameterDouble) param;
                dParam.setDoubleValue(InputFieldDouble.getDoubleValue(sValue, param.getShortName()));
            }
            else if (param instanceof InputParameterFloat)
            {
                InputParameterFloat fParam = (InputParameterFloat) param;
                fParam.setFloatValue(InputFieldFloat.getFloatValue(sValue, param.getShortName()));
            }
            else if (param instanceof InputParameterInteger)
            {
                InputParameterInteger iParam = (InputParameterInteger) param;
                iParam.setIntValue(InputFieldInteger.getIntValue(sValue, param.getShortName()));
            }
            else if (param instanceof InputParameterLong)
            {
                InputParameterLong lParam = (InputParameterLong) param;
                lParam.setLongValue(InputFieldLong.getLongValue(sValue, param.getShortName()));
            }
            index++;
        }
    }

}
