package nl.tudelft.simulation.dsol.swing.gui.animation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.TimedEvent;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.animation.D2.AutoPanAnimationPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.ButtonPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.InfoTextPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.PropertiesPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.SearchPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.TogglePanel;
import nl.tudelft.simulation.dsol.swing.gui.util.Icons;
import nl.tudelft.simulation.language.DSOLException;

/**
 * Animation panel with various controls. Code based on OpenTrafficSim project and Meslabs project component with the same
 * purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class DSOLAnimationTab extends JPanel implements ActionListener, EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 20150617L;

    /** the simulator. */
    private final SimulatorInterface<?> simulator;

    /**
     * Border panel for the DSOLAnimationTab. The layout is as follows: CENTER: AnimationPanel; NORTH: AnimationControlPanel
     * with ButtonPanel, SearchPanel, InfoTextPanel; LEFT: TogglePanel; RIGHT: PropertiesPanel.
     */
    private final JPanel borderPanel;

    /** The animation panel on tab position 0. */
    private final AnimationPanel animationPanel;

    /** Bar north of the animation with the controls. */
    private JPanel animationControlPanel;

    /** Toggle panel with which animation features can be shown/hidden. */
    private TogglePanel togglePanel;

    /** Button panel with navigation buttons for the animation. */
    private ButtonPanel buttonPanel;

    /** the search panel (can be null). */
    private SearchPanel searchPanel = null;

    /** the info panel with e.g., the mouse coordinates (can be null). */
    private InfoTextPanel infoTextPanel = null;

    /** the search panel (can be null). */
    private PropertiesPanel propertiesPanel = null;

    /** Map of toggle names to toggle animation classes. */
    private Map<String, Class<? extends Locatable>> toggleLocatableMap = new LinkedHashMap<>();

    /** Set of animation classes to toggle buttons. */
    private Map<Class<? extends Locatable>, JToggleButton> toggleButtons = new LinkedHashMap<>();

    /**
     * Construct a tab with an AnimationPanel for the animation of a DSOLModel.
     * @param homeExtent Bounds2d; initial extent of the animation
     * @param simulator SimulatorInterface; the simulator
     * @throws RemoteException on network error in case of a distributed simulation
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public DSOLAnimationTab(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DSOLException
    {
        this(simulator, new AnimationPanel(homeExtent, simulator));
    }

    /**
     * Construct a tab with an AnimationPanel for the animation of a DSOLModel.
     * @param simulator SimulatorInterface; the simulator
     * @param animationPanel AnimationPanel; the animation panel to use, e.g. the AutoPanAnimationPanel
     * @throws RemoteException on network error in case of a distributed simulation
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public DSOLAnimationTab(final SimulatorInterface<?> simulator, final AnimationPanel animationPanel)
            throws RemoteException, DSOLException
    {
        if (!(simulator instanceof AnimatorInterface))
        {
            throw new DSOLException("DSOLAnimationTab: simulator is not an instance of AnimatorInterface");
        }
        this.simulator = simulator;
        this.animationPanel = animationPanel;
        this.borderPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        add(this.borderPanel, BorderLayout.CENTER);
        init();
    }

    /**
     * Construct a tab with an AutoPanAnimationPanel and a linked SearchPanel for the animation of a DSOLModel.
     * @param homeExtent Bounds2d; initial extent of the animation
     * @param simulator SimulatorInterface; the simulator
     * @return DSOLAnimationTab; a tab with an AutoPanAnimationPanel and a linked SearchPanel
     * @throws RemoteException on network error in case of a distributed simulation
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public static DSOLAnimationTab createAutoPanTab(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DSOLException
    {
        DSOLAnimationTab tab = new DSOLAnimationTab(simulator, new AutoPanAnimationPanel(homeExtent, simulator));
        tab.setSearchPanel(new SearchPanel());
        return tab;
    }

    /**
     * This method makes the lay-out for the AnimationTab and can be overridden to create another lay-out.
     * @throws RemoteException on network error in case of a distributed simulation
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init() throws RemoteException
    {
        // Add the animation panel in the CENTER.
        this.animationPanel.showGrid(true);
        this.borderPanel.add(this.animationPanel, BorderLayout.CENTER);

        // Include the TogglePanel WEST of the animation.
        this.togglePanel = new TogglePanel(this);
        this.borderPanel.add(this.togglePanel, BorderLayout.WEST);

        // create the animation control NORTH of the animation
        this.animationControlPanel = new JPanel();
        this.animationControlPanel.setLayout(new BoxLayout(this.animationControlPanel, BoxLayout.X_AXIS));
        this.borderPanel.add(this.animationControlPanel, BorderLayout.NORTH);

        // add the buttons for home, zoom all, grid, and mouse coordinates
        this.buttonPanel = new ButtonPanel(this.animationPanel);
        this.animationControlPanel.add(this.buttonPanel);

        // add info labels next to buttons
        this.infoTextPanel = new InfoTextPanel(this.animationPanel);
        this.animationControlPanel.add(this.infoTextPanel);

        // Tell the animation to build the list of animation objects.
        this.animationPanel.notify(new TimedEvent(ReplicationInterface.START_REPLICATION_EVENT, this.simulator.getSourceId(), null,
                this.simulator.getSimulatorTime()));

        // do not show the X and Y coordinates in a tooltip.
        this.animationPanel.setShowToolTip(false);
    }

    /**
     * Set the search panel for this animation tab. Register the AnimationPanel as a listener for the SearchPanel, so it can
     * track or highlight objects. Register the PropertiesPanel (if existent) as a listener for the SearchPanel, as it might
     * display the properties of the searched object.
     * @param searchPanel SearchPanel; the search panel to use.
     */
    public void setSearchPanel(final SearchPanel searchPanel)
    {
        try
        {
            if (this.searchPanel != null)
            {
                this.animationControlPanel.remove(this.searchPanel);
                this.searchPanel.removeListener(this.animationPanel, SearchPanel.ANIMATION_SEARCH_OBJECT_EVENT);
                if (this.propertiesPanel != null)
                {
                    this.searchPanel.removeListener(this.propertiesPanel, SearchPanel.ANIMATION_SEARCH_OBJECT_EVENT);
                }
            }
            searchPanel.addListener(this.animationPanel, SearchPanel.ANIMATION_SEARCH_OBJECT_EVENT);
            if (this.propertiesPanel != null)
            {
                this.searchPanel.addListener(this.propertiesPanel, SearchPanel.ANIMATION_SEARCH_OBJECT_EVENT);
            }
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().warn(exception);
        }
        this.searchPanel = searchPanel;
        this.searchPanel.setMinimumSize(new Dimension(500, 10));
        this.searchPanel.setPreferredSize(new Dimension(500, 10));
        this.searchPanel.setMaximumSize(new Dimension(500, 30));
        this.animationControlPanel.add(Box.createHorizontalGlue());
        this.animationControlPanel.add(this.searchPanel);
    }

    /**
     * Add a button for toggling an animatable class on or off. Button icons for which 'idButton' is true will be placed to the
     * right of the previous button, which should be the corresponding button without the id. An example is an icon for
     * showing/hiding the class 'Lane' followed by the button to show/hide the Lane ids.
     * @param name String; the name of the button
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the button holds (e.g., Person.class)
     * @param iconPath String; the path to the 24x24 icon to display
     * @param toolTipText String; the tool tip text to show when hovering over the button
     * @param initiallyVisible boolean; whether the class is initially shown or not
     * @param idButton boolean; id button that needs to be placed next to the previous button
     */
    public void addToggleAnimationButtonIcon(final String name, final Class<? extends Locatable> locatableClass,
            final String iconPath, final String toolTipText, final boolean initiallyVisible, final boolean idButton)
    {
        JToggleButton button;
        Icon icon = Icons.loadIcon(iconPath);
        Icon unIcon = Icons.loadGrayscaleIcon(iconPath);
        button = new JCheckBox();
        button.setSelectedIcon(icon);
        button.setIcon(unIcon);
        button.setPreferredSize(new Dimension(32, 28));
        button.setName(name);
        button.setEnabled(true);
        button.setSelected(initiallyVisible);
        button.setActionCommand(name);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        // place an Id button to the right of the corresponding content button
        if (idButton && this.togglePanel.getComponentCount() > 0)
        {
            JPanel lastToggleBox = (JPanel) this.togglePanel.getComponent(this.togglePanel.getComponentCount() - 1);
            lastToggleBox.add(button);
        }
        else
        {
            JPanel toggleBox = new JPanel();
            toggleBox.setLayout(new BoxLayout(toggleBox, BoxLayout.X_AXIS));
            toggleBox.add(button);
            this.togglePanel.add(toggleBox);
            toggleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        if (initiallyVisible)
        {
            this.animationPanel.showClass(locatableClass);
        }
        else
        {
            this.animationPanel.hideClass(locatableClass);
        }
        this.toggleLocatableMap.put(name, locatableClass);
        this.toggleButtons.put(locatableClass, button);
    }

    /**
     * Add a button for toggling an animatable class on or off.
     * @param name String; the name of the button
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the button holds (e.g., Person.class)
     * @param toolTipText String; the tool tip text to show when hovering over the button
     * @param initiallyVisible boolean; whether the class is initially shown or not
     */
    public void addToggleAnimationButtonText(final String name, final Class<? extends Locatable> locatableClass,
            final String toolTipText, final boolean initiallyVisible)
    {
        JToggleButton button;
        button = new JCheckBox(name);
        button.setName(name);
        button.setEnabled(true);
        button.setSelected(initiallyVisible);
        button.setActionCommand(name);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        JPanel toggleBox = new JPanel();
        toggleBox.setLayout(new BoxLayout(toggleBox, BoxLayout.X_AXIS));
        toggleBox.add(button);
        this.togglePanel.add(toggleBox);
        toggleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (initiallyVisible)
        {
            this.animationPanel.showClass(locatableClass);
        }
        else
        {
            this.animationPanel.hideClass(locatableClass);
        }
        this.toggleLocatableMap.put(name, locatableClass);
        this.toggleButtons.put(locatableClass, button);
    }

    /**
     * Add a text to explain animatable classes.
     * @param text String; the text to show
     */
    public void addToggleText(final String text)
    {
        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.X_AXIS));
        textBox.add(new JLabel(text));
        this.togglePanel.add(textBox);
        textBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (this.toggleLocatableMap.containsKey(actionCommand))
            {
                Class<? extends Locatable> locatableClass = this.toggleLocatableMap.get(actionCommand);
                this.animationPanel.toggleClass(locatableClass);
                this.togglePanel.repaint();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Return the animation panel at the center of the screen.
     * @return AnimationPanel; the animation panel at the center of the screen
     */
    public AnimationPanel getAnimationPanel()
    {
        return this.animationPanel;
    }

    /**
     * Return the toggle panel to turn objects or layers on the screen on or off.
     * @return togglePanel TogglePanel; the toggle panel to turn objects or layers on the screen on or off
     */
    public JPanel getTogglePanel()
    {
        return this.togglePanel;
    }

    /**
     * Return the search panel; can be null.
     * @return SearchPanel the search panel; can be null
     */
    public SearchPanel getSearchPanel()
    {
        return this.searchPanel;
    }

    /**
     * Update the checkmark related to a programmatically changed animation state.
     * @param locatableClass Class&lt;? extends Locatable&gt;; class to show the checkmark for
     */
    public void updateAnimationClassCheckBox(final Class<? extends Locatable> locatableClass)
    {
        JToggleButton button = this.toggleButtons.get(locatableClass);
        if (button == null)
        {
            return;
        }
        button.setSelected(getAnimationPanel().isShowClass(locatableClass));
    }

    /**
     * Return the simulator.
     * @return SimulatorInterface; the simulator
     */
    public SimulatorInterface<?> getSimulator()
    {
        return this.simulator;
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        // At the moment there are no notifications for this tab.
    }

}
