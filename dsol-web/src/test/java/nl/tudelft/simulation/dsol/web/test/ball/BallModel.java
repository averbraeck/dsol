package nl.tudelft.simulation.dsol.web.test.ball;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 */
public class BallModel extends AbstractDsolModel<Double, DevsRealTimeAnimator.TimeDouble>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new BallModel.
     * @param simulator DevsRealTimeClock&lt;Double&gt;; the simulator
     */
    public BallModel(final DevsRealTimeAnimator.TimeDouble simulator)
    {
        super(simulator);
    }

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
