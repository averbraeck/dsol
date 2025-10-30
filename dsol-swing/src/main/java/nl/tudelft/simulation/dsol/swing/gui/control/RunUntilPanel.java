package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControlButton;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControlLabel;
import nl.tudelft.simulation.dsol.swing.gui.util.Icons;
import nl.tudelft.simulation.dsol.swing.gui.util.RegexFormatter;

/**
 * Panel that enables a panel that allows editing of the "run until" time.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class RunUntilPanel<T extends Number & Comparable<T>> extends JPanel
        implements AppearanceControl, ActionListener
{
    /** */
    private static final long serialVersionUID = 20141211L;

    /** the simulator. */
    private final SimulatorInterface<T> simulator;

    /** the input field. */
    private final JFormattedTextField textField;

    /** the initial / reset value of the timeUntil field. */
    private String initialValue;

    /** the apply or cancel button. */
    private JButton runUntilButton;

    /** the state: is there a valid value or not? */
    private boolean applyState = false;

    /** the "run until" time, or null when not set. */
    private T runUntilTime = null;

    /**
     * Construct a clock panel.
     * @param simulator the simulator
     * @param initialValue the initial value of the time to display
     * @param regex the regular expression to which the entered text needs to adhere
     */
    public RunUntilPanel(final SimulatorInterface<T> simulator, final String initialValue, final String regex)
    {
        Throw.whenNull(simulator, "simulator");
        Throw.whenNull(initialValue, "initialValue");
        Throw.whenNull(regex, "regex");
        this.simulator = simulator;
        this.initialValue = initialValue;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.textField = new JFormattedTextField(new RegexFormatter(regex));
        setTextFieldSize(new Dimension(120, 20));
        this.textField.setValue(this.initialValue);

        setPanelSize(new Dimension(250, 35));

        Icon runUntilIcon = Icons.loadIcon("/resources/Apply.png");
        this.runUntilButton = new AppearanceControlButton(runUntilIcon);
        this.runUntilButton.setName("runUntil");
        this.runUntilButton.setEnabled(true);
        this.runUntilButton.setActionCommand("RunUntil");
        this.runUntilButton.setToolTipText("Run until the given time; ignored if earlier than current simulation time");
        this.runUntilButton.addActionListener(this);

        add(new AppearanceControlLabel("Run until: "));
        add(this.textField);
        add(this.runUntilButton);
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("RunUntil"))
            {
                if (this.applyState)
                {
                    cancel();
                    return;
                }
                this.textField.commitEdit();
                String stopTimeValue = (String) this.textField.getValue();
                this.runUntilTime = parseSimulationTime(stopTimeValue);
                if (this.runUntilTime == null || getSimulator().getSimulatorTime().compareTo(this.runUntilTime) >= 0
                        || getSimulator().getReplication().getEndTime().compareTo(this.runUntilTime) < 0)
                {
                    cancel();
                    return;
                }
                apply();
            }
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception);
            try
            {
                cancel();
            }
            catch (Exception e)
            {
                CategoryLogger.always().warn(e);
            }
        }
    }

    /**
     * Text field ok -- make green and show cancel button.
     */
    private void apply()
    {
        synchronized (this.textField)
        {
            this.textField.setBackground(Color.GREEN);
            this.runUntilButton.setIcon(Icons.loadIcon("/resources/Cancel.png"));
            this.textField.validate();
            // getSimulator().addListener(this, SimulatorInterface.TIME_CHANGED_EVENT);
            synchronized (this)
            {
                if (getSimulator().isStartingOrRunning())
                {
                    getSimulator().stop();
                }
                if (getSimulator().getSimulatorTime().compareTo(this.runUntilTime) < 0)
                {
                    getSimulator().runUpTo(this.runUntilTime);
                }
            }
            this.applyState = true;
        }
    }

    /**
     * Text field not ok, or runUntil time reached -- reset field, make field white, and show apply button.
     */
    protected void cancel()
    {
        synchronized (this.textField)
        {
            this.runUntilTime = null;
            this.textField.setValue(this.initialValue);
            this.textField.setBackground(Color.WHITE);
            this.runUntilButton.setIcon(Icons.loadIcon("/resources/Apply.png"));
            this.textField.validate();
            this.applyState = false;
        }
    }

    /**
     * Returns the formatted simulation time.
     * @param simulationTime simulation time
     * @return formatted simulation time
     */
    protected abstract String formatSimulationTime(T simulationTime);

    /**
     * Returns the simulation time from the formatted string.
     * @param simulationTimeString simulation time as a string
     * @return simulation time contained in the String or null when not valid
     */
    protected abstract T parseSimulationTime(String simulationTimeString);

    /**
     * Set the size of the run-until panel. The proposed height is 35 pixels.
     * @param dimension the new dimension of the run-until panel on the screen
     */
    public void setPanelSize(final Dimension dimension)
    {
        Throw.whenNull(dimension, "dimension");
        setMinimumSize(dimension);
        setSize(dimension);
        setPreferredSize(dimension);
        setMaximumSize(dimension);
    }

    /**
     * Set the size of the text field in the run-until panel. The proposed height is 20 pixels.
     * @param dimension the new dimension of the text field in the run-until panel
     */
    public void setTextFieldSize(final Dimension dimension)
    {
        Throw.whenNull(dimension, "dimension");
        this.textField.setMinimumSize(dimension);
        this.textField.setSize(dimension);
        this.textField.setPreferredSize(dimension);
        this.textField.setMaximumSize(dimension);
    }

    /**
     * Return the simulator.
     * @return the simulator
     */
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    /**
     * Return the text field, e.g. to give it a different size or font.
     * @return the text field, e.g. to give it a different size or font
     */
    public JFormattedTextField getTextField()
    {
        return this.textField;
    }

    @Override
    public boolean isForeground()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "RunUntilPanel [time=" + this.textField.getText() + "]";
    }

    /**
     * RunUntilPanel for a double time. The time formatter and time display can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends RunUntilPanel<Double>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct a clock panel with a double time.
         * @param simulator the simulator
         */
        public TimeDouble(final SimulatorInterface<Double> simulator)
        {
            super(simulator, "0.0", "^([0-9]+([.][0-9]*)?|[.][0-9]+)$");
        }

        @Override
        protected String formatSimulationTime(final Double simulationTime)
        {
            return String.format("%s", simulationTime);
        }

        @Override
        protected Double parseSimulationTime(final String simulationTimeString)
        {
            try
            {
                double t = Double.parseDouble(simulationTimeString);
                return t > 0.0 ? t : null;
            }
            catch (Exception exception)
            {
                return null;
            }
        }
    }

    /**
     * RunUntilPanel for a float time. The time formatter and time display can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends RunUntilPanel<Float>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct a clock panel with a float time.
         * @param simulator the simulator
         */
        public TimeFloat(final SimulatorInterface<Float> simulator)
        {
            super(simulator, "0.0", "^([0-9]+([.][0-9]*)?|[.][0-9]+)$");
        }

        @Override
        protected String formatSimulationTime(final Float simulationTime)
        {
            return String.format("%s", simulationTime);
        }

        @Override
        protected Float parseSimulationTime(final String simulationTimeString)
        {
            try
            {
                float t = Float.parseFloat(simulationTimeString);
                return t > 0.0f ? t : null;
            }
            catch (Exception exception)
            {
                return null;
            }
        }
    }

    /**
     * RunUntilPanel for a long time. The time formatter and time display can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends RunUntilPanel<Long>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct a clock panel with a long time.
         * @param simulator the simulator
         */
        public TimeLong(final SimulatorInterface<Long> simulator)
        {
            super(simulator, "0", "[0-9]+");
        }

        @Override
        protected String formatSimulationTime(final Long simulationTime)
        {
            return String.format("%s", simulationTime);
        }

        @Override
        protected Long parseSimulationTime(final String simulationTimeString)
        {
            try
            {
                long t = Long.parseLong(simulationTimeString);
                return t > 0L ? t : null;
            }
            catch (Exception exception)
            {
                return null;
            }
        }
    }

    /**
     * RunUntilPanel for a double djunits Time. The time formatter and time display can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends RunUntilPanel<Duration>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct a clock panel with a double djunits Time.
         * @param simulator the simulator
         */
        public TimeDoubleUnit(final SimulatorInterface<Duration> simulator)
        {
            super(simulator, "0.0 s",
                    "^[0-9]*[.]?[0-9]+([eE][-+]?[0-9]+)?\\s*(s|sec|Ys|Ysec|Zs|Zsec|Es|Esec|Ps|Psec|Ts|Tsec|Gs|Gsec"
                            + "|Ms|Msec|ks|ksec|hs|hsec|das|dasec|ds|dsec|cs|csec|ms|\\u03BCs|mus|\\u03BCsec|musec"
                            + "|ns|nsec|ps|psec|fs|fsec|as|asec|zs|zsec|ys|ysec|day|h|hr|hour|min|wk|week)");
        }

        @Override
        protected String formatSimulationTime(final Duration simulationTime)
        {
            return String.format("%s", simulationTime.toString());
        }

        @Override
        protected Duration parseSimulationTime(final String simulationTimeString)
        {
            try
            {
                Duration t = Duration.valueOf(simulationTimeString);
                return t.gt(Duration.ZERO) ? t : null;
            }
            catch (Exception exception)
            {
                return null;
            }
        }
    }

    /**
     * RunUntilPanel for a float djunits Time. The time formatter and time display can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends RunUntilPanel<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct a clock panel with a djunits FloatDuration.
         * @param simulator the simulator
         */
        public TimeFloatUnit(final SimulatorInterface<FloatDuration> simulator)
        {
            super(simulator, "0.0 s",
                    "^[0-9]*[.]?[0-9]+([eE][-+]?[0-9]+)?\\s*(s|sec|Ys|Ysec|Zs|Zsec|Es|Esec|Ps|Psec|Ts|Tsec|Gs|Gsec"
                            + "|Ms|Msec|ks|ksec|hs|hsec|das|dasec|ds|dsec|cs|csec|ms|\\u03BCs|mus|\\u03BCsec|musec"
                            + "|ns|nsec|ps|psec|fs|fsec|as|asec|zs|zsec|ys|ysec|day|h|hr|hour|min|wk|week)");
        }

        @Override
        protected String formatSimulationTime(final FloatDuration simulationTime)
        {
            return String.format("%s", simulationTime.toString());
        }

        @Override
        protected FloatDuration parseSimulationTime(final String simulationTimeString)
        {
            try
            {
                FloatDuration t = FloatDuration.valueOf(simulationTimeString);
                return t.gt(FloatDuration.ZERO) ? t : null;
            }
            catch (Exception exception)
            {
                return null;
            }
        }
    }

}
