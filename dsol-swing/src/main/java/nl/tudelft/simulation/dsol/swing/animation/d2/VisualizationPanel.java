package nl.tudelft.simulation.dsol.swing.animation.d2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.rmi.RemoteException;
import java.text.NumberFormat;
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

import javax.naming.NamingException;
import javax.swing.JPanel;
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
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dComparator;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.d2.actions.IntrospectionAction;
import nl.tudelft.simulation.naming.context.ContextInterface;

/**
 * The VisualizationPanel implements the basic functions to visualize Locatable objects on the screen. It also allows for
 * toggling a grid on/off, zooming, panning, translation between world coordinates and screen coordinates, and changing the
 * displayed extent such as the home extent.
 * <p>
 * The screen has the possibility to witch layers on and off. <br>
 * <br>
 * <b>Asynchronous and synchronous calls:</b><br>
 * The internal functions of the AnimationPanel are handled in a synchronous way inside the animation panel, possibly through
 * (mouse or keyboard) listeners and handlers that implement the functions.There are several exceptions, though:
 * </p>
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
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author Niels Lang
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class VisualizationPanel extends JPanel implements EventProducer, EventListener
{
    /** */
    private static final long serialVersionUID = 20230305L;

    /** the UP directions for moving/zooming. */
    public static final int UP = 1;

    /** the DOWN directions for moving/zooming. */
    public static final int DOWN = 2;

    /** the LEFT directions for moving/zooming. */
    public static final int LEFT = 3;

    /** the RIGHT directions for moving/zooming. */
    public static final int RIGHT = 4;

    /** the ZOOM factor. */
    public static final double ZOOMFACTOR = 1.2;

    /** the extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private Bounds2d extent = null;

    /** the initial and default extent of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private Bounds2d homeExtent = null;

    /** show the grid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showGrid = true;

    /** the gridSize for the X-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeX = 100.0;

    /** the gridSize for the Y-direction in world Units. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected double gridSizeY = 100.0;

    /** gridColor. */
    private Color gridColor = Color.BLACK;

    /** the formatter to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected NumberFormat formatter = NumberFormat.getInstance();

    /** the last computed Dimension. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension lastDimension = null;

    /** the last known world coordinate of the mouse. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Point2d worldCoordinate = new Point2d(0.0, 0.0);

    /** whether to show a tooltip with the coordinates or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean showToolTip = true;

    /** the renderable scale (X/Y ratio) to use. */
    private RenderableScale renderableScale;

    /** the elements of this panel. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected SortedSet<Renderable2dInterface<? extends Locatable>> elements =
            new TreeSet<Renderable2dInterface<? extends Locatable>>(new Renderable2dComparator());

    /** filter for types to be shown or not. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<Class<? extends Locatable>, Boolean> visibilityMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** cache of the classes that are hidden. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Class<? extends Locatable>> hiddenClasses = new LinkedHashSet<>();

    /** cache of the classes that are shown. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Class<? extends Locatable>> shownClasses = new LinkedHashSet<>();

    /** the context with the path /experiment/replication/animation/2D. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected ContextInterface context = null;

    /** a line that helps the user to see where she/he is dragging. */
    private int[] dragLine = new int[4];

    /** enable drag line. */
    private boolean dragLineEnabled = false;

    /** List of drawable objects. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected List<Renderable2dInterface<? extends Locatable>> elementList = new ArrayList<>();

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
     * constructs a new VisualizationPanel.
     * @param homeExtent Bounds2d; the initial extent.
     * @param producer EventProducer; the object firing animation update events
     * @throws RemoteException on error when remote panel and producer cannot connect
     */
    public VisualizationPanel(final Bounds2d homeExtent, final EventProducer producer) throws RemoteException
    {
        setPreferredSize(new Dimension(1024, 768));
        this.animationEventProducer = new AnimationEventProducer();
        this.showGrid = true;
        addListeners();
        this.renderableScale = new RenderableScale();
        this.homeExtent = homeExtent;
        this.setBackground(Color.WHITE);
        this.lastDimension = this.getSize();
        setExtent(homeExtent);
        producer.addListener(this, AnimatorInterface.UPDATE_ANIMATION_EVENT);
    }

    /**
     * constructs a new VisualizationPanel with a context, so it can start drawing right away.
     * @param homeExtent Bounds2d; the initial extent.
     * @param producer EventProducer; the object firing animation update events
     * @param context ContextInterface; the context that contains the drawing objects
     * @throws RemoteException on error when remote panel and producer cannot connect
     * @throws NamingException on context error
     */
    public VisualizationPanel(final Bounds2d homeExtent, final EventProducer producer, final ContextInterface context)
            throws RemoteException, NamingException
    {
        this(homeExtent, producer);
        this.context = context.createSubcontext("animation/2D");
        subscribeToContext();
    }

    /**
     * Method to add listeners in the constructor. Can be overridden in a subclass in case another type of input listener is
     * needed to handle mouse and keyboard events.
     */
    public void addListeners()
    {
        InputListener listener = new InputListener(this);
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
        this.addMouseWheelListener(listener);
        this.addKeyListener(listener);
    }

    @Override
    public void paintComponent(final Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);

        // draw the grid.
        if (!this.getSize().equals(this.lastDimension))
        {
            this.lastDimension = this.getSize();
            setExtent(this.renderableScale.computeVisibleExtent(this.extent, this.getSize()));
        }
        if (this.showGrid)
        {
            this.drawGrid(g);
        }

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
        for (Renderable2dInterface<? extends Locatable> element : this.elementList)
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
     * @param element Renderable2dInterface&lt;? extends Locatable&gt;; the renderable element to test
     * @return whether the element needs to be shown or not
     */
    public boolean isShowElement(final Renderable2dInterface<? extends Locatable> element)
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

    /**
     * Subscribe to the context to receive object added and object removed events.
     * @throws RemoteException on network error
     */
    @SuppressWarnings("unchecked")
    protected void subscribeToContext() throws RemoteException
    {
        this.context.addListener(this, ContextInterface.OBJECT_ADDED_EVENT);
        this.context.addListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
        for (Object element : this.context.values())
        {
            if (element instanceof Renderable2dInterface)
            {
                objectAdded((Renderable2dInterface<? extends Locatable>) element);
            }
            else
            {
                System.err.println("odd object in context: " + element);
            }
        }
        this.repaint();
    }

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
            objectAdded((Renderable2dInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }

        else if (event.getType().equals(ContextInterface.OBJECT_REMOVED_EVENT))
        {
            objectRemoved((Renderable2dInterface<? extends Locatable>) ((Object[]) event.getContent())[2]);
        }
    }

    /**
     * returns the extent of this panel.
     * @return Bounds2d
     */
    public Bounds2d getExtent()
    {
        return this.extent;
    }

    /**
     * set a new extent for this panel.
     * @param extent Bounds2d; set a new extent
     */
    public void setExtent(final Bounds2d extent)
    {
        this.extent = extent;
        this.repaint();
    }

    /**
     * show the grid?
     * @param bool boolean; true/false
     */
    public synchronized void showGrid(final boolean bool)
    {
        this.showGrid = bool;
        this.repaint();
    }

    /**
     * Set the world coordinates based on a mouse move.
     * @param point Point2D; the x,y world coordinates
     */
    public synchronized void setWorldCoordinate(final Point2d point)
    {
        this.worldCoordinate = point;
    }

    /**
     * @return worldCoordinate
     */
    public synchronized Point2d getWorldCoordinate()
    {
        return this.worldCoordinate;
    }

    /**
     * Display a tooltip with the last known world coordinates of the mouse, in case the tooltip should be displayed.
     */
    public synchronized void displayWorldCoordinateToolTip()
    {
        if (this.showToolTip)
        {
            String worldPoint = "(x=" + this.formatter.format(this.worldCoordinate.getX()) + " ; y="
                    + this.formatter.format(this.worldCoordinate.getY()) + ")";
            setToolTipText(worldPoint);
        }
    }

    /**
     * @return showToolTip
     */
    public synchronized boolean isShowToolTip()
    {
        return this.showToolTip;
    }

    /**
     * @param showToolTip boolean; set showToolTip
     */
    public synchronized void setShowToolTip(final boolean showToolTip)
    {
        this.showToolTip = showToolTip;
    }

    /**
     * pans the panel in a specified direction.
     * @param direction int; the direction
     * @param percentage double; the percentage
     */
    public synchronized void pan(final int direction, final double percentage)
    {
        if (percentage <= 0 || percentage > 1.0)
        {
            throw new IllegalArgumentException("percentage<=0 || >1.0");
        }
        switch (direction)
        {
            case LEFT:
                setExtent(new Bounds2d(this.extent.getMinX() - percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() - percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY()));
                break;
            case RIGHT:
                setExtent(new Bounds2d(this.extent.getMinX() + percentage * this.extent.getDeltaX(),
                        this.extent.getMaxX() + percentage * this.extent.getDeltaX(), this.extent.getMinY(),
                        this.extent.getMaxY()));
                break;
            case UP:
                setExtent(new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() + percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() + percentage * this.extent.getDeltaY()));
                break;
            case DOWN:
                setExtent(new Bounds2d(this.extent.getMinX(), this.extent.getMaxX(),
                        this.extent.getMinY() - percentage * this.extent.getDeltaY(),
                        this.extent.getMaxY() - percentage * this.extent.getDeltaY()));
                break;
            default:
                throw new IllegalArgumentException("direction unkown");
        }
    }

    /**
     * resets the panel to its original extent.
     */
    public synchronized void home()
    {
        setExtent(this.renderableScale.computeVisibleExtent(this.homeExtent, this.getSize()));
    }

    /**
     * @return Returns the showGrid.
     */
    public boolean isShowGrid()
    {
        return this.showGrid;
    }

    /**
     * @param showGrid boolean; The showGrid to set.
     */
    public void setShowGrid(final boolean showGrid)
    {
        this.showGrid = showGrid;
    }

    /**
     * Return the current grid color for this VisualizationPanel.
     * @return the current grid color
     */
    public Color getGridColor()
    {
        return this.gridColor;
    }

    /**
     * Set a new grid color for this VisualizationPanel
     * @param gridColor the new grid color
     */
    public void setGridColor(final Color gridColor)
    {
        this.gridColor = gridColor;
    }

    /**
     * zooms in/out.
     * @param factor double; The zoom factor
     */
    public synchronized void zoom(final double factor)
    {
        zoom(factor, (int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
    }

    /**
     * zooms in/out.
     * @param factor double; The zoom factor
     * @param mouseX int; x-position of the mouse around which we zoom
     * @param mouseY int; y-position of the mouse around which we zoom
     */
    public synchronized void zoom(final double factor, final int mouseX, final int mouseY)
    {
        Point2d mwc = this.renderableScale.getWorldCoordinates(new Point2D.Double(mouseX, mouseY), this.extent, this.getSize());
        double minX = mwc.getX() - (mwc.getX() - this.extent.getMinX()) * factor;
        double minY = mwc.getY() - (mwc.getY() - this.extent.getMinY()) * factor;
        double w = this.extent.getDeltaX() * factor;
        double h = this.extent.getDeltaY() * factor;

        setExtent(new Bounds2d(minX, minX + w, minY, minY + h));
    }

    /**
     * Added to make sure the recursive render-call calls THIS render method instead of a potential super-class defined
     * 'paintComponent' render method.
     * @param g Graphics; the graphics object
     */
    protected synchronized void drawGrid(final Graphics g)
    {
        // we prepare the graphics object for the grid
        g.setFont(g.getFont().deriveFont(11.0f));
        g.setColor(this.gridColor);
        double scaleX = this.renderableScale.getXScale(this.extent, this.getSize());
        double scaleY = this.renderableScale.getYScale(this.extent, this.getSize());

        int count = 0;
        int gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
        while (gridSizePixelsX < 40)
        {
            this.gridSizeX = 10 * this.gridSizeX;
            int maximumNumberOfDigits = (int) Math.max(0, 1 + Math.ceil(Math.log(1 / this.gridSizeX) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
            if (count++ > 10)
            {
                break;
            }
        }

        count = 0;
        while (gridSizePixelsX > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeX) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeX = this.gridSizeX / 10;
            gridSizePixelsX = (int) Math.round(this.gridSizeX / scaleX);
            if (count++ > 10)
            {
                break;
            }
        }

        int gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
        while (gridSizePixelsY < 40)
        {
            this.gridSizeY = 10 * this.gridSizeY;
            int maximumNumberOfDigits = (int) Math.max(0, 1 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            {
                break;
            }
        }

        count = 0;
        while (gridSizePixelsY > 10 * 40)
        {
            int maximumNumberOfDigits = (int) Math.max(0, 2 + Math.ceil(Math.log(1 / this.gridSizeY) / Math.log(10)));
            this.formatter.setMaximumFractionDigits(maximumNumberOfDigits);
            this.gridSizeY = this.gridSizeY / 10;
            gridSizePixelsY = (int) Math.round(this.gridSizeY / scaleY);
            if (count++ > 10)
            {
                break;
            }
        }

        // Let's draw the vertical lines
        double mod = this.extent.getMinX() % this.gridSizeX;
        int x = (int) -Math.round(mod / scaleX);
        while (x < this.getWidth())
        {
            Point2d point = this.renderableScale.getWorldCoordinates(new Point2D.Double(x, 0), this.extent, this.getSize());
            if (point != null)
            {
                String label = this.formatter.format(Math.round(point.getX() / this.gridSizeX) * this.gridSizeX);
                double labelWidth = this.getFontMetrics(this.getFont()).getStringBounds(label, g).getWidth();
                if (x > labelWidth + 4)
                {
                    g.drawLine(x, 15, x, this.getHeight());
                    g.drawString(label, (int) Math.round(x - 0.5 * labelWidth), 11);
                }
            }
            x = x + gridSizePixelsX;
        }

        // Let's draw the horizontal lines
        mod = Math.abs(this.extent.getMinY()) % this.gridSizeY;
        int y = (int) Math.round(this.getSize().getHeight() - (mod / scaleY));
        while (y > 15)
        {
            Point2d point = this.renderableScale.getWorldCoordinates(new Point2D.Double(0, y), this.extent, this.getSize());
            if (point != null)
            {
                String label = this.formatter.format(Math.round(point.getY() / this.gridSizeY) * this.gridSizeY);
                RectangularShape labelBounds = this.getFontMetrics(this.getFont()).getStringBounds(label, g);
                g.drawLine((int) Math.round(labelBounds.getWidth() + 4), y, this.getWidth(), y);
                g.drawString(label, 2, (int) Math.round(y + labelBounds.getHeight() * 0.3));
            }
            y = y - gridSizePixelsY;
        }
    }

    /**
     * @return renderableScale
     */
    public RenderableScale getRenderableScale()
    {
        return this.renderableScale;
    }

    /**
     * @param renderableScale set renderableScale
     */
    public void setRenderableScale(final RenderableScale renderableScale)
    {
        this.renderableScale = renderableScale;
    }

    /**
     * Add a locatable object to the animation.
     * @param element Renderable2dInterface&lt;? extends Locatable&gt;; the element to add to the animation
     */
    public void objectAdded(final Renderable2dInterface<? extends Locatable> element)
    {
        synchronized (this.elementList)
        {
            this.elements.add(element);
            this.dirty = true;
        }
    }

    /**
     * Remove a locatable object from the animation.
     * @param element Renderable2dInterface&lt;? extends Locatable&gt;; the element to add to the animation
     */
    public void objectRemoved(final Renderable2dInterface<? extends Locatable> element)
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
            for (Renderable2dInterface<? extends Locatable> renderable : this.elementList)
            {
                if (renderable.getSource() == null)
                {
                    continue;
                }
                Point<?> l = renderable.getSource().getLocation();
                if (l != null)
                {
                    Bounds<?, ?> b = renderable.getSource().getBounds();
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
     *     vice versa.
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
            for (Renderable2dInterface<?> renderable : getElements())
            {
                // if (isShowElement(renderable) && renderable.contains(point, getExtent()))
                if (isShowElement(renderable)
                        && renderable.contains(mousePoint, this.extent, getSize(), getRenderableScale(), 1.0, false))
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
     * Returns the clicked Renderable2d with the highest z-value.
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
    public SortedSet<Renderable2dInterface<? extends Locatable>> getElements()
    {
        return this.elements;
    }

    /**
     * EventProducer to which to delegate the event producing methods.
     * <p>
     * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
     * DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
     * </p>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    class AnimationEventProducer extends LocalEventProducer
    {
        /** */
        private static final long serialVersionUID = 20210213L;

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

    @Override
    public boolean addListener(final EventListener listener, final EventType eventType)
    {
        return this.animationEventProducer.addListener(listener, eventType);
    }

    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final ReferenceType referenceType)
    {
        return this.animationEventProducer.addListener(listener, eventType, referenceType);
    }

    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position)
    {
        return this.animationEventProducer.addListener(listener, eventType, position);
    }

    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position,
            final ReferenceType referenceType)
    {
        return this.animationEventProducer.addListener(listener, eventType, position, referenceType);
    }

    @Override
    public boolean removeListener(final EventListener listener, final EventType eventType)
    {
        return this.animationEventProducer.removeListener(listener, eventType);
    }

    @Override
    public int removeAllListeners()
    {
        return this.animationEventProducer.removeAllListeners();
    }

    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.animationEventProducer.getEventListenerMap();
    }

}
