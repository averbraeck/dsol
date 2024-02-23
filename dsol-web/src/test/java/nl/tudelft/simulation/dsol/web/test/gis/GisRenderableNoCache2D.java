package nl.tudelft.simulation.dsol.web.test.gis;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point2d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.esri.EsriFileXmlParser;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * This renderable draws CAD/GIS objects.
 * <p>
 * Copyright (c) 2002- 2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class GisRenderableNoCache2D implements Renderable2dInterface<GisRenderableNoCache2D>, Locatable
{
    /** the map to display. */
    protected GisMapInterface map = null;

    /** the location of the map. */
    protected OrientedPoint3d location = null;

    /** the bounds of the map. */
    protected Bounds3d bounds = null;

    /**
     * constructs a new GisRenderable2D.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator.
     * @param mapFile URL; the mapfile to use.
     */
    public GisRenderableNoCache2D(final SimulatorInterface<?> simulator, final URL mapFile)
    {
        this(simulator, mapFile, new CoordinateTransform.NoTransform());
    }

    /**
     * constructs a new GisRenderable2D.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator.
     * @param mapFile URL; the mapfile to use.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     */
    public GisRenderableNoCache2D(final SimulatorInterface<?> simulator, final URL mapFile,
            final CoordinateTransform coordinateTransform)
    {
        this(simulator, mapFile, coordinateTransform, -Double.MAX_VALUE);
    }

    /**
     * constructs a new GisRenderable2D.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator.
     * @param mapFile URL; the mapfile to use.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param z double; the z-value to use
     */
    public GisRenderableNoCache2D(final SimulatorInterface<?> simulator, final URL mapFile,
            final CoordinateTransform coordinateTransform, final double z)
    {
        if (!(simulator instanceof AnimatorInterface))
        {
            return;
        }
        try
        {
            this.map = EsriFileXmlParser.parseMapFile(mapFile, coordinateTransform);
            this.location =
                    new OrientedPoint3d(this.map.getExtent().midPoint().getX(), this.map.getExtent().midPoint().getY(), z);
            this.bounds = new Bounds3d(this.map.getExtent().getDeltaX(), this.map.getExtent().getDeltaY(), 0.0);
            // XXX simulator.getReplication().getTreatment().getProperties().put("animationPanel.extent",
            // XXX this.map.getExtent());
            this.bind2Context(simulator);
        }
        catch (Exception exception)
        {
            simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * constructs a new GisRenderable2D based on an existing Map.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator.
     * @param map GisMapInterface; the map to use.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param z double; the z-value to use
     */
    public GisRenderableNoCache2D(final SimulatorInterface<?> simulator, final GisMapInterface map,
            final CoordinateTransform coordinateTransform, final double z)
    {
        if (!(simulator instanceof AnimatorInterface))
        {
            return;
        }
        try
        {
            this.map = map;
            this.location =
                    new OrientedPoint3d(this.map.getExtent().midPoint().getX(), this.map.getExtent().midPoint().getY(), z);
            this.bounds = new Bounds3d(this.map.getExtent().getDeltaX(), this.map.getExtent().getDeltaY(), 100.0);
            // XXX simulator.getReplication().getTreatment().getProperties().put("animationPanel.extent",
            // XXX this.map.getExtent());
            this.bind2Context(simulator);
        }
        catch (Exception exception)
        {
            simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * binds a renderable2D to the context. The reason for specifying this in an independent method instead of adding the code
     * in the constructor is related to the RFE submitted by van Houten that in specific distributed context, such binding must
     * be overwritten.
     * @param simulator SimulatorInterface&lt;?,?,?&gt;; the simulator used for binding the object.
     */
    protected void bind2Context(final SimulatorInterface<?> simulator)
    {
        try
        {
            ContextUtil.lookupOrCreateSubContext(simulator.getReplication().getContext(), "animation/2D")
                    .bindObject(Integer.toString(System.identityHashCode(this)));
        }
        catch (NamingException | RemoteException exception)
        {
            simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void paintComponent(final Graphics2D graphics, final Bounds2d extent, final Dimension screen,
            final RenderableScale renderableScale, final ImageObserver observer)
    {
        try
        {
            this.map.setExtent(extent);
            this.map.drawMap(graphics);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "paint");
        }
    }

    /** {@inheritDoc} */
    @Override
    public GisRenderableNoCache2D getSource()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return this.bounds;
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation()
    {
        return this.location;
    }

    /**
     * @return map the Shapefile map
     */
    public GisMapInterface getMap()
    {
        return this.map;
    }

    /** {@inheritDoc} */
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

    /**
     * destroys an RenderableObject by unsubscribing it from the context.
     * @param simulator the simulator
     */
    public void destroy(final SimulatorInterface<?> simulator)
    {
        destroy(simulator.getReplication());
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(final Point2d pointWorldCoordinates, final Bounds2d extent)
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public long getId()
    {
        return -1; // in case of same z-values make sure map is drawn first
    }
}
