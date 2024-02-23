package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.RunState;

/**
 * ControlPanel container for the a DEVS simulator, with clocks for different time units.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @param <S> the simulator type to use
 */
public class DevsControlPanel<T extends Number & Comparable<T>, S extends DevsSimulatorInterface<T>>
        extends AbstractControlPanel<T, S>
{
    /** */
    private static final long serialVersionUID = 20201227L;

    /** The currently registered stop at event. */
    private SimEvent<T> stopAtEvent = null;

    /**
     * Generic control panel with a different set of control buttons. The control panel assumes a DevsSimulator that can be
     * paused, but it does not assume animation.
     * @param model DsolModel&lt;T, ? extends DevsSimulationInterface&lt;T&gt;&gt;; the model for the control panel, to allow a
     *            reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DevsAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public DevsControlPanel(final DsolModel<T, ? extends DevsSimulatorInterface<T>> model, final S simulator)
            throws RemoteException
    {
        super(model, simulator);

        // add the buttons to step the simulation
        getControlButtonsPanel().add(makeButton("stepButton", "/resources/Step.png", "Step", "Execute one event", true));
        getControlButtonsPanel().add(makeButton("nextTimeButton", "/resources/StepTime.png", "NextTime",
                "Execute all events scheduled for the current time", true));
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("Step"))
            {
                if (getSimulator().isStartingOrRunning())
                {
                    getSimulator().stop();
                }
                getSimulator().step();
            }
            if (actionCommand.equals("NextTime"))
            {
                if (getSimulator().isStartingOrRunning())
                {
                    getSimulator().stop();
                }
                T now = getSimulator().getSimulatorTime();
                try
                {
                    this.stopAtEvent =
                            new SimEvent<T>(now, SimEvent.MIN_PRIORITY, this,"autoPauseSimulator", null);
                    getSimulator().scheduleEvent(this.stopAtEvent);
                }
                catch (SimRuntimeException exception)
                {
                    getSimulator().getLogger().always()
                            .error("Caught an exception while trying to schedule an autoPauseSimulator event "
                                    + "at the current simulator time");
                }
                getSimulator().start();
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
            if (actionCommand.equals("Step"))
            {
                button.setEnabled(moreWorkToDo && isControlButtonsEnabled());
            }
            else if (actionCommand.equals("NextTime"))
            {
                button.setEnabled(moreWorkToDo && isControlButtonsEnabled());
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
            if (actionCommand.equals("Step"))
            {
                button.setEnabled(false);
            }
            else if (actionCommand.equals("NextTime"))
            {
                button.setEnabled(false);
            }
        }
        super.invalidateButtons(); // handles the start/stop button
    }

    /**
     * Pause the simulator.
     */
    public void autoPauseSimulator()
    {
        if (getSimulator().isStartingOrRunning())
        {
            try
            {
                getSimulator().stop();
            }
            catch (SimRuntimeException exception1)
            {
                exception1.printStackTrace();
            }
            T currentTick = getSimulator().getSimulatorTime();
            T nextTick = getSimulator().getEventList().first().getAbsoluteExecutionTime();
            if (nextTick.compareTo(currentTick) > 0)
            {
                // The clock is now just beyond where it was when the user requested the NextTime operation
                // Insert another autoPauseSimulator event just before what is now the time of the next event
                // and let the simulator time increment to that time
                try
                {
                    this.stopAtEvent =
                            new SimEvent<T>(nextTick, SimEvent.MAX_PRIORITY, this,"autoPauseSimulator", null);
                    getSimulator().scheduleEvent(this.stopAtEvent);
                    getSimulator().start();
                }
                catch (SimRuntimeException exception)
                {
                    getSimulator().getLogger().always()
                            .error("Caught an exception while trying to re-schedule an autoPauseEvent at the next real event");
                }
            }
            else
            {
                if (SwingUtilities.isEventDispatchThread())
                {
                    fixButtons();
                }
                else
                {
                    try
                    {
                        SwingUtilities.invokeAndWait(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                fixButtons();
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        if (e instanceof InterruptedException)
                        {
                            System.out.println("Caught " + e);
                        }
                        else
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * DEVS ControlPanel for a Double timeunit.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDouble extends DevsControlPanel<Double, DevsSimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a DEVS control panel for a Double time unit, with a different set of control buttons. The control panel
         * assumes a DevsSimulator, but not animation.
         * @param model DsolModel&lt;Double&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDouble(final DsolModel<Double, ? extends DevsSimulatorInterface<Double>> model,
                final DevsSimulatorInterface<Double> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDouble(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDouble(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDouble(getSimulator()));
        }
    }

    /**
     * DEVS ControlPanel for a Float timeunit.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloat extends DevsControlPanel<Float, DevsSimulatorInterface<Float>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a DEVS control panel for a Float time unit, with a different set of control buttons. The control panel
         * assumes a DevsSimulator, but not animation.
         * @param model DsolModel&lt;Float&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DevsSimulatorInterface&lt;Float&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloat(final DsolModel<Float, ? extends DevsSimulatorInterface<Float>> model,
                final DevsSimulatorInterface<Float> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloat(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloat(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloat(getSimulator()));
        }
    }

    /**
     * DEVS ControlPanel for a Long timeunit.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeLong extends DevsControlPanel<Long, DevsSimulatorInterface<Long>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a DEVS control panel for a Long time unit, with a different set of control buttons. The control panel
         * assumes a DevsSimulator, but not animation.
         * @param model DsolModel&lt;Long&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DevsSimulatorInterface&lt;Long&gt;; the simulator. Specified separately, because the model can have been
         *            specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has been
         *            specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeLong(final DsolModel<Long, ? extends DevsSimulatorInterface<Long>> model,
                final DevsSimulatorInterface<Long> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeLong(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeLong(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeLong(getSimulator()));
        }
    }

    /**
     * DEVS ControlPanel for a djunits double timeunit.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeDoubleUnit extends DevsControlPanel<Duration, DevsSimulatorInterface<Duration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a DEVS control panel for a djunits double time unit, with a different set of control buttons. The control
         * panel assumes a DevsSimulator, but not animation.
         * @param model DsolModel&lt;Duration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DevsSimulatorInterface&lt;Duration&gt;; the simulator. Specified separately, because the model can have
         *            been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model has
         *            been specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeDoubleUnit(final DsolModel<Duration, ? extends DevsSimulatorInterface<Duration>> model,
                final DevsSimulatorInterface<Duration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeDoubleUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeDoubleUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeDoubleUnit(getSimulator()));
        }
    }

    /**
     * DEVS ControlPanel for a djunits float timeunit.
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class TimeFloatUnit extends DevsControlPanel<FloatDuration, DevsSimulatorInterface<FloatDuration>>
    {
        /** */
        private static final long serialVersionUID = 20201227L;

        /**
         * Construct a DEVS control panel for a djunits float time unit, with a different set of control buttons. The control
         * panel assumes a DevsSimulator, but not animation.
         * @param model DsolModel&lt;FloatDuration&gt;; the model for the control panel, to allow a reset of the model
         * @param simulator DevsSimulatorInterface&lt;FloatDuration&gt;; the simulator. Specified separately, because the model can
         *            have been specified with a superclass of the simulator that the ControlPanel actually needs (e.g., model
         *            has been specified with a DevsAnimator, whereas the panel needs a RealTimeControlAnimator)
         * @throws RemoteException when simulator cannot be accessed for listener attachment
         */
        public TimeFloatUnit(final DsolModel<FloatDuration, ? extends DevsSimulatorInterface<FloatDuration>> model,
                final DevsSimulatorInterface<FloatDuration> simulator) throws RemoteException
        {
            super(model, simulator);
            setClockPanel(new ClockPanel.TimeFloatUnit(getSimulator()));
            setSpeedPanel(new SpeedPanel.TimeFloatUnit(getSimulator()));
            setRunUntilPanel(new RunUntilPanel.TimeFloatUnit(getSimulator()));
        }
    }

}
