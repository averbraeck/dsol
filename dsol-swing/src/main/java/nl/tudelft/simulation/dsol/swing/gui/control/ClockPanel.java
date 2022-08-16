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
 * Panel that displays the simulation time.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
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
    private Font timeFont = new Font("SansSerif", Font.BOLD, 18);

    /** The timer (so we can cancel it). */
    private Timer timer;

    /** Timer update interval in msec. */
    private long updateInterval = 1000;

    /** Simulation time time. */
    private T prevSimTime;

    /**
     * Construct a clock panel.
     * @param simulator SimulatorInterface&lt;T&gt;; the simulator
     */
    public ClockPanel(final SimulatorInterface<T> simulator)
    {
        this.simulator = simulator;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setFont(getTimeFont());

        this.timeLabel = new AppearanceControlLabel();
        this.timeLabel.setFont(getTimeFont());
        this.timeLabel.setMaximumSize(new Dimension(150, 35));
        add(this.timeLabel);

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimeUpdateTask(), 0, this.updateInterval);
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

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            T simulationTime = ClockPanel.this.getSimulator().getSimulatorTime();
            getTimeLabel().setText(formatSimulationTime(simulationTime));
            getTimeLabel().repaint();
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "TimeUpdateTask of ClockPanel";
        }
    }

    /**
     * @return timeLabel
     */
    public JLabel getTimeLabel()
    {
        return this.timeLabel;
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
    public long getUpdateInterval()
    {
        return this.updateInterval;
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
     * @param prevSimTime T; the new simulation time to be used in the next calculation for the speed
     */
    protected void setPrevSimTime(final T prevSimTime)
    {
        this.prevSimTime = prevSimTime;
    }

    /**
     * Returns the formatted simulation time.
     * @param simulationTime T; simulation time
     * @return formatted simulation time
     */
    protected abstract String formatSimulationTime(T simulationTime);

    /** {@inheritDoc} */
    @Override
    public boolean isForeground()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ClockPanel";
    }

    /**
     * ClockLabel for a double time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends ClockPanel<Double>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a double time.
         * @param simulator SimulatorInterface&lt;T&gt;; the simulator
         */
        public TimeDouble(final SimulatorInterface<Double> simulator)
        {
            super(simulator);
            setPrevSimTime(0.0);
        }

        /** {@inheritDoc} */
        @Override
        protected String formatSimulationTime(final Double simulationTime)
        {
            return String.format(" t = %8.2f ", simulationTime);
        }
    }

    /**
     * ClockLabel for a float time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends ClockPanel<Float>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a float time.
         * @param simulator SimulatorInterface&lt;T&gt;; the simulator
         */
        public TimeFloat(final SimulatorInterface<Float> simulator)
        {
            super(simulator);
            setPrevSimTime(0.0f);
        }

        /** {@inheritDoc} */
        @Override
        protected String formatSimulationTime(final Float simulationTime)
        {
            return String.format(" t = %8.2f ", simulationTime);
        }
    }

    /**
     * ClockLabel for a long time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends ClockPanel<Long>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a long time.
         * @param simulator SimulatorInterface&lt;T&gt;; the simulator
         */
        public TimeLong(final SimulatorInterface<Long> simulator)
        {
            super(simulator);
            setPrevSimTime(0L);
        }

        /** {@inheritDoc} */
        @Override
        protected String formatSimulationTime(final Long simulationTime)
        {
            return String.format(" t = %8d ", simulationTime);
        }
    }

    /**
     * ClockLabel for a djunits Time. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends ClockPanel<Duration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a double time with a unit.
         * @param simulator SimulatorInterface&lt;T&gt;; the simulator
         */
        public TimeDoubleUnit(final SimulatorInterface<Duration> simulator)
        {
            super(simulator);
            setPrevSimTime(Duration.ZERO);
        }

        /** {@inheritDoc} */
        @Override
        protected String formatSimulationTime(final Duration simulationTime)
        {
            return "t = " + simulationTime.toString(false, true);
        }
    }

    /**
     * ClockLabel for a djunits FloatTime. The formatter can be adjusted.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends ClockPanel<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a clock panel with a float time with a unit.
         * @param simulator SimulatorInterface&lt;T&gt;; the simulator
         */
        public TimeFloatUnit(final SimulatorInterface<FloatDuration> simulator)
        {
            super(simulator);
            setPrevSimTime(FloatDuration.ZERO);
        }

        /** {@inheritDoc} */
        @Override
        protected String formatSimulationTime(final FloatDuration simulationTime)
        {
            return "t = " + simulationTime.toString(false, true);
        }
    }

}
