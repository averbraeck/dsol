package nl.tudelft.simulation.examples.dsol.terminal;

import java.rmi.RemoteException;

/**
 * This interface provides a callback method to the resource. Whenever resource is available this method is invoked on the
 * requestor..
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the simulation time type.
 * @since 1.5
 */
public interface IntResourceRequestorInterface<T extends Number & Comparable<T>>
{
    /**
     * receive the requested resource.
     * @param requestedCapacity long; reflects the amount requested.
     * @param resource IntResource&lt;T&gt;; the requested resource.
     * @throws RemoteException on network failure.
     */
    void receiveRequestedResource(long requestedCapacity, IntResource<T> resource) throws RemoteException;

    /**
     * receive the requested resource.
     * @param requestedCapacity int; reflects the amount requested.
     * @param resource IntResource&lt;T&gt;; the requested resource.
     * @throws RemoteException on network failure.
     */
    default void receiveRequestedResource(final int requestedCapacity, final IntResource<T> resource) throws RemoteException
    {
        receiveRequestedResource((long) requestedCapacity, resource);
    }
}
