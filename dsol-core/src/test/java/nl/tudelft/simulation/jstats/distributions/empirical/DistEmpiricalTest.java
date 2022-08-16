package nl.tudelft.simulation.jstats.distributions.empirical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.Test;

import nl.tudelft.simulation.jstats.distributions.DistEmpiricalDiscreteDouble;
import nl.tudelft.simulation.jstats.distributions.DistEmpiricalDiscreteLong;
import nl.tudelft.simulation.jstats.distributions.DistEmpiricalInterpolated;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistEmpiricalTest tests the DistEmpirical and the DistEmpiricalDiscreteLong, based on several ways of constructing the
 * underlying EmpiricalDistribution.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistEmpiricalTest
{
    /**
     * Test the DistEmpirical based on a provided cumulative distribution, not interpolated.
     */
    @Test
    public void testDistEmpiricalCumulativeNonInterpolated()
    {
        StreamInterface stream = new MersenneTwister(12L);
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution empDist = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        DistEmpiricalDiscreteDouble dist = new DistEmpiricalDiscreteDouble(stream, empDist);
        int[] bins = new int[4];
        for (int i = 0; i < 100_000; i++)
        {
            double d = dist.draw();
            assertTrue("value = " + d, d == 1.0 || d == 2.0 || d == 3.0 || d == 4.0);
            bins[(int) (d - 1.0)]++;
        }
        double[] density = {0.1, 0.4, 0.3, 0.2};
        for (int i = 0; i < 4; i++)
        {
            assertEquals(density[i], 1.0 * bins[i] / 100_000.0, 1E-3);
            assertEquals(density[i], dist.getProbabilityDensity(vd[i]), 1E-6);
        }
        assertEquals(0.0, dist.getProbabilityDensity(-0.1), 1E-6);
        assertEquals(0.0, dist.getProbabilityDensity(1.1), 1E-6);
    }

    /**
     * Test the DistEmpirical based on a provided cumulative distribution, with interpolation.
     */
    @Test
    @SuppressWarnings("checkstyle:needbraces")
    public void testDistEmpiricalCumulativeInterpolated()
    {
        StreamInterface stream = new MersenneTwister(12L);
        double[] cpd = {0.0, 0.1, 0.5, 0.8, 1.0};
        double[] vd = {0.0, 1.0, 2.0, 3.0, 4.0};
        InterpolatedEmpiricalDistribution empDist = CumulativeProbabilities.createInterpolatedDistribution(vd, cpd);
        DistEmpiricalInterpolated dist = new DistEmpiricalInterpolated(stream, empDist);
        int[] bins = new int[4];
        for (int i = 0; i < 100_000; i++)
        {
            double d = dist.draw();
            assertTrue(d >= 0.0 && d <= 4.0);
            if (d >= 0.0 && d < 1.0)
                bins[0]++;
            else if (d >= 1.0 && d < 2.0)
                bins[1]++;
            else if (d >= 2.0 && d < 3.0)
                bins[2]++;
            else if (d >= 3.0 && d <= 4.0)
                bins[3]++;
        }
        double[] density = {0.1, 0.4, 0.3, 0.2};
        for (int i = 0; i < 4; i++)
        {
            assertEquals(density[i], 1.0 * bins[i] / 100_000.0, 1E-3);
        }
        for (double x = -1.0; x < 5.0; x += 0.1)
        {
            if (x >= 0.0 && x < 1.0)
                assertEquals("value = " + x, 0.1, dist.getProbabilityDensity(x), 1E-6);
            else if (x >= 1.0 && x < 2.0)
                assertEquals("value = " + x, 0.4, dist.getProbabilityDensity(x), 1E-6);
            else if (x >= 2.0 && x < 3.0)
                assertEquals("value = " + x, 0.3, dist.getProbabilityDensity(x), 1E-6);
            else if (x >= 3.0 && x <= 4.0)
                assertEquals("value = " + x, 0.2, dist.getProbabilityDensity(x), 1E-6);
            else
                assertEquals("value = " + x, 0.0, dist.getProbabilityDensity(x), 1E-6);
        }
        assertEquals(0.0, dist.getProbabilityDensity(-0.1), 1E-6);
        assertEquals(0.0, dist.getProbabilityDensity(6.1), 1E-6);
    }

    /**
     * Test the DistDiscreteEmpirical based on a provided cumulative distribution.
     */
    @Test
    public void testDistDiscreteEmpiricalCumulative()
    {
        StreamInterface stream = new MersenneTwister(12L);
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        long[] vl = {1, 2, 3, 4};
        DiscreteEmpiricalDistribution empDist = CumulativeProbabilities.createDiscreteDistribution(vl, cpd);
        DistEmpiricalDiscreteLong dist = new DistEmpiricalDiscreteLong(stream, empDist);
        int[] bins = new int[4];
        for (int i = 0; i < 100_000; i++)
        {
            long v = dist.draw();
            assertTrue("value = " + v, v == 1L || v == 2L || v == 3L || v == 4L);
            bins[(int) (v - 1)]++;
        }
        double[] density = {0.1, 0.4, 0.3, 0.2};
        for (int i = 0; i < 4; i++)
        {
            assertEquals(density[i], bins[i] / 100_000.0, 1E-3);
            assertEquals(density[i], dist.probability(vl[i]), 1E-6);
        }
        assertEquals(0.0, dist.probability(-1L), 1E-6);
        assertEquals(0.0, dist.probability(5L), 1E-6);

        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution empDist2 = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        Try.testFail(() -> { new DistEmpiricalDiscreteLong(stream, empDist2); });

        Number[] vf = {1.0f, 2.0f, 3.0f, 4.0f};
        DiscreteEmpiricalDistribution empDist3 = CumulativeProbabilities.createDiscreteDistribution(vf, cpd);
        Try.testFail(() -> { new DistEmpiricalDiscreteLong(stream, empDist3); });
    }

}
