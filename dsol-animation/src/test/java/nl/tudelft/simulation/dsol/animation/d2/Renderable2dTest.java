package nl.tudelft.simulation.dsol.animation.d2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.draw.point.Point3d;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.Contextualized;
import nl.tudelft.simulation.naming.context.event.InitialEventContext;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * Unit tests for the Renderable2d class.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Renderable2dTest
{
    /**
     * Test the flags for a Rendeable2D.
     * @throws NamingException when context cannot be created or found
     * @throws RemoteException when context is remote and cannot be reached
     */
    @Test
    public void testFlags() throws RemoteException, NamingException
    {
        LocatableAngle locatable = new LocatableAngle();
        ContextProvider contextProvider = new ContextProvider();
        Renderable2d<Locatable> renderable = new Renderable2d<Locatable>(locatable, contextProvider)
        {
            /** */
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(final Graphics2D graphics, final ImageObserver observer)
            {
                // placeholder
            }
        };
        assertEquals(locatable, renderable.getSource());
        assertTrue(renderable.getId() > 0); // 0 means not incremented
        assertTrue(renderable.toString().contains("Renderable2d"));

        BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = img.createGraphics();
        Bounds2d extent = new Bounds2d(0, 200, 0, 100);
        Dimension screenSize = new Dimension(200, 100);
        RenderableScale renderableScale = new RenderableScale();

        for (boolean translate : new boolean[] {true, false})
        {
            renderable.setTranslate(translate);
            for (boolean flip : new boolean[] {true, false})
            {
                renderable.setFlip(flip);
                for (boolean rotate : new boolean[] {true, false})
                {
                    renderable.setRotate(rotate);
                    for (boolean scale : new boolean[] {true, false})
                    {
                        renderable.setScale(scale);
                        for (boolean scaleY : new boolean[] {true, false})
                        {
                            renderable.setScaleY(scaleY);
                            for (boolean scaleObject : new boolean[] {true, false})
                            {
                                renderable.setScaleObject(scaleObject);
                                assertEquals(translate, renderable.isTranslate());
                                assertEquals(flip, renderable.isFlip());
                                assertEquals(rotate, renderable.isRotate());
                                assertEquals(scale, renderable.isScale());
                                assertEquals(scaleY, renderable.isScaleY());
                                assertEquals(scaleObject, renderable.isScaleObject());

                                for (double angle : new double[] {0.0, Math.PI / 4.0, 1.5 * Math.PI})
                                {
                                    // test if no exceptions happen when we paint
                                    // if an exception occurred, the transform will not have been reset
                                    AffineTransform transform = graphics.getTransform();
                                    locatable.setAngle(angle);
                                    renderable.paintComponent(graphics, extent, screenSize, renderableScale, null);
                                    assertEquals(transform, graphics.getTransform());
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(renderable.contains(new Point2d(10, 20), new Bounds2d(0, 40, 0, 80)));
        assertFalse(renderable.contains(new Point2d(5, 10), new Bounds2d(0, 40, 0, 80)));
        renderable.contains(new Point2d(10, 20), new Bounds2d(0, 5, 0, 10));
        renderable.destroy(contextProvider);
    }

    /** Locatable with a settable dirZ. */
    class LocatableAngle implements Locatable
    {
        /** the angle. */
        private double angle;

        @Override
        public Point<?> getLocation() throws RemoteException
        {
            return new OrientedPoint3d(10, 20, 0, 0, 0, this.angle);
        }

        @Override
        public Bounds<?, ?> getRelativeBounds() throws RemoteException
        {
            return new Bounds3d(4, 4, 4);
        }

        /**
         * Set the angle.
         * @param angle the new angle
         */
        public void setAngle(final double angle)
        {
            this.angle = angle;
        }
    }

    /**
     * @return a Locatable3d.
     */
    Locatable createLocatable3dFakeZ()
    {
        return new Locatable()
        {
            @Override
            public Point<?> getLocation() throws RemoteException
            {
                return new Point3d(10, 20, 10);
            }

            @Override
            public Bounds<?, ?> getRelativeBounds() throws RemoteException
            {
                return new Bounds3d(4, 4, 4);
            }

            @Override
            public double getZ() throws RemoteException
            {
                return -10.0;
            }
        };
    }

    /** */
    static class ContextProvider implements Contextualized
    {
        /** the context. */
        private ContextInterface context;

        /**
         * Create the InitialContext for the provider.
         * @throws NamingException when context cannot be created or found
         * @throws RemoteException when context is remote and cannot be reached
         */
        ContextProvider() throws RemoteException, NamingException
        {
            ContextInterface rootContext = InitialEventContext.instantiate("root");
            this.context = ContextUtil.lookupOrCreateSubContext(rootContext, "testRenderable");
        }

        @Override
        public ContextInterface getContext()
        {
            return this.context;
        }
    }
}
