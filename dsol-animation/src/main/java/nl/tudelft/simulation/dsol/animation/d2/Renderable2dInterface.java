package nl.tudelft.simulation.dsol.animation.d2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * The Renderable2d interface defines the basic interface for 2d animation. This is a hard-to-use interface. It is implemented
 * by the easy-to-use Renderable2d class.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <L> the Locatable class of the source that indicates the location of the Renderable on the screen
 */
public interface Renderable2dInterface<L extends Locatable> extends Serializable
{
    /**
     * the overarching method that is called when painting, usually redirecting to paint(...) but possibly retrieving the
     * drawing from a cache.
     * @param graphics Graphics2D; the graphics object
     * @param extent Bounds2d; the extent of the panel
     * @param screenSize Dimension; the screen of the panel
     * @param renderableScale RenderableScale; the scale to use (usually RenderableScaleDefault where X/Y ratio is 1)
     * @param observer ImageObserver; the observer of the renderableInterface
     */
    void paintComponent(Graphics2D graphics, Bounds2d extent, Dimension screenSize, RenderableScale renderableScale,
            ImageObserver observer);

    /**
     * gets the source of this renderable.
     * @return Locatable the source
     */
    L getSource();

    /**
     * This method determines whether the shape drawn on the screen (rotated, zoomed, translated) contain the point that is
     * provided in world coordinates. The default implementation is to project the bounds of the object on the XY-plane, and
     * return the intersection with the given point (x,y) in world coordinates. Note that the bounds of the object are given in
     * world coordinates, and are relative to point (0,0,0).
     * @param pointWorldCoordinates Point2d; the point in world coordinates.
     * @param extent Bounds2d; the extent of the viewport that is visible on the screen, in world coordinates
     * @return whether the point is in the shape
     */
    boolean contains(Point2d pointWorldCoordinates, Bounds2d extent);

    /**
     * This method determines whether the shape drawn on the screen (rotated, zoomed, translated) contain the provided point
     * that is provided in screen coordinates (e.g., the position of a mouse click on the screen).
     * @param pointScreenCoordinates Point2D; the point in screen coordinates, e.g. from a mouse event; note that the point is
     *     given in AWT/Swing coordinates, so as a `Point2D` and not a `Point2d`
     * @param extent Bounds2d; the extent of the viewport that is visible on the screen, in world coordinates
     * @param screenSize Dimension; the dimensions of the screen, in screen coordinates
     * @param scale RenderableScale; the current zoom factor of the screen, as well as the y-scale ratio
     * @param worldMargin double; the margin to apply 'around' the object, in screen coordinates at a zoom level of 1, which is
     *     the same as world coordinates. This allows for a smaller or larger 'click box', since it is allowed for margin to be
     *     negative. This margin grows and shrinks in absolute sense withe the zoom factor.
     * @param pixelMargin double; the number of pixels around the drawn object for contains to be 'true'. This guarantees that a
     *     mouse click can be pointed to a very small object. 
     * @return whether the point is in the shape or in a margin around the shape
     */
    boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale scale, final double worldMargin, final double pixelMargin);

    /**
     * destroys this editable. How to do this must be implemented by the modeler.
     * @param contextProvider Contextualized; the object that can provide the context to bind and unbind the animation objects
     * @throws RemoteException RemoteException when the (remote) Context cannot be accessed
     * @throws NamingException when there is a problem retrieving the animation object from the Context
     */
    void destroy(Contextualized contextProvider) throws RemoteException, NamingException;

    /**
     * return the id of the renderable component.
     * @return long; the id of the renderable component
     */
    long getId();
}
