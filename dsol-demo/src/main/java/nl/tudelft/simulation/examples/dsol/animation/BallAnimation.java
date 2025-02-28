package nl.tudelft.simulation.examples.dsol.animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.animation.SimRenderable2d;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * The Animation of a Ball.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 * @since 1.4
 */
public class BallAnimation extends SimRenderable2d<Ball>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * the color of the ballAnimation.
     */
    private Color color = Color.ORANGE;

    /**
     * constructs a new BallAnimation.
     * @param source Locatable; the source
     * @param simulator SimulatorInterface&lt;Double&gt;; the simulator
     * @throws NamingException on registration error
     * @throws RemoteException on remote animation error
     */
    public BallAnimation(final Ball source, final SimulatorInterface<Double> simulator)
            throws RemoteException, NamingException
    {
        super(source, simulator);
        setScaleObject(true);
        setScaleY(true);
        // even numbered balls are vertically scaled; odd numbered balls not. Balls 6-10 are twice as small.
//        int nr = Integer.parseInt(source.toString());
//        setScaleObject(nr > 5);
//        setScaleY(nr % 2 == 1);
    }

    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        graphics.setColor(this.color);
        graphics.fillOval(-(int) Ball.RADIUS, -(int) Ball.RADIUS, (int) (Ball.RADIUS * 2.0), (int) (Ball.RADIUS * 2.0));
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD).deriveFont(6.0f));
        graphics.setColor(Color.GRAY);
        if (Integer.parseInt(getSource().toString()) > 9)
            graphics.drawString(getSource().toString(), (float) (Ball.RADIUS * -0.8), (float) (Ball.RADIUS * 0.5));
        else
            graphics.drawString(getSource().toString(), (float) (Ball.RADIUS * -0.5), (float) (Ball.RADIUS * 0.5));
    }

    /**
     * @return Returns the color.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @param color Color; The color to set.
     */
    public void setColor(final Color color)
    {
        this.color = color;
    }
}
