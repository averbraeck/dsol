package nl.tudelft.simulation.dsol.animation.gis.esri;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point2d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2d;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * This renderable draws CAD/GIS objects.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class EsriRenderable2d implements GisRenderable2d
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** the map to display. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected GisMapInterface map = null;

    /** the image cached image. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected BufferedImage cachedImage = null;

    /** the cached extent. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Bounds2d cachedExtent = new Bounds2d(0, 0, 0, 0);

    /** the cached screenSize. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Dimension cachedScreenSize = new Dimension();

    /** the location of the map. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected OrientedPoint3d location = null;

    /** the bounds of the map. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Bounds3d bounds = null;

    /**
     * constructs a new GisRenderable2d.
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param map the parsed map to use
     */
    public EsriRenderable2d(final Contextualized contextProvider, final GisMapInterface map)
    {
        this(contextProvider, map, new CoordinateTransform.NoTransform());
    }

    /**
     * constructs a new GisRenderable2d.
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param map the parsed map to use
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     */
    public EsriRenderable2d(final Contextualized contextProvider, final GisMapInterface map,
            final CoordinateTransform coordinateTransform)
    {
        this(contextProvider, map, coordinateTransform, -Double.MAX_VALUE);
    }

    /**
     * constructs a new GisRenderable2d based on a parsed Map.
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param map the parsed map to use
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates
     * @param z the z-value to use
     */
    public EsriRenderable2d(final Contextualized contextProvider, final GisMapInterface map,
            final CoordinateTransform coordinateTransform, final double z)
    {
        try
        {
            this.map = map;
            this.location = new OrientedPoint3d(this.cachedExtent.midPoint().getX(), this.cachedExtent.midPoint().getY(), z);
            this.bounds = new Bounds3d(this.cachedExtent.getDeltaX(), this.cachedExtent.getDeltaY(), 0.0);
            this.bind2Context(contextProvider);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "<init>");
        }
    }

    /**
     * binds a renderable2D to the context. The reason for specifying this in an independent method instead of adding the code
     * in the constructor is related to the RFE submitted by van Houten that in specific distributed context, such binding must
     * be overwritten.
     * @param contextProvider the object that can provide the context to store the animation objects
     */
    protected void bind2Context(final Contextualized contextProvider)
    {
        try
        {
            ContextUtil.lookupOrCreateSubContext(contextProvider.getContext(), "animation/2D")
                    .bindObject(Integer.toString(System.identityHashCode(this)), this);
        }
        catch (NamingException | RemoteException exception)
        {
            CategoryLogger.always().warn(exception, "<init>");
        }
    }

    @Override
    public void paintComponent(final Graphics2D graphics, final Bounds2d extent, final Dimension screen,
            final RenderableScale renderableScale, final ImageObserver observer)
    {
        try
        {
            // is the extent or the screen size still the same
            if (extent.equals(this.cachedExtent) && screen.equals(this.cachedScreenSize) && this.map.isSame())
            {
                graphics.drawImage(this.cachedImage, 0, 0, null);
                return;
            }
            this.map.setExtent(extent);
            this.map.getImage().setSize(screen);
            this.cacheImage();
            this.paintComponent(graphics, extent, screen, renderableScale, observer);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "paint");
        }
    }

    @Override
    public EsriRenderable2d getSource()
    {
        return this;
    }

    @Override
    public Bounds3d getRelativeBounds()
    {
        return this.bounds;
    }

    @Override
    public OrientedPoint3d getLocation()
    {
        return this.location;
    }

    /**
     * @return map the Shapefile map
     */
    @Override
    public GisMapInterface getMap()
    {
        return this.map;
    }

    /**
     * caches the GIS map by creating an image. This prevents continuous rendering.
     * @throws Exception on graphicsProblems and network connection failures.
     */
    private void cacheImage() throws Exception
    {
        this.cachedImage = new BufferedImage((int) this.map.getImage().getSize().getWidth(),
                (int) this.map.getImage().getSize().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D bg = this.cachedImage.createGraphics();
        this.map.drawMap(bg);
        bg.dispose();
        this.cachedScreenSize = (Dimension) this.map.getImage().getSize().clone();
        this.cachedExtent = this.map.getExtent();
        this.location = new OrientedPoint3d(this.cachedExtent.midPoint().getX(), this.cachedExtent.midPoint().getY(),
                -Double.MIN_VALUE);
        this.bounds = new Bounds3d(this.cachedExtent.getDeltaX(), this.cachedExtent.getDeltaY(), 0.0);
    }

    /**
     * destroys an RenderableObject by unsubscribing it from the context.
     */
    @Override
    public void destroy(final Contextualized contextProvider)
    {
        try
        {
            ContextUtil.lookupOrCreateSubContext(contextProvider.getContext(), "animation/2D")
                    .unbindObject(Integer.toString(System.identityHashCode(this)));
        }
        catch (Throwable throwable)
        {
            CategoryLogger.always().warn(throwable, "finalize");
        }
    }

    @Override
    public boolean contains(final Point2d pointWorldCoordinates, final Bounds2d extent)
    {
        return false;
    }

    @Override
    public boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale scale, final double worldMargin, final double pixelMargin)
    {
        return false;
    }

    @Override
    public long getId()
    {
        return -1; // drawn before the rest in case all z-values are the same
    }

}
