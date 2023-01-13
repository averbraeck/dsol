package nl.tudelft.simulation.dsol.web.animation.D2;

import java.awt.Color;
import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point;
import org.djutils.event.Event;
import org.djutils.event.EventListener;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DComparator;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2D;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.web.animation.HTMLGraphics2D;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The AnimationPanel to display animated (Locatable) objects. Added the possibility to witch layers on and off. By default all
 * layers will be drawn, so no changes to existing software need to be made.<br>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl">www.simulation.tudelft.nl </a>.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class HTMLAnimationPanel extends HTMLGridPanel implements EventListener
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** the elements of this panel. */
    private SortedSet<Renderable2DInterface<? extends Locatable>> elements =
            new TreeSet<Renderable2DInterface<? extends Locatable>>(new Renderable2DComparator());

    /** filter for types to be shown or not. */
    private Map<Class<? extends Locatable>, Boolean> visibilityMap = new LinkedHashMap<>();

    /** cache of the classes that are hidden. */
    private Set<Class<? extends Locatable>> hiddenClasses = new LinkedHashSet<>();

    /** cache of the classes that are shown. */
    private Set<Class<? extends Locatable>> shownClasses = new LinkedHashSet<>();

    /** the simulator. */
    private SimulatorInterface<?> simulator;

    /** the eventContext. */
    private ContextInterface context = null;

    /** a line that helps the user to see where s/he is dragging. */
    private int[] dragLine = new int[4];

    /** enable drag line. */
    private boolean dragLineEnabled = false;

    /** List of drawable objects. */
    private List<Renderable2DInterface<? extends Locatable>> elementList = new ArrayList<>();

    /** dirty flag for the list. */
    private boolean dirtyElements = false;

    /** Map of toggle names to toggle animation classes. */
    private Map<String, Class<? extends Locatable>> toggleLocatableMap = new LinkedHashMap<>();

    /** Set of animation classes to toggle buttons. */
    private Map<Class<? extends Locatable>, ToggleButtonInfo> toggleButtonMap = new LinkedHashMap<>();

    /** Set of GIS layer names to toggle GIS layers . */
    private Map<String, GisMapInterface> toggleGISMap = new LinkedHashMap<>();

    /** Set of GIS layer names to toggle buttons. */
    private Map<String, ToggleButtonInfo> toggleGISButtonMap = new LinkedHashMap<>();

    /** List of buttons in the right order. */
    private List<ToggleButtonInfo> toggleButtons = new ArrayList<>();

    /**
     * constructs a new AnimationPanel.
     * @param extent Bounds2d; the extent of the panel
     * @param size Dimension; the size of the panel.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator of which we want to know the events for animation
     * @throws RemoteException on network error for one of the listeners
     */
    public HTMLAnimationPanel(final Bounds2d extent, final Dimension size, final SimulatorInterface<?> simulator)
            throws RemoteException
    {
        super(extent, size);
        super.showGrid = true;
        this.simulator = simulator;
        simulator.addListener(this, Replication.START_REPLICATION_EVENT);
    }

    /** {@inheritDoc} */
    @Override
    public void paintComponent(final HTMLGraphics2D g2)
    {
        // draw the grid.
        super.paintComponent(g2);

        // update drawable elements when necessary
        if (this.dirtyElements)
        {
            synchronized (this.elementList)
            {
                this.elementList.clear();
                this.elementList.addAll(this.elements);
                this.dirtyElements = false;
            }
        }

        // draw the animation elements.
        for (Renderable2DInterface<? extends Locatable> element : this.elementList)
        {
            if (isShowElement(element))
            {
                element.paintComponent(g2, this.getExtent(), this.getSize(), getRenderableScale(), this);
            }
        }

        // draw drag line if enabled.
        if (this.dragLineEnabled)
        {
            g2.setColor(Color.BLACK);
            g2.drawLine(this.dragLine[0], this.dragLine[1], this.dragLine[2], this.dragLine[3]);
            this.dragLineEnabled = false;
        }
    }

    /**
     * Test whether the element needs to be shown on the screen or not.
     * @param element Renderable2DInterface&lt;? extends Locatable&gt;; the renderable element to test
     * @return whether the element needs to be shown or not
     */
    public boolean isShowElement(final Renderable2DInterface<? extends Locatable> element)
    {
        return isShowClass(element.getSource().getClass());
    }

    /**
     * Test whether a certain class needs to be shown on the screen or not. The class needs to implement Locatable, otherwise it
     * cannot be shown at all.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class to test
     * @return whether the class needs to be shown or not
     */
    public boolean isShowClass(final Class<? extends Locatable> locatableClass)
    {
        if (this.hiddenClasses.contains(locatableClass))
        {
            return false;
        }
        else
        {
            boolean show = true;
            if (!this.shownClasses.contains(locatableClass))
            {
                for (Class<? extends Locatable> lc : this.visibilityMap.keySet())
                {
                    if (lc.isAssignableFrom(locatableClass))
                    {
                        if (!this.visibilityMap.get(lc))
                        {
                            show = false;
                        }
                    }
                }
                // add to the right cache
                if (show)
                {
                    this.shownClasses.add(locatableClass);
                }
                else
                {
                    this.hiddenClasses.add(locatableClass);
                }
            }
            return show;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(ContextInterface.OBJECT_ADDED_EVENT))
        {
            objectAdded((Renderable2DInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }

        else if (event.getType().equals(ContextInterface.OBJECT_REMOVED_EVENT))
        {
            objectRemoved((Renderable2DInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }

        else if // (this.simulator.getSourceId().equals(event.getSourceId()) && // TODO: improve check
        (event.getType().equals(Replication.START_REPLICATION_EVENT))
        {
            synchronized (this.elementList)
            {
                this.elements.clear();
                try
                {
                    if (this.context != null)
                    {
                        this.context.removeListener(this, ContextInterface.OBJECT_ADDED_EVENT);
                        this.context.removeListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
                    }

                    this.context =
                            ContextUtil.lookupOrCreateSubContext(this.simulator.getReplication().getContext(), "animation/2D");
                    this.context.addListener(this, ContextInterface.OBJECT_ADDED_EVENT);
                    this.context.addListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
                    for (Object element : this.context.values())
                    {
                        try
                        {
                            objectAdded((Renderable2DInterface<? extends Locatable>) element);
                        }
                        catch (Exception e)
                        {
                            //
                        }
                    }
                    this.repaint();
                }
                catch (Exception exception)
                {
                    this.simulator.getLogger().always().warn(exception, "notify");
                }
            }
        }
    }

    /**
     * Add a locatable object to the animation.
     * @param element Renderable2DInterface&lt;? extends Locatable&gt;; the element to add to the animation
     */
    public void objectAdded(final Renderable2DInterface<? extends Locatable> element)
    {
        synchronized (this.elementList)
        {
            this.elements.add(element);
            this.dirtyElements = true;
        }
    }

    /**
     * Remove a locatable object from the animation.
     * @param element Renderable2DInterface&lt;? extends Locatable&gt;; the element to add to the animation
     */
    public void objectRemoved(final Renderable2DInterface<? extends Locatable> element)
    {
        synchronized (this.elementList)
        {
            this.elements.remove(element);
            this.dirtyElements = true;
        }
    }

    /**
     * Calculate the full extent based on the current positions of the objects.
     * @return the full extent of the animation.
     */
    public synchronized Bounds2d fullExtent()
    {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        try
        {
            for (Renderable2DInterface<? extends Locatable> renderable : this.elementList)
            {
                Point<?> l = renderable.getSource().getLocation();
                if (l != null)
                {
                    Bounds<?, ?, ?> b = renderable.getSource().getBounds();
                    minX = Math.min(minX, l.getX() + b.getMinX());
                    minY = Math.min(minY, l.getY() + b.getMinY());
                    maxX = Math.max(maxX, l.getX() + b.getMaxX());
                    maxY = Math.max(maxY, l.getY() + b.getMaxY());
                }
            }
        }
        catch (Exception e)
        {
            // ignore
        }

        minX -= 0.05 * Math.abs(maxX - minX);
        minY -= 0.05 * Math.abs(maxY - minY);
        maxX += 0.05 * Math.abs(maxX - minX);
        maxY += 0.05 * Math.abs(maxY - minY);

        return new Bounds2d(minX, maxX, minY, maxY);
    }

    /**
     * resets the panel to its an extent that covers all displayed objects.
     */
    public synchronized void zoomAll()
    {
        this.extent = getRenderableScale().computeVisibleExtent(fullExtent(), this.getSize());
        this.repaint();
    }

    /**
     * Set a class to be shown in the animation to true.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the animation has to be shown.
     */
    public void showClass(final Class<? extends Locatable> locatableClass)
    {
        this.visibilityMap.put(locatableClass, true);
        this.shownClasses.clear();
        this.hiddenClasses.clear();
        this.repaint();
    }

    /**
     * Set a class to be hidden in the animation to true.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the animation has to be hidden.
     */
    public void hideClass(final Class<? extends Locatable> locatableClass)
    {
        this.visibilityMap.put(locatableClass, false);
        this.shownClasses.clear();
        this.hiddenClasses.clear();
        this.repaint();
    }

    /**
     * Toggle a class to be displayed in the animation to its reverse value.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which a visible animation has to be turned off or
     *            vice versa.
     */
    public void toggleClass(final Class<? extends Locatable> locatableClass)
    {
        if (!this.visibilityMap.containsKey(locatableClass))
        {
            showClass(locatableClass);
        }
        this.visibilityMap.put(locatableClass, !this.visibilityMap.get(locatableClass));
        this.shownClasses.clear();
        this.hiddenClasses.clear();
        this.repaint();
    }

    /**
     * @return the set of animation elements.
     */
    public SortedSet<Renderable2DInterface<? extends Locatable>> getElements()
    {
        return this.elements;
    }

    /**
     * @return returns the dragLine.
     */
    public int[] getDragLine()
    {
        return this.dragLine;
    }

    /**
     * @return returns the dragLineEnabled.
     */
    public boolean isDragLineEnabled()
    {
        return this.dragLineEnabled;
    }

    /**
     * @param dragLineEnabled boolean; the dragLineEnabled to set.
     */
    public void setDragLineEnabled(final boolean dragLineEnabled)
    {
        this.dragLineEnabled = dragLineEnabled;
    }

    /**********************************************************************************************************/
    /******************************************* TOGGLES ******************************************************/
    /**********************************************************************************************************/

    /**
     * Add a button for toggling an animatable class on or off.
     * @param name String; the name of the button
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the button holds (e.g., GTU.class)
     * @param toolTipText String; the tool tip text to show when hovering over the button
     * @param initiallyVisible boolean; whether the class is initially shown or not
     */
    public void addToggleAnimationButtonText(final String name, final Class<? extends Locatable> locatableClass,
            final String toolTipText, final boolean initiallyVisible)
    {
        ToggleButtonInfo.LocatableClass buttonInfo =
                new ToggleButtonInfo.LocatableClass(name, locatableClass, toolTipText, initiallyVisible);
        if (initiallyVisible)
        {
            showClass(locatableClass);
        }
        else
        {
            hideClass(locatableClass);
        }
        this.toggleButtons.add(buttonInfo);
        this.toggleLocatableMap.put(name, locatableClass);
        this.toggleButtonMap.put(locatableClass, buttonInfo);
    }

    /**
     * Show a Locatable class based on the name.
     * @param name the name of the class to show
     */
    public void showClass(final String name)
    {
        showClass(this.toggleLocatableMap.get(name));
    }

    /**
     * Hide a Locatable class based on the name.
     * @param name the name of the class to hide
     */
    public void hideClass(final String name)
    {
        hideClass(this.toggleLocatableMap.get(name));
    }

    /**
     * Add a text to explain animatable classes.
     * @param text String; the text to show
     */
    public void addToggleText(final String text)
    {
        this.toggleButtons.add(new ToggleButtonInfo.Text(text, true));
    }

    /**
     * Add buttons for toggling all GIS layers on or off.
     * @param header String; the name of the group of layers
     * @param gisMap GisRenderable2D; the GIS map for which the toggles have to be added
     * @param toolTipText String; the tool tip text to show when hovering over the button
     */
    public void addAllToggleGISButtonText(final String header, final GisRenderable2D gisMap, final String toolTipText)
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
     * @param gisMap GisRenderable2D; the map
     * @param toolTipText String; the tool tip text
     */
    public void addToggleGISButtonText(final String layerName, final String displayName, final GisRenderable2D gisMap,
            final String toolTipText)
    {
        ToggleButtonInfo.Gis buttonInfo = new ToggleButtonInfo.Gis(displayName, layerName, toolTipText, true);
        this.toggleButtons.add(buttonInfo);
        this.toggleGISMap.put(layerName, gisMap.getMap());
        this.toggleGISButtonMap.put(layerName, buttonInfo);
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
                this.toggleGISButtonMap.get(layerName).setVisible(true);
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
                this.toggleGISButtonMap.get(layerName).setVisible(false);
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
                    this.toggleGISButtonMap.get(layerName).setVisible(false);
                }
                else
                {
                    gisMap.showLayer(layerName);
                    this.toggleGISButtonMap.get(layerName).setVisible(true);
                }
            }
            catch (RemoteException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * @return toggleButtons
     */
    public List<ToggleButtonInfo> getToggleButtons()
    {
        return this.toggleButtons;
    }

}
