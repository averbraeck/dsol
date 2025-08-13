package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.clock.ClockDevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControlLabel;

/**
 * Panel that displays the simulation time.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class ClockPanel<T extends Number & Comparable<T>> extends JPanel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 20141211L;

    /** The JLabel that displays the time. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public JLabel timeLabel;

    /** the simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    final SimulatorInterface<T> simulator;

    /** Font used to display the clock. */
    private Font timeFont;

    /** The timer (so we can cancel it). */
    private Timer timer;

    /** Timer update interval in msec. */
    private long updateIntervalMs = 1000;

    /** Simulation time time. */
    private T prevSimTime;

    /** Clock time supplier. */
    private Supplier<String> clockTimeSupplier;

    /**
     * Construct a clock panel with an initial dimension of 150x35 pixels. Provide the supplier of the time string as the second
     * parameter. The supplier can use `getSimulator()` to get access to the time (absolute, relative) and simulator status
     * (initialized, started, running, stopped).
     * @param simulator the simulator
     * @param clockTimeSupplier the supplier of the clock time to be printed
     */
    public ClockPanel(final SimulatorInterface<T> simulator, final Supplier<String> clockTimeSupplier)
    {
        Throw.whenNull(simulator, "simulator");
        this.simulator = simulator;
        setClockTimeSupplier(clockTimeSupplier);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.timeLabel = new AppearanceControlLabel();
        setTimeFont(new Font("SansSerif", Font.BOLD, 18));
        setPanelSize(new Dimension(150, 35));
        add(this.timeLabel);

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimeUpdateTask(), 0, this.updateIntervalMs);
    }

    /**
     * Set the size of the time label on the screen. The proposed height is 35 pixels.
     * @param dimension the new dimension of the time label on the screen
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
     * Set (update) the supplier of the clock time to be printed.
     * @param clockTimeSupplier the new supplier of the clock time to be printed
     */
    public void setClockTimeSupplier(final Supplier<String> clockTimeSupplier)
    {
        Throw.whenNull(clockTimeSupplier, "clockTimeSupplier");
        this.clockTimeSupplier = clockTimeSupplier;
    }

    /**
     * Set the font to display the time.
     * @param timeFont the font to display the time
     */
    public void setTimeFont(final Font timeFont)
    {
        Throw.whenNull(timeFont, "timeFont");
        this.timeFont = timeFont;
        setFont(this.timeFont);
        this.timeLabel.setFont(this.timeFont);
    }

    /**
     * Cancel the timer task.
     */
    public void cancelTimer()
    {
        if (this.timer != null)
        {
            this.timer.cancel();
        }
        this.timer = null;
    }

    /** Updater for the clock panel. */
    protected class TimeUpdateTask extends TimerTask implements Serializable
    {
        /** */
        private static final long serialVersionUID = 20140000L;

        @Override
        public void run()
        {
            getTimeLabel().setText(ClockPanel.this.clockTimeSupplier.get());
            getTimeLabel().repaint();
        }

        @Override
        public String toString()
        {
            return "TimeUpdateTask of ClockPanel";
        }
    }

    /**
     * Return the label in which the time is written.
     * @return the label in which the time is written
     */
    public JLabel getTimeLabel()
    {
        return this.timeLabel;
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
     * Return the update interval (in ms).
     * @return the update interval (in ms)
     */
    public long getUpdateIntervalMs()
    {
        return this.updateIntervalMs;
    }

    /**
     * Return the previous simulation time.
     * @return the previous simulation time
     */
    public T getPrevSimTime()
    {
        return this.prevSimTime;
    }

    /**
     * Set the new simulation time to be used in the next calculation for the speed.
     * @param prevSimTime the new simulation time to be used in the next calculation for the speed
     */
    protected void setPrevSimTime(final T prevSimTime)
    {
        this.prevSimTime = prevSimTime;
    }

    @Override
    public boolean isForeground()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "ClockPanel";
    }

    /**
     * ClockLabel for a double time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends ClockPanel<Double>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a double time.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public TimeDouble(final SimulatorInterface<Double> simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(0.0);
        }

        /**
         * Construct a clock panel with a default supplier for the double time.
         * @param simulator the simulator
         */
        public TimeDouble(final SimulatorInterface<Double> simulator)
        {
            this(simulator, () -> String.format(" t = %8.2f ", simulator.getSimulatorTime()));
        }
    }

    /**
     * ClockLabel for a float time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends ClockPanel<Float>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a float time.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public TimeFloat(final SimulatorInterface<Float> simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(0.0f);
        }

        /**
         * Construct a clock panel with a float time.
         * @param simulator the simulator
         */
        public TimeFloat(final SimulatorInterface<Float> simulator)
        {
            this(simulator, () -> String.format(" t = %8.2f ", simulator.getSimulatorTime()));
        }
    }

    /**
     * ClockLabel for a long time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends ClockPanel<Long>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a long time.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public TimeLong(final SimulatorInterface<Long> simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(0L);
        }

        /**
         * Construct a clock panel with a long time.
         * @param simulator the simulator
         */
        public TimeLong(final SimulatorInterface<Long> simulator)
        {
            this(simulator, () -> String.format(" t = %8d ", simulator.getSimulatorTime()));
        }
    }

    /**
     * ClockLabel for a djunits Time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends ClockPanel<Duration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a double time with unit.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public TimeDoubleUnit(final SimulatorInterface<Duration> simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(Duration.ZERO);
        }

        /**
         * Construct a clock panel with a double time with a unit.
         * @param simulator the simulator
         */
        public TimeDoubleUnit(final SimulatorInterface<Duration> simulator)
        {
            this(simulator, () -> simulator.getSimulatorTime().toString(false, true));
        }
    }

    /**
     * ClockLabel for a djunits FloatTime. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends ClockPanel<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a float time with unit.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public TimeFloatUnit(final SimulatorInterface<FloatDuration> simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(FloatDuration.ZERO);
        }

        /**
         * Construct a clock panel with a float time with a unit.
         * @param simulator the simulator
         */
        public TimeFloatUnit(final SimulatorInterface<FloatDuration> simulator)
        {
            this(simulator, () -> simulator.getSimulatorTime().toString(false, true));

        }
    }

    /**
     * ClockLabel for a djunits Time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class ClockTime extends ClockPanel<Duration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a clock-based time and clock-based simulator.
         * @param simulator the simulator
         * @param clockTimeSupplier the supplier of the clock time to be printed
         */
        public ClockTime(final ClockDevsSimulatorInterface simulator, final Supplier<String> clockTimeSupplier)
        {
            super(simulator, clockTimeSupplier);
            setPrevSimTime(Duration.instantiateSI(simulator.getStartClockTime().si));
        }

        /**
         * Construct a clock panel with a clock-based time and clock-based simulator.
         * @param simulator the simulator
         */
        public ClockTime(final ClockDevsSimulatorInterface simulator)
        {
            this(simulator, () -> simulator.getSimulatorClockTime().localDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }
    }

}
