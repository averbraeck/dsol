package nl.tudelft.simulation.dsol.web.test.gis;

import java.awt.Dimension;

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
public class GISWebApplication extends DsolWebServer
{
    /** */
    private static final long serialVersionUID = 1L;
    
    /** the default extent. */
    private static Bounds2d extent;

    static
    {
        double x1 = 115.637;
        double y1 = 39.247;
        double x2 = 117.099;
        double y2 = 40.408;
        extent = new Bounds2d(x1, x2, y1, y2);
    }

    /**
     * @param title the tile for the model
     * @param simulator the simulator
     * @throws Exception on jetty error
     */
    public GISWebApplication(final String title, final DevsRealTimeAnimator.TimeDouble simulator) throws Exception
    {
        super(title, simulator, extent);
        getAnimationPanel().setSize(new Dimension(800, 600));
    }

    /**
     * @param args arguments, expected to be empty
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("GISWebApplication", 0.01);
        GISModel model = new GISModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);
        new GISWebApplication("GIS Animation model", simulator);
    }

}
