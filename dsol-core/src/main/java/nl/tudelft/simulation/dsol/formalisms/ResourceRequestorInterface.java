package nl.tudelft.simulation.dsol.formalisms;

import java.rmi.RemoteException;

/**
 * This interface provides a callback method to the resource. Whenever resource is available this method is invoked on the
 * requestor.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the simulation time type.
 */
public interface ResourceRequestorInterface<T extends Number & Comparable<T>>
{
    /**
     * receive the requested resource.
     * @param requestedCapacity double; reflects the amount requested.
     * @param resource Resource&lt;T&gt;; the requested resource.
     * @throws RemoteException on network failure.
     */
    void receiveRequestedResource(double requestedCapacity, Resource<T> resource) throws RemoteException;
}
