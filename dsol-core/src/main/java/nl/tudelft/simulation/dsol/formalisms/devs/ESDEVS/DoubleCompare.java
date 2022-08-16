package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS;

/**
 * DoubleCompare class. Compares two doubles except for the last two bits of the mantissa.
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 */
public final class DoubleCompare
{
    /**
     * Constructor that should not be used.
     */
    private DoubleCompare()
    {
        // unreachable code
    }

    /**
     * Fuzzy compare of two double variables. When they differ less than 4 ulps, that they are equal.
     * @param d1 double; the first double to compare
     * @param d2 double; the second double to compare
     * @return 0 if (almost) equal, -1 of d1 &lt; d2, and 1 if d1 &gt; d2.
     */
    @SuppressWarnings("checkstyle:needbraces")
    public static int compare(final double d1, final double d2)
    {
        double diff = d1 - d2;
        if (Math.abs(diff) == 0.0) // catches -0.0 and 0.0
            return 0;

        // From
        // long thisBits = Double.doubleToLongBits(d1); long anotherBits = Double.doubleToLongBits(d2);
        // thisBits == anotherBits => 0 : Values are equal
        // thisBits < anotherBits => -1 : (-0.0, 0.0) or (!NaN, NaN)
        // thisBits < anotherBits => +1 : (0.0, -0.0) or (NaN, !NaN)

        if (Double.isNaN(d1))
            return Double.isNaN(d2) ? 0 : 1;
        if (Double.isNaN(d2))
            return -1;
        if (Double.isInfinite(d1))
            return Double.isInfinite(d2) ? 0 : d1 > 0 ? -1 : 1;
        if (Double.isInfinite(d2))
            return d2 > 0 ? 1 : -1;

        if (diff > 0)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Test.
     * @param args String[]; empty
     */
    public static void main(final String[] args)
    {
        cmp(1.0, 1.0);
        cmp(-1.0, 1.0);
        cmp(1.0, -1.0);
        cmp(0.0, 0.0);
        cmp(1.0, 2.0);
        cmp(2.0, 1.0);
        cmp(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        cmp(Double.NEGATIVE_INFINITY, Double.NaN);
        cmp(Double.NaN, Double.NaN);
        cmp(Double.NaN, 2.0);
    }

    /**
     * Use the compare method for testing.
     * @param a double; first double
     * @param b double; second double
     */
    private static void cmp(final double a, final double b)
    {
        System.out.println("compare(" + a + ", " + b + ") => " + compare(a, b));
    }

}
