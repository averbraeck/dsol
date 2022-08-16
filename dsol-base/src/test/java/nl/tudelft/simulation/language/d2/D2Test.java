package nl.tudelft.simulation.language.d2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.djutils.draw.bounds.Bounds2d;
import org.junit.Test;

import nl.tudelft.simulation.language.d2.DirectionalLine.Side;

/**
 * D2Test.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class D2Test
{

    /**
     * Angle test.
     */
    @Test
    public void angleTest()
    {
        assertEquals(0.0, Angle.normalize2Pi(0.0), 0.001);
        assertEquals(0.0, Angle.normalizePi(0.0), 0.001);
        assertEquals(Math.PI / 2.0, Angle.normalize2Pi(Math.PI / 2.0), 0.001);
        assertEquals(Math.PI / 2.0, Angle.normalizePi(Math.PI / 2.0), 0.001);
        assertEquals(Math.PI, Angle.normalize2Pi(Math.PI), 0.001);
        assertTrue(Math.abs(Math.abs(Angle.normalizePi(Math.PI)) - Math.PI) < 0.001);
        assertEquals(1.5 * Math.PI, Angle.normalize2Pi(1.5 * Math.PI), 0.001);
        assertEquals(-0.5 * Math.PI, Angle.normalizePi(1.5 * Math.PI), 0.001);
        assertEquals(0.0, Angle.normalize2Pi(2.0 * Math.PI), 0.001);
        assertEquals(0.0 * Math.PI, Angle.normalizePi(2.0 * Math.PI), 0.001);
        assertEquals(1.5 * Math.PI, Angle.normalize2Pi(-0.5 * Math.PI), 0.001);
        assertEquals(-0.5 * Math.PI, Angle.normalizePi(-0.5 * Math.PI), 0.001);
    }

    /**
     * Circle test.
     */
    @Test
    public void circleTest()
    {
        Point2D p00 = new Point2D.Double(0.0, 0.0);
        Point2D p20 = new Point2D.Double(2.0, 0.0);

        Point2D[] intersect = Circle.intersection(p00, 1.0, p00, 2.0);
        assertEquals(0, intersect.length);

        intersect = Circle.intersection(p00, 1.0, p20, 1.0);
        assertEquals(1, intersect.length);
        assertEquals(1.0, intersect[0].getX(), 0.001);
        assertEquals(0.0, intersect[0].getY(), 0.001);

        intersect = Circle.intersection(p00, 2.0, p20, 2.0);
        assertEquals(2, intersect.length);
        int up = intersect[0].getY() > 0 ? 0 : 1;
        assertEquals(1.0, intersect[up].getX(), 0.001);
        assertEquals(Math.sqrt(3.0), intersect[up].getY(), 0.001);
        assertEquals(1.0, intersect[1 - up].getX(), 0.001);
        assertEquals(-Math.sqrt(3.0), intersect[1 - up].getY(), 0.001);
    }

    /**
     * DirectionalLine test.
     */
    @Test
    public void directionalLineTest()
    {
        DirectionalLine l0010 = new DirectionalLine(0.0, 0.0, 1.0, 0.0);
        assertEquals(0.0, l0010.x1, 0.001);
        assertEquals(0.0, l0010.y1, 0.001);
        assertEquals(1.0, l0010.x2, 0.001);
        assertEquals(0.0, l0010.y2, 0.001);
        float f0 = 0.0f;
        float f1 = 1.0f;
        DirectionalLine l0010a = new DirectionalLine(f0, f0, f1, f0);
        DirectionalLine l0010b = new DirectionalLine(1.0, 0.0, 0.0, 0.0);
        assertTrue(l0010.equalsCoordinates(l0010a));
        assertFalse(l0010.equalsCoordinates(l0010b));
        assertTrue(l0010.equalsCoordinatesIgnoreOrder(l0010b));
        l0010b.flip();
        assertTrue(l0010.equalsCoordinates(l0010a));
        assertTrue(l0010.equalsCoordinates(l0010b));
        assertTrue(l0010.equalsCoordinatesIgnoreOrder(l0010b));
        assertEquals(0.0, l0010.getNormalx(), 0.001);
        assertEquals(1.0, l0010.getNormaly(), 0.001);
        DirectionalLine l0022 = new DirectionalLine(0.0, 0.0, 2.0, 2.0);
        assertFalse(l0010.equalsCoordinates(l0022));
        assertEquals(-2.0, l0022.getNormalx(), 0.001);
        assertEquals(2.0, l0022.getNormaly(), 0.001);
        l0022.normalize();
        assertEquals(-0.5 * Math.sqrt(2.0), l0022.getNormalx(), 0.001);
        assertEquals(0.5 * Math.sqrt(2.0), l0022.getNormaly(), 0.001);
        assertEquals("(0.0,0.0)->(1.0,0.0)", l0010.toString());
        assertEquals(1.0, l0022.getLineThickness(), 0.001);
        l0022.setLineThickness(0.1);
        assertEquals(0.1, l0022.getLineThickness(), 0.001);
        DirectionalLine l0111 = new DirectionalLine(0.0, 1.0, 1.0, 1.0);
        assertEquals(-1.0, l0010.getIntersection(l0111), 0.001);
        assertEquals(-1.0, l0010.getIntersection(l0010), 0.001);
        DirectionalLine l2002 = new DirectionalLine(2.0, 0.0, 0.0, 2.0);
        assertEquals(0.5, l0022.getIntersection(l2002), 0.001);
        Point2D intersection = l0022.getIntersectionPoint(l2002);
        assertEquals(1.0, intersection.getX(), 0.001);
        assertEquals(1.0, intersection.getY(), 0.001);

        assertEquals(Side.COLLINEAR, l0010.getSideThick(0.5, 0.0));
        assertEquals(Side.COLLINEAR, l0010.getSideThin(0.5, 0.0));
        assertEquals(Side.COLLINEAR, l0010.getSideThick(0.5, 0.01));
        assertEquals(Side.COLLINEAR, l0010.getSideThick(0.5, -0.01));
        assertNotEquals(Side.COLLINEAR, l0010.getSideThin(0.5, 0.01));
        assertNotEquals(Side.COLLINEAR, l0010.getSideThin(0.5, -0.01));
        assertNotEquals(Side.COLLINEAR, l0010.getSideThick(0.5, 5.0));
        assertNotEquals(Side.COLLINEAR, l0010.getSideThick(0.5, -5.0));
        assertEquals(Side.FRONTSIDE, l0010.getSideThick(0.5, 5.0));
        assertEquals(Side.BACKSIDE, l0010.getSideThick(0.5, -5.0));
        assertEquals(Side.FRONTSIDE, l0010.getSideThin(0.5, 5.0));
        assertEquals(Side.BACKSIDE, l0010.getSideThin(0.5, -5.0));

        assertEquals(Side.COLLINEAR, l0010.getSide(l0010));
        assertEquals(Side.COLLINEAR, l0010.getSide(l0010b));
        assertEquals(Side.FRONTSIDE, l0010.getSide(new DirectionalLine(0.0, 1.0, 1.0, 1.0)));
        assertEquals(Side.BACKSIDE, l0010.getSide(new DirectionalLine(0.0, -1.0, 1.0, -1.0)));
        assertEquals(Side.SPANNING, l0010.getSide(new DirectionalLine(0.5, -1.0, 0.5, 1.0)));
        assertEquals(Side.FRONTSIDE, l0010.getSide(new DirectionalLine(0.5, 1.0, 0.5, 0.0)));
        assertEquals(Side.BACKSIDE, l0010.getSide(new DirectionalLine(0.5, -1.0, 0.5, 0.0)));
        assertEquals(Side.FRONTSIDE, l0010.getSide(new DirectionalLine(0.5, 0.0, 0.5, 1.0)));
        assertEquals(Side.BACKSIDE, l0010.getSide(new DirectionalLine(0.5, 0.0, 0.5, -1.0)));
        
        assertNotEquals(0, l0010.getSideThick(0.5, -5.0).getValue());
        assertEquals(1, l0010.getSideThick(0.5, 5.0).getValue());
        assertEquals(-1, l0010.getSideThick(0.5, -5.0).getValue());

    }

    /**
     * Shape test (screen coordinates).
     */
    @Test
    public void shapeTest()
    {
        Rectangle2D r0011 = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
        Rectangle2D r1122 = new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0);
        Rectangle2D r0033 = new Rectangle2D.Double(0.0, 0.0, 3.0, 3.0);
        assertTrue(Shape.contains(r1122, r0033));
        assertFalse(Shape.contains(r0033, r1122));
        assertTrue(Shape.contains(r0011, r0033));
        assertFalse(Shape.contains(r0033, r0011));
        assertFalse(Shape.intersects(r1122, r0033));
        assertFalse(Shape.intersects(r0033, r1122));
        assertTrue(Shape.overlaps(r1122, r0033));
        assertTrue(Shape.overlaps(r0033, r1122));
        assertTrue(Shape.overlaps(r0011, r1122)); // one point in common...
        assertTrue(Shape.intersects(r0011, r1122)); // one point in common...
        Rectangle2D r4455 = new Rectangle2D.Double(4.0, 4.0, 1.0, 1.0);
        assertFalse(Shape.overlaps(r0011, r4455));
        assertFalse(Shape.contains(r0011, r4455));
        assertFalse(Shape.intersects(r0011, r4455));
        Rectangle2D r2244 = new Rectangle2D.Double(2.0, 2.0, 2.0, 2.0);
        assertTrue(Shape.overlaps(r2244, r0033));
        assertFalse(Shape.contains(r2244, r0033));
        assertFalse(Shape.contains(r0033, r2244));
        assertTrue(Shape.intersects(r2244, r0033));
    }
    
    /**
     * Shape2d test (world coordinates).
     */
    @Test
    public void shape2dTest()
    {
        Bounds2d r0011 = new Bounds2d(0.0, 1.0, 0.0, 1.0);
        Bounds2d r1122 = new Bounds2d(1.0, 2.0, 1.0, 2.0);
        Bounds2d r0033 = new Bounds2d(0.0, 3.0, 0.0, 3.0);
        assertTrue(Shape2d.contains(r1122, r0033));
        assertFalse(Shape2d.contains(r0033, r1122));
        assertTrue(Shape2d.contains(r0011, r0033));
        assertFalse(Shape2d.contains(r0033, r0011));
        assertFalse(Shape2d.intersects(r1122, r0033));
        assertFalse(Shape2d.intersects(r0033, r1122));
        assertTrue(Shape2d.overlaps(r1122, r0033));
        assertTrue(Shape2d.overlaps(r0033, r1122));
        assertTrue(Shape2d.overlaps(r0011, r1122)); // one point in common...
        assertTrue(Shape2d.intersects(r0011, r1122)); // one point in common...
        Bounds2d r4455 = new Bounds2d(4.0, 5.0, 4.0, 5.0);
        assertFalse(Shape2d.overlaps(r0011, r4455));
        assertFalse(Shape2d.contains(r0011, r4455));
        assertFalse(Shape2d.intersects(r0011, r4455));
        Bounds2d r2244 = new Bounds2d(2.0, 4.0, 2.0, 4.0);
        assertTrue(Shape2d.overlaps(r2244, r0033));
        assertFalse(Shape2d.contains(r2244, r0033));
        assertFalse(Shape2d.contains(r0033, r2244));
        assertTrue(Shape2d.intersects(r2244, r0033));
    }
}
