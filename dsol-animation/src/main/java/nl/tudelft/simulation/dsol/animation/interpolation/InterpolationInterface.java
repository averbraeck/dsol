package nl.tudelft.simulation.dsol.animation.interpolation;

import java.rmi.RemoteException;

import org.djutils.draw.point.OrientedPoint3d;

/**
 * A InterpolationInterface.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public interface InterpolationInterface
{
    /**
     * returns the current location.
     * @param time double; the current time
     * @return OrientedPoint3d the current location
     * @throws RemoteException on network failure
     */
    OrientedPoint3d getLocation(double time) throws RemoteException;
}
