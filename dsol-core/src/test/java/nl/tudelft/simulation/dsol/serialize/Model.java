package nl.tudelft.simulation.dsol.serialize;

import java.rmi.MarshalledObject;
import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The histogram specifies a histogram chart for the DSOL framework.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class Model extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Model.
     * @param simulator the simulator
     */
    public Model(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        getSimulator().scheduleEventAbs(10.0, this, "pause", null);
    }

    /**
     * pauses the model.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     */
    protected void pause() throws SimRuntimeException, RemoteException
    {
        getSimulator().stop();
        try
        {
            @SuppressWarnings({"rawtypes", "unchecked"})
            MarshalledObject serializedModel = new MarshalledObject(this);
            Model mySelf = (Model) serializedModel.get();
            mySelf.getSimulator().start();
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "pause");
        }
    }
}
