package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;

/**
 * OutputPortInterface class. Describes the contract for an output port of the Classic Parallel DEVS Atomic Model with Ports
 * conform Zeigler et al (2000), section 4.2.2. and section 4.3 (pp. 84 ff).
 * <p>
 * Copyright (c) 2009-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 * @param <TYPE> The type of messages the input port accepts.
 */
public interface OutputPortInterface<T extends Number & Comparable<T>, TYPE>
{

    /**
     * Send a message through the output port.
     * @param value TYPE; the value to transfer.
     * @throws SimRuntimeException a simulation runtime exception
     * @throws RemoteException a remote exception
     */
    void send(TYPE value) throws SimRuntimeException, RemoteException;

    /**
     * @return the model to which the port belongs.
     */
    AbstractDevsModel<T> getModel();

}
