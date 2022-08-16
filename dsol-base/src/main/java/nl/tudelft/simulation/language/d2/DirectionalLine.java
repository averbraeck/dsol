package nl.tudelft.simulation.language.d2;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A directional line with normal vector. Based on the BSPLine-example from the book Developing games in Java from David
 * Brackeen. 
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="mailto:royc@tbm.tudelft.nl">Roy Chin </a>
 */
public class DirectionalLine extends Line2D.Double
{
    /** the default serialVersionUId. */
    private static final long serialVersionUID = 1L;

    /** different values for the side a point can be at w.r.t. the line. */
    public enum Side
    {
        /** point at the back of the line. */
        BACKSIDE(-1),

        /** point collinear with the line. */
        COLLINEAR(0),

        /** point in front of the line. */
        FRONTSIDE(1),

        /** other line is spanning this line. */
        SPANNING(2);
    
        /** the value from DSOL-1 before enum was introduced. */
        private final int value;
        
        /**
         * Create a side; store the value from DSOL-1 as well.
         * @param value int; the value from DSOL-1 before enum was introduced
         */
        Side(final int value)
        {
            this.value = value;
        }

        /**
         * Returns the value from DSOL-1 before enum was introduced.
         * @return int; the value from DSOL-1 before enum was introduced
         */
        public int getValue()
        {
            return this.value;
        }
    }

    /** the thickness of the line. */
    private double lineThickness = 1;

    /** x coordinate of the line normal. */
    private double normalX;

    /** y coordinate of the line normal. */
    private double normalY;

    /**
     * Creates a new DirectionalLine based on the specified coordinates.
     * @param x1 double; Coordinate x1
     * @param y1 double; Coordinate y1
     * @param x2 double; Coordinate x2
     * @param y2 double; Coordinate y2
     */
    public DirectionalLine(final double x1, final double y1, final double x2, final double y2)
    {
        this.setLine(x1, y1, x2, y2);
    }

    /**
     * Creates a new DirectionalLine based on the specified (float) coordinates.
     * @param x1 float; Coordinate x1
     * @param y1 float; Coordinate y1
     * @param x2 float; Coordinate x2
     * @param y2 float; Coordinate y2
     */
    public DirectionalLine(final float x1, final float y1, final float x2, final float y2)
    {
        this.setLine(x1, y1, x2, y2);
    }

    /**
     * Calculates the normal to this line. The normal of (a, b) is (-b, a).
     */
    public void calcNormal()
    {
        this.normalX = this.y1 - this.y2;
        this.normalY = this.x2 - this.x1;
    }

    /**
     * Normalizes the normal of this line (make the normal's length 1).
     */
    public void normalize()
    {
        double length = Math.sqrt(this.normalX * this.normalX + this.normalY * this.normalY);
        this.normalX /= length;
        this.normalY /= length;
    }

    /**
     * Set the line using floats.
     * @param x1 float; x1 coordinate
     * @param y1 float; y1 coordinate
     * @param x2 float; x2 coordinate
     * @param y2 float; y2 coordinate
     */
    public void setLine(final float x1, final float y1, final float x2, final float y2)
    {
        super.setLine(x1, y1, x2, y2);
        this.calcNormal();
    }

    /** {@inheritDoc} */
    @Override
    public void setLine(final double x1, final double y1, final double x2, final double y2)
    {
        super.setLine(x1, y1, x2, y2);
        this.calcNormal();
    }

    /**
     * Flips this line so that the end points are reversed (in other words, (x1,y1) becomes (x2,y2) and vice versa) and the
     * normal is changed to point the opposite direction.
     */
    public void flip()
    {
        double tx = this.x1;
        double ty = this.y1;
        this.x1 = this.x2;
        this.y1 = this.y2;
        this.x2 = tx;
        this.y2 = ty;
        this.normalX = -this.normalX;
        this.normalY = -this.normalY;
    }

    /**
     * Returns true if the endpoints of this line match the endpoints of the specified line. Ignores normal and height values.
     * @param line DirectionalLine; another line
     * @return true if this line's coordinates are equal to the other line's coordinates
     */
    public boolean equalsCoordinates(final DirectionalLine line)
    {
        return (this.x1 == line.x1 && this.x2 == line.x2 && this.y1 == line.y1 && this.y2 == line.y2);
    }

    /**
     * Returns true if the endpoints of this line match the endpoints of the specified line, ignoring endpoint order (if the
     * first point of this line is equal to the second point of the specified line, and vice versa, returns true). Ignores
     * normal and height values.
     * @param line DirectionalLine; another line
     * @return true if coordinates match independent of the order
     */
    public boolean equalsCoordinatesIgnoreOrder(final DirectionalLine line)
    {
        return equalsCoordinates(line)
                || ((this.x1 == line.x2 && this.x2 == line.x1 && this.y1 == line.y2 && this.y2 == line.y1));
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "(" + this.x1 + "," + this.y1 + ")->(" + this.x2 + "," + this.y2 + ")";
    }

    /**
     * Gets the side of this line the specified point is on. This method treats the line as 1-unit thick, so points within this
     * 1-unit border are considered collinear. For this to work correctly, the normal of this line must be normalized, either by
     * setting this line to a polygon or by calling normalize(). Returns either FRONTSIDE, BACKSIDE, or COLLINEAR.
     * @param x double; coordinate x
     * @param y double; coordinate y
     * @return the side
     */
    public Side getSideThick(final double x, final double y)
    {
        double normalX2 = this.normalX * this.lineThickness;
        double normalY2 = this.normalY * this.lineThickness;

        Side frontSide = getSideThin(x - normalX2 / 2, y - normalY2 / 2);
        if (frontSide.equals(Side.FRONTSIDE))
        {
            return Side.FRONTSIDE;
        }
        else if (frontSide.equals(Side.BACKSIDE))
        {
            Side backSide = getSideThin(x + normalX2 / 2, y + normalY2 / 2);
            if (backSide.equals(Side.BACKSIDE))
            {
                return Side.BACKSIDE;
            }
        }
        return Side.COLLINEAR;
    }

    /**
     * Gets the side of this line the specified point is on. Because of doubling point inaccuracy, a collinear line will be
     * rare. For this to work correctly, the normal of this line must be normalized, either by setting this line to a polygon or
     * by calling normalize(). Returns either FRONTSIDE, BACKSIDE, or COLLINEAR.
     * @param x double; coordinate x
     * @param y double; coordinate y
     * @return the side
     */
    public Side getSideThin(final double x, final double y)
    {
        // dot product between vector to the point and the normal
        double side = (x - this.x1) * this.normalX + (y - this.y1) * this.normalY;
        if (side < 0)
        {
            return Side.BACKSIDE;
        }
        else if (side > 0)
        {
            return Side.FRONTSIDE;
        }
        else
        {
            return Side.COLLINEAR;
        }
    }

    /**
     * Gets the side of this line that the specified line segment is on. Returns either FRONT, BACK, COLINEAR, or SPANNING.
     * @param line Line2D.Double; line segment
     * @return the side
     */
    public Side getSide(final Line2D.Double line)
    {
        if (this.x1 == line.x1 && this.x2 == line.x2 && this.y1 == line.y1 && this.y2 == line.y2)
        {
            return Side.COLLINEAR;
        }
        Side p1Side = getSideThick(line.x1, line.y1);
        Side p2Side = getSideThick(line.x2, line.y2);
        if (p1Side == p2Side)
        {
            return p1Side;
        }
        else if (p1Side == Side.COLLINEAR)
        {
            return p2Side;
        }
        else if (p2Side == Side.COLLINEAR)
        {
            return p1Side;
        }
        else
        {
            return Side.SPANNING;
        }
    }

    /**
     * Returns the fraction of intersection along this line. Returns a value from 0 to 1 if the segments intersect. For example,
     * a return value of 0 means the intersection occurs at point (x1, y1), 1 means the intersection occurs at point (x2, y2),
     * and .5 mean the intersection occurs halfway between the two endpoints of this line. Returns -1 if the lines are parallel.
     * @param line Line2D.Double; a line
     * @return the intersection
     */
    public double getIntersection(final Line2D.Double line)
    {
        // The intersection point I, of two vectors, A1->A2 and
        // B1->B2, is:
        // I = A1 + Ua * (A2 - A1)
        // I = B1 + Ub * (B2 - B1)
        //
        // Solving for Ua gives us the following formula.
        // Ua is returned.
        double denominator = (line.y2 - line.y1) * (this.x2 - this.x1) - (line.x2 - line.x1) * (this.y2 - this.y1);

        // check if the two lines are parallel
        if (denominator == 0)
        {
            return -1;
        }

        double numerator = (line.x2 - line.x1) * (this.y1 - line.y1) - (line.y2 - line.y1) * (this.x1 - line.x1);

        return numerator / denominator;
    }

    /**
     * Returns the intersection point of this line with the specified line.
     * @param line Line2D.Double; a line
     * @return intersection point
     */
    public Point2D.Double getIntersectionPoint(final Line2D.Double line)
    {
        double fraction = getIntersection(line);
        Point2D.Double intersection = new Point2D.Double();
        intersection.setLocation(this.x1 + fraction * (this.x2 - this.x1), this.y1 + fraction * (this.y2 - this.y1));
        return intersection;
    }

    /**
     * Gets the thickness of the line.
     * @return returns the lineThickness
     */
    public double getLineThickness()
    {
        return this.lineThickness;
    }

    /**
     * Sets the thickness of the line.
     * @param lineThickness double; the lineThickness to set
     */
    public void setLineThickness(final double lineThickness)
    {
        this.lineThickness = lineThickness;
    }

    /**
     * @return returns the normalX
     */
    public double getNormalx()
    {
        return this.normalX;
    }

    /**
     * @return returns the normalY
     */
    public double getNormaly()
    {
        return this.normalY;
    }
}
