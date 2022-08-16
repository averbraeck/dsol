package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The SerializablePath class is a serializable version of the <code>java.awt.geom.Path2D.Float</code> class.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SerializablePath extends Path2D.Float implements Serializable, Shape, Cloneable
{
    /**
     * Construct a default general path that can be serialized.
     */
    public SerializablePath()
    {
        super();
    }

    /**
     * constructs a new SerializablePath with a winding rule.
     * @param rule int; the windingRule
     */
    public SerializablePath(final int rule)
    {
        super(rule);
    }

    /**
     * constructs a new SerializablePath with a winding rule and a capacity.
     * @param rule int; the windingRule
     * @param initialCapacity int; the initial capacity
     */
    public SerializablePath(final int rule, final int initialCapacity)
    {
        super(rule, initialCapacity);
    }

    /**
     * constructs a new SerializablePath based on a shape.
     * @param s Shape; the shape
     */
    public SerializablePath(final Shape s)
    {
        super(s);
    }

    /**
     * writes a float array of points to a stream.
     * @param out ObjectOutputStream; the output stream
     * @param array float[]; the array
     * @param length int; the length
     * @throws IOException on exception
     */
    private void writeFloatArray(final ObjectOutputStream out, final float[] array, final int length) throws IOException
    {
        for (int i = 0; i < length; i++)
        {
            out.writeFloat(array[i]);
        }
    }

    /**
     * Serialized an object to the stream.
     * @param out ObjectOutputStream; the stream
     * @throws IOException on IO failure
     */
    private void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.writeInt(getWindingRule());
        float[] coords = new float[6];
        PathIterator i = getPathIterator(null);
        // Now the Path iterator is present, we simply walk along the shape and serialize the points.
        while (!i.isDone())
        {
            int segment = i.currentSegment(coords);
            out.writeInt(segment);
            switch (segment)
            {
                case PathIterator.SEG_CLOSE:
                    writeFloatArray(out, coords, 0);
                    // no float is serialized.. Keeps the bytestream as minimal as possible
                    break;
                case PathIterator.SEG_CUBICTO:
                    writeFloatArray(out, coords, 6);
                    // All 6 floats are used and therefore serialized.
                    break;
                case PathIterator.SEG_LINETO:
                    writeFloatArray(out, coords, 2);
                    // 2 floats are used and serialized. Keeps the bytestream as minimal as possible
                    break;
                case PathIterator.SEG_MOVETO:
                    writeFloatArray(out, coords, 2);
                    // 2 floats are used and serialized. Keeps the bytestream as minimal as possible
                    break;
                case PathIterator.SEG_QUADTO:
                    writeFloatArray(out, coords, 4);
                    // 2 floats are used and serialized.. Keeps the bytestream as minimal as possible
                    break;
                default:
                    throw new RuntimeException("unknown segment");
            }
            i.next();
        }
        out.writeInt(-1); // We are ready and give an end-signal
    }

    /**
     * Reads a serialized object from a stream.
     * @param in ObjectInputStream; the input stream
     * @throws IOException on IO Exception
     */
    private void readObject(final ObjectInputStream in) throws IOException
    {
        int segment;
        while ((segment = in.readInt()) != -1)
        // The -1 value was our ending point.
        {
            switch (segment)
            {
                case PathIterator.SEG_CLOSE:
                    closePath();
                    break;
                case PathIterator.SEG_CUBICTO:
                    curveTo(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
                    break;
                case PathIterator.SEG_LINETO:
                    lineTo(in.readFloat(), in.readFloat());
                    break;
                case PathIterator.SEG_MOVETO:
                    moveTo(in.readFloat(), in.readFloat());
                    break;
                case PathIterator.SEG_QUADTO:
                    quadTo(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
                    break;
                default:
                    throw new RuntimeException("unknown segment");
            }
        }
    }
}
