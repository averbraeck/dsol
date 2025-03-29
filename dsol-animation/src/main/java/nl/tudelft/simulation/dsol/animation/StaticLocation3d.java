package nl.tudelft.simulation.dsol.animation;

import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

/**
 * A StaticLocation in 3 dimensions.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
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
     * @param x the x location
     * @param y the y location
     * @param z the z location
     * @param rotX rotation around x-axis (theta)
     * @param rotY rotation around y-axis (phi)
     * @param rotZ rotation around z-axis (rho)
     * @param bounds the bounds
     */
    public StaticLocation3d(final double x, final double y, final double z, final double rotX, final double rotY,
            final double rotZ, final Bounds3d bounds)
    {
        super(x, y, z, rotX, rotY, rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param point2D the point2d
     * @param rotZ the rotation in the xy plane
     * @param bounds the bounds
     */
    public StaticLocation3d(final Point2D point2D, final double rotZ, final Bounds3d bounds)
    {
        super(point2D.getX(), point2D.getY(), 0.0, 0.0, 0.0, rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param location the location
     * @param bounds the bounds
     */
    public StaticLocation3d(final OrientedPoint3d location, final Bounds3d bounds)
    {
        super(location.getX(), location.getY(), location.getZ(), location.getDirX(), location.getDirY(), location.getDirZ());
        this.bounds = bounds;
    }

    @Override
    public OrientedPoint3d getLocation()
    {
        return this;
    }

    @Override
    public Bounds3d getBounds()
    {
        return this.bounds;
    }

}
