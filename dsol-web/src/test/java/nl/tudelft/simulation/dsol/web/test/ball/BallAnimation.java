package nl.tudelft.simulation.dsol.web.test.ball;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.SimRenderable2d;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * The Animation of a Ball.
 * <p>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 * @since 1.4
 */
public class BallAnimation extends SimRenderable2d<Locatable>
{
    /** */
    private static final long serialVersionUID = 20220201L;
    
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
    public BallAnimation(final Locatable source, final SimulatorInterface<Double> simulator)
            throws RemoteException, NamingException
    {
        super(source, simulator);
    }

    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        graphics.setColor(this.color);
        graphics.fillOval(-(int) Ball.RADIUS, -(int) Ball.RADIUS, (int) (Ball.RADIUS * 2.0), (int) (Ball.RADIUS * 2.0));
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
        graphics.setColor(Color.GRAY);
        graphics.drawString(getSource().toString(), (float) (Ball.RADIUS * -1.0), (float) (Ball.RADIUS * 1.0));
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
