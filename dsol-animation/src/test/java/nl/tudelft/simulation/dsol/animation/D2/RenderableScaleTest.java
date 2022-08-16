package nl.tudelft.simulation.dsol.animation.D2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;
import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * RenderableScaleTest to test the RenderableScale class.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RenderableScaleTest
{
    /** Test RenderableScale. */
    @Test
    public void testRenderableScale()
    {
        Bounds2d bounds = new Bounds2d(-100, 100, -50, 50);
        Dimension screen = new Dimension(200, 100);
        RenderableScale scale1 = new RenderableScale();

        assertNull(scale1.computeVisibleExtent(null, screen));
        assertNull(scale1.computeVisibleExtent(bounds, null));
        assertNull(scale1.computeVisibleExtent(bounds, new Dimension(-1, 100)));
        assertNull(scale1.computeVisibleExtent(bounds, new Dimension(100, -1)));
        assertNull(scale1.computeVisibleExtent(bounds, new Dimension(0, 100)));
        assertNull(scale1.computeVisibleExtent(bounds, new Dimension(100, 0)));

        Bounds2d extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -50, 50);

        screen = new Dimension(100, 100);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -100, 100);

        screen = new Dimension(400, 100);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -200, 200, -50, 50);

        screen = new Dimension(400, 200);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -50, 50);

        screen = new Dimension(400, 400);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -100, 100);

        // compressed y-axis

        RenderableScale scale2 = new RenderableScale(2.0);
        screen = new Dimension(200, 100); // data on the screen is 200 x 200
        extent = scale2.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -100, 100);

        screen = new Dimension(200, 200); // data on the screen is 200 x 400
        extent = scale2.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -200, 200);

        screen = new Dimension(400, 100); // data on the screen is 400 x 200
        extent = scale2.computeVisibleExtent(bounds, screen);
        testExtent(extent, -100, 100, -50, 50);

        // asymmetric, not scaled

        bounds = new Bounds2d(0, 100, -50, 0);
        screen = new Dimension(200, 100);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, 0, 100, -50, 0);

        screen = new Dimension(100, 100);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, 0, 100, -75, 25);

        screen = new Dimension(400, 100);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, -50, 150, -50, 0);

        screen = new Dimension(400, 200);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, 0, 100, -50, 0);

        screen = new Dimension(400, 400);
        extent = scale1.computeVisibleExtent(bounds, screen);
        testExtent(extent, 0, 100, -75, 25);

        // asymmetric scaled, expanded y-axis

        RenderableScale scale5 = new RenderableScale(0.5);
        screen = new Dimension(200, 100); // data on the screen is 200 x 50
        extent = scale5.computeVisibleExtent(bounds, screen);
        testExtent(extent, -50, 150, -50, 0);

        screen = new Dimension(200, 200); // data on the screen is 200 x 100
        extent = scale5.computeVisibleExtent(bounds, screen);
        testExtent(extent, 0, 100, -50, 0);

        screen = new Dimension(400, 100); // data on the screen is 400 x 50
        extent = scale5.computeVisibleExtent(bounds, screen);
        testExtent(extent, -150, 250, -50, 0);
    }

    /** Test whether the visible extent of a calculated visible extent remains unchanged. */
    @Test
    public void testUnchangedScaleIdentity()
    {
        for (double scale = 0.5; scale <= 2; scale += 0.5)
        {
            RenderableScale renderableScale = new RenderableScale(scale);
            assertEquals(scale, renderableScale.getYScaleRatio(), 0.0001);
            for (int deltax = 50; deltax < 200; deltax += 50)
            {
                for (int deltay = 50; deltay < 200; deltay += 50)
                {
                    Bounds2d bounds = new Bounds2d(-100, -100 + deltax, -50, -50 + deltay);
                    Dimension screen = new Dimension(200, 100);
                    Bounds2d extent = renderableScale.computeVisibleExtent(bounds, screen);
                    assertEquals(extent, renderableScale.computeVisibleExtent(extent, screen));
                    assertEquals(2.0 / scale, extent.getDeltaX() / extent.getDeltaY(), 0.001);
                }
            }
        }
    }

    /**
     * @param extent Bounds2d; the extent to test
     * @param minX double; expected minX
     * @param maxX double; expected maxX
     * @param minY double; expected minY
     * @param maxY double; expected maxY
     */
    private void testExtent(final Bounds2d extent, final double minX, final double maxX, final double minY, final double maxY)
    {
        assertEquals(minX, extent.getMinX(), 0.001);
        assertEquals(maxX, extent.getMaxX(), 0.001);
        assertEquals(minY, extent.getMinY(), 0.001);
        assertEquals(maxY, extent.getMaxY(), 0.001);
    }

    /**
     * Test the xScale and yScale.
     */
    @Test
    public void testXYScale()
    {
        Bounds2d extent = new Bounds2d(-100, 100, -50, 50);
        Dimension screen = new Dimension(10, 5);
        RenderableScale scale = new RenderableScale();
        assertEquals(20.0, scale.getXScale(extent, screen), 0.001);
        assertEquals(20.0, scale.getYScale(extent, screen), 0.001);
        scale = new RenderableScale(2.0);
        assertEquals(20.0, scale.getXScale(extent, screen), 0.001);
        assertEquals(20.0, scale.getYScale(extent, screen), 0.001);

        assertEquals(Double.NaN, scale.getXScale(extent, new Dimension(-10, 10)), 0.001);
        assertEquals(Double.NaN, scale.getYScale(extent, new Dimension(-10, 10)), 0.001);
        assertEquals(Double.NaN, scale.getXScale(extent, new Dimension(10, -10)), 0.001);
        assertEquals(Double.NaN, scale.getYScale(extent, new Dimension(10, -10)), 0.001);

        scale = new RenderableScale(1.0, 0.5);
        assertEquals(0.5, scale.getObjectScaleFactor(), 0.0001);

        Try.testFail(() -> { new RenderableScale(0.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new RenderableScale(-10.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new RenderableScale(1.0, 0.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new RenderableScale(1.0, -10.0); }, IllegalArgumentException.class);
    }

    /**
     * Test coordinate transformations between extent and screen.
     */
    @Test
    public void testCoordinateTransformations()
    {
        Bounds2d extent = new Bounds2d(-100, 100, -50, 50);
        Dimension screen = new Dimension(200, 100);
        RenderableScale scale = new RenderableScale();

        // halfway
        assertEquals(new Point2D.Double(100, 50), scale.getScreenCoordinates(new Point2d(0, 0), extent, screen));
        assertEquals(new Point2d(0, 0), scale.getWorldCoordinates(new Point2D.Double(100, 50), extent, screen));

        // note that y is down
        assertEquals(new Point2D.Double(0, 100), scale.getScreenCoordinates(new Point2d(-100, -50), extent, screen));
        assertEquals(new Point2d(-100, -50), scale.getWorldCoordinates(new Point2D.Double(0, 100), extent, screen));
        assertEquals(new Point2D.Double(200, 0), scale.getScreenCoordinates(new Point2d(100, 50), extent, screen));
        assertEquals(new Point2d(100, 50), scale.getWorldCoordinates(new Point2D.Double(200, 0), extent, screen));
    }
}
