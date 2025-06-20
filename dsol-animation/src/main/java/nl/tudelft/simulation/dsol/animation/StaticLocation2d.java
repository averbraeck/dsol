package nl.tudelft.simulation.dsol.animation;

import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.DirectedPoint2d;

/**
 * A StaticLocation in 2 dimensions.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class StaticLocation2d extends DirectedPoint2d implements Locatable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the bounds. */
    private Bounds2d bounds = null;

    /**
     * constructs a new StaticLocation.
     * @param x the x location
     * @param y the y location
     * @param rotZ rotation aroud z-axis
     * @param bounds the bounds
     */
    public StaticLocation2d(final double x, final double y, final double rotZ, final Bounds2d bounds)
    {
        super(x, y, rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param point2D the point2d
     * @param rotZ the rotation in the xy plane
     * @param bounds the bounds
     */
    public StaticLocation2d(final Point2D point2D, final double rotZ, final Bounds2d bounds)
    {
        super(point2D.getX(), point2D.getY(), rotZ);
        this.bounds = bounds;
    }

    /**
     * constructs a new StaticLocation.
     * @param location the location
     * @param bounds the bounds
     */
    public StaticLocation2d(final DirectedPoint2d location, final Bounds2d bounds)
    {
        super(location.getX(), location.getY(), location.getDirZ());
        this.bounds = bounds;
    }

    @Override
    public DirectedPoint2d getLocation()
    {
        return this;
    }

    @Override
    public Bounds2d getRelativeBounds()
    {
        return this.bounds;
    }

}
