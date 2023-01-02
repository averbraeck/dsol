package nl.tudelft.simulation.dsol.web.test.ball;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeAnimator;

/**
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 */
public class BallModel extends AbstractDSOLModel<Double, DEVSRealTimeAnimator.TimeDouble>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new BallModel.
     * @param simulator DEVSRealTimeClock&lt;Double&gt;; the simulator
     */
    public BallModel(final DEVSRealTimeAnimator.TimeDouble simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        for (int i = 0; i < 10; i++)
        {
            try
            {
                new DiscreteBall(this.simulator, i);
            }
            catch (RemoteException exception)
            {
                this.simulator.getLogger().always().error(exception);
            }
        }
    }
}
