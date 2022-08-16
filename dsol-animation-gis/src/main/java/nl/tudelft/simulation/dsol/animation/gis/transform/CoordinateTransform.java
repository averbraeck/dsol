package nl.tudelft.simulation.dsol.animation.gis.transform;

/**
 * Transforms an (x, y) coordinate to a new (x', y') coordinate.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface CoordinateTransform
{
    /**
     * transform the (x, y) coordinate to a new (x', y') coordinate represented as a float[2].
     * @param x double; the original x-coordinate, e.g. lon in dd (double degrees)
     * @param y double; the original y-coordinate, e.g. lat in dd (double degrees)
     * @return the new (x', y') coordinate represented as a float[2]
     */
    float[] floatTransform(double x, double y);

    /**
     * transform the (x, y) coordinate to a new (x', y') coordinate represented as a double[2].
     * @param x double; the original x-coordinate, e.g. lon in dd (double degrees)
     * @param y double; the original y-coordinate, e.g. lat in dd (double degrees)
     * @return the new (x', y') coordinate represented as a double[2]
     */
    double[] doubleTransform(double x, double y);

    /**
     * The identical transformation (x,y) =&gt; (x,y).
     */
    class NoTransform implements CoordinateTransform
    {

        /** {@inheritDoc} */
        @Override
        public float[] floatTransform(final double x, final double y)
        {
            return new float[] {(float) x, (float) y};
        }

        /** {@inheritDoc} */
        @Override
        public double[] doubleTransform(final double x, final double y)
        {
            return new double[] {x, y};
        }

    }
}
