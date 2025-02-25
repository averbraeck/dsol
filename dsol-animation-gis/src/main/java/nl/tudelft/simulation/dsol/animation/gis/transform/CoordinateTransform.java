package nl.tudelft.simulation.dsol.animation.gis.transform;

import nl.tudelft.simulation.dsol.animation.gis.DoubleXY;
import nl.tudelft.simulation.dsol.animation.gis.FloatXY;

/**
 * Transforms an (x, y) coordinate to a new (x', y') coordinate.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public interface CoordinateTransform
{
    /**
     * transform the (x, y) coordinate to a new (x', y') coordinate represented as a FloatXY record.
     * @param x double; the original x-coordinate, e.g. lon in dd (double degrees)
     * @param y double; the original y-coordinate, e.g. lat in dd (double degrees)
     * @return the new (x', y') coordinate represented as a float[2]
     */
    FloatXY floatTransform(float x, float y);

    /**
     * transform the (x, y) coordinate to a new (x', y') coordinate represented as a FloatXY record.
     * @param x double; the original x-coordinate, e.g. lon in dd (double degrees)
     * @param y double; the original y-coordinate, e.g. lat in dd (double degrees)
     * @return the new (x', y') coordinate represented as a float[2]
     */
    default FloatXY floatTransform(final double x, final double y)
    {
        return floatTransform((float) x, (float) y);
    }

    /**
     * transform the (x, y) coordinate to a new (x', y') coordinate represented as a DoubleXY record.
     * @param x double; the original x-coordinate, e.g. lon in dd (double degrees)
     * @param y double; the original y-coordinate, e.g. lat in dd (double degrees)
     * @return the new (x', y') coordinate represented as a double[2]
     */
    DoubleXY doubleTransform(double x, double y);

    /**
     * The identical transformation (x,y) =&gt; (x,y).
     */
    class NoTransform implements CoordinateTransform
    {

        @Override
        public FloatXY floatTransform(final float x, final float y)
        {
            return new FloatXY(x, y);
        }

        @Override
        public DoubleXY doubleTransform(final double x, final double y)
        {
            return new DoubleXY(x, y);
        }

    }
}
