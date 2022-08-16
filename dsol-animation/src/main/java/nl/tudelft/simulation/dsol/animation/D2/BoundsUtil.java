package nl.tudelft.simulation.dsol.animation.D2;

import org.djutils.draw.Oriented;
import org.djutils.draw.Transform2d;
import org.djutils.draw.Transform3d;
import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.draw.point.Point3d;

/**
 * A Bounds3d utility class to help with finding intersections between bounds, to make transformations, and to see if a point
 * lies within a bounds. The static methods can help in animation to see whether a shape needs to be drawn on the screen
 * (3D-viewport) or not.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public final class BoundsUtil
{
    /**
     * constructs a new BoundsUtil.
     */
    private BoundsUtil()
    {
        // unreachable code
    }

    /**
     * Computes s new bounding box based on the projection of the original bounding box on the XY-plane, translated by the
     * center point coordinates, and possibly rotated by the orientation of the center point.
     * @param center Point; the point relative to which the bounds need to be calculated
     * @param bounds Bounds; the bounds for which the intersection needs to be calculated. The Bounds3d are <b>relative to the
     *            center</b> that is provided
     * @return Bounds2d the projected rectangle of the intersection, or null if there is no intersection
     */
    public static Bounds2d projectBounds(final Point<?> center, final Bounds<?, ?, ?> bounds)
    {
        if (center == null)
        {
            return new Bounds2d(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());
        }
        if (center instanceof Oriented)
        {
            Bounds2d b = new Bounds2d(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());
            Point2d c = new Point2d(center.getX(), center.getY());
            Oriented<?> o = (Oriented<?>) center;
            Transform2d transformation = new Transform2d();
            transformation.translate(c);
            transformation.rotation(o.getDirZ());
            return transformation.transform(b);
        }
        return new Bounds2d(bounds.getMinX() + center.getX(), bounds.getMaxX() + center.getX(),
                bounds.getMinY() + center.getY(), bounds.getMaxY() + center.getY());
    }

    /**
     * Rotates and translates a bound relative to an oriented point. Often this point will be the given center point for the
     * animation.
     * @param center OrientedPoint3d; the point relative to which the bounds need to be transformed
     * @param bounds Bounds3d; the bounds that need to be rotated and translated
     * @return the bounds after rotation and translation
     */
    public static Bounds3d transform(final OrientedPoint3d center, final Bounds3d bounds)
    {
        if (center == null)
        {
            return bounds;
        }
        Transform3d transform = new Transform3d();
        transform.translate(center); // note: opposite order of how it should be carried out (!)
        transform.rotX(center.getDirX());
        transform.rotY(center.getDirY());
        transform.rotZ(center.getDirZ());
        return transform.transform(bounds);
    }

    /**
     * Rotates and translates a bound relative to an oriented point. Often this point will be the given center point for the
     * animation.
     * @param center OrientedPoint2d; the point relative to which the bounds need to be transformed
     * @param bounds Bound2ds; the bounds that need to be rotated and translated
     * @return the bounds after rotation and translation
     */
    public static Bounds2d transform(final OrientedPoint2d center, final Bounds2d bounds)
    {
        if (center == null)
        {
            return bounds;
        }
        Transform2d transform = new Transform2d();
        transform.translate(center); // note: opposite order of how it should be carried out (!)
        transform.rotation(center.getDirZ());
        return transform.transform(bounds);
    }

    /**
     * Check whether a point is in the bounds, after transforming the bounds relative to the center point (in animation that is
     * the location). Usually the center is in the bounds, but that is not necessary. The center is in many occasions the
     * Location of an animated object, and the bounds indicate the outer values of its animation without translation and
     * rotation (as if center is 0,0,0) and has no direction (rotX, rotY and rotZ are 0.0).
     * @param center OrientedPoint3d; the 'center' of the bounds.
     * @param bounds Bounds3d; the bounds relative to 0,0,0
     * @param point Point3d; the point that might be in or out of the bounds after they have been rotated and translated
     *            relative to the center.
     * @return whether or not the point is in the bounds
     */
    public static boolean contains(final OrientedPoint3d center, final Bounds3d bounds, final Point3d point)
    {
        if (center == null)
        {
            return false;
        }
        Transform3d transform = new Transform3d();
        transform.translate(center);
        transform.rotX(center.getDirX());
        transform.rotY(center.getDirY());
        transform.rotZ(center.getDirZ());
        Bounds3d box = transform.transform(bounds);
        return box.covers(point);
    }

    /**
     * Check whether a point is in the bounds, after transforming the bounds relative to the center point (in animation that is
     * the location). Usually the center is in the bounds, but that is not necessary. The center is in many occasions the
     * Location of an animated object, and the bounds indicate the outer values of its animation without translation and
     * rotation (as if center is 0,0) and has no direction (rotation 0.0).
     * @param center OrientedPoint2d; the 'center' of the bounds.
     * @param bounds Bounds2d; the bounds relative to 0,0
     * @param point Point2d; the point that might be in or out of the bounds after they have been rotated and translated
     *            relative to the center.
     * @return whether or not the point is in the bounds
     */
    public static boolean contains(final OrientedPoint2d center, final Bounds2d bounds, final Point2d point)
    {
        if (center == null)
        {
            return false;
        }
        Transform2d transform = new Transform2d();
        transform.translate(center);
        transform.rotation(center.getDirZ());
        Bounds2d box = transform.transform(bounds);
        return box.covers(point);
    }
}
