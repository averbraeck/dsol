package nl.tudelft.simulation.examples.dsol.animation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.DirectedPoint2d;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 */
public class BallModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
{
    /** the balls for 'search'. */
    private List<Ball> ballList = new ArrayList<>();

    /**
     * constructs a new BallModel.
     * @param simulator the simulator
     */
    public BallModel(final DevsSimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        makeAnimation();
        for (int i = 1; i <= 10; i++)
        {
            this.ballList.add(new DiscreteBall(i, this.simulator));
        }
    }

    /**
     * Make the animation.
     */
    protected void makeAnimation()
    {
        Wall wallL = new Wall("L", new Bounds2d(-104, -100, -104, 104), Color.BLUE);
        Wall wallR = new Wall("R", new Bounds2d(208, 4), Color.BLUE, new DirectedPoint2d(102, 0, Math.PI / 2.0));
        Wall wallB = new Wall("B", new Bounds2d(-104, 104, -104, -100), Color.BLUE);
        Wall wallT = new Wall("T", new Bounds2d(4, 208), Color.BLUE, new DirectedPoint2d(0, 102, Math.PI / 2.0));
        new WallAnimation(wallL, getSimulator());
        new WallAnimation(wallR, getSimulator());
        new WallAnimation(wallB, getSimulator());
        new WallAnimation(wallT, getSimulator());
    }

    /**
     * Return the ball with number 'i', or null when it is above or below the available balls.
     * @param i the ball number to return
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
