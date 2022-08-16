package nl.tudelft.simulation.language.d2;

/**
 * The Angle class presents a number of mathematical utility functions on the angle. For now, the class only implements static
 * helper methods. No instances of the class should be made now.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class Angle
{
    /**
     * constructs a new Angle.
     */
    private Angle()
    {
        // unreachable code
    }

    /**
     * Normalize an angle between 0 and 2*pi.
     * @param angle double; the angle to normalize
     * @return normalized angle
     */
    public static double normalize2Pi(final double angle)
    {
        double result = angle + 2.0d * Math.PI;
        double times = Math.floor(result / (2.0d * Math.PI));
        result -= times * 2.0d * Math.PI;
        return result;
    }

    /**
     * Normalize an angle between -pi and +pi.
     * @param angle double; the angle to normalize
     * @return normalized angle
     */
    public static double normalizePi(final double angle)
    {
        double result = angle + 2.0d * Math.PI;
        double times = Math.floor((result + Math.PI) / (2.0d * Math.PI));
        result -= times * 2.0d * Math.PI;
        return result;
    }
}
