package nl.tudelft.simulation.dsol.animation.D2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * The Renderable2D interface defines the basic interface for 2d animation. This is a hard-to-use interface. It is implemented
 * by the easy-to-use Renderable2D class.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <L> the Locatable class of the source that indicates the location of the Renderable on the screen
 */
public interface Renderable2DInterface<L extends Locatable> extends Serializable
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
     *            bounds on location.z and to return the bounds2D of this intersect.
     * @param extent Bounds2d; the extent of the panel.
     * @return whether the point is in the shape
     * @throws IllegalStateException when the renderable does not overlap with the extent
     */
    boolean contains(Point2d pointWorldCoordinates, Bounds2d extent);

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
