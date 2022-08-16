package nl.tudelft.simulation.dsol.animation.interpolation;

import org.djutils.draw.point.OrientedPoint3d;

/**
 * A LinearInterpolation.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class LinearInterpolation implements InterpolationInterface
{
    /** the start time. */
    private double startTime = Double.NaN;

    /** the end time. */
    private double endTime = Double.NaN;

    /**
     * the origin.
     */
    private OrientedPoint3d origin = null;

    /**
     * the destination.
     */
    private OrientedPoint3d destination = null;

    /**
     * constructs a new LinearInterpolation.
     * @param startTime double; the startingTime
     * @param endTime double; the endTime
     * @param origin OrientedPoint3d; the origin
     * @param destination OrientedPoint3d; the destination
     */
    public LinearInterpolation(final double startTime, final double endTime, final OrientedPoint3d origin,
            final OrientedPoint3d destination)
    {
        super();
        if (endTime < startTime)
        {
            throw new IllegalArgumentException("endTime < startTime");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.origin = origin; // immutable
        this.destination = destination; // immutable
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation(final double time)
    {
        if (time <= this.startTime)
        {
            return this.origin;
        }
        if (time >= this.endTime)
        {
            return this.destination;
        }
        double fraction = (time - this.startTime) / (this.endTime - this.startTime);
        double x = this.origin.getX() + (this.destination.getX() - this.origin.getX()) * fraction;
        double y = this.origin.getY() + (this.destination.getY() - this.origin.getY()) * fraction;
        double z = this.origin.getZ() + (this.destination.getZ() - this.origin.getZ()) * fraction;
        double rotY = this.origin.getDirY() + (this.destination.getDirY() - this.origin.getDirY()) * fraction;
        double rotZ = this.origin.getDirZ() + (this.destination.getDirZ() - this.origin.getDirZ()) * fraction;
        double rotX = this.origin.getDirX() + (this.destination.getDirX() - this.origin.getDirX()) * fraction;
        return new OrientedPoint3d(x, y, z, rotX, rotY, rotZ);
    }
}
