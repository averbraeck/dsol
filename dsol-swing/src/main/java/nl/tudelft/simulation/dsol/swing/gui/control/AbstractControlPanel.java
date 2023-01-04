package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.RunState;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControlButton;
import nl.tudelft.simulation.dsol.swing.gui.util.Icons;

/**
 * Simulation control panel. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @param <S> the simulator type to use
 */
public abstract class AbstractControlPanel<
        T extends Number & Comparable<T>, S extends SimulatorInterface<T>> extends JPanel
        implements ActionListener, WindowListener, EventListener
{
    /** */
    private static final long serialVersionUID = 20150617L;

    /** The simulator. */
    private S simulator;

    /** The model, needed for its properties. */
    private final DSOLModel<T, ? extends SimulatorInterface<T>> model;

    /** The clock. */
    private ClockPanel<T> clockPanel;

    /** The speed. */
    private SpeedPanel<T> speedPanel;

    /** the Time editing panel. */
    private RunUntilPanel<T> runUntilPanel;

    /** The control buttons. */
    private final ArrayList<JButton> controlButtons = new ArrayList<>();

    /** The control buttons panel. */
    private final JPanel controlButtonsPanel;

    /** The current enabled state of the buttons. */
    private boolean controlButtonsEnabled = false;

    /** Has the window close handler been registered? */
    private boolean closeHandlerRegistered = false;

    /** Has cleanup taken place? */
    private boolean isCleanUp = false;

    /**
     * Define a generic control panel with a different set of control buttons. This abstract class defines those features that
     * are used by any simulator (continuous, discrete, real-time) and for any type of simulation time (floating point, integer,
     * or unit based). Specific classes extend this abstract control panel to define the additional features that are
     * necessary for those simulators.
     * @param model DSOLModel&lt;T, ? extends SimulationInterface&lt;T&gt;&gt;; the model for the control panel, to
     *            allow a reset of the model
     * @param simulator S; the simulator. Specified separately, because the model can have been specified with a superclass of
     *            the simulator that the ControlPanel actually needs (e.g., model has been specified with a DEVSAnimator,
     *            whereas the panel needs a RealTimeControlAnimator)
     * @throws RemoteException when simulator cannot be accessed for listener attachment
     */
    public AbstractControlPanel(final DSOLModel<T, ? extends SimulatorInterface<T>> model, final S simulator)
            throws RemoteException
    {
        Throw.whenNull(model, "model cannot be null");
        Throw.whenNull(simulator, "simulator cannot be null");
        this.model = model;
        this.simulator = simulator;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        this.controlButtonsPanel = new JPanel();
        this.controlButtonsPanel.setLayout(new BoxLayout(this.controlButtonsPanel, BoxLayout.X_AXIS));
        this.controlButtonsPanel.add(makeButton("resetButton", "/resources/Reset.png", "Reset", "Reset the simulation", false));
        this.controlButtonsPanel.add(makeButton("runPauseButton", "/resources/Run.png", "RunPause", "Run or pause the simulation", true));
        this.add(this.controlButtonsPanel);
        fixButtons();

        installWindowCloseHandler();

        this.simulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
        this.simulator.addListener(this, SimulatorInterface.START_EVENT);
        this.simulator.addListener(this, SimulatorInterface.STOP_EVENT);
    }

    /**
     * Change the enabled/disabled state of the various simulation control buttons.
     * @param newState boolean; true if the buttons should become enabled; false if the buttons should become disabled
     */
    public void setControlButtonsState(final boolean newState)
    {
        this.controlButtonsEnabled = newState;
        fixButtons();
    }

    /**
     * Create a button.
     * @param name String; name of the button
     * @param iconPath String; path to the resource
     * @param actionCommand String; the action command
     * @param toolTipText String; the hint to show when the mouse hovers over the button
     * @param enabled boolean; true if the new button must initially be enable; false if it must initially be disabled
     * @return JButton
     */
    protected JButton makeButton(final String name, final String iconPath, final String actionCommand, final String toolTipText,
            final boolean enabled)
    {
        JButton result = new AppearanceControlButton(Icons.loadIcon(iconPath));
        result.setName(name);
        result.setEnabled(enabled);
        result.setActionCommand(actionCommand);
        result.setToolTipText(toolTipText);
        result.addActionListener(this);
        this.controlButtons.add(result);
        return result;
    }

    /**
     * Install a handler for the window closed event that stops the simulator (if it is running).
     */
    public void installWindowCloseHandler()
    {
        if (this.closeHandlerRegistered)
        {
            return;
        }

        // make sure the root frame gets disposed of when the closing X icon is pressed.
        new DisposeOnCloseThread(this).start();
    }

    /** Install the dispose on close when the ControlPanel is registered as part of a frame. */
    protected class DisposeOnCloseThread extends Thread
    {
        /** The current container. */
        private AbstractControlPanel<T, S> panel;

        /**
         * @param panel ControlPanel; the control panel container.
         */
        public DisposeOnCloseThread(final AbstractControlPanel<T, S> panel)
        {
            this.panel = panel;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            Container root = this.panel;
            while (!(root instanceof JFrame))
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException exception)
                {
                    // nothing to do
                }

                // Search towards the root of the Swing components until we find a JFrame
                root = this.panel;
                while (null != root.getParent() && !(root instanceof JFrame))
                {
                    root = root.getParent();
                }
            }
            JFrame frame = (JFrame) root;
            frame.addWindowListener(this.panel);
            setCloseHandlerRegistered(true);
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "DisposeOnCloseThread [panel=" + this.panel + "]";
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (actionCommand.equals("RunPause"))
            {
                if (this.simulator.isStartingOrRunning())
                {
                    // System.out.println("RunPause: Stopping simulator");
                    this.simulator.stop();
                }
                else if (getSimulator().getRunState() != RunState.ENDED)
                {
                    // System.out.println("RunPause: Starting simulator");
                    this.simulator.start();
                }
            }
            else if (actionCommand.equals("Reset"))
            {
                // FIXME: Reset does not work yet, so button is greyed out for now...
                if (getSimulator().isStartingOrRunning())
                {
                    getSimulator().stop();
                }

                if (null == getModel())
                {
                    throw new RuntimeException("Do not know how to restart this simulation");
                }

                // find the JFrame position and dimensions
                Container root = this;
                while (!(root instanceof JFrame))
                {
                    root = root.getParent();
                }
                JFrame frame = (JFrame) root;
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.dispose();
                cleanup();

                // re-construct the model and reset the simulator and replication / experiment
                this.model.constructModel();
                // TODO reset the simulator and replication / experiment
            }
            fixButtons();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * clean up timers, contexts, threads, etc. that could prevent garbage collection.
     */
    private void cleanup()
    {
        if (!this.isCleanUp)
        {
            this.isCleanUp = true;
            try
            {
                if (this.simulator != null)
                {
                    if (this.simulator.isStartingOrRunning())
                    {
                        this.simulator.stop();
                    }

                    // unbind the old animation and statistics
                    if (getSimulator().getReplication().getContext().hasKey("animation"))
                    {
                        getSimulator().getReplication().getContext().destroySubcontext("animation");
                    }
                    if (getSimulator().getReplication().getContext().hasKey("statistics"))
                    {
                        getSimulator().getReplication().getContext().destroySubcontext("statistics");
                    }
                    if (getSimulator().getReplication().getContext().hasKey("statistics"))
                    {
                        getSimulator().getReplication().getContext().destroySubcontext("statistics");
                    }
                    // TODO: getSimulator().getReplication().getExperiment().removeFromContext(); // clean up the context
                }

                if (this.clockPanel != null)
                {
                    this.clockPanel.cancelTimer(); // cancel the timer on the clock panel.
                }
                if (this.speedPanel != null)
                {
                    this.speedPanel.cancelTimer(); // cancel the timer on the speed panel.
                }
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Update the enabled state of all the buttons.
     */
    protected void fixButtons()
    {
        final boolean moreWorkToDo = getSimulator().getRunState() != RunState.ENDED;
        for (JButton button : this.controlButtons)
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("RunPause"))
            {
                button.setEnabled(moreWorkToDo && this.controlButtonsEnabled);
                if (this.simulator.isStartingOrRunning())
                {
                    button.setToolTipText("Pause the simulation");
                    button.setIcon(Icons.loadIcon("/resources/Pause.png"));
                }
                else
                {
                    button.setToolTipText("Run the simulation at the indicated speed");
                    button.setIcon(Icons.loadIcon("/resources/Run.png"));
                }
                button.setEnabled(moreWorkToDo && this.controlButtonsEnabled);
            }
            else if (actionCommand.equals("Reset"))
            {
                // FIXME: should be disabled when the simulator was just reset or initialized
                button.setEnabled(false); // XXX: should be true when "reset" works!
            }
        }
    }

    /**
     * Update the state of all the control buttons to represent end-of-run (reset might be available).
     */
    protected void invalidateButtons()
    {
        for (JButton button : this.controlButtons)
        {
            final String actionCommand = button.getActionCommand();
            if (actionCommand.equals("RunPause"))
            {
                button.setToolTipText("Run the simulation at the indicated speed");
                button.setIcon(Icons.loadIcon("/resources/Run.png"));
                button.setEnabled(false);
            }
        }
    }

    /**
     * @return model
     */
    public DSOLModel<T, ? extends SimulatorInterface<T>> getModel()
    {
        return this.model;
    }

    /**
     * @return simulator.
     */
    public S getSimulator()
    {
        return this.simulator;
    }

    /**
     * @return clockPanel
     */
    public ClockPanel<T> getClockPanel()
    {
        return this.clockPanel;
    }

    /**
     * @param clockPanel set clockPanel
     */
    public void setClockPanel(final ClockPanel<T> clockPanel)
    {
        if (this.clockPanel != null)
        {
            // remove old clock panel from screen and stop its timer
            remove(this.clockPanel);
            this.clockPanel.cancelTimer();
        }
        this.clockPanel = clockPanel;
        add(this.clockPanel, 1); // after the buttons
    }

    /**
     * @return speedPanel
     */
    public SpeedPanel<T> getSpeedPanel()
    {
        return this.speedPanel;
    }

    /**
     * @param speedPanel set speedPanel
     */
    public void setSpeedPanel(final SpeedPanel<T> speedPanel)
    {
        if (this.speedPanel != null)
        {
            // remove old clock panel from screen and stop its timer
            remove(this.speedPanel);
            this.speedPanel.cancelTimer();
        }
        this.speedPanel = speedPanel;
        if (this.clockPanel != null)
        {
            add(this.speedPanel, 2); // place after the clockPanel
        }
        else
        {
            add(this.speedPanel, 1); // place after the buttons
        }
    }

    /**
     * @return controlButtons
     */
    public ArrayList<JButton> getControlButtons()
    {
        return this.controlButtons;
    }

    /**
     * @return controlButtonsPanel
     */
    public JPanel getControlButtonsPanel()
    {
        return this.controlButtonsPanel;
    }

    /**
     * @return runUntilPanel
     */
    public RunUntilPanel<T> getRunUntilPanel()
    {
        return this.runUntilPanel;
    }

    /**
     * @param runUntilPanel set runUntilPanel
     */
    public void setRunUntilPanel(final RunUntilPanel<T> runUntilPanel)
    {
        if (this.runUntilPanel != null)
        {
            remove(this.runUntilPanel);
        }
        this.runUntilPanel = runUntilPanel;
        add(this.runUntilPanel);
    }

    /**
     * @return closeHandlerRegistered
     */
    public boolean isCloseHandlerRegistered()
    {
        return this.closeHandlerRegistered;
    }

    /**
     * @return controlButtonsEnabled
     */
    public boolean isControlButtonsEnabled()
    {
        return this.controlButtonsEnabled;
    }

    /**
     * @param closeHandlerRegistered set closeHandlerRegistered
     */
    public void setCloseHandlerRegistered(final boolean closeHandlerRegistered)
    {
        this.closeHandlerRegistered = closeHandlerRegistered;
    }

    /** {@inheritDoc} */
    @Override
    public void windowOpened(final WindowEvent e)
    {
        // No action
    }

    /** {@inheritDoc} */
    @Override
    public void windowClosing(final WindowEvent e)
    {
        if (this.simulator != null)
        {
            try
            {
                if (this.simulator.isStartingOrRunning())
                {
                    this.simulator.stop();
                }
            }
            catch (SimRuntimeException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void windowClosed(final WindowEvent e)
    {
        cleanup();
    }

    /** {@inheritDoc} */
    @Override
    public void windowIconified(final WindowEvent e)
    {
        // No action
    }

    /** {@inheritDoc} */
    @Override
    public void windowDeiconified(final WindowEvent e)
    {
        // No action
    }

    /** {@inheritDoc} */
    @Override
    public void windowActivated(final WindowEvent e)
    {
        // No action
    }

    /** {@inheritDoc} */
    @Override
    public void windowDeactivated(final WindowEvent e)
    {
        // No action
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(SimulatorInterface.START_EVENT) || event.getType().equals(SimulatorInterface.STOP_EVENT))
        {
            fixButtons();
        }
        if (event.getType().equals(ReplicationInterface.END_REPLICATION_EVENT))
        {
            invalidateButtons();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ControlPanel [simulatorTime=" + this.simulator.getSimulatorTime() + "]";
    }

}
