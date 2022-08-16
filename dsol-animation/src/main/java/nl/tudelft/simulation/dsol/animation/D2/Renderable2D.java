package nl.tudelft.simulation.dsol.animation.D2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.language.d2.Shape2d;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The Renderable2D provides an easy accessible renderable object that can be drawn on an absolute or relative position, scaled,
 * flipped, and rotated. For scaling, several options exist:<br>
 * - scale: whether to scale the drawing at all; e.g. for a legend, absolute coordinates might be used (scale = false);<br>
 * - scaleY: whether to scale differently in X and Y direction, e.g. for a map at higher latitudes (scaleY = true);<br>
 * - scaleObject: whether to scale the drawing larger or smaller than the scale factor of the extent (e.g., to draw an object on
 * a map where the units of the object are in meters, while the map is in lat / lon degrees).<br>
 * The default values are: translate = true; scale = true; flip = false; rotate = true; scaleY = false; scaleObject = false.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <L> the Locatable class of the source that can return the location of the Renderable on the screen
 */
public abstract class Renderable2D<L extends Locatable> implements Renderable2DInterface<L>
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /**
     * Storage of the boolean flags, to prevent each flag from taking 32 bits... The initial value is binary 1011 = 0B: rotate =
     * true, flip = false, scale = true, translate = true, scaleY = false; scaleObject = false.
     */
    private byte flags = 0x0B;

    /** whether to scale the X/Y-value with the value of RenderableScale.OnjectScaleFactor. Flag is 00100000 */
    private static final byte SCALE_OBJECT_FLAG = 0x20;

    /** whether to scale the Y-value in case of a compressed Y-axis. Flag is 00010000 */
    private static final byte SCALE_Y_FLAG = 0x10;

    /** whether to rotate the renderable. Flag is 1000 */
    private static final byte ROTATE_FLAG = 0x08;

    /** whether to flip the renderable after rotating 180 degrees. Flag is 0100 */
    private static final byte FLIP_FLAG = 0x04;

    /** whether to scale the renderable when zooming in or out. Flag is 0010 */
    private static final byte SCALE_FLAG = 0x02;

    /** whether to translate the renderable when panning. Flag is 0001 */
    private static final byte TRANSLATE_FLAG = 0x01;

    /** the source of the renderable. */
    private L source;

    /** the object number counter for a unique id. */
    private static AtomicInteger animationObjectCounter = new AtomicInteger(0);

    /** the unique id of this animation object. */
    private int id;

    /**
     * Constructs a new Renderable2D.
     * @param source T; the source
     * @param contextProvider Contextualized; the object that can provide the context to store the animation objects
     */
    public Renderable2D(final L source, final Contextualized contextProvider)
    {
        this.source = source;
        this.bind2Context(contextProvider);
    }

    /**
     * Bind a renderable2D to the context. The reason for specifying this in an independent method instead of adding the code in
     * the constructor is related to the RFE submitted by van Houten that in specific distributed context, such binding must be
     * overwritten.
     * @param contextProvider Contextualized; the object that can provide the context to store the animation objects
     */
    public void bind2Context(final Contextualized contextProvider)
    {
        try
        {
            this.id = animationObjectCounter.incrementAndGet();
            ContextUtil.lookupOrCreateSubContext(contextProvider.getContext(), "animation/2D")
                    .bind(Integer.toString(this.id), this);
        }
        catch (NamingException | RemoteException exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }

    /**
     * Return whether to flip the renderable, if the direction is 'left' or not.
     * @return boolean; whether to flip the renderable, if the direction is 'left' or not
     */
    public boolean isFlip()
    {
        return (this.flags & FLIP_FLAG) != 0;
    }

    /**
     * Set whether to flip the renderable, if the direction is 'left' or not.
     * @param flip boolean; whether to flip the renderable, if the direction is 'left' or not
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setFlip(final boolean flip)
    {
        if (flip)
            this.flags |= FLIP_FLAG;
        else
            this.flags &= (~FLIP_FLAG);
    }

    /**
     * Return whether to rotate the renderable or not.
     * @return boolean; whether to rotate the renderable or not
     */
    public boolean isRotate()
    {
        return (this.flags & ROTATE_FLAG) != 0;
    }

    /**
     * Set whether to rotate the renderable or not.
     * @param rotate boolean; whether to rotate the renderable or not
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setRotate(final boolean rotate)
    {
        if (rotate)
            this.flags |= ROTATE_FLAG;
        else
            this.flags &= (~ROTATE_FLAG);
    }

    /**
     * Return whether to scale the renderable or not.
     * @return boolean; whether to scale the renderable or not
     */
    public boolean isScale()
    {
        return (this.flags & SCALE_FLAG) != 0;
    }

    /**
     * Set whether to scale the renderable or not.
     * @param scale boolean; whether to scale the renderable or not
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setScale(final boolean scale)
    {
        if (scale)
            this.flags |= SCALE_FLAG;
        else
            this.flags &= (~SCALE_FLAG);
    }

    /**
     * Return whether to scale the renderable in the Y-direction when there is a compressed Y-axis or not.
     * @return boolean; whether to scale the renderable in the Y-direction when there is a compressed Y-axis or not
     */
    public boolean isScaleY()
    {
        return (this.flags & SCALE_Y_FLAG) != 0;
    }

    /**
     * Set whether to scale the renderable in the X/Y-direction with the value of RenderableScale.objectScaleFactor or not.
     * @param scaleY boolean; whether to scale the renderable in the X/Y-direction with the value of
     *            RenderableScale.objectScaleFactor or not
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setScaleObject(final boolean scaleY)
    {
        if (scaleY)
            this.flags |= SCALE_OBJECT_FLAG;
        else
            this.flags &= (~SCALE_OBJECT_FLAG);
    }

    /**
     * Return whether to scale the renderable in the X/Y-direction with the value of RenderableScale.objectScaleFactor or not.
     * @return boolean; whether to scale the renderable in the X/Y-direction with the value of RenderableScale.objectScaleFactor
     *         or not
     */
    public boolean isScaleObject()
    {
        return (this.flags & SCALE_OBJECT_FLAG) != 0;
    }

    /**
     * Set whether to scale the renderable in the Y-direction when there is a compressed Y-axis or not.
     * @param scaleY boolean; whether to scale the renderable in the Y-direction when there is a compressed Y-axis or not
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setScaleY(final boolean scaleY)
    {
        if (scaleY)
            this.flags |= SCALE_Y_FLAG;
        else
            this.flags &= (~SCALE_Y_FLAG);
    }

    /**
     * Return whether to translate the renderable to its position or not (false means absolute position).
     * @return boolean; whether to translate the renderable to its position or not (false means absolute position)
     */
    public boolean isTranslate()
    {
        return (this.flags & TRANSLATE_FLAG) != 0;
    }

    /**
     * Set whether to translate the renderable to its position or not (false means absolute position).
     * @param translate boolean; whether to translate the renderable to its position or not (false means absolute position)
     */
    @SuppressWarnings("checkstyle:needbraces")
    public void setTranslate(final boolean translate)
    {
        if (translate)
            this.flags |= TRANSLATE_FLAG;
        else
            this.flags &= (~TRANSLATE_FLAG);
    }

    /** {@inheritDoc} */
    @Override
    public L getSource()
    {
        return this.source;
    }

    /** {@inheritDoc} */
    @Override
    public void paintComponent(final Graphics2D graphics, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale renderableScale, final ImageObserver observer)
    {
        // by default: delegate to the paint() method.
        paint(graphics, extent, screenSize, renderableScale, observer);
    }

    /**
     * The methods that actually paints the object at the right scale, rotation, and position on the screen using the
     * user-implemented <code>paint(graphics, observer)</code> method to do the actual work.
     * @param graphics Graphics2D; the graphics object
     * @param extent Bounds2d; the extent of the panel
     * @param screenSize Dimension; the screen of the panel
     * @param renderableScale RenderableScale; the scale to use (usually RenderableScaleDefault where X/Y ratio is 1)
     * @param observer ImageObserver; the observer of the renderableInterface
     */
    protected synchronized void paint(final Graphics2D graphics, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale renderableScale, final ImageObserver observer)
    {
        if (this.source == null)
        {
            return;
        }
        // save the transform -- clone because transform is a volatile object
        AffineTransform transform = (AffineTransform) graphics.getTransform().clone();
        try
        {
            Point<?> center = this.source.getLocation();
            if (center == null)
            {
                return;
            }

            Bounds2d rectangle = BoundsUtil.projectBounds(center, this.source.getBounds());
            if (rectangle == null || (!Shape2d.overlaps(extent, rectangle) && isTranslate()))
            {
                return;
            }

            // Let's transform
            if (isTranslate())
            {
                Point2D screenCoordinates = renderableScale.getScreenCoordinates(center, extent, screenSize);
                graphics.translate(screenCoordinates.getX(), screenCoordinates.getY());
            }
            if (isScale())
            {
                double objectScaleFactor = isScaleObject() ? renderableScale.getObjectScaleFactor() : 1.0;
                if (isScaleY())
                {
                    graphics.scale(objectScaleFactor / renderableScale.getXScale(extent, screenSize),
                            objectScaleFactor / renderableScale.getYScale(extent, screenSize));
                }
                else
                {
                    graphics.scale(objectScaleFactor / renderableScale.getXScale(extent, screenSize), objectScaleFactor
                            * renderableScale.getYScaleRatio() / renderableScale.getYScale(extent, screenSize));
                }
            }
            double angle = -this.source.getDirZ();
            if (angle != 0.0)
            {
                if (isFlip() && angle < -Math.PI)
                {
                    angle = angle + Math.PI;
                }
                if (isRotate())
                {
                    graphics.rotate(angle);
                }
            }

            // Now we paint
            this.paint(graphics, observer);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "paint");
        }
        finally
        {
            // Let's untransform
            graphics.setTransform(transform);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(final Point2d pointWorldCoordinates, final Bounds2d extent)
    {
        try
        {
            if (pointWorldCoordinates == null || this.source == null || this.source.getLocation() == null)
            {
                return false;
            }
            Bounds2d intersect = BoundsUtil.projectBounds(this.source.getLocation(), this.source.getBounds());
            return intersect.contains(pointWorldCoordinates);
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().warn(exception, "contains");
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void destroy(final Contextualized contextProvider)
    {
        try
        {
            ContextUtil.lookupOrCreateSubContext(contextProvider.getContext(), "animation/2D")
                    .unbind(Integer.toString(this.id));
            this.source = null; // to indicate the animation is destroyed. Remove pointer to source for GC.
        }
        catch (NamingException | RemoteException exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getId()
    {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Renderable2D [source=" + this.source + "]";
    }

    /**
     * Draws an animation on a world coordinate around [x,y] = [0,0].
     * @param graphics Graphics2D; the graphics object
     * @param observer ImageObserver; the observer
     */
    public abstract void paint(Graphics2D graphics, ImageObserver observer);

}
