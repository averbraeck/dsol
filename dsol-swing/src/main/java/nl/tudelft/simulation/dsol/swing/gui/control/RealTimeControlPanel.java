package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import javax.swing.JButton;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.event.Event;

import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.DevsxRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.RunState;

/**
 * ControlPanel container for the a DEVS simulator, with clocks for different time units.
 * <p>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <S> the simulator type to use
 */
public class RealTimeControlPanel<T extends Number & Comparable<T>, S extends DevsxRealTimeAnimator<T>>
        extends DEVSControlPanel<T, S> implements PropertyChangeListener
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /** The timeWarpPanel to control the speed. */
    private final RunSpeedSliderPanel runSpeedSliderPanel;

    /** The default animation delay (stored during fast forward). */
    private long savedAnimationDelay = 100L;

    /**
     * Generic control panel with a different set of control buttons. The control panel assumes a RealTimeDEVSAnimator and
     * animation, but the model specification is not necessarily specified as "real time"; its execution is.
     * @param model DSOLModel&lt;T, ? extends DEVSSimulationInterface&lt;T&gt;&gt;; the model for the control panel, to allow a
     *            reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DEVSAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public RealTimeControlPanel(final DSOLModel<T, ? extends DevsSimulatorInterface<T>> model, final S simulator)
            throws RemoteException
    {
        super(model, simulator);

        getControlButtonsPanel().add(makeButton("fastForwardButton", "/resources/FastForward.png", "FastForward",
                "Run the simulation as fast as possible", true));

        this.runSpeedSliderPanel = new RunSpeedSliderPanel(0.1, 1000, 1, 3, getSimulator());
        add(this.runSpeedSliderPanel);

        getSimulator().addListener(this, DevsxRealTimeAnimator.CHANGE_SPEED_FACTOR_EVENT);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("FastForward"))
            {
                if (getSimulator().isStoppingOrStopped())
                {
                    this.savedAnimationDelay = getSimulator().getAnimationDelay();
                    getSimulator().setAnimationDelay(0L);
                    getSimulator().setUpdateMsec(1000);
                    getSimulator().setAnimationDelay(500); // 2 Hz
                    getSimulator().start();
                }
            }
            if (actionCommand.equals("RunPause") || actionCommand.equals("Reset"))
            {
                getSimulator().setAnimationDelay(this.savedAnimationDelay);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        super.actionPerformed(actionEvent); // includes fixButtons()
    }

    /** {@inheritDoc} */
    @Override
    protected void fixButtons()
    {
        final boolean moreWorkToDo = getSimulator().getRunState() != RunState.ENDED;
        for (JButton button : getControlButtons())
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("FastForward"))
            {
                button.setEnabled(moreWorkToDo && isControlButtonsEnabled() && getSimulator().isStoppingOrStopped());
            }
        }
        super.fixButtons(); // handles the start/stop button
    }

    /** {@inheritDoc} */
    @Override
    protected void invalidateButtons()
    {
        for (JButton button : getControlButtons())
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("FastForward"))
            {
                button.setEnabled(false);
            }
        }
        super.invalidateButtons(); // handles the start/stop button
    }

    /** {@inheritDoc} */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        // TODO: when external change on speed -- update the slider panel
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(DevsxRealTimeAnimator.CHANGE_SPEED_FACTOR_EVENT))
        {
            this.runSpeedSliderPanel.setSpeedFactor((Double) event.getContent());
            fixButtons();
        }
        super.notify(event);
    }

    /**
     * DEVS Real Time ControlPanel for a Double timeunit.
     * <p>
     * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends RealTimeControlPanel<Double, DevsxRealTimeAnimator<Double>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a real time control panel for a Double time unit, with a different set of control buttons. The control
         * panel assumes a DEVSSimulator and animation. The model specification is not necessarily specified as "real time"; its
         * execution is.
         * @param model DSOLModel&lt;Double&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DEVSRealTimeAnimator.TimeDouble; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDouble(final DSOLModel<Double, ? extends DevsSimulatorInterface<Double>> model,
                final DevsxRealTimeAnimator<Double> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDouble(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDouble(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDouble(getSimulator()));
        }
    }

    /**
     * DEVS Real Time ControlPanel for a Float timeunit.
     * <p>
     * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends RealTimeControlPanel<Float, DevsxRealTimeAnimator<Float>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a real time control panel for a Float time unit, with a different set of control buttons. The control panel
         * assumes a DEVSSimulator and animation. The model specification is not necessarily specified as "real time"; its
         * execution is.
         * @param model DSOLModel&lt;Float&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DEVSRealTimeAnimator&lt;Float&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloat(final DSOLModel<Float, ? extends DevsSimulatorInterface<Float>> model,
                final DevsxRealTimeAnimator<Float> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloat(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloat(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloat(getSimulator()));
        }
    }

    /**
     * DEVS Real Time ControlPanel for a Long timeunit.
     * <p>
     * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends RealTimeControlPanel<Long, DevsxRealTimeAnimator<Long>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a real time control panel for a Long time unit, with a different set of control buttons. The control panel
         * assumes a DEVSSimulator and animation. The model specification is not necessarily specified as "real time"; its
         * execution is.
         * @param model DSOLModel&lt;Long&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DEVSRealTimeAnimator&lt;Long&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeLong(final DSOLModel<Long, ? extends DevsSimulatorInterface<Long>> model,
                final DevsxRealTimeAnimator<Long> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeLong(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeLong(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeLong(getSimulator()));
        }
    }

    /**
     * DEVS Real Time ControlPanel for a djunits double timeunit.
     * <p>
     * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends RealTimeControlPanel<Duration, DevsxRealTimeAnimator<Duration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a real time control panel for a djunits double time unit, with a different set of control buttons. The
         * control panel assumes a DEVSSimulator and animation. The model specification is not necessarily specified as "real
         * time"; its execution is.
         * @param model DSOLModel&lt;Duration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DEVSRealTimeAnimator&lt;Duration&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDoubleUnit(final DSOLModel<Duration, ? extends DevsSimulatorInterface<Duration>> model,
                final DevsxRealTimeAnimator<Duration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDoubleUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDoubleUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDoubleUnit(getSimulator()));
        }
    }

    /**
     * DEVS Real Time ControlPanel for a djunits float timeunit.
     * <p>
     * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends RealTimeControlPanel<FloatDuration, DevsxRealTimeAnimator<FloatDuration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a real time control panel for a djunits float time unit, with a different set of control buttons. The
         * control panel assumes a DEVSSimulator and animation. The model specification is not necessarily specified as "real
         * time"; its execution is.
         * @param model DSOLModel&lt;FloatDuration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DEVSRealTimeAnimator&lt;FloatDuration&gt;; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DEVSAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloatUnit(final DSOLModel<FloatDuration, ? extends DevsSimulatorInterface<FloatDuration>> model,
                final DevsxRealTimeAnimator<FloatDuration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloatUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloatUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloatUnit(getSimulator()));
        }
    }

}
