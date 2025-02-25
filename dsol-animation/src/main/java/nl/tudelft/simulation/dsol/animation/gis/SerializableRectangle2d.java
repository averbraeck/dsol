package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The SerializableRectangle2d class is a serializable version of the <code>java.awt.geom.Rectangle2D</code> class, yet in an
 * immutable form.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SerializableRectangle2d extends Rectangle2D implements Serializable
{
    /** */
    private static final long serialVersionUID = 20201015L;

    /**
     * constructs a new SerializableRectangle2d.
     */
    protected SerializableRectangle2d()
    {
        super();
    }

    /**
     * The SerializableRectangle2d.Double class is a serializable version of the <code>java.awt.geom.Rectangle2D.Double</code>
     * class.
     */
    public static class Double extends SerializableRectangle2d implements Serializable
    {
        /** */
        private static final long serialVersionUID = 20201015L;

        /** the rectangle. */
        private Rectangle2D.Double rectangle;

        /**
         * constructs a new SerializableRectangle2D.Double.
         */
        public Double()
        {
            this.rectangle = new Rectangle2D.Double();
        }

        /**
         * constructs a new SerializableRectangle2d.Double.
         * @param x double; lower x
         * @param y double; lower y
         * @param w double; width
         * @param h double; height
         */
        public Double(final double x, final double y, final double w, final double h)
        {
            this.rectangle = new Rectangle2D.Double(x, y, w, h);
        }

        @Override
        public Rectangle2D createIntersection(final Rectangle2D r)
        {
            return this.rectangle.createIntersection(r);
        }

        @Override
        public Rectangle2D createUnion(final Rectangle2D r)
        {
            return this.rectangle.createUnion(r);
        }

        @Override
        public Rectangle2D getBounds2D()
        {
            return this.rectangle.getBounds2D();
        }

        @Override
        public double getHeight()
        {
            return this.rectangle.getHeight();
        }

        @Override
        public double getWidth()
        {
            return this.rectangle.getWidth();
        }

        @Override
        public double getX()
        {
            return this.rectangle.getX();
        }

        @Override
        public double getY()
        {
            return this.rectangle.getY();
        }

        @Override
        public boolean isEmpty()
        {
            return this.rectangle.isEmpty();
        }

        @Override
        public int outcode(final double x, final double y)
        {
            return this.rectangle.outcode(x, y);
        }

        @Override
        public void setRect(final double x, final double y, final double w, final double h)
        {
            this.rectangle.setRect(x, y, w, h);
        }

        @Override
        public void setRect(final Rectangle2D r)
        {
            this.rectangle.setRect(r);
        }

        @Override
        public String toString()
        {
            return this.rectangle.toString();
        }

        /**
         * Serialize the object to the stream.
         * @param out ObjectOutputStream; the outputstream
         * @throws java.io.IOException on exception
         */
        private void writeObject(final ObjectOutputStream out) throws java.io.IOException
        {
            out.writeDouble(this.rectangle.getX());
            out.writeDouble(this.rectangle.getY());
            out.writeDouble(this.rectangle.getWidth());
            out.writeDouble(this.rectangle.getHeight());
        }

        /**
         * Read a serialized object from the stream.
         * @param in ObjectInputStream; the input
         * @throws IOException on exception
         */
        private void readObject(final ObjectInputStream in) throws IOException
        {
            this.rectangle = new Rectangle2D.Double(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
        }
    }

    /**
     * The SerializableRectangle2d.Float class is a serializable version of the <code>java.awt.geom.Rectangle2D.Double</code>
     * class.
     */
    public static class Float extends SerializableRectangle2d
    {

        /** the rectangle. */
        private Rectangle2D rectangle;

        /**
         * constructs a new SerializableRectangle2d.Float.
         */
        public Float()
        {
            this.rectangle = new Rectangle2D.Float();
        }

        /**
         * constructs a new SerializableRectangle2d.Float.
         * @param x float; the lower x
         * @param y float; the lower y
         * @param w float; the width
         * @param h float; the height
         */
        public Float(final float x, final float y, final float w, final float h)
        {
            this.rectangle = new Rectangle2D.Float(x, y, w, h);
        }

        @Override
        public Rectangle2D createIntersection(final Rectangle2D r)
        {
            return this.rectangle.createIntersection(r);
        }

        @Override
        public Rectangle2D createUnion(final Rectangle2D r)
        {
            return this.rectangle.createUnion(r);
        }

        @Override
        public Rectangle2D getBounds2D()
        {
            return this.rectangle.getBounds2D();
        }

        @Override
        public double getHeight()
        {
            return this.rectangle.getHeight();
        }

        @Override
        public double getWidth()
        {
            return this.rectangle.getWidth();
        }

        @Override
        public double getX()
        {
            return this.rectangle.getX();
        }

        @Override
        public double getY()
        {
            return this.rectangle.getY();
        }

        @Override
        public boolean isEmpty()
        {
            return this.rectangle.isEmpty();
        }

        @Override
        public int outcode(final double x, final double y)
        {
            return this.rectangle.outcode(x, y);
        }

        /**
         * @param x float; the lower x
         * @param y float; the lower y
         * @param w float; the width
         * @param h float; the height
         */
        public void setRect(final float x, final float y, final float w, final float h)
        {
            this.rectangle.setRect(x, y, w, h);
        }

        @Override
        public void setRect(final double x, final double y, final double w, final double h)
        {
            this.rectangle.setRect(x, y, w, h);
        }

        @Override
        public void setRect(final Rectangle2D r)
        {
            this.rectangle.setRect(r);
        }

        @Override
        public String toString()
        {
            return this.rectangle.toString();
        }

        /**
         * Serialize the object to the stream.
         * @param out ObjectOutputStream; the stream
         * @throws IOException on IOException
         */
        private void writeObject(final ObjectOutputStream out) throws IOException
        {
            out.writeDouble(this.rectangle.getX());
            out.writeDouble(this.rectangle.getY());
            out.writeDouble(this.rectangle.getWidth());
            out.writeDouble(this.rectangle.getHeight());
        }

        /**
         * Read a serialized object from the stream.
         * @param in ObjectInputStream; the stream
         * @throws IOException on IOException
         */
        private void readObject(final ObjectInputStream in) throws IOException
        {
            this.rectangle = new Rectangle2D.Float();
            this.rectangle.setRect(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
        }
    }
}
