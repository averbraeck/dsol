package nl.tudelft.simulation.language.d3;

import java.io.Serializable;

/**
 * A spherical point as defined in <a href="https://mathworld.wolfram.com/SphericalCoordinates.html">
 * https://mathworld.wolfram.com/SphericalCoordinates.html </a>. Also according to ISO 31-11
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class SphericalPoint implements Serializable
{
    /** */
    private static final long serialVersionUID = 20191116L;

    /** radius. */
    private double radius = 0.0;

    /** theta. */
    private double theta = 0.0;

    /** phi. */
    private double phi = 0.0;

    /**
     * constructs a new SphericalPoint.
     * @param radius radius
     * @param theta theta
     * @param phi phi
     */
    public SphericalPoint(final double radius, final double theta, final double phi)
    {
        super();
        this.phi = phi;
        this.radius = radius;
        this.theta = theta;
    }

    /**
     * @return phi
     */
    public double getPhi()
    {
        return this.phi;
    }

    /**
     * @return radius
     */
    public double getRadius()
    {
        return this.radius;
    }

    /**
     * @return theta
     */
    public double getTheta()
    {
        return this.theta;
    }

    /**
     * converts a spherical point to a Cartesian point.
     * @return the Cartesian point
     */
    public CartesianPoint toCartesianPoint()
    {
        return SphericalPoint.toCartesianPoint(this);
    }

    /**
     * converts a spherical point to a Cartesian point.
     * @param point the spherical point
     * @return the Cartesian point
     */
    public static CartesianPoint toCartesianPoint(final SphericalPoint point)
    {
        double x = point.radius * Math.sin(point.phi) * Math.cos(point.theta);
        double y = point.radius * Math.sin(point.phi) * Math.sin(point.theta);
        double z = point.radius * Math.cos(point.phi);
        return new CartesianPoint(x, y, z);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.phi);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.radius);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.theta);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    @SuppressWarnings({"checkstyle:designforextension", "checkstyle:needbraces"})
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SphericalPoint other = (SphericalPoint) obj;
        if (Double.doubleToLongBits(this.phi) != Double.doubleToLongBits(other.phi))
            return false;
        if (Double.doubleToLongBits(this.radius) != Double.doubleToLongBits(other.radius))
            return false;
        if (Double.doubleToLongBits(this.theta) != Double.doubleToLongBits(other.theta))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "SphericalPoint [radius=" + this.radius + ", phi=" + this.phi + ", theta=" + this.theta + "]";
    }

}
