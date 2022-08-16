package nl.tudelft.simulation.language.d2;

import java.awt.geom.Point2D;

/**
 * The Circle class presents a number of mathematical utility functions for circles. For now, the class only implements static
 * helper methods. No instances of the class should be made.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class Circle
{
    /**
     * constructs a new Circle.
     */
    private Circle()
    {
        // unreachable code
    }

    /**
     * Elegant intersection algorithm from http://astronomy.swin.edu.au/~pbourke/geometry/2circle/.
     * @param center0 Point2D; the center of the first circle
     * @param radius0 double; the radius of the first circle
     * @param center1 Point2D; the center of the second circle
     * @param radius1 double; the radius of the second circle
     * @return the intersection
     */
    public static Point2D[] intersection(final Point2D center0, final double radius0, final Point2D center1,
            final double radius1)
    {
        double distance = center0.distance(center1);
        double x0 = center0.getX();
        double x1 = center1.getX();
        double y0 = center0.getY();
        double y1 = center1.getY();
        Point2D[] result;
        if ((distance > radius0 + radius1) || (distance < Math.abs(radius0 - radius1)))
        {
            return new Point2D.Double[0];
        }
        double a = (radius0 * radius0 - radius1 * radius1 + distance * distance) / (2 * distance);
        double h = Math.sqrt(radius0 * radius0 - a * a);
        double x2 = x0 + ((a / distance) * (x1 - x0));
        double y2 = y0 + ((a / distance) * (y1 - y0));
        if (distance == radius0 + radius1)
        {
            result = new Point2D.Double[1];
            result[0] = new Point2D.Double(x2, y2);
        }
        else
        {
            result = new Point2D.Double[2];
            result[0] = new Point2D.Double(x2 + h * (y1 - y0) / distance, y2 - h * (x1 - x0) / distance);
            result[1] = new Point2D.Double(x2 - h * (y1 - y0) / distance, y2 + h * (x1 - x0) / distance);
        }
        return result;
    }
}
