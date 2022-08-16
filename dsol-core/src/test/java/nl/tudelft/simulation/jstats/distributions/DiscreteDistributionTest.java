package nl.tudelft.simulation.jstats.distributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.djutils.stats.summarizers.Tally;
import org.junit.Test;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistributionTest tests the correct statistics of the discrete distributions, based on a tally of their values.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DiscreteDistributionTest
{
    /** the random stream to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    StreamInterface stream;

    /**
     * Test the distributions for the correct stats based on a tally of their values.
     */
    @Test
    public void testDistributionsMeanVariance()
    {
        this.stream = new MersenneTwister(12L);
        double nan = Double.NaN;
        testDist("DistBernoulli", new DistBernoulli(this.stream, 0.25), 0.25, 0.25 * (1.0 - 0.25), 0.0, 1.0, 0.01);
        testDist("DistBinomial", new DistBinomial(this.stream, 3, 0.25), 3 * 0.25, 3 * 0.25 * 0.75, 0.0, 3.0, 0.01);
        testDist("DistDiscreteConstant", new DistDiscreteConstant(this.stream, 14), 14, 0, 14, 14, 0.001);
        testDist("DistDiscreteUniform", new DistDiscreteUniform(this.stream, 1, 5), 3.0, (5.0 - 1.0) * (5.0 + 1.0) / 12.0, 1, 5,
                0.05);
        testDist("DistGeometric", new DistGeometric(this.stream, 0.25), (1 - 0.25) / 0.25, (1 - 0.25) / (0.25 * 0.25), 0.0, nan,
                0.05);
        testDist("DistGeometric", new DistGeometric(this.stream, 0.9), (1 - 0.9) / 0.9, (1 - 0.9) / (0.9 * 0.9), 0.0, nan,
                0.05);
        testDist("DistNegBinomial", new DistNegBinomial(this.stream, 10, 0.25), 10 * (1.0 - 0.25) / 0.25,
                10 * (1.0 - 0.25) / (0.25 * 0.25), 0.0, nan, 0.05);
        testDist("DistPoisson", new DistPoisson(this.stream, 8.21), 8.21, 8.21, 0.0, nan, 0.01);
    }

    /**
     * @param name String; the name of the distribution to test
     * @param dist Dist; the distribution to test
     * @param expectedMean double; the expected mean of a large number of samples
     * @param expectedVariance double; the expected variance of a large number of samples; test the standard deviations
     * @param expectedMin double; the expected lowest value of a large number of samples, or NaN if unbounded
     * @param expectedMax double; the expected highest value of a large number of samples, or NaN if unbounded
     * @param precision double; the precision for mean, standard deviation, min and max
     */
    @SuppressWarnings("checkstyle:parameternumber")
    private void testDist(final String name, final DistDiscrete dist, final double expectedMean, final double expectedVariance,
            final double expectedMin, final double expectedMax, final double precision)
    {
        Tally tally = new Tally("distTally " + dist.toString());
        tally.initialize();
        for (int i = 0; i < 1_000_000; i++)
        {
            double d = 1.0 * dist.draw();
            if (!Double.isNaN(expectedMin))
            {
                assertTrue(name + " min", d >= expectedMin);
            }
            if (!Double.isNaN(expectedMax))
            {
                assertTrue(name + " max", d <= expectedMax);
            }
            tally.ingest(d);
        }
        assertEquals(name + " mean", expectedMean, tally.getPopulationMean(), precision);
        assertEquals(name + " stdev", Math.sqrt(expectedVariance), tally.getPopulationStDev(), precision);
    }

    /**
     * Test the Bernoulli distribution.
     */
    @Test
    public void testBernoulli()
    {
        this.stream = new MersenneTwister(10L);
        DistBernoulli dist = new DistBernoulli(this.stream, 0.25);
        assertEquals(this.stream, dist.getStream());
        assertEquals(0.25, dist.getP(), 0.0001);
        assertTrue(dist.toString().contains("Bernoulli"));
        assertTrue(dist.toString().contains("0.25"));
        long value = dist.draw();
        assertTrue(value == 0 || value == 1);
        assertEquals(0.75, dist.probability(0), 0.0001);
        assertEquals(0.25, dist.probability(1), 0.0001);
        assertEquals(0.0, dist.probability(2), 0.0001);

        Try.testFail(() -> { new DistBernoulli(null, 0.1); }, NullPointerException.class);
        Try.testFail(() -> { new DistBernoulli(DiscreteDistributionTest.this.stream, -0.1); }, IllegalArgumentException.class);
        Try.testFail(() -> { new DistBernoulli(DiscreteDistributionTest.this.stream, 2.0); }, IllegalArgumentException.class);

        DistBernoulli dist1 = new DistBernoulli(new MersenneTwister(10L), 0.75);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the Binomial distribution.
     */
    @Test
    public void testBinomial()
    {
        this.stream = new MersenneTwister(10L);
        DistBinomial dist = new DistBinomial(this.stream, 4, 0.25);
        assertEquals(this.stream, dist.getStream());
        assertEquals(0.25, dist.getP(), 0.0001);
        assertEquals(4, dist.getN());
        assertTrue(dist.toString().contains("Binomial"));
        assertTrue(dist.toString().contains("0.25"));
        assertTrue(dist.toString().contains("4"));
        long value = dist.draw();
        assertTrue(value >= 0 || value <= 4);
        assertEquals(combinations(4, 0) * Math.pow(0.25, 0) * Math.pow(0.75, 4), dist.probability(0), 0.0001);
        assertEquals(combinations(4, 1) * Math.pow(0.25, 1) * Math.pow(0.75, 3), dist.probability(1), 0.0001);
        assertEquals(combinations(4, 2) * Math.pow(0.25, 2) * Math.pow(0.75, 2), dist.probability(2), 0.0001);
        assertEquals(combinations(4, 3) * Math.pow(0.25, 3) * Math.pow(0.75, 1), dist.probability(3), 0.0001);
        assertEquals(combinations(4, 4) * Math.pow(0.25, 4) * Math.pow(0.75, 0), dist.probability(4), 0.0001);
        assertEquals(0.0, dist.probability(5), 0.0001);
        assertEquals(0.0, dist.probability(-1), 0.0001);

        Try.testFail(() -> { new DistBinomial(null, 4, 0.1); }, NullPointerException.class);
        Try.testFail(() -> { new DistBinomial(DiscreteDistributionTest.this.stream, -4, 0.1); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistBinomial(DiscreteDistributionTest.this.stream, 2, -0.1); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistBinomial(DiscreteDistributionTest.this.stream, 2, 1.1); }, IllegalArgumentException.class);

        DistBinomial dist1 = new DistBinomial(new MersenneTwister(10L), 5, 0.75);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the DiscreteConstant distribution.
     */
    @Test
    public void testDiscreteConstant()
    {
        this.stream = new MersenneTwister(10L);
        DistDiscreteConstant dist = new DistDiscreteConstant(this.stream, 7);
        assertEquals(this.stream, dist.getStream());
        assertEquals(7L, dist.getConstant(), 0.0001);
        assertTrue(dist.toString().contains("DiscreteConstant"));
        assertTrue(dist.toString().contains("7"));
        long value = dist.draw();
        assertTrue(value == 7);
        assertEquals(0.0, dist.probability(0), 0.0001);
        assertEquals(0.0, dist.probability(1), 0.0001);
        assertEquals(1.0, dist.probability(7), 0.0001);

        Try.testFail(() -> { new DistDiscreteConstant(null, 2); }, NullPointerException.class);

        DistDiscreteConstant dist1 = new DistDiscreteConstant(new MersenneTwister(10L), 8L);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the DiscreteUniform distribution.
     */
    @Test
    public void testDiscreteUniform()
    {
        this.stream = new MersenneTwister(10L);
        DistDiscreteUniform dist = new DistDiscreteUniform(this.stream, 1, 6);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1, dist.getMin(), 0.0001);
        assertEquals(6, dist.getMax(), 0.0001);
        assertTrue(dist.toString().contains("DiscreteUniform"));
        assertTrue(dist.toString().contains("6"));
        long value = dist.draw();
        assertTrue(value >= 1 && value <= 6);
        assertEquals(0.0, dist.probability(0), 0.0001);
        assertEquals(1. / 6., dist.probability(1), 0.0001);
        assertEquals(1. / 6., dist.probability(2), 0.0001);
        assertEquals(1. / 6., dist.probability(3), 0.0001);
        assertEquals(1. / 6., dist.probability(4), 0.0001);
        assertEquals(1. / 6., dist.probability(5), 0.0001);
        assertEquals(1. / 6., dist.probability(6), 0.0001);
        assertEquals(0.0, dist.probability(7), 0.0001);

        Try.testFail(() -> { new DistDiscreteUniform(null, 2, 4); }, NullPointerException.class);
        Try.testFail(() -> { new DistDiscreteUniform(DiscreteDistributionTest.this.stream, 4, 2); },
                IllegalArgumentException.class);

        DistDiscreteUniform dist1 = new DistDiscreteUniform(new MersenneTwister(10L), 5, 10);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the Geometric distribution.
     */
    @Test
    public void testGeometric()
    {
        this.stream = new MersenneTwister(10L);
        for (double p = 0.1; p <= 1.0; p += 0.1)
        {
            DistGeometric dist = new DistGeometric(this.stream, p);
            assertEquals(this.stream, dist.getStream());
            assertEquals(p, dist.getP(), 0.0001);
            assertTrue(dist.toString().contains("Geometric"));
            assertTrue(dist.toString().contains("" + p));
            long value = dist.draw();
            assertTrue(value >= 0);
            assertEquals(0.0, dist.probability(-1), 0.01);
            for (int k = 0; k < 10; k++)
            {
                assertEquals(Math.pow(1 - p, k) * p, dist.probability(k), 0.01);
            }
        }

        Try.testFail(() -> { new DistGeometric(DiscreteDistributionTest.this.stream, 0.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new DistGeometric(DiscreteDistributionTest.this.stream, -1.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new DistGeometric(DiscreteDistributionTest.this.stream, 1.01); }, IllegalArgumentException.class);

        DistGeometric dist1 = new DistGeometric(new MersenneTwister(10L), 0.75);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the Negative Binomial distribution.
     */
    @Test
    public void testNegBinomial()
    {
        this.stream = new MersenneTwister(10L);
        for (int s = 1; s < 20; s++)
        {
            for (double p = 0.1; p < 1.0; p += 0.1)
            {
                DistNegBinomial dist = new DistNegBinomial(this.stream, s, p);
                assertEquals(this.stream, dist.getStream());
                assertEquals(p, dist.getP(), 0.0001);
                assertEquals(s, dist.getS());
                assertTrue(dist.toString().contains("NegBinomial"));
                assertTrue(dist.toString().contains("" + p));
                assertTrue(dist.toString().contains("" + s));
                long value = dist.draw();
                assertTrue(value >= 0);
                assertEquals(0.0, dist.probability(-1), 0.01);
                for (int k = 0; k < 10; k++)
                {
                    assertEquals(ProbMath.combinations(k + s - 1, s - 1) * Math.pow(1 - p, k) * Math.pow(p, s),
                            dist.probability(k), 0.01);
                }
            }
        }

        Try.testFail(() -> { new DistNegBinomial(DiscreteDistributionTest.this.stream, 0, 2.0); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistNegBinomial(DiscreteDistributionTest.this.stream, -1, 2.0); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistNegBinomial(DiscreteDistributionTest.this.stream, 2, 0.0); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistNegBinomial(DiscreteDistributionTest.this.stream, 2, -1.0); },
                IllegalArgumentException.class);
        Try.testFail(() -> { new DistNegBinomial(DiscreteDistributionTest.this.stream, 2, 1.01); },
                IllegalArgumentException.class);

        DistNegBinomial dist1 = new DistNegBinomial(new MersenneTwister(10L), 10, 0.75);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /**
     * Test the Poisson distribution.
     */
    @Test
    public void testPoisson()
    {
        this.stream = new MersenneTwister(10L);
        for (double lambda = 0.1; lambda <= 20; lambda += 0.3)
        {
            DistPoisson dist = new DistPoisson(this.stream, lambda);
            assertEquals(this.stream, dist.getStream());
            assertEquals(lambda, dist.getLambda(), 0.0001);
            assertTrue(dist.toString().contains("Poisson"));
            assertTrue(dist.toString().contains("" + lambda));
            long value = dist.draw();
            assertTrue(value >= 0);
            assertEquals(0.0, dist.probability(-1), 0.01);
            for (int x = 0; x < 50; x++)
            {
                assertEquals(Math.exp(-lambda) * Math.pow(lambda, x) / ProbMath.factorial(x), dist.probability(x), 0.001);
            }
        }

        Try.testFail(() -> { new DistPoisson(DiscreteDistributionTest.this.stream, 0.0); }, IllegalArgumentException.class);
        Try.testFail(() -> { new DistPoisson(DiscreteDistributionTest.this.stream, -1.0); }, IllegalArgumentException.class);

        DistPoisson dist1 = new DistPoisson(new MersenneTwister(10L), 8.3);
        long v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw());
    }

    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */

    /**
     * Calculate the number of k-combinations in an n-set, or "n over k" = n! / (k! * (n-k)!)
     * @param n int; number in the set
     * @param k int; number of combinations to look for
     * @return n over k
     */
    private static double combinations(final int n, final int k)
    {
        return 1.0 * fac(n) / (1.0 * fac(k) * fac(n - k));
    }

    /**
     * Calculate fac(n) = n * (n-1) * (n-2) * ... 1, where fac(0) = 1.
     * @param n int; param
     * @return n!
     */
    private static long fac(final int n)
    {
        return n == 0 ? 1L : n * fac(n - 1);
    }

}
