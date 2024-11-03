package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.rmi.RemoteException;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.model.DsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.appearance.AppearanceControl;
import nl.tudelft.simulation.dsol.swing.gui.control.AbstractControlPanel;

/**
 * Tabbed content panel for the simulation with a control bar on top.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class DsolPanel extends JPanel implements AppearanceControl
{
    /** */
    private static final long serialVersionUID = 20150617L;

    /** The control panel to control start/stop, speed of the simulation. */
    private AbstractControlPanel<?, ?> controlPanel;

    /** The tabbed pane that contains the different (default) screens. */
    private final TabbedContentPane tabbedPane;

    static
    {
        // use narrow border for TabbedPane, which cannot be changed afterwards
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 1, 1, 1));
    }

    /**
     * Construct a panel for an interactive simulation model.
     * @param controlPanel AbstractControlPanel&lt;?, ?&gt;; the control panel to use (especially with relation to time
     *            control)
     * @throws RemoteException when communications to a remote machine fails
     */
    public DsolPanel(final AbstractControlPanel<?, ?> controlPanel) throws RemoteException
    {
        setPreferredSize(new Dimension(1024, 768));
        this.tabbedPane = new AppearanceControlTabbedContentPane(SwingConstants.BOTTOM);
        setLayout(new BorderLayout());

        // add the simulationControl at the top
        this.controlPanel = controlPanel;
        add(this.controlPanel, BorderLayout.NORTH);

        // add the tabbed contentPane in the center
        add(this.tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Add a tab to the DsolPanel as the last tab.
     * @param tabTitle String; the title of the tab
     * @param component Component; the swing component to add as this tab
     */
    public void addTab(final String tabTitle, final Component component)
    {
        this.tabbedPane.addTab(tabTitle, component);
    }

    /**
     * Add a tab to the DsolPanel at a given position.
     * @param position int; the position to insert the tab at (0 is first)
     * @param tabTitle String; the title of the tab
     * @param component Component; the swing component to add as this tab
     * @throws IndexOutOfBoundsException when position is less than zero or larger than the number of tabs
     */
    public void addTab(final int position, final String tabTitle, final Component component)
    {
        this.tabbedPane.addTab(position, tabTitle, component);
    }

    /**
     * Adds a console tab for the Logger.
     * @param logLevel Level the logLevel to use;
     */
    public void addConsoleLogger(final Level logLevel)
    {
        addTab("logger", new ConsoleLogger(logLevel));
    }

    /**
     * Adds a console tab for stdout and stderr.
     */
    public void addConsoleOutput()
    {
        addTab("console", new ConsoleOutput());
    }

    /**
     * Adds a properties tab.
     */
    public void addInputParametersTab()
    {
        addTab("parameters", new InputParametersTab(getModel()));
    }

    /**
     * @return tabbedPane
     */
    public TabbedContentPane getTabbedPane()
    {
        return this.tabbedPane;
    }

    /**
     * @return simulator.
     */
    public SimulatorInterface<?> getSimulator()
    {
        return this.controlPanel.getSimulator();
    }

    /**
     * Return the control panel of this SimulationPanel.
     * @return ControlPanel; the control panel
     */
    public AbstractControlPanel<?, ?> getControlPanel()
    {
        return this.controlPanel;
    }

    /**
     * @return the Model
     */
    public DsolModel<?, ?> getModel()
    {
        return this.controlPanel.getModel();
    }

    /**
     * Enable the simulation or animation buttons in the GUI. This method HAS TO BE CALLED in order for the buttons to be
     * enabled, because the initial state is DISABLED. Typically, this is done after all tabs, statistics, and other user
     * interface and model components have been constructed and initialized.
     */
    public void enableSimulationControlButtons()
    {
        this.controlPanel.setControlButtonsState(true);
    }

    /**
     * Disable the simulation or animation buttons in the GUI.
     */
    public void disableSimulationControlButtons()
    {
        this.controlPanel.setControlButtonsState(false);
    }

    @Override
    public String toString()
    {
        return "SimulationPanel";
    }

    /**
     * TabbedContentPane which ignores appearance (it has too much colors looking ugly / becoming unreadable).
     * <p>
     * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
     * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
     */
    static class AppearanceControlTabbedContentPane extends TabbedContentPane implements AppearanceControl
    {
        /** */
        private static final long serialVersionUID = 20180206L;

        /**
         * @param tabPlacement int; tabPlacement
         */
        AppearanceControlTabbedContentPane(final int tabPlacement)
        {
            super(tabPlacement);
        }

        @Override
        public String toString()
        {
            return "AppearanceControlTabbedContentPane []";
        }

    }
}
