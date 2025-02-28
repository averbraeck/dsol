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
     * does the shape contain the point?
     * @param pointWorldCoordinates Point2d; the point in world coordinates. Default implementation is to intersect the 3D
     *     bounds on location.z and to return the bounds2D of this intersect.
     * @param extent Bounds2d; the extent of the panel.
     * @return whether the point is in the shape
     * @throws IllegalStateException when the renderable does not overlap with the extent
     */
    boolean contains(Point2d pointWorldCoordinates, Bounds2d extent);

    /**
     * This method determines whether the shape drawn on the screen (rotated, zoomed, translated) contain the provided point.
     * @param pointScreenCoordinates Point2D; the point in screen coordinates.
     * @param extent Bounds2d; the extent of the panel on the screen.
     * @param screenSize Dimension; the dimensions of the screen
     * @param scale RenderableScale; the current zoom factor of the screen
     * @param margin double; the margin to apply 'around' the object
     * @param relativeMargin boolean; whether the margin should be scaled with the zoom factor (true) or not (false)
     * @return whether the point is in the shape or in a margin around the shape
     * @throws IllegalStateException when the renderable does not overlap with the extent
     */
    boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale scale, final double margin, final boolean relativeMargin);

    /**
     * destroys this editable. How to do this must be implemented by the modeler.
     * @param contextProvider Contextualized; the object that can provide the context to bind and unbind the animation objects
     * @throws RemoteException RemoteException
     * @throws NamingException NamingException
     */
    void destroy(Contextualized contextProvider) throws RemoteException, NamingException;

    /**
     * return the id of the renderable component.
     * @return long; the id of the renderable component
     */
    long getId();
}
