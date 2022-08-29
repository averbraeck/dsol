# Custom InputParameter Layout

### Other layout for an InputParameter field

Suppose you want your own Swing layout for a field in the `TabbedParameterDialog`. The code below shows an example for an `InputParameterSelectionMap<?, ?>` for which we want to use a set of radio buttons o choose the option, tather than the default combo box.


## Creating a RadioButton layout

In this case, we create a new `InputField` that uses radio buttons for the selections. We keep a list of buttons (to later read the one that was pressed) and a list of values (so we know what to return as a value when radio button _i_ is pressed):

```java
public static class InputFieldSelectionMapRadio<K, T> extends AbstractInputField
{
    private List<JRadioButton> buttons = new ArrayList<>();
    private List<T> values = new ArrayList<>();
```

We then create the radio buttons in a `ButtonGroup` (so only one button can be pressed) in the constructor:

```java
    public InputFieldSelectionMapRadio(final JPanel panel, 
        final InputParameterSelectionMap<K, T> parameter)
    {
        super(parameter);
        Box box = Box.createVerticalBox();
        box.add(new JLabel("  "));
        box.add(new JLabel(parameter.getShortName()));
        ButtonGroup group = new ButtonGroup();
        for (K option : parameter.getOptions().keySet())
        {
            String item = option.toString();
            T value = parameter.getOptions().get(option);
            JRadioButton button = new JRadioButton(item);
            button.setActionCommand(item);
            if (value.equals(parameter.getDefaultValue()))
            {
                button.setSelected(true);
            }
            group.add(button);
            box.add(button);
            this.buttons.add(button);
            this.values.add(value);
        }
        panel.add(box);
    }
```

Finally, the InputField class should enable us to return the parameter in a properly casted way, and to return the value of the choice of the user:

```java
    public InputParameterSelectionMap<K, T> getParameter()
    {
        return (InputParameterSelectionMap<K, T>) super.getParameter();
    }

    public T getValue()
    {
        for (JRadioButton button : this.buttons)
        {
            if (button.isSelected())
            {
                return this.values.get(this.buttons.indexOf(button));
            }
        }
        return this.values.get(0); // or throw an Exception
    }
}
```

This completes the InputField class.


## Extending the TabbedParameterDialog

Now the `TabbedParameterDialog` should use the special InputField class to display the `InputParameterSelectionMap`. The first thing to do is to create your own dialog that extends `TabbedParameterDialog`:

```java
private static class ButtonParameterDialog extends TabbedParameterDialog
{
    ButtonParameterDialog(final InputParameterMap inputParameterMap)
    {
        super(inputParameterMap);
    }
```

The `ButtonParameterDialog` now has to override the `addParameterField` method to make sure that for the `InputParameterSelectionMap`, our own dialog class is used that displays radio butttons rather than a combo box.

```java
    public void addParameterField(final JPanel panel, final InputParameter<?, ?> parameter)
    {
        if (parameter instanceof InputParameterSelectionMap<?, ?>)
        {
            this.fields.add(new InputFieldSelectionMapRadio(
                panel, (InputParameterSelectionMap<?, ?>) parameter));
        }
        else
        {
            super.addParameterField(panel, parameter);
        }
    }
```

We have to make sure that we get the value from our own parameter field, rather than from the standard field. This is done in the `ActionPerformed` method that is called when the user presses the 'Start Simulation' button.

```java
    public void actionPerformed(final ActionEvent e)
    {
        boolean ok = true;
        try
        {
            for (InputField field : this.fields)
            {
                if (field instanceof InputFieldSelectionMapRadio<?, ?>)
                {
                    InputFieldSelectionMapRadio<?, ?> f = (InputFieldSelectionMapRadio<?, ?>) field;
                    f.getParameter().setObjectValue(f.getValue());
                }
            }
        }
        catch (Exception exception)
        {
            JOptionPane.showMessageDialog(null, exception.getMessage(), 
                "Data Entry Error", JOptionPane.ERROR_MESSAGE);
            ok = false;
        }
        if (ok)
        {
            super.actionPerformed(e);
        }
    }
```

As a last step, we can add the static `process` method that returns whether the user has successfully entered all data and pressed the 'Start Simulation' button. The static method eases the use in the code by allowing a statement like: `if (ButtonParameterDialog.process(this.InputParameterMap) ...` to have the user enter the data.

```java
    public static boolean process(final InputParameterMap inputParameterMap)
    {
        ButtonParameterDialog dialog = new ButtonParameterDialog(inputParameterMap);
        return !dialog.stopped;
    }
```

This completes the `ButtonParameterDialog` setup.
