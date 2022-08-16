package nl.tudelft.simulation.language.d2;

import org.djutils.draw.bounds.Bounds2d;

/**
 * Shape utilities for world coordinates.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public final class Shape2d
{
    /**
     * constructs a new Shape.
     */
    private Shape2d()
    {
        // utility class.
    }

    /**
     * overlaps extent and shape. Overlap = Intersect + Contain
     * @param extent Bounds2d; the extent
     * @param shape Bounds2d; the shape
     * @return whether extent overlaps shape
     */
    public static boolean overlaps(final Bounds2d extent, final Bounds2d shape)
    {
        if (extent.getMaxX() < shape.getMinX())
        {
            return false;
        }
        if (extent.getMaxY() < shape.getMinY())
        {
            return false;
        }
        if (extent.getMinX() > shape.getMaxX())
        {
            return false;
        }
        if (extent.getMinY() > shape.getMaxY())
        {
            return false;
        }
        return true;
    }

    /**
     * do the rectangles really intersect (so, not being contained)?
     * @param r1 Bounds2d; the first rectangle
     * @param r2 Bounds2d; the second rectangle
     * @return whether r1 really intersects r2
     */
    public static boolean intersects(final Bounds2d r1, final Bounds2d r2)
    {
        return !Shape2d.contains(r1, r2) && !Shape2d.contains(r2, r1) && overlaps(r1, r2);
    }

    /**
     * is r1 completely in r2.
     * @param r1 Bounds2d; the first rectangle
     * @param r2 Bounds2d; the second rectangle
     * @return whether r1 in r2
     */
    public static boolean contains(final Bounds2d r1, final Bounds2d r2)
    {
        boolean contains = (r2.getMinX() <= r1.getMinX() && r2.getMinY() <= r1.getMinY() && r2.getMaxX() >= r1.getMaxX()
                && r2.getMaxY() >= r1.getMaxY());
        return contains;
    }
}
