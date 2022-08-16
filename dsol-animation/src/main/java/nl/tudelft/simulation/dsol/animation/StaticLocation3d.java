package nl.tudelft.simulation.dsol.animation;

import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

/**
 * A StaticLocation in 3 dimensions.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class StaticLocation3d extends OrientedPoint3d implements Locatable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the bounds. */
    private Bounds3d bounds = null;

    /**
     * constructs a new StaticLocation.
     * @param x double; the x location
     * @param y double; the y location
     * @param z double; the z location
     * @param rotX double; rotation around x-axis (theta)
     * @param rotY double; rotation around y-axis (phi)
     * @param rotZ double; rotation around z-axis (rho)
     * @param bounds Bounds3d; the bounds
     */
    public StaticLocation3d(final double x, final double y, final double z, final double rotX, final double rotY,
            final double rotZ, final Bounds3d bounds)
    {
        super(x, y, z, rotX, rotY, rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param point2D Point2D; the point2d
     * @param rotZ double; the rotation in the xy plane
     * @param bounds Bounds; the bounds
     */
    public StaticLocation3d(final Point2D point2D, final double rotZ, final Bounds3d bounds)
    {
        super(point2D.getX(), point2D.getY(), 0.0, 0.0, 0.0, rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param location OrientedPoint3d; the location
     * @param bounds Bounds; the bounds
     */
    public StaticLocation3d(final OrientedPoint3d location, final Bounds3d bounds)
    {
        super(location.getX(), location.getY(), location.getZ(), location.getDirX(), location.getDirY(), location.getDirZ());
        this.bounds = bounds;
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return this.bounds;
    }

}
