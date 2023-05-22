package nl.tudelft.simulation.examples.dsol.animation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 */
public class BallModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the balls for 'search'. */
    private List<Ball> ballList = new ArrayList<>();

    /**
     * constructs a new BallModel.
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     */
    public BallModel(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        for (int i = 1; i <= 10; i++)
        {
            try
            {
                this.ballList.add(new DiscreteBall(i, this.simulator));
            }
            catch (RemoteException exception)
            {
                getSimulator().getLogger().always().error(exception);
            }
        }
    }

    /**
     * Return the ball with number 'i', or null when it is above or below the available balls.
     * @param i int; the ball number to return
     * @return the ball with number 'i'
     */
    public Ball getBall(final int i)
    {
        try
        {
            return this.ballList.get(i);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

}
