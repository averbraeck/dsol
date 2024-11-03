package nl.tudelft.simulation.dsol.swing.gui.control;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * Generic ControlPanel container for the different types of control panel, with different clocks. These control panels do not
 * assume a DevsSimulator, nor animation.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @param <S> the simulator type to use
 */
public class GenericControlPanel<
        T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends AbstractControlPanel<T, S>
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /**
     * Generic control panel with a different set of control buttons. The control panel does not assume a DevsSimulator, nor
     * animation.
     * @param model DsolModel&lt;T, ? extends SimulationInterface&lt;T&gt;&gt;; the model for the control panel, to
     *            allow a reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DevsAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public GenericControlPanel(final DsolModel<T, ? extends SimulatorInterface<T>> model, final S simulator)
            throws RemoteException
    {
        super(model, simulator);
    }

    /**
     * Generic ControlPanel for a Double time unit. The control panel does not assume a DevsSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends GenericControlPanel<Double, SimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Double time unit, with a different set of control buttons. The control panel
         * does not assume a DevsSimulator, nor animation.
         * @param model DsolModel&lt;Double&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface&lt;Double&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDouble(final DsolModel<Double, ? extends SimulatorInterface<Double>> model,
                final SimulatorInterface<Double> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDouble(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDouble(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDouble(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a Float time unit. The control panel does not assume a DevsSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends GenericControlPanel<Float, SimulatorInterface<Float>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Float time unit, with a different set of control buttons. The control panel
         * does not assume a DevsSimulator, nor animation.
         * @param model DsolModel&lt;Float&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface&lt;Float&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloat(final DsolModel<Float, ? extends SimulatorInterface<Float>> model,
                final SimulatorInterface<Float> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloat(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloat(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloat(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a Long time unit. The control panel does not assume a DevsSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends GenericControlPanel<Long, SimulatorInterface<Long>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Long time unit, with a different set of control buttons. The control panel
         * does not assume a DevsSimulator, nor animation.
         * @param model DsolModel&lt;Long&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface&lt;Long&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeLong(final DsolModel<Long, ? extends SimulatorInterface<Long>> model,
                final SimulatorInterface<Long> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeLong(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeLong(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeLong(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a djunits double time unit. The control panel does not assume a DevsSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit
            extends GenericControlPanel<Duration, SimulatorInterface<Duration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a djunits double time unit, with a different set of control buttons. The
         * control panel does not assume a DevsSimulator, nor animation.
         * @param model DsolModel&lt;Duration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface&lt;Duration&gt;; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDoubleUnit(final DsolModel<Duration, ? extends SimulatorInterface<Duration>> model,
                final SimulatorInterface<Duration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDoubleUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDoubleUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDoubleUnit(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a djunits float time unit. The control panel does not assume a DevsSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit
            extends GenericControlPanel<FloatDuration, SimulatorInterface<FloatDuration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a djunits float time unit, with a different set of control buttons. The
         * control panel does not assume a DevsSimulator, nor animation.
         * @param model DsolModel&lt;FloatDuration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface&lt;FloatDuration&gt;; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloatUnit(final DsolModel<FloatDuration, ? extends SimulatorInterface<FloatDuration>> model,
                final SimulatorInterface<FloatDuration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloatUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloatUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloatUnit(getSimulator()));
        }
    }

}
