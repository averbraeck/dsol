package nl.tudelft.simulation.dsol.swing.gui.animation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2d;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.d2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.animation.d2.AutoPanAnimationPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.SearchPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * Animation panel with GIS layers and various controls.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class DsolAnimationGisTab extends DsolAnimationTab
{
    /** */
    private static final long serialVersionUID = 20150617L;

    /** Set of GIS layer names to toggle GIS layers . */
    private Map<String, GisMapInterface> toggleGISMap = new LinkedHashMap<>();

    /** Set of GIS layer names to toggle buttons. */
    private Map<String, JToggleButton> toggleGISButtons = new LinkedHashMap<>();

    /**
     * Construct a tab with an AnimationPane for the animation of a DsolModel, including GIS layers.
     * @param homeExtent Bounds2d; initial extent of the animation
     * @param simulator SimulatorInterface; the simulator
     * @throws RemoteException when notification of the animation panel fails
     * @throws DsolException when simulator does not implement the AnimatorInterface
     */
    public DsolAnimationGisTab(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DsolException
    {
        super(homeExtent, simulator);
    }

    /**
     * Construct a tab with an AnimationPane for the animation of a DsolModel, including GIS layers.
     * @param simulator SimulatorInterface; the simulator
     * @param animationPanel AnimationPanel; the animation panel to use, e.g. the AutoPanAnimationPanel
     * @throws RemoteException when notification of the animation panel fails
     * @throws DsolException when simulator does not implement the AnimatorInterface
     */
    public DsolAnimationGisTab(final SimulatorInterface<?> simulator, final AnimationPanel animationPanel)
            throws RemoteException, DsolException
    {
        super(simulator, animationPanel);
    }

    /**
     * Construct a tab with an AutoPanAnimationPanel and a linked SearchPanel for the animation of a DsolModel, including GIS
     * layers.
     * @param homeExtent Bounds2d; initial extent of the animation
     * @param simulator SimulatorInterface; the simulator
     * @return DsolAnimationTab; a tab with an AutoPanAnimationPanel and a linked SearchPanel
     * @throws RemoteException when notification of the animation panel fails
     * @throws DsolException when simulator does not implement the AnimatorInterface
     */
    public static DsolAnimationTab createAutoPanTab(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DsolException
    {
        DsolAnimationTab tab = new DsolAnimationTab(simulator, new AutoPanAnimationPanel(homeExtent, simulator));
        tab.setSearchPanel(new SearchPanel());
        return tab;
    }

    /**
     * Add buttons for toggling all GIS layers on or off.
     * @param header String; the name of the group of layers
     * @param gisMap GisRenderable2d; the GIS map for which the toggles have to be added
     * @param toolTipText String; the tool tip text to show when hovering over the button
     */
    public void addAllToggleGISButtonText(final String header, final GisRenderable2d gisMap, final String toolTipText)
    {
        addToggleText(" ");
        addToggleText(header);
        try
        {
            for (String layerName : gisMap.getMap().getLayerMap().keySet())
            {
                addToggleGISButtonText(layerName, layerName, gisMap, toolTipText);
            }
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Add a button to toggle a GIS Layer on or off.
     * @param layerName String; the name of the layer
     * @param displayName String; the name to display next to the tick box
     * @param gisMap GisRenderable2d; the map
     * @param toolTipText String; the tool tip text
     */
    public void addToggleGISButtonText(final String layerName, final String displayName, final GisRenderable2d gisMap,
            final String toolTipText)
    {
        JToggleButton button;
        button = new JCheckBox(displayName);
        button.setName(layerName);
        button.setEnabled(true);
        button.setSelected(true);
        button.setActionCommand(layerName);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        JPanel toggleBox = new JPanel();
        toggleBox.setLayout(new BoxLayout(toggleBox, BoxLayout.X_AXIS));
        toggleBox.add(button);
        getTogglePanel().add(toggleBox);
        toggleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.toggleGISMap.put(layerName, gisMap.getMap());
        this.toggleGISButtons.put(layerName, button);
    }

    /**
     * Set a GIS layer to be shown in the animation to true.
     * @param layerName String; the name of the GIS-layer that has to be shown.
     */
    public void showGISLayer(final String layerName)
    {
        GisMapInterface gisMap = this.toggleGISMap.get(layerName);
        if (gisMap != null)
        {
            try
            {
                gisMap.showLayer(layerName);
                this.toggleGISButtons.get(layerName).setSelected(true);
                getAnimationPanel().repaint();
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Set a GIS layer to be hidden in the animation to true.
     * @param layerName String; the name of the GIS-layer that has to be hidden.
     */
    public void hideGISLayer(final String layerName)
    {
        GisMapInterface gisMap = this.toggleGISMap.get(layerName);
        if (gisMap != null)
        {
            try
            {
                gisMap.hideLayer(layerName);
                this.toggleGISButtons.get(layerName).setSelected(false);
                getAnimationPanel().repaint();
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Toggle a GIS layer to be displayed in the animation to its reverse value.
     * @param layerName String; the name of the GIS-layer that has to be turned off or vice versa.
     */
    public void toggleGISLayer(final String layerName)
    {
        GisMapInterface gisMap = this.toggleGISMap.get(layerName);
        if (gisMap != null)
        {
            try
            {
                if (gisMap.getVisibleLayers().contains(gisMap.getLayerMap().get(layerName)))
                {
                    gisMap.hideLayer(layerName);
                    this.toggleGISButtons.get(layerName).setSelected(false);
                }
                else
                {
                    gisMap.showLayer(layerName);
                    this.toggleGISButtons.get(layerName).setSelected(true);
                }
                getAnimationPanel().repaint();
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent)
    {
        String actionCommand = actionEvent.getActionCommand();
        try
        {
            if (this.toggleGISMap.containsKey(actionCommand))
            {
                this.toggleGISLayer(actionCommand);
                getTogglePanel().repaint();
            }
            super.actionPerformed(actionEvent); // handle other commands
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }

}
