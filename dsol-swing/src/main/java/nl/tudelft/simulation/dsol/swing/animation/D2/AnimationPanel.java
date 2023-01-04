package nl.tudelft.simulation.dsol.swing.animation.D2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.reference.ReferenceType;
import org.djutils.logger.CategoryLogger;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DComparator;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.actions.IntrospectionAction;
import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The AnimationPanel to display animated (Locatable) objects. Added the possibility to witch layers on and off. By default all
 * layers will be drawn, so no changes to existing software need to be made. <br>
 * <br>
 * <b>Asynchronous and synchronous calls:</b><br>
 * The internal functions of the AnimationPanel are handled in a synchronous way inside the animation panel, possibly through
 * (mouse or keyboard) listeners and handlers that implement the functions.There are several exceptions, though:
 * <ul>
 * <li><i>Clicking on one or more objects:</i> what has to happen is very much dependent on the implementation. Therefore, the
 * click on an object will lead to firing of an event, where the listener(s), if any, can decide what to do. This can be
 * dependent on whether CTRL, SHIFT, or ALT were pressed at the same time as the mouse button. Example behaviors could be:
 * pop-up with properties of the object; showing properties in a special pane; highlighting the object; or setting the auto-pan
 * on the clicked object. The event to use is the ANIMATION_MOUSE_CLICK_EVENT.</li>
 * <li><i>Right click on one or more objects:</i> what has to happen is very much dependent on the implementation. Therefore,
 * the click on an object will lead to firing of an event, where the listener(s), if any, can decide what to do. The event to
 * use is the ANIMATION_MOUSE_POPUP_EVENT.</li>
 * </ul>
 * Furthermore, the AnimationPanel is an event listener, and listens, e.g., to the event of a searched object: the
 * ANIMATION_SEARCH_OBJECT_EVENT to highlight the object, or, in case of an AutoPanAnimationPanel, to keep the object in the
 * middle of the screen.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class AnimationPanel extends GridPanel implements EventListener, EventProducer
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the elements of this panel. */
    private SortedSet<Renderable2DInterface<? extends Locatable>> elements =
            new TreeSet<Renderable2DInterface<? extends Locatable>>(new Renderable2DComparator());

    /** filter for types to be shown or not. */
    private Map<Class<? extends Locatable>, Boolean> visibilityMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** cache of the classes that are hidden. */
    private Set<Class<? extends Locatable>> hiddenClasses = new LinkedHashSet<>();

    /** cache of the classes that are shown. */
    private Set<Class<? extends Locatable>> shownClasses = new LinkedHashSet<>();

    /** the simulator. */
    private SimulatorInterface<?> simulator;

    /** the context with the path /experiment/replication/animation/2D. */
    private ContextInterface context = null;

    /** a line that helps the user to see where she/he is dragging. */
    private int[] dragLine = new int[4];

    /** enable drag line. */
    private boolean dragLineEnabled = false;

    /** List of drawable objects. */
    private List<Renderable2DInterface<? extends Locatable>> elementList = new ArrayList<>();

    /** dirty flag for the list. */
    private boolean dirty = false;

    /** delegate class to do handle event producing. */
    private final AnimationEventProducer animationEventProducer;

    /** the margin factor 'around' the extent. */
    public static final double EXTENT_MARGIN_FACTOR = 0.05;

    /** the event when the user clicked ith the left mouse button, possibly on one or more objects. */
    public static final EventType ANIMATION_MOUSE_CLICK_EVENT = new EventType(new MetaData("ANIMATION_MOUSE_CLICK_EVENT",
            "ANIMATION_MOUSE_CLICK_EVENT",
            new ObjectDescriptor("worldCoordinate", "x and y position in world coordinates", Point2d.class),
            new ObjectDescriptor("screenCoordinate", "x and y position in screen coordinates", java.awt.Point.class),
            new ObjectDescriptor("shiftCtrlAlt", "shift[0], ctrl[1], and/or alt[2] pressed", boolean[].class),
            new ObjectDescriptor("objectList", "List of objects whose bounding box includes the coordinate", List.class)));

    /** the event when the user clicked with the right mouse button, selecting from on one or more objects. */
    public static final EventType ANIMATION_MOUSE_POPUP_EVENT = new EventType(new MetaData("ANIMATION_MOUSE_POPUP_EVENT",
            "ANIMATION_MOUSE_POPUP_EVENT",
            new ObjectDescriptor("worldCoordinate", "x and y position in world coordinates", Point2d.class),
            new ObjectDescriptor("screenCoordinate", "x and y position in screen coordinates", java.awt.Point.class),
            new ObjectDescriptor("shiftCtrlAlt", "shift[0], ctrl[1], and/or alt[2] pressed", boolean[].class),
            new ObjectDescriptor("object", "Selected object whose bounding box includes the coordinate", Object.class)));

    /**
     * constructs a new AnimationPanel.
     * @param homeExtent Bounds2d; the home (initial) extent of the panel
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator of which we want to know the events for animation
     * @throws RemoteException on network error for one of the listeners
     * @throws DSOLException when the simulator is not implementing the AnimatorInterface
     */
    public AnimationPanel(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DSOLException
    {
        super(homeExtent);
        if (!(simulator instanceof AnimatorInterface))
        {
            throw new DSOLException("Simulator must implement the AnimatorInterface");
        }
        this.animationEventProducer = new AnimationEventProducer();
        super.showGrid = true;
        InputListener listener = new InputListener(this);
        this.simulator = simulator;
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
        this.addMouseWheelListener(listener);
        this.addKeyListener(listener);
        simulator.addListener(this, AnimatorInterface.UPDATE_ANIMATION_EVENT);
        simulator.addListener(this, ReplicationInterface.START_REPLICATION_EVENT);
    }

    /** {@inheritDoc} */
    @Override
    public void paintComponent(final Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        // draw the grid.
        super.paintComponent(g2);

        // update drawable elements when necessary
        if (this.dirty)
        {
            synchronized (this.elementList)
            {
                this.elementList.clear();
                this.elementList.addAll(this.elements);
                this.dirty = false;
            }
        }

        // draw the animation elements
        for (Renderable2DInterface<? extends Locatable> element : this.elementList)
        {
            // destroy has been called?
            if (element.getSource() == null)
            {
                objectRemoved(element);
            }
            else if (isShowElement(element))
            {
                element.paintComponent(g2, this.getExtent(), this.getSize(), getRenderableScale(), this);
            }
        }

        // draw drag line if enabled.
        if (this.dragLineEnabled)
        {
            g.setColor(Color.BLACK);
            g.drawLine(this.dragLine[0], this.dragLine[1], this.dragLine[2], this.dragLine[3]);
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
        return element.getSource() == null ? false : isShowClass(element.getSource().getClass());
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
                synchronized (this.visibilityMap)
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
            }
            return show;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(AnimatorInterface.UPDATE_ANIMATION_EVENT) && this.isShowing())
        {
            if (this.getWidth() > 0 || this.getHeight() > 0)
            {
                this.repaint();
            }
            return;
        }

        else if (event.getType().equals(ContextInterface.OBJECT_ADDED_EVENT))
        {
            objectAdded((Renderable2DInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }

        else if (event.getType().equals(ContextInterface.OBJECT_REMOVED_EVENT))
        {
            objectRemoved((Renderable2DInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }

        else if (event.getType().equals(ReplicationInterface.START_REPLICATION_EVENT))
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
                        if (element instanceof Renderable2DInterface)
                        {
                            objectAdded((Renderable2DInterface<? extends Locatable>) element);
                        }
                        else
                        {
                            System.err.println("odd object in context: " + element);
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
            this.dirty = true;
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
            this.dirty = true;
        }
    }

    /**
     * Calculate the full extent based on the current positions of the objects.
     * @return Bounds2d; the full extent of the animation.
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
                if (renderable.getSource() == null)
                {
                    continue;
                }
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

        minX -= EXTENT_MARGIN_FACTOR * Math.abs(maxX - minX);
        minY -= EXTENT_MARGIN_FACTOR * Math.abs(maxY - minY);
        maxX += EXTENT_MARGIN_FACTOR * Math.abs(maxX - minX);
        maxY += EXTENT_MARGIN_FACTOR * Math.abs(maxY - minY);

        return new Bounds2d(minX, maxX, minY, maxY);
    }

    /**
     * resets the panel to its an extent that covers all displayed objects.
     */
    public synchronized void zoomAll()
    {
        setExtent(getRenderableScale().computeVisibleExtent(fullExtent(), this.getSize()));
    }

    /**
     * Set a class to be shown in the animation to true.
     * @param locatableClass Class&lt;? extends Locatable&gt;; the class for which the animation has to be shown.
     */
    public void showClass(final Class<? extends Locatable> locatableClass)
    {
        synchronized (this.visibilityMap)
        {
            this.visibilityMap.put(locatableClass, true);
        }
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
        synchronized (this.visibilityMap)
        {
            this.visibilityMap.put(locatableClass, false);
        }
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
        synchronized (this.visibilityMap)
        {
            if (!this.visibilityMap.containsKey(locatableClass))
            {
                showClass(locatableClass);
            }
            this.visibilityMap.put(locatableClass, !this.visibilityMap.get(locatableClass));
        }
        this.shownClasses.clear();
        this.hiddenClasses.clear();
        this.repaint();
    }

    /**
     * Handle the movement of the mouse.
     * @param point Point; the location of the mouse relative to the AnimationPanel
     */
    public void mouseMoved(final java.awt.Point point)
    {
        Point2d world = getRenderableScale().getWorldCoordinates(point, getExtent(), getSize());
        setWorldCoordinate(world);
        displayWorldCoordinateToolTip();
    }

    // public static final EventType ANIMATION_MOUSE_CLICK_EVENT = new EventType(new MetaData("ANIMATION_MOUSE_CLICK_EVENT",
    // "ANIMATION_MOUSE_CLICK_EVENT",
    // new ObjectDescriptor("worldCoordinate", "x and y position in world coordinates", Point2d.class),
    // new ObjectDescriptor("screenCoordinate", "x and y position in screen coordinates", java.awt.Point.class),
    // new ObjectDescriptor("shiftCtrlAlt", "shift[0], ctrl[1], and/or alt[2] pressed", boolean[].class),
    // new ObjectDescriptor("objectList", "List of objects whose bounding box includes the coordinate", List.class)));
    //
    // public static final EventType ANIMATION_MOUSE_POPUP_EVENT = new EventType(new MetaData("ANIMATION_MOUSE_POPUP_EVENT",
    // "ANIMATION_MOUSE_POPUP_EVENT",
    // new ObjectDescriptor("worldCoordinate", "x and y position in world coordinates", Point2d.class),
    // new ObjectDescriptor("screenCoordinate", "x and y position in screen coordinates", java.awt.Point.class),
    // new ObjectDescriptor("shiftCtrlAlt", "shift[0], ctrl[1], and/or alt[2] pressed", boolean[].class),
    // new ObjectDescriptor("object", "Selected object whose bounding box includes the coordinate", Object.class)));

    /**
     * What to do if the left mouse button was released after a drag.
     * @param mouseClickedPoint Point2D; the point where the mouse was clicked
     * @param mouseReleasedPoint Point2D; the point where the mouse was released
     */
    protected void pan(final Point2D mouseClickedPoint, final Point2D mouseReleasedPoint)
    {
        // Drag extend to new location
        double dx = mouseReleasedPoint.getX() - mouseClickedPoint.getX();
        double dy = mouseReleasedPoint.getY() - mouseClickedPoint.getY();
        double scaleX = getRenderableScale().getXScale(getExtent(), getSize());
        double scaleY = getRenderableScale().getYScale(getExtent(), getSize());
        Bounds2d extent = getExtent();
        setExtent(new Bounds2d(extent.getMinX() - dx * scaleX, extent.getMinX() - dx * scaleX + extent.getDeltaX(),
                extent.getMinY() + dy * scaleY, extent.getMinY() + dy * scaleY + extent.getDeltaY()));
    }

    /**
     * returns the list of selected objects at a certain mousePoint.
     * @param mousePoint Point2D; the mousePoint
     * @return the selected objects
     */
    protected List<Locatable> getSelectedObjects(final Point2D mousePoint)
    {
        List<Locatable> targets = new ArrayList<Locatable>();
        try
        {
            Point2d point = getRenderableScale().getWorldCoordinates(mousePoint, getExtent(), getSize());
            for (Renderable2DInterface<?> renderable : getElements())
            {
                if (isShowElement(renderable) && renderable.contains(point, getExtent()))
                {
                    targets.add(renderable.getSource());
                }
            }
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "getSelectedObjects");
        }
        return targets;
    }

    /**
     * popup on a mouseEvent.
     * @param e MouseEvent; the mouseEvent
     */
    protected void popup(final MouseEvent e)
    {
        List<Locatable> targets = this.getSelectedObjects(e.getPoint());
        if (targets.size() > 0)
        {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("Introspect");
            popupMenu.add(new JSeparator());
            for (Iterator<Locatable> i = targets.iterator(); i.hasNext();)
            {
                popupMenu.add(new IntrospectionAction(i.next()));
            }
            popupMenu.show(this, e.getX(), e.getY());
        }
    }

    /**
     * Returns the clicked Renderable2D with the highest z-value.
     * @param targets List&lt;Locatable&gt;; which are selected by the mouse.
     * @return the selected Object (e.g. the one with the highest zValue).
     */
    protected Object getSelectedObject(final List<Locatable> targets)
    {
        Object selectedObject = null;
        try
        {
            double zValue = -Double.MAX_VALUE;
            for (Locatable next : targets)
            {
                double z = next.getZ();
                if (z > zValue)
                {
                    zValue = z;
                    selectedObject = next;
                }
            }
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().warn(exception, "getSelectedObject");
        }
        return selectedObject;
    }

    /**
     * set the drag line: a line that shows where the user is dragging.
     * @param mousePosition Point2D; the position of the mouse pointer
     * @param mouseClicked Point2D; the position where the mouse was clicked before dragging
     */
    protected void setDragLine(final Point2D mousePosition, final Point2D mouseClicked)
    {
        if ((mousePosition != null) && (mouseClicked != null))
        {
            setDragLineEnabled(false); // to avoid problems with concurrency
            this.dragLine = new int[4];
            this.dragLine[0] = (int) mousePosition.getX();
            this.dragLine[1] = (int) mousePosition.getY();
            this.dragLine[2] = (int) mouseClicked.getX();
            this.dragLine[3] = (int) mouseClicked.getY();
            setDragLineEnabled(true);
        }
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

    /**
     * @return the set of animation elements.
     */
    public SortedSet<Renderable2DInterface<? extends Locatable>> getElements()
    {
        return this.elements;
    }

    /**
     * EventProducer to which to delegate the event producing methods.
     * <p>
     * Copyright (c) 2021-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    class AnimationEventProducer extends LocalEventProducer
    {
        /** */
        private static final long serialVersionUID = 20210213L;

        /** {@inheritDoc} */
        @Override
        public void fireEvent(final Event event)
        {
            super.fireEvent(event);
        }
    }

    /**
     * Return the delegate event producer.
     * @return AnimationEventProducer; the delegate event producer
     */
    public AnimationEventProducer getAnimationEventProducer()
    {
        return this.animationEventProducer;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType)
    {
        return this.animationEventProducer.addListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final ReferenceType referenceType)
    {
        return this.animationEventProducer.addListener(listener, eventType, referenceType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position)
    {
        return this.animationEventProducer.addListener(listener, eventType, position);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position,
            final ReferenceType referenceType)
    {
        return this.animationEventProducer.addListener(listener, eventType, position, referenceType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeListener(final EventListener listener, final EventType eventType)
    {
        return this.animationEventProducer.removeListener(listener, eventType);
    }

    /** {@inheritDoc} */
    @Override
    public int removeAllListeners()
    {
        return this.animationEventProducer.removeAllListeners();
    }

    /** {@inheritDoc} */
    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.animationEventProducer.getEventListenerMap();
    }

}
