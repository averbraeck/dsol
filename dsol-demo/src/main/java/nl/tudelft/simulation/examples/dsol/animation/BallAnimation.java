package nl.tudelft.simulation.examples.dsol.animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.SimRenderable2d;
import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
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
     * @param source the source
     * @param simulator the simulator
     * @throws NamingException on registration error
     * @throws RemoteException on remote animation error
     */
    public BallAnimation(final Ball source, final SimulatorInterface<Double> simulator) throws RemoteException, NamingException
    {
        super(source, simulator);
        setScaleObject(true);
        setScaleY(true);
        setRotate(true);
        new BallTextAnimation(source, simulator);
    }

    @Override
    public boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale scale, final double worldMargin, final double pixelMargin)
    {
        return super.contains(pointScreenCoordinates, extent, screenSize, scale, 0.0, 4.0);
    }

    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        graphics.setColor(this.color);
        graphics.fillRect(-(int) Ball.RADIUS, -(int) Ball.RADIUS, (int) (Ball.RADIUS * 2.0), (int) (Ball.RADIUS * 2.0));
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD).deriveFont(6.0f));
    }

    /**
     * @return Returns the color.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @param color The color to set.
     */
    public void setColor(final Color color)
    {
        this.color = color;
    }

    /** Separately animating text without a ScaleY. */
    public class BallTextAnimation extends SimRenderable2d<Ball>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * constructs a new BallTextAnimation, possibly without a ScaleY.
         * @param source the source
         * @param simulator the simulator
         * @throws NamingException on registration error
         * @throws RemoteException on remote animation error
         */
        public BallTextAnimation(final Ball source, final SimulatorInterface<Double> simulator)
                throws RemoteException, NamingException
        {
            super(source, simulator);
            setScaleObject(true);
            setScaleY(false);
            setRotate(false);
        }

        @Override
        public boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
                final RenderableScale scale, final double worldMargin, final double pixelMargin)
        {
            return false;
        }

        @Override
        public void paint(final Graphics2D graphics, final ImageObserver observer)
        {
            graphics.setColor(Color.GRAY);
            if (Integer.parseInt(getSource().toString()) > 9)
                graphics.drawString(getSource().toString(), (float) (Ball.RADIUS * -0.8), (float) (Ball.RADIUS * 0.5));
            else
                graphics.drawString(getSource().toString(), (float) (Ball.RADIUS * -0.5), (float) (Ball.RADIUS * 0.5));
        }

    }
}
