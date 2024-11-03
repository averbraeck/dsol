package nl.tudelft.simulation.dsol.swing.animation.d2;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.event.Event;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.SearchPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * Animation panel that adds autopan functionality. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The
 * DSOL project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class AutoPanAnimationPanel extends AnimationPanel
{
    /** */
    private static final long serialVersionUID = 20180430L;

    /** Last Object that was followed. */
    private Object lastFollowedObject;

    /** Id of object to auto pan to. */
    private String autoPanId = null;

    /** Type of object to auto pan to. */
    private SearchPanel.ObjectKind<?> autoPanKind = null;

    /** Track auto pan object continuously? */
    private boolean autoPanTrack = false;

    /** Track auto on the next paintComponent operation; then copy state from autoPanTrack. */
    private boolean autoPanOnNextPaintComponent = false;

    /**
     * Constructor for the AutoPanAnimationPanel.
     * @param homeExtent Bounds2d; home extent
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; simulator
     * @throws RemoteException on remote animation error
     * @throws DsolException when simulator does not implement AnimatorInterface
     */
    public AutoPanAnimationPanel(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DsolException
    {
        super(homeExtent, simulator);
        MouseListener[] listeners = getMouseListeners();
        for (MouseListener listener : listeners)
        {
            removeMouseListener(listener);
        }
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                if (e.isControlDown())
                {
                    Object searchedObject = getSelectedObject(e.getPoint());
                    if (searchedObject != null)
                    {
                        // TODO: should be done through notify of ANIMATION_MOUSE_CLICK_EVENT
                        // getSearchPanel().selectAndTrackObject("Object", searchedObject.toString(), true);
                        e.consume(); // sadly doesn't work to prevent a pop up
                    }
                }
                e.consume();
            }
        });
        for (MouseListener listener : listeners)
        {
            addMouseListener(listener);
        }
        // mouse wheel
        MouseWheelListener[] wheelListeners = getMouseWheelListeners();
        for (MouseWheelListener wheelListener : wheelListeners)
        {
            removeMouseWheelListener(wheelListener);
        }
        this.addMouseWheelListener(new InputListener(this)
        {
            @Override
            public void mouseWheelMoved(final MouseWheelEvent e)
            {
                super.mouseWheelMoved(e);
            }
        });
    }

    /**
     * returns the list of selected objects at a certain mousePoint.
     * @param mousePoint Point2D; the mousePoint
     * @return the selected objects
     */
    protected Object getSelectedObject(final Point2D mousePoint)
    {
        List<Object> targets = new ArrayList<>();
        Point2d point = getRenderableScale().getWorldCoordinates(mousePoint,
                getExtent(), getSize());
        for (Renderable2dInterface<?> renderable : getElements())
        {
            if (isShowElement(renderable)
                    && renderable.contains(point, getExtent()))
            {
                // TODO: build in a check for the right class.
                // if (renderable.getSource() instanceof Person)
                {
                    targets.add(renderable.getSource());
                }
            }
        }
        if (targets.size() == 1)
        {
            return targets.get(0);
        }
        return null;
    }

    /**
     * Change auto pan target.
     * @param newAutoPanId String; id of object to track (or
     * @param newAutoPanKind String; kind of object to track
     * @param newAutoPanTrack boolean; if true; tracking is continuously; if false; tracking is once
     */
    public void setAutoPan(final String newAutoPanId, final SearchPanel.ObjectKind<?> newAutoPanKind,
            final boolean newAutoPanTrack)
    {
        this.autoPanId = newAutoPanId;
        this.autoPanKind = newAutoPanKind;
        this.autoPanTrack = newAutoPanTrack;
        this.autoPanOnNextPaintComponent = true;
        // System.out.println("AutoPan id=" + newAutoPanId + ", kind=" + newAutoPanKind + ", track=" + newAutoPanTrack);
        if (null != this.autoPanId && this.autoPanId.length() > 0 && null != this.autoPanKind)
        {
            repaint();
        }
    }

    @Override
    public void paintComponent(final Graphics g)
    {
        if (this.autoPanTrack && this.autoPanKind != null && this.autoPanId != null)
        {
            Locatable locatable = this.autoPanKind.searchObject(this.autoPanId);
            if (null != locatable)
            {
                try
                {
                    Point<?> point = locatable.getLocation();
                    if (point != null) // Center extent around point
                    {
                        double w = getExtent().getDeltaX();
                        double h = getExtent().getDeltaY();
                        setExtent(new Bounds2d(point.getX() - w / 2, point.getX() + w / 2, point.getY() - h / 2,
                                point.getY() + h / 2));
                    }
                }
                catch (RemoteException exception)
                {
                    CategoryLogger.always().warn("Caught RemoteException trying to locate {} with id {}.",
                            this.autoPanKind, this.autoPanId);
                    return;
                }
            }
        }
        super.paintComponent(g);
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(SearchPanel.ANIMATION_SEARCH_OBJECT_EVENT))
        {
            Object[] content = (Object[]) event.getContent();
            this.autoPanKind = (SearchPanel.ObjectKind<?>) content[0];
            this.autoPanId = (String) content[1];
            this.autoPanTrack = (Boolean) content[2];
        }
        super.notify(event);
    }

    @Override
    public String toString()
    {
        return "AutoAnimationPanel [lastPerson=" + this.lastFollowedObject + "]";
    }
}
