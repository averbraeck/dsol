package nl.tudelft.simulation.dsol.animation;

import java.rmi.RemoteException;

import org.djutils.draw.Oriented;
import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point3d;

/**
 * The locatable interface enforces knowledge on position.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface Locatable
{
    /**
     * returns the location of an object. This value may be null, e.g., when the object is still being constructed or being
     * destroyed.
     * @return Point&lt;?&gt;; the location
     * @throws RemoteException on network failure
     */
    Point<?> getLocation() throws RemoteException;

    /**
     * Return the z-value of the location, or 0.0 when the location is in 2 dimensions.
     * @return double; the z-value of the location, or 0.0 when the location is in 2 dimensions, or when getLocation() returns
     *         null
     * @throws RemoteException on network failure
     */
    default double getZ() throws RemoteException
    {
        Point<?> p = getLocation();
        return p == null ? 0.0 : p instanceof Point3d ? ((Point3d) p).getZ() : 0.0;
    }

    /**
     * Return the z-direction of the location in radians, or 0.0 when the location has no direction.
     * @return double; the z-direction of the location in radians, or 0.0 when the location has no direction, or when
     *         getLocation() returns null
     * @throws RemoteException on network failure
     */
    default double getDirZ() throws RemoteException
    {
        Point<?> p = getLocation();
        return p == null ? 0.0 : p instanceof Oriented ? ((Oriented<?>) p).getDirZ() : 0.0;
    }

    /**
     * returns the bounds of the locatable object. The bounds is the not rotated bounds around [0;0;0]. In contrast to the
     * getLocation() method the getBounds() method should never return null.
     * @return Bounds&lt;?, ?, ?, ?&gt;; the bounds of the object around getLocation() as center of the box.
     * @throws RemoteException on network failure
     */
    Bounds<?, ?, ?> getBounds() throws RemoteException;
}
