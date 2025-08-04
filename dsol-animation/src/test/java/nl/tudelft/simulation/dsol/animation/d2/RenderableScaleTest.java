package nl.tudelft.simulation.dsol.animation.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;
import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

/**
 * RenderableScaleTest to test the RenderableScale class.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class RenderableScaleTest
{
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

        Try.testFail(() ->
        {
            new RenderableScale(0.0);
        }, IllegalArgumentException.class);
        Try.testFail(() ->
        {
            new RenderableScale(-10.0);
        }, IllegalArgumentException.class);
        Try.testFail(() ->
        {
            new RenderableScale(1.0, 0.0);
        }, IllegalArgumentException.class);
        Try.testFail(() ->
        {
            new RenderableScale(1.0, -10.0);
        }, IllegalArgumentException.class);
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
