package nl.tudelft.simulation.dsol.animation.D2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;

import org.djutils.draw.Transform3d;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point2d;
import org.djutils.draw.point.Point3d;
import org.junit.Test;

import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * Unit tests for the BoundsUtil class.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class BoundsUtilTest
{
    /**
     * test transform method.
     */
    @Test
    public void testTransform()
    {
        Bounds3d box000 = new Bounds3d(new Point3d[] {new Point3d(-2, -2, -2), new Point3d(2, 2, 2)});
        OrientedPoint3d location = new OrientedPoint3d(4, 4, 4);
        // rotate 45 degrees around z-axis w.r.t. (0,0,0) ->
        // bounding box becomes (-2.sqrt(2), -2.sqrt(2), -2), (2.sqrt(2), 2.sqrt(2), 2)
        // the box is around (4,4,4), so its real coordinates are (4-2.sqrt(2), 4-2.sqrt(2), 2), (4+2.sqrt(2), 4+2.sqrt(2), 6).
        Transform3d tr45 = new Transform3d();
        tr45.rotZ(Math.toRadians(45.0));
        Bounds3d box45 = tr45.transform(box000);
        Bounds3d box = BoundsUtil.transform(location, box45);
        double s2 = 2.0 * Math.sqrt(2.0);
        testRect(box.project(), 4.0 - s2, 4.0 - s2, 4.0 + s2, 4.0 + s2);
        assertEquals(2.0, box.getMinZ(), 0.001);
        assertEquals(6.0, box.getMaxZ(), 0.001);
    }

    /**
     * Test the bounds of the rectangle.
     * @param rect the rectangle to test
     * @param minx expected minx
     * @param miny expected miny
     * @param maxx expected maxx
     * @param maxy expected maxy
     */
    private void testRect(final Bounds2d rect, final double minx, final double miny, final double maxx, final double maxy)
    {
        assertEquals(minx, rect.getMinX(), 0.001);
        assertEquals(miny, rect.getMinY(), 0.001);
        assertEquals(maxx, rect.getMaxX(), 0.001);
        assertEquals(maxy, rect.getMaxY(), 0.001);
    }

    /**
     * test contains method.
     */
    @Test
    public void testContains()
    {
        Bounds3d bounds = new Bounds3d(new Point3d[] {new Point3d(-2, -2, -2), new Point3d(2, 2, 2)});
        OrientedPoint3d center = new OrientedPoint3d(4, 4, 4);
        // the box is around (4,4,4), so its real coordinates are (2,2,2, 6,6,6).
        assertFalse(BoundsUtil.contains(center, bounds, new Point3d(0, 0, 0)));
        assertTrue(BoundsUtil.contains(center, bounds, new Point3d(4, 4, 4)));
        assertTrue(BoundsUtil.contains(center, bounds, new Point3d(2, 2, 2)));
        assertTrue(BoundsUtil.contains(center, bounds, new Point3d(6, 6, 6)));
    }

    /**
     * Test the projectBounds and bounds transformation for an OrientedPoint3d.
     * @throws RemoteException on network error
     */
    @Test
    public void testProjectBoundsOrientedPoint3d() throws RemoteException
    {
        Bounds3d box000 = new Bounds3d(new Point3d[] {new Point3d(-2, -2, -2), new Point3d(2, 2, 2)});
        OrientedPoint3d location = new OrientedPoint3d(4, 4, 4);
        // the box is around (4,4,4), so its real coordinates are (2,2,2, 6,6,6).
        testRect(BoundsUtil.projectBounds(location, box000), 2, 2, 6, 6);

        class L implements Locatable
        {
            private double x, y, dirZ;

            L(final double x, final double y, final double dirZ)
            {
                this.x = x;
                this.y = y;
                this.dirZ = dirZ;
            }

            @Override
            public OrientedPoint3d getLocation() throws RemoteException
            {
                return new OrientedPoint3d(this.x, this.y, 0, 0, 0, this.dirZ);
            }

            @Override
            public Bounds3d getBounds() throws RemoteException
            {
                return new Bounds3d(-4, 4, -4, 4, -4, 4);
            }

        }

        L l = new L(0.0, 0.0, 0.0);
        Bounds3d b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(-4, b.getMinX(), 0.001);
        assertEquals(+4, b.getMaxX(), 0.001);
        assertEquals(-4, b.getMinY(), 0.001);
        assertEquals(+4, b.getMaxY(), 0.001);
        assertEquals(-4, b.getMinZ(), 0.001);
        assertEquals(+4, b.getMaxZ(), 0.001);

        l = new L(20.0, 10.0, 0.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - 4, b.getMinX(), 0.001);
        assertEquals(20 + 4, b.getMaxX(), 0.001);
        assertEquals(10 - 4, b.getMinY(), 0.001);
        assertEquals(10 + 4, b.getMaxY(), 0.001);
        assertEquals(-4, b.getMinZ(), 0.001);
        assertEquals(+4, b.getMaxZ(), 0.001);

        l = new L(20.0, 10.0, Math.toRadians(90.0));
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - 4, b.getMinX(), 0.001);
        assertEquals(20 + 4, b.getMaxX(), 0.001);
        assertEquals(10 - 4, b.getMinY(), 0.001);
        assertEquals(10 + 4, b.getMaxY(), 0.001);
        assertEquals(-4, b.getMinZ(), 0.001);
        assertEquals(+4, b.getMaxZ(), 0.001);

        l = new L(0.0, 0.0, Math.toRadians(45.0));
        double d = 4.0 * Math.sqrt(2.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(-d, b.getMinX(), 0.001);
        assertEquals(d, b.getMaxX(), 0.001);
        assertEquals(-d, b.getMinY(), 0.001);
        assertEquals(d, b.getMaxY(), 0.001);
        assertEquals(-4, b.getMinZ(), 0.001);

        l = new L(20.0, 10.0, Math.toRadians(45.0));
        d = 4.0 * Math.sqrt(2.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - d, b.getMinX(), 0.001);
        assertEquals(20 + d, b.getMaxX(), 0.001);
        assertEquals(10 - d, b.getMinY(), 0.001);
        assertEquals(10 + d, b.getMaxY(), 0.001);
        assertEquals(-4, b.getMinZ(), 0.001);
        assertEquals(+4, b.getMaxZ(), 0.001);
    }

    /**
     * Test the projectBounds and bounds transformation for a Point3d.
     * @throws RemoteException on network error
     */
    @Test
    public void testProjectBoundsPoint3d() throws RemoteException
    {
        Bounds3d box000 = new Bounds3d(new Point3d[] {new Point3d(-2, -2, -2), new Point3d(2, 2, 2)});
        Point3d location = new Point3d(4, 4, 4);
        // the box is around (4,4,4), so its real coordinates are (2,2,2, 6,6,6).
        testRect(BoundsUtil.projectBounds(location, box000), 2, 2, 6, 6);
    }

    /**
     * Test the projectBounds and bounds transformation for an OrientedPoint2d.
     * @throws RemoteException on network error
     */
    @Test
    public void testProjectBoundsOrientedPoint2d() throws RemoteException
    {
        Bounds2d box00 = new Bounds2d(new Point2d[] {new Point2d(-2, -2), new Point2d(2, 2)});
        OrientedPoint2d location = new OrientedPoint2d(4, 4);
        // the box is around (4,4), so its real coordinates are (2,2, 6,6).
        testRect(BoundsUtil.projectBounds(location, box00), 2, 2, 6, 6);

        class L implements Locatable
        {
            private double x, y, dirZ;

            L(final double x, final double y, final double dirZ)
            {
                this.x = x;
                this.y = y;
                this.dirZ = dirZ;
            }

            @Override
            public OrientedPoint2d getLocation() throws RemoteException
            {
                return new OrientedPoint2d(this.x, this.y, this.dirZ);
            }

            @Override
            public Bounds2d getBounds() throws RemoteException
            {
                return new Bounds2d(-4, 4, -4, 4);
            }

        }

        L l = new L(0.0, 0.0, 0.0);
        Bounds2d b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(-4, b.getMinX(), 0.001);
        assertEquals(+4, b.getMaxX(), 0.001);
        assertEquals(-4, b.getMinY(), 0.001);
        assertEquals(+4, b.getMaxY(), 0.001);

        l = new L(20.0, 10.0, 0.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - 4, b.getMinX(), 0.001);
        assertEquals(20 + 4, b.getMaxX(), 0.001);
        assertEquals(10 - 4, b.getMinY(), 0.001);
        assertEquals(10 + 4, b.getMaxY(), 0.001);

        l = new L(20.0, 10.0, Math.toRadians(90.0));
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - 4, b.getMinX(), 0.001);
        assertEquals(20 + 4, b.getMaxX(), 0.001);
        assertEquals(10 - 4, b.getMinY(), 0.001);
        assertEquals(10 + 4, b.getMaxY(), 0.001);

        l = new L(0.0, 0.0, Math.toRadians(45.0));
        double d = 4.0 * Math.sqrt(2.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(-d, b.getMinX(), 0.001);
        assertEquals(d, b.getMaxX(), 0.001);
        assertEquals(-d, b.getMinY(), 0.001);
        assertEquals(d, b.getMaxY(), 0.001);

        l = new L(20.0, 10.0, Math.toRadians(45.0));
        d = 4.0 * Math.sqrt(2.0);
        b = BoundsUtil.transform(l.getLocation(), l.getBounds());
        assertEquals(20 - d, b.getMinX(), 0.001);
        assertEquals(20 + d, b.getMaxX(), 0.001);
        assertEquals(10 - d, b.getMinY(), 0.001);
        assertEquals(10 + d, b.getMaxY(), 0.001);
    }

    /**
     * Test the projectBounds and bounds transformation for a Point2d.
     * @throws RemoteException on network error
     */
    @Test
    public void testProjectBoundsPoint2d() throws RemoteException
    {
        Bounds2d box00 = new Bounds2d(new Point2d[] {new Point2d(-2, -2), new Point2d(2, 2)});
        Point2d location = new Point2d(4, 4);
        // the box is around (4,4), so its real coordinates are (2,2, 6,6).
        testRect(BoundsUtil.projectBounds(location, box00), 2, 2, 6, 6);
    }

}
