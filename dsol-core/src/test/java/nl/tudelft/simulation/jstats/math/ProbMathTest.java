package nl.tudelft.simulation.jstats.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * The tests for the ProbMath class.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class ProbMathTest
{
    /**
     * Test the factorial functions.
     */
    @Test
    public void testFactorial()
    {
        // Int

        assertEquals(1.0, ProbMath.factorial(0), 0.00001);
        assertEquals(3628800.0, ProbMath.factorial(10), 0.00001);
        assertEquals(1L, ProbMath.fac(0));
        assertEquals(3628800L, ProbMath.fac(10));

        assertTrue(ProbMath.factorial(170) > 0.0);
        assertTrue(ProbMath.fac(20) > 0L);

        Try.testFail(() -> { ProbMath.factorial(-1); }, "factorial(-1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.fac(-1); }, "fac(-1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.factorial(171); }, "factorial(171)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.fac(21); }, "fac(21)", IllegalArgumentException.class);

        // Long

        assertEquals(1.0, ProbMath.factorial(0L), 0.00001);
        assertEquals(3628800.0, ProbMath.factorial(10L), 0.00001);
        assertEquals(1L, ProbMath.fac(0L));
        assertEquals(3628800L, ProbMath.fac(10L));

        assertTrue(ProbMath.factorial(170L) > 0.0);
        assertTrue(ProbMath.fac(20L) > 0L);

        Try.testFail(() -> { ProbMath.factorial(-1L); }, "factorial(-1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.fac(-1L); }, "fac(-1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.factorial(171L); }, "factorial(171)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.fac(21L); }, "fac(21)", IllegalArgumentException.class);

    }

    /**
     * Test the permutations functions.
     */
    @Test
    public void testPermutation()
    {
        // Int

        assertEquals(336.0, ProbMath.permutations(8, 3), 0.0001);
        assertEquals(336L, ProbMath.perm(8, 3));

        Try.testFail(() -> { ProbMath.permutations(2, 5); }, "permutations(2, 5)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.perm(2, 5); }, "perm(2, 5)", IllegalArgumentException.class);

        // Long

        assertEquals(336.0, ProbMath.permutations(8L, 3L), 0.0001);
        assertEquals(336L, ProbMath.perm(8L, 3L));

        Try.testFail(() -> { ProbMath.permutations(2L, 5L); }, "permutations(2, 5)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.perm(2L, 5L); }, "perm(2, 5)", IllegalArgumentException.class);
    }

    /**
     * Test the combination functions.
     */
    @Test
    public void testCombination()
    {
        // Int

        assertEquals(56.0, ProbMath.combinations(8, 3), 0.0001);
        assertEquals(56L, ProbMath.comb(8, 3));

        Try.testFail(() -> { ProbMath.combinations(2, 5); }, "combinations(2, 5)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.comb(2, 5); }, "comb(2, 5)", IllegalArgumentException.class);

        // Long

        assertEquals(56.0, ProbMath.combinations(8L, 3L), 0.0001);
        assertEquals(56L, ProbMath.comb(8L, 3L));

        Try.testFail(() -> { ProbMath.combinations(2L, 5L); }, "combinations(2, 5)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.comb(2L, 5L); }, "comb(2, 5)", IllegalArgumentException.class);
    }

    /**
     * Test the erf(z)and erfInv(z) functions.
     */
    @Test
    public void testErf()
    {
        assertEquals(0.0, ProbMath.erf(0.0), 0.0001);
        assertEquals(0.52049987781, ProbMath.erf(0.5), 0.0001);
        assertEquals(-0.52049987781, ProbMath.erf(-0.5), 0.0001);
        assertEquals(0.84270079295, ProbMath.erf(1.0), 0.0001);
        assertEquals(-0.84270079295, ProbMath.erf(-1.0), 0.0001);
        assertEquals(1.0, ProbMath.erf(3.0), 0.0001);
        assertEquals(1.0, ProbMath.erf(4.0), 0.0001);
        assertEquals(-1.0, ProbMath.erf(-4.0), 0.0001);
        assertEquals(0.47693672, ProbMath.erfInv(0.5), 0.0001);
        assertEquals(Double.POSITIVE_INFINITY, ProbMath.erfInv(1), 0.0001);

        double[] testWikipedia = new double[] {0.02, 0.022564575, 0.04, 0.045111106, 0.06, 0.067621594, 0.08, 0.090078126, 0.1,
                0.112462916, 0.2, 0.222702589, 0.3, 0.328626759, 0.4, 0.428392355, 0.5, 0.520499878, 0.6, 0.603856091, 0.7,
                0.677801194, 0.8, 0.742100965, 0.9, 0.796908212, 1.0, 0.842700793, 1.1, 0.88020507, 1.2, 0.910313978, 1.3,
                0.934007945, 1.4, 0.95228512, 1.5, 0.966105146, 1.6, 0.976348383, 1.7, 0.983790459, 1.8, 0.989090502, 1.9,
                0.992790429, 2, 0.995322265, 2.1, 0.997020533, 2.2, 0.998137154, 2.3, 0.998856823, 2.4, 0.999311486, 2.5,
                0.999593048};
        for (int i = 0; i < testWikipedia.length / 2; i++)
        {
            assertEquals("erf for value " + testWikipedia[2 * i], testWikipedia[2 * i + 1], ProbMath.erf(testWikipedia[2 * i]),
                    0.0001);
            assertEquals("erfInv(" + testWikipedia[2 * i + 1] + ") for value " + testWikipedia[2 * i], testWikipedia[2 * i],
                    ProbMath.erfInv(testWikipedia[2 * i + 1]), 0.0001);
        }
    }

    /**
     * Test the gammaln, gamma, and beta functions.
     */
    @Test
    public void testGammaBeta()
    {
        for (int i = 1; i < 20; i++)
        {
            double x = ProbMath.factorial(i - 1);
            assertEquals(x, ProbMath.gamma(i), x * 0.0001);
            assertEquals(Math.log(x), ProbMath.gammaln(i), 1E-6);
        }
        assertEquals(Double.POSITIVE_INFINITY, ProbMath.gamma(0), 1E-6);
        assertEquals(Math.sqrt(Math.PI), ProbMath.gamma(0.5), 1E-6);
        assertEquals(0.5 * Math.sqrt(Math.PI), ProbMath.gamma(1.5), 1E-6);
        assertEquals(0.75 * Math.sqrt(Math.PI), ProbMath.gamma(2.5), 1E-6);
        assertEquals((15.0 / 8.0) * Math.sqrt(Math.PI), ProbMath.gamma(3.5), 1E-6);
        assertEquals(2.6789385347077476337, ProbMath.gamma(1.0 / 3.0), 1E-6);
        // local minimum
        assertEquals(0.88560319441088870027881590058258, ProbMath.gamma(1.46163214496836234126265954232572), 1E-6);

        for (int i = 1; i < 20; i++)
        {
            for (int j = 1; j < 20; j++)
            {
                double x = ProbMath.factorial(i - 1);
                double y = ProbMath.factorial(j - 1);
                double z = ProbMath.factorial(i + j - 1);
                assertEquals(x * y / z, ProbMath.beta(i, j), 0.0001 * x * y / z);
            }
        }

        Try.testFail(() -> { ProbMath.gamma(-1); }, "gamma(-1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.beta(-1, 1); }, "beta(-1, 1)", IllegalArgumentException.class);
        Try.testFail(() -> { ProbMath.beta(1, -1); }, "beta(1, -1)", IllegalArgumentException.class);

    }
}
