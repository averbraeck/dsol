package nl.tudelft.simulation.dsol.web.test.ball;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.web.DsolWebServer;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class BallWebApplication extends DsolWebServer
{
   /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param title String; the tile for the model
     * @param simulator DevsRealTimeClock&lt;Double&gt;; the simulator
     * @throws Exception on jetty error
     */
    public BallWebApplication(final String title, final DevsRealTimeAnimator.TimeDouble simulator) throws Exception
    {
        super(title, simulator, new Bounds2d(-100, 100, -100, 100));
    }

    /**
     * @param args String[]; arguments, expected to be empty
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("BallWebApplication", 0.01);
        BallModel model = new BallModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);
        new BallWebApplication("Ball Animation model", simulator);
    }

}
