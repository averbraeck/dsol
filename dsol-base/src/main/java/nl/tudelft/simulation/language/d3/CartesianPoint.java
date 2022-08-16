package nl.tudelft.simulation.language.d3;

import java.awt.geom.Point2D;

import org.djutils.draw.point.Point3d;

/**
 * The Point3D class with utilities to convert to point2D where the z-axis is neglected.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class CartesianPoint extends Point3d
{
    /** */
    private static final long serialVersionUID = 20191116L;

    /**
     * constructs a new CartesianPoint.
     * @param x double; x
     * @param y double; y
     * @param z double; z
     */
    public CartesianPoint(final double x, final double y, final double z)
    {
        super(x, y, z);
    }

    /**
     * constructs a new CartesianPoint.
     * @param xyz double[]; x,y,z
     */
    public CartesianPoint(final double[] xyz)
    {
        super(xyz);
    }

    /**
     * constructs a new CartesianPoint.
     * @param point2D Point2D; a 2D point
     */
    public CartesianPoint(final Point2D point2D)
    {
        this(point2D.getX(), point2D.getY(), 0);
    }

    /**
     * returns the 2D representation of the point.
     * @return Point2D the result
     */
    public Point2D to2D()
    {
        return new Point2D.Double(this.getX(), this.getY());
    }

    /**
     * converts the point to a sperical point.
     * @return the spherical point
     */
    public SphericalPoint toSphericalPoint()
    {
        return CartesianPoint.toSphericalPoint(this);
    }

    /**
     * converts a cartesian point to a sperical point. See https://mathworld.wolfram.com/SphericalCoordinates.html
     * @param point CartesianPoint; the cartesian point
     * @return the spherical point
     */
    public static SphericalPoint toSphericalPoint(final CartesianPoint point)
    {
        double rho = Math.sqrt(Math.pow(point.getX(), 2) + Math.pow(point.getY(), 2) + Math.pow(point.getZ(), 2));
        double theta = Math.atan2(point.getY(), point.getX());
        double phi = Math.acos(point.getZ() / rho);
        return new SphericalPoint(rho, theta, phi);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "CartesianPoint [x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "]";
    }

}
