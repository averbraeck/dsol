package nl.tudelft.simulation.language.d3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.Test;

/**
 * PointTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class PointTest
{
    /**
     * Test the CartesianPoint and the SphericalPoint.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testCartesianSphericalPoint()
    {
        CartesianPoint cp = new CartesianPoint(1.0, 2.0, 3.0);
        assertTrue(cp.equals(cp));
        assertEquals(1.0, cp.to2D().getX(), 0.001);
        assertEquals(2.0, cp.to2D().getY(), 0.001);
        cp = new CartesianPoint(1, 1, 1);
        SphericalPoint sp = cp.toSphericalPoint();
        assertEquals(Math.sqrt(3), sp.getRadius(), 0.001);
        // theta = arctan y/x
        assertEquals(Math.atan(1.0), sp.getTheta(), 0.001);
        // phi = arccos z/r
        assertEquals(Math.acos(1.0 / Math.sqrt(3)), sp.getPhi(), 0.001);
        assertTrue(sp.equals(sp));
        assertFalse(sp.equals(cp));
        assertFalse(cp.equals(sp));
        CartesianPoint cp1 = cp.toSphericalPoint().toCartesianPoint();
        assertEquals(cp.getX(), cp1.getX(), 0.001);
        assertEquals(cp.getY(), cp1.getY(), 0.001);
        assertEquals(cp.getZ(), cp1.getZ(), 0.001);
        SphericalPoint sp1 = sp.toCartesianPoint().toSphericalPoint();
        assertEquals(sp.getRadius(), sp1.getRadius(), 0.001);
        assertEquals(sp.getTheta(), sp1.getTheta(), 0.001);
        assertEquals(sp.getPhi(), sp1.getPhi(), 0.001);
        sp = new SphericalPoint(2.5, 0.2, 1.2);
        assertTrue(sp.equals(new SphericalPoint(2.5, 0.2, 1.2)));
        assertEquals(sp.hashCode(), new SphericalPoint(2.5, 0.2, 1.2).hashCode());
        assertFalse(sp.equals(new SphericalPoint(2.0, 0.2, 1.2)));
        assertFalse(sp.equals(new SphericalPoint(2.5, 0.25, 1.2)));
        assertFalse(sp.equals(new SphericalPoint(2.5, 0.2, 1.25)));
        assertFalse(sp.equals(null));
        assertTrue(sp.toString().contains("radius=2.5"));
        assertTrue(sp.toString().contains("theta=0.2"));
        assertTrue(sp.toString().contains("phi=1.2"));
        
        cp = new CartesianPoint(1.0, 2.0, 3.0);
        CartesianPoint cp2 = new CartesianPoint(new double[] {1.0, 2.0, 3.0});
        assertEquals(cp, cp2);
        cp2 = new CartesianPoint(new Point2D.Double(2.0, 4.0));
        assertEquals(2.0, cp2.getX(), 0.001);
        assertEquals(4.0, cp2.getY(), 0.001);
        assertEquals(0.0, cp2.getZ(), 0.001);
        String s = cp2.toString();
        assertTrue(s.contains("x=2.0"));
        assertTrue(s.contains("y=4.0"));
        assertTrue(s.contains("z=0.0"));
    }
}
