package nl.tudelft.simulation.dsol.swing.gui.control;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * Generic ControlPanel container for the different types of control panel, with different clocks. These control panels do not
 * assume a DEVSSimulator, nor animation.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <S> the simulator type to use
 */
public class GenericControlPanel<
        T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends AbstractControlPanel<T, S>
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /**
     * Generic control panel with a different set of control buttons. The control panel does not assume a DEVSSimulator, nor
     * animation.
     * @param model DSOLModel&lt;T, ? extends SimulationInterface&lt;T&gt;&gt;; the model for the control panel, to
     *            allow a reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DEVSAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public GenericControlPanel(final DSOLModel<T, ? extends SimulatorInterface<T>> model, final S simulator)
            throws RemoteException
    {
        super(model, simulator);
    }

    /**
     * Generic ControlPanel for a Double time unit. The control panel does not assume a DEVSSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends GenericControlPanel<Double, SimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Double time unit, with a different set of control buttons. The control panel
         * does not assume a DEVSSimulator, nor animation.
         * @param model DSOLModel<Double>; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface<Double>; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDouble(final DSOLModel<Double, ? extends SimulatorInterface<Double>> model,
                final SimulatorInterface<Double> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDouble(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDouble(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDouble(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a Float time unit. The control panel does not assume a DEVSSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends GenericControlPanel<Float, SimulatorInterface<Float>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Float time unit, with a different set of control buttons. The control panel
         * does not assume a DEVSSimulator, nor animation.
         * @param model DSOLModel<Float>; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface<Float>; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloat(final DSOLModel<Float, ? extends SimulatorInterface<Float>> model,
                final SimulatorInterface<Float> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloat(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloat(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloat(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a Long time unit. The control panel does not assume a DEVSSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends GenericControlPanel<Long, SimulatorInterface<Long>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a Long time unit, with a different set of control buttons. The control panel
         * does not assume a DEVSSimulator, nor animation.
         * @param model DSOLModel<Long>; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface<Long>; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeLong(final DSOLModel<Long, ? extends SimulatorInterface<Long>> model,
                final SimulatorInterface<Long> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeLong(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeLong(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeLong(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a djunits double time unit. The control panel does not assume a DEVSSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit
            extends GenericControlPanel<Duration, SimulatorInterface<Duration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a djunits double time unit, with a different set of control buttons. The
         * control panel does not assume a DEVSSimulator, nor animation.
         * @param model DSOLModel<Duration>; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface<Duration>; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDoubleUnit(final DSOLModel<Duration, ? extends SimulatorInterface<Duration>> model,
                final SimulatorInterface<Duration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDoubleUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDoubleUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDoubleUnit(getSimulator()));
        }
    }

    /**
     * Generic ControlPanel for a djunits float time unit. The control panel does not assume a DEVSSimulator, nor animation.
     * <p>
     * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit
            extends GenericControlPanel<FloatDuration, SimulatorInterface<FloatDuration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a generic control panel for a djunits float time unit, with a different set of control buttons. The
         * control panel does not assume a DEVSSimulator, nor animation.
         * @param model DSOLModel<FloatDuration>; the model for the control panel, to allow a reset of the model
         * @param simulator SimulatorInterface<FloatDuration>; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloatUnit(final DSOLModel<FloatDuration, ? extends SimulatorInterface<FloatDuration>> model,
                final SimulatorInterface<FloatDuration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloatUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloatUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloatUnit(getSimulator()));
        }
    }

}
