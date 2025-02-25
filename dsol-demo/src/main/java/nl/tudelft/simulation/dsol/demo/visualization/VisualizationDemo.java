package nl.tudelft.simulation.dsol.demo.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.StaticLocation2d;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2d;
import nl.tudelft.simulation.dsol.swing.animation.d2.AnimationUpdaterThread;
import nl.tudelft.simulation.dsol.swing.animation.d2.VisualizationPanel;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * VisualizationDemo shows how to use the VisualizationPanel to display a number of Locatable objects in a panel. <br>
 * <br>
 * Copyright (c) 2023-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class VisualizationDemo implements Contextualized
{
    /** the context to store the animation objects. */
    private final ContextInterface context;

    /**
     * Construct a new VisualizationDemo.
     * @throws RemoteException on remote error
     * @throws NamingException when context cannot be created
     */
    public VisualizationDemo() throws RemoteException, NamingException
    {
        ContextInterface rootContext = InitialEventContext.instantiate("root");
        this.context = ContextUtil.lookupOrCreateSubContext(rootContext, "visdemo");
        JFrame frame = new JFrame("VisualizationDemo");
        frame.setPreferredSize(new Dimension(800, 600));
        Bounds2d extent = new Bounds2d(0.0, 800.0, 0.0, 600.0);
        AnimationUpdaterThread updater = new AnimationUpdaterThread();
        VisualizationPanel panel = new VisualizationPanel(extent, updater, this.context);
        panel.setPreferredSize(new Dimension(800, 600));
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        new DrawObjectsThread(this).start();
        updater.start();
    }

    @Override
    public ContextInterface getContext()
    {
        return this.context;
    }

    /**
     * Main program.
     * @param args not used
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        new VisualizationDemo();
    }

    /** class to draw objects. */
    static class DrawObjectsThread extends Thread
    {
        /** context provider. */
        private final Contextualized contextProvider;

        /**
         * @param contextProvider Contextualized; the context provider
         */
        public DrawObjectsThread(final Contextualized contextProvider)
        {
            this.contextProvider = contextProvider;
        }

        @Override
        public void run()
        {
            StreamInterface stream = new MersenneTwister(111L);
            DistContinuous xDist = new DistUniform(stream, 0, 800);
            DistContinuous yDist = new DistUniform(stream, 0, 600);
            List<DrawObject> drawObjects = new ArrayList<>();
            while (true)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException exception)
                {
                    // ignore
                }
                if (drawObjects.size() > 20)
                {
                    drawObjects.remove(1).destroy(this.contextProvider);
                }
                drawObjects.add(new DrawObject(this.contextProvider, xDist.draw(), yDist.draw()));
            }
        }
    }

    /** DrawObject to draw an object on the screen. */
    static class DrawObject extends Renderable2d<StaticLocation2d>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param contextProvider Contextualized; the context in which drawing objects will be placed
         * @param x double; x coordinate
         * @param y double; y coordinate
         */
        public DrawObject(final Contextualized contextProvider, final double x, final double y)
        {
            super(new StaticLocation2d(x, y, 0.0, new Bounds2d(10.0, 10.0)), contextProvider);
        }

        @Override
        public void paint(final Graphics2D graphics, final ImageObserver observer)
        {
            graphics.setColor(Color.BLUE);
            graphics.draw(new Rectangle2D.Double(0, 0, 10.0, 10.0));
        }
    }

}
