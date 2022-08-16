package nl.tudelft.simulation.dsol.animation.D2;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.exceptions.Throw;

/**
 * A helper class for transforming between screen coordinates and world coordinates. The x-axis and y-axis can be scaled
 * independently from each other, causing expansion or compression of the y-axis relative to the x-axis.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RenderableScale
{
    /**
     * the Y-scale factor with respect to the X-scale; 1 means X and Y scales are the same; A number larger than 1 means the
     * y-axis is compressed.
     */
    private final double yScaleRatio;

    /**
     * the scale factor for the object, e.g., to get 1 pixel unit to equal 1 meter on a map. A scale factor smaller than 1 means
     * that the object will be drawn smaller on the screen.
     */
    private final double objectScaleFactor;

    /**
     * Construct a translator between world coordinates and screen coordinates that uses a different scale factor for x and y.
     * @param yScaleRatio double; the Y-scale ratio with respect to the X-scale (when yScale &lt; 1 it results in a condensed
     *            Y-axis, where yScale &gt; 1 results in an expanded Y-axis
     * @param objectScaleFactor double; the scale factor for the object, e.g., to get 1 pixel unit to equal 1 meter on a map. A
     *            scale factor smaller than 1 means that the object will be drawn smaller on the screen.
     * @throws IllegalArgumentException when yScale &lt;= 0
     */
    public RenderableScale(final double yScaleRatio, final double objectScaleFactor)
    {
        Throw.when(yScaleRatio <= 0, IllegalArgumentException.class, "yScaleRatio should be larger than 0");
        Throw.when(objectScaleFactor <= 0, IllegalArgumentException.class, "objectScaleFactor should be larger than 0");
        this.yScaleRatio = yScaleRatio;
        this.objectScaleFactor = objectScaleFactor;
    }

    /**
     * Construct a translator between world coordinates and screen coordinates that uses a different scale factor for x and y.
     * @param yScaleRatio double; the Y-scale ratio with respect to the X-scale (when yScale &lt; 1 it results in a condensed
     *            Y-axis, where yScale &gt; 1 results in an expanded Y-axis
     * @throws IllegalArgumentException when yScale &lt;= 0
     */
    public RenderableScale(final double yScaleRatio)
    {
        this(yScaleRatio, 1.0);
    }

    /**
     * Construct a translator between world coordinates and screen coordinates that uses an X/Y ratio of 1.
     * @throws IllegalArgumentException when yScale &lt;= 0
     */
    public RenderableScale()
    {
        this(1.0, 1.0);
    }

    /**
     * Returns the X-scale of a screen compared to an extent. If the height or the width of the screen are &lt; 0 Double.NaN is
     * returned.
     * @param extent Rectangle2D; the extent of this animation
     * @param screen Dimension; the screen dimensions
     * @return double; the scale. Can return Double.NaN
     */
    public double getXScale(final Bounds2d extent, final Dimension screen)
    {
        if (screen.getHeight() <= 0 || screen.getWidth() <= 0)
        {
            return Double.NaN;
        }
        return extent.getDeltaX() / screen.getWidth();
    }

    /**
     * Returns the Y-scale of a screen compared to an extent. If the height or the width of the screen are &lt; 0 Double.NaN is
     * returned.
     * @param extent Rectangle2D; the extent of this animation
     * @param screen Dimension; the screen dimensions
     * @return double; the scale. Can return Double.NaN
     */
    public double getYScale(final Bounds2d extent, final Dimension screen)
    {
        if (screen.getHeight() <= 0 || screen.getWidth() <= 0)
        {
            return Double.NaN;
        }
        return extent.getDeltaY() / screen.getHeight();
    }

    /**
     * computes the visible extent.
     * @param extent Rectangle2D; the extent
     * @param screen Dimension; the screen
     * @return a new extent or null if parameters are null or screen is invalid (width / height &lt;= 0)
     */
    public Bounds2d computeVisibleExtent(final Bounds2d extent, final Dimension screen)
    {
        if (extent == null || screen == null || screen.getHeight() <= 0 || screen.getWidth() <= 0)
        {
            return null;
        }
        double xScale = extent.getDeltaX() / screen.getWidth();
        double yScale = extent.getDeltaY() / (this.yScaleRatio * screen.getHeight());
        Bounds2d result;
        if (xScale < yScale)
        {
            result = new Bounds2d(extent.midPoint().getX() - 0.5 * screen.getWidth() * yScale,
                    extent.midPoint().getX() + 0.5 * screen.getWidth() * yScale, extent.getMinY(), extent.getMaxY());
        }
        else
        {
            result = new Bounds2d(extent.getMinX(), extent.getMaxX(),
                    extent.midPoint().getY() - 0.5 * screen.getHeight() * xScale * this.yScaleRatio,
                    extent.midPoint().getY() + 0.5 * screen.getHeight() * xScale * this.yScaleRatio);
        }
        return result;
    }

    /**
     * returns the frame xy-coordinates of a point in world coordinates. If parameters are invalid (i.e. screen.size &lt;= 0) a
     * null value is returned. If parameter combinations (i.e !extent.contains(point)) are invalid a null value is returned.
     * @param worldCoordinates Point&lt;?, ?&gt;; the world coordinates
     * @param extent Bounds2d; the extent of this animation
     * @param screen Dimension; the screen dimentsions
     * @return Point2D (x,y) on screen. Can be null.
     */
    public Point2D getScreenCoordinates(final Point<?> worldCoordinates, final Bounds2d extent, final Dimension screen)
    {
        double x = (worldCoordinates.getX() - extent.getMinX()) * (1.0 / getXScale(extent, screen));
        double y = screen.getHeight() - (worldCoordinates.getY() - extent.getMinY()) * (1.0 / getYScale(extent, screen));
        return new Point2D.Double(x, y);
    }

    /**
     * returns the frame xy-coordinates of a point in screen coordinates. If parameters are invalid (i.e. screen.size &lt; 0) a
     * null value is returned. If parameter combinations (i.e !screen.contains(point)) are invalid a null value is returned.
     * @param screenCoordinates Point2D; the screen coordinates
     * @param extent Bounds2d; the extent of this animation
     * @param screen Dimension; the screen dimensions
     * @return Point2d (x,y) in the 2D or 3D world
     */
    public Point2d getWorldCoordinates(final Point2D screenCoordinates, final Bounds2d extent, final Dimension screen)
    {
        double x = (screenCoordinates.getX()) * getXScale(extent, screen) + extent.getMinX();
        double y = ((screen.getHeight() - screenCoordinates.getY())) * getYScale(extent, screen) + extent.getMinY();
        return new Point2d(x, y);
    }

    /**
     * Return the y-scale ratio. A number larger than 1 means the y-axis is compressed.
     * @return double; the y-scale ratio. A number larger than 1 means the y-axis is compressed
     */
    public double getYScaleRatio()
    {
        return this.yScaleRatio;
    }

    /**
     * Return the scale factor for the object, e.g., to get 1 pixel unit to equal 1 meter on a map. A scale factor smaller than
     * 1 means that the object will be drawn smaller on the screen.
     * @return double; the scale factor for the object, e.g., to get 1 pixel unit to equal 1 meter on a map
     */
    public double getObjectScaleFactor()
    {
        return this.objectScaleFactor;
    }

}
