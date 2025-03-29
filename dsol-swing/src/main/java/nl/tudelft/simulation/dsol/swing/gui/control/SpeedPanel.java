package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControlLabel;

/**
 * Panel that displays the simulation speed.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class SpeedPanel<T extends Number & Comparable<T>> extends JPanel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 20141211L;

    /** The JLabel that displays the simulation speed. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public JLabel speedLabel;

    /** the simulator. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    final SimulatorInterface<T> simulator;

    /** Font used to display the clock. */
    private Font timeFont = new Font("SansSerif", Font.BOLD, 18);

    /** The timer (so we can cancel it). */
    private Timer timer;

    /** Timer update interval in msec. */
    private long updateIntervalMs = 1000;

    /** Simulation time time. */
    private T prevSimTime;

    /**
     * Construct a clock panel.
     * @param simulator the simulator
     */
    public SpeedPanel(final SimulatorInterface<T> simulator)
    {
        this.simulator = simulator;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setFont(getTimeFont());

        this.speedLabel = new AppearanceControlLabel();
        this.speedLabel.setFont(getTimeFont());
        this.speedLabel.setMaximumSize(new Dimension(100, 35));
        add(this.speedLabel);

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimeUpdateTask(), 0, this.updateIntervalMs);
    }

    /**
     * Cancel the timer task.
     */
    public void cancelTimer()
    {
        if (this.timer != null)
        { this.timer.cancel(); }
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
            T simulationTime = SpeedPanel.this.getSimulator().getSimulatorTime();
            getSpeedLabel().setText(formatSpeed(simulationTime));
            getSpeedLabel().repaint();
        }

        @Override
        public String toString()
        {
            return "TimeUpdateTask of SpeedPanel";
        }
    }

    /**
     * @return speedLabel
     */
    public JLabel getSpeedLabel()
    {
        return this.speedLabel;
    }

    /**
     * @return simulator
     */
    public SimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    /**
     * @return timeFont.
     */
    public Font getTimeFont()
    {
        return this.timeFont;
    }

    /**
     * @return updateInterval
     */
    public long getUpdateIntervalMs()
    {
        return this.updateIntervalMs;
    }

    /**
     * @return prevSimTime
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

    /**
     * Returns the simulation speed as a String.
     * @param simulationTime simulation time
     * @return simulation speed
     */
    protected abstract String formatSpeed(T simulationTime);

    @Override
    public boolean isForeground()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "SpeedPanel";
    }

    /**
     * SpeedPanel for a double time. The speed calculation can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends SpeedPanel<Double>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a double time.
         * @param simulator the simulator
         */
        public TimeDouble(final SimulatorInterface<Double> simulator)
        {
            super(simulator);
            setPrevSimTime(0.0);
        }

        @Override
        protected String formatSpeed(final Double simulationTime)
        {
            if (simulationTime == null)
            { return "0.0"; }
            double speed = (simulationTime - getPrevSimTime()) / (0.001 * getUpdateIntervalMs());
            setPrevSimTime(simulationTime);
            return String.format("%6.2f x ", speed);
        }
    }

    /**
     * SpeedPanel for a float time. The speed calculation can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends SpeedPanel<Float>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a float time.
         * @param simulator the simulator
         */
        public TimeFloat(final SimulatorInterface<Float> simulator)
        {
            super(simulator);
            setPrevSimTime(0.0f);
        }

        @Override
        protected String formatSpeed(final Float simulationTime)
        {
            if (simulationTime == null)
            { return "0.0"; }
            double speed = (simulationTime - getPrevSimTime()) / (0.001 * getUpdateIntervalMs());
            setPrevSimTime(simulationTime);
            return String.format("%6.2f x ", speed);
        }
    }

    /**
     * SpeedPanel for a long time. The speed calculation can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends SpeedPanel<Long>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a long time.
         * @param simulator the simulator
         */
        public TimeLong(final SimulatorInterface<Long> simulator)
        {
            super(simulator);
            setPrevSimTime(0L);
        }

        @Override
        protected String formatSpeed(final Long simulationTime)
        {
            if (simulationTime == null)
            { return "0.0"; }
            double speed = (simulationTime - getPrevSimTime()) / (0.001 * getUpdateIntervalMs());
            setPrevSimTime(simulationTime);
            return String.format("%6.2f x ", speed);
        }
    }

    /**
     * SpeedPanel for a djutils Duration. The speed calculation can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends SpeedPanel<Duration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a djutils Duration.
         * @param simulator the simulator
         */
        public TimeDoubleUnit(final SimulatorInterface<Duration> simulator)
        {
            super(simulator);
            setPrevSimTime(Duration.ZERO);
        }

        @Override
        protected String formatSpeed(final Duration simulationTime)
        {
            if (simulationTime == null)
            { return "0.0"; }
            double speed = (simulationTime.si - getPrevSimTime().si) / (0.001 * getUpdateIntervalMs());
            setPrevSimTime(simulationTime);
            return String.format("%6.2f x ", speed);
        }
    }

    /**
     * SpeedPanel for a djutils FloatDuration. The speed calculation can be adjusted.
     * <p>
     * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends SpeedPanel<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a djutils Duration.
         * @param simulator the simulator
         */
        public TimeFloatUnit(final SimulatorInterface<FloatDuration> simulator)
        {
            super(simulator);
            setPrevSimTime(FloatDuration.ZERO);
        }

        @Override
        protected String formatSpeed(final FloatDuration simulationTime)
        {
            if (simulationTime == null)
            { return "0.0"; }
            double speed = (simulationTime.si - getPrevSimTime().si) / (0.001 * getUpdateIntervalMs());
            setPrevSimTime(simulationTime);
            return String.format("%6.2f x ", speed);
        }
    }

}
