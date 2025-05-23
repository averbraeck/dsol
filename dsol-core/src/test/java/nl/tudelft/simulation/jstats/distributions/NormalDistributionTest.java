package nl.tudelft.simulation.jstats.distributions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.exceptions.Try;
import org.djutils.stats.summarizers.Tally;
import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.jstats.math.ProbMath;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistributionTest tests the correct statistics of the Normal, LogNormal, Truncated Normal, and Truncated LogNormal
 * distributions, based on a tally of their values.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class NormalDistributionTest
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
        testDist("DistLogNormal", new DistLogNormal(this.stream, 0.0, 0.5), Math.exp(0.5 * 0.5 / 2.0),
                (Math.exp(0.5 * 0.5) - 1.0) * Math.exp(0.5 * 0.5), 0.0, nan, 0.01);
        testDist("DistLogNormal", new DistLogNormal(this.stream, 5.0, 0.5), Math.exp(5.0 + 0.5 * 0.5 / 2.0),
                (Math.exp(0.5 * 0.5) - 1.0) * Math.exp(2 * 5.0 + 0.5 * 0.5), 0.0, nan, 0.5);
        testDist("DistNormal", new DistNormal(this.stream), 0.0, 1.0, nan, nan, 0.01);
        testDist("DistNormal", new DistNormal(this.stream, 5.0, 2.0), 5.0, 4.0, nan, nan, 0.01);

        // truncated distributions are covered later because of functions needed
    }

    /**
     * @param name the name of the distribution to test
     * @param dist the distribution to test
     * @param expectedMean the expected mean of a large number of samples
     * @param expectedVariance the expected variance of a large number of samples; test the standard deviations
     * @param expectedMin the expected lowest value of a large number of samples, or NaN if unbounded
     * @param expectedMax the expected highest value of a large number of samples, or NaN if unbounded
     * @param precision the precision for mean, standard deviation, min and max
     */
    @SuppressWarnings("checkstyle:parameternumber")
    private void testDist(final String name, final DistContinuous dist, final double expectedMean,
            final double expectedVariance, final double expectedMin, final double expectedMax, final double precision)
    {
        Tally tally = new Tally("distTally " + dist.toString());
        tally.initialize();
        for (int i = 0; i < 1_000_000; i++)
        {
            double d = dist.draw();
            if (!Double.isNaN(expectedMin))
            { assertTrue(d >= expectedMin, name + " min: " + d + ">=" + expectedMin); }
            if (!Double.isNaN(expectedMax))
            { assertTrue(d <= expectedMax, name + " max: " + d + "<=" + expectedMax); }
            tally.register(d);
        }
        assertEquals(expectedMean, tally.getPopulationMean(), precision, name + " mean");
        assertEquals(Math.sqrt(expectedVariance), tally.getPopulationStDev(), precision, name + " stdev");
    }

    /**
     * Test the LogNormal distribution.
     */
    @Test
    public void testLogNormal()
    {
        this.stream = new MersenneTwister(10L);
        DistLogNormal dist = new DistLogNormal(this.stream, 1.5, 0.5);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1.5, dist.getMu(), 0.0001);
        assertEquals(0.5, dist.getSigma(), 0.0001);
        assertTrue(dist.toString().contains("LogNormal"));
        assertTrue(dist.toString().contains("1.5"));
        assertTrue(dist.toString().contains("0.5"));
        double value = dist.draw();
        assertTrue(value > 0);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(lnpdf(1.5, 0.5, 0.5), dist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(lnpdf(1.5, 0.5, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(lnpdf(1.5, 0.5, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(lnpdf(1.5, 0.5, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(lnpdf(1.5, 0.5, 8.0), dist.getProbabilityDensity(8.0), 0.0001);

        dist = new DistLogNormal(this.stream, 0.0, 2.5);
        value = dist.draw();
        assertTrue(value > 0);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(lnpdf(0.0, 2.5, 0.5), dist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(lnpdf(0.0, 2.5, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(lnpdf(0.0, 2.5, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(lnpdf(0.0, 2.5, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(lnpdf(0.0, 2.5, 8.0), dist.getProbabilityDensity(8.0), 0.0001);

        assertEquals(1, even(0));
        assertEquals(-1, even(1));
        assertEquals(1, even(2));
        assertEquals(0.0, erf(0.0), 0.01);
        assertEquals(0.52049987781, erf(0.5), 0.001);
        assertEquals(-0.52049987781, erf(-0.5), 0.001);
        assertEquals(0.84270079295, erf(1.0), 0.001);

        assertEquals(0.0, dist.getCumulativeProbability(-1.0), 0.0001);
        assertEquals(0.0, dist.getCumulativeProbability(0.0), 0.0001);
        assertEquals(lncdf(0.0, 2.5, 0.5), dist.getCumulativeProbability(0.5), 0.1);
        assertEquals(lncdf(0.0, 2.5, 1.0), dist.getCumulativeProbability(1.0), 0.1);
        assertEquals(lncdf(0.0, 2.5, 2.0), dist.getCumulativeProbability(2.0), 0.1);
        assertEquals(lncdf(0.0, 2.5, 4.0), dist.getCumulativeProbability(4.0), 0.1);
        assertEquals(lncdf(0.0, 2.5, 8.0), dist.getCumulativeProbability(8.0), 0.1);

        assertEquals(0.5, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(0.5)), 0.05);
        assertEquals(1.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(1.0)), 0.05);
        assertEquals(2.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(2.0)), 0.05);
        assertEquals(4.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(4.0)), 0.05);
        assertEquals(8.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(8.0)), 0.05);

        Try.testFail(() -> {
            new DistLogNormal(null, 1.0, 2.0);
        }, NullPointerException.class);
        Try.testFail(() -> {
            new DistLogNormal(NormalDistributionTest.this.stream, 2.0, 0.0);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistLogNormal(NormalDistributionTest.this.stream, 2.0, -1.0);
        }, IllegalArgumentException.class);

        DistLogNormal dist1 = new DistLogNormal(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Normal distribution.
     */
    @Test
    public void testNormal()
    {
        this.stream = new MersenneTwister(10L);
        DistNormal dist = new DistNormal(this.stream, 5.0, 0.5);
        assertEquals(this.stream, dist.getStream());
        assertEquals(5.0, dist.getMu(), 0.0001);
        assertEquals(0.5, dist.getSigma(), 0.0001);
        assertTrue(dist.toString().contains("Normal"));
        assertTrue(dist.toString().contains("5.0"));
        assertTrue(dist.toString().contains("0.5"));

        DistNormal stdDist = new DistNormal(this.stream);
        assertEquals(1.0 / Math.sqrt(2.0 * Math.PI), stdDist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(0.5, stdDist.getCumulativeProbability(0.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, 0.5), stdDist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(normpdf(0.0, 1.0, 1.0), stdDist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, 2.0), stdDist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, 4.0), stdDist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, 8.0), stdDist.getProbabilityDensity(8.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, -0.5), stdDist.getProbabilityDensity(-0.5), 0.0001);
        assertEquals(normpdf(0.0, 1.0, -1.0), stdDist.getProbabilityDensity(-1.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, -2.0), stdDist.getProbabilityDensity(-2.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, -4.0), stdDist.getProbabilityDensity(-4.0), 0.0001);
        assertEquals(normpdf(0.0, 1.0, -8.0), stdDist.getProbabilityDensity(-8.0), 0.0001);

        assertEquals(1.96, stdDist.getInverseCumulativeProbability(0.975), 0.001);
        assertEquals(-1.96, stdDist.getInverseCumulativeProbability(0.025), 0.001);
        assertEquals(0.975, stdDist.getCumulativeProbability(1.96), 0.001);
        assertEquals(0.025, stdDist.getCumulativeProbability(-1.96), 0.001);

        assertEquals(0.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(0.0)), 0.0001);
        assertEquals(0.5, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(0.5)), 0.0001);
        assertEquals(1.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(1.0)), 0.0001);
        assertEquals(2.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(2.0)), 0.0001);
        assertEquals(4.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(4.0)), 0.0001);
        assertEquals(-0.5, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(-0.5)), 0.0001);
        assertEquals(-1.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(-1.0)), 0.0001);
        assertEquals(-2.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(-2.0)), 0.0001);
        assertEquals(-4.0, stdDist.getInverseCumulativeProbability(stdDist.getCumulativeProbability(-4.0)), 0.0001);

        dist = new DistNormal(this.stream, 5.0, 2.0);
        assertEquals(0.5, dist.getCumulativeProbability(5.0), 0.0001);
        assertEquals(normpdf(5.0, 2.0, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(normpdf(5.0, 2.0, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(normpdf(5.0, 2.0, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(normpdf(5.0, 2.0, 8.0), dist.getProbabilityDensity(8.0), 0.0001);
        assertEquals(normpdf(5.0, 2.0, 10.0), dist.getProbabilityDensity(10.0), 0.0001);

        assertEquals(normcdf(5.0, 2.0, 0.5), dist.getCumulativeProbability(0.5), 0.0001);
        assertEquals(normcdf(5.0, 2.0, 1.0), dist.getCumulativeProbability(1.0), 0.0001);
        assertEquals(normcdf(5.0, 2.0, 2.0), dist.getCumulativeProbability(2.0), 0.0001);
        assertEquals(normcdf(5.0, 2.0, 4.0), dist.getCumulativeProbability(4.0), 0.0001);
        assertEquals(normcdf(5.0, 2.0, 8.0), dist.getCumulativeProbability(8.0), 0.0001);
        assertEquals(normcdf(5.0, 2.0, 10.0), dist.getCumulativeProbability(10.0), 0.0001);

        assertEquals(0.5, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(0.5)), 0.0001);
        assertEquals(1.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(1.0)), 0.0001);
        assertEquals(2.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(2.0)), 0.0001);
        assertEquals(4.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(4.0)), 0.0001);
        assertEquals(8.0, dist.getInverseCumulativeProbability(dist.getCumulativeProbability(8.0)), 0.0001);

        for (int i = 0; i < Z_VALUES.length / 2; i++)
        {
            double p = Z_VALUES[2 * i];
            double c = Z_VALUES[2 * i + 1];
            assertEquals(c, stdDist.getCumulativeProbability(p), 0.0001);
            assertEquals(p, stdDist.getInverseCumulativeProbability(c), 0.0001);
        }

        Try.testFail(() -> {
            new DistNormal(null, 1.0, 2.0);
        }, NullPointerException.class);
        Try.testFail(() -> {
            new DistNormal(NormalDistributionTest.this.stream, 2.0, 0.0);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistNormal(NormalDistributionTest.this.stream, 2.0, -1.0);
        }, IllegalArgumentException.class);

        DistNormal dist1 = new DistNormal(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the truncated standard normal distribution.
     */
    @Test
    public void testTruncatedStandardNormal()
    {
        this.stream = new MersenneTwister(10L);

        // standard normal truncated to -2, 2
        DistNormalTrunc dist = new DistNormalTrunc(this.stream, -2, 2);
        assertTrue(dist.toString().contains("NormalTrunc("));
        double a = -2;
        double b = 2;
        double mu = 0;
        double sigma = 1;
        double alpha = (a - mu) / sigma;
        double beta = (b - mu) / sigma;
        double z = PHI(beta) - PHI(alpha);
        for (double x = -20; x <= 20; x += 0.2)
        {
            double xi = (x - mu) / sigma;
            if (x < a || x > b)
            {
                assertEquals(0.0, dist.getProbabilityDensity(x), 0.0001,
                        "pdf(x,m,s,a,b)=pdf(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
            }
            else
            {
                assertEquals(phi(xi) / (sigma * z), dist.getProbabilityDensity(x), 0.0001,
                        "pdf(x,m,s,a,b)=pdf(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
            }
        }
        double expectedMean = mu + sigma * (phi(alpha) - phi(beta)) / z;
        double expectedVariance =
                sigma * sigma * (1 + ((alpha * phi(alpha) - beta * phi(beta)) / z - Math.pow((phi(alpha) - phi(beta)) / z, 2)));
        testDist("TruncatedStandardNormal", dist, expectedMean, expectedVariance, a, b, 0.01);

        DistNormalTrunc dist1 = new DistNormalTrunc(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the truncated normal distribution for a large interval (equaling the underlying normal distribution).
     */
    @Test
    public void testTruncatedNormalLargeInterval()
    {
        this.stream = new MersenneTwister(10L);
        DistNormalTrunc dist = new DistNormalTrunc(this.stream, -10, 10);
        for (int i = 0; i < Z_VALUES.length / 2; i++)
        {
            double p = Z_VALUES[2 * i];
            double c = Z_VALUES[2 * i + 1];
            assertEquals(c, dist.getCumulativeProbability(p), 0.0001);
            assertEquals(p, dist.getInverseCumulativeProbability(c), 0.0001);
        }
        for (double x = -8; x <= 8; x += 0.1)
        {
            assertEquals(normpdf(0.0, 1.0, x), dist.getProbabilityDensity(x), 0.0001);
        }

        dist = new DistNormalTrunc(this.stream, 2, 0.2, -10, 10);
        for (double x = -8; x <= 8; x += 0.1)
        {
            assertEquals(normpdf(2, 0.2, x), dist.getProbabilityDensity(x), 0.0001, "x=" + x);
            assertEquals(normcdf(2, 0.2, x), dist.getCumulativeProbability(x), 0.0001, "x=" + x);
        }
    }

    /**
     * Test the PDF and CDF for the truncated normal distribution.
     */
    @Test
    public void testTruncatedNormalPDFandCDF()
    {
        this.stream = new MersenneTwister(10L);

        for (double mu = -2; mu <= 2; mu += 0.5)
        {
            for (double sigma = 0.1; sigma <= 4; sigma += 0.5)
            {
                for (double a = -10; a <= 10; a += 5)
                {
                    for (double width = 1; width < 20; width += 4)
                    {
                        double b = a + width;

                        // see if the density is enough
                        double cumulProbMin = 0.5 + 0.5 * ProbMath.erf((a - mu) / (Math.sqrt(2.0) * sigma));
                        double cumulProbDiff = 0.5 + 0.5 * ProbMath.erf((b - mu) / (Math.sqrt(2.0) * sigma)) - cumulProbMin;
                        if (cumulProbDiff < 1E-6)
                        {
                            // too small
                            continue;
                        }

                        DistNormalTrunc dist = new DistNormalTrunc(this.stream, mu, sigma, a, b);
                        assertEquals(mu, dist.getMu(), 0.0001);
                        assertEquals(sigma, dist.getSigma(), 0.0001);
                        assertEquals(a, dist.getMin(), 0.0001);
                        assertEquals(b, dist.getMax(), 0.0001);
                        double alpha = (a - mu) / sigma;
                        double beta = (b - mu) / sigma;
                        double z = PHI(beta) - PHI(alpha);

                        for (double x = -20; x <= 20; x += 0.2)
                        {
                            double xi = (x - mu) / sigma;
                            if (x < a || x > b)
                            {
                                assertEquals(0.0, dist.getProbabilityDensity(x), 0.0001,
                                        "pdf(x,m,s,a,b)=pdf(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
                                assertEquals(x < a ? 0 : 1, dist.getCumulativeProbability(x), 0.0001,
                                        "CDF(x,m,s,a,b)=CDF(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
                            }
                            else
                            {
                                assertEquals(phi(xi) / (sigma * z), dist.getProbabilityDensity(x), 0.0001,
                                        "pdf(x,m,s,a,b)=pdf(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
                                assertEquals((PHI(xi) - PHI(alpha)) / z, dist.getCumulativeProbability(x), 0.0001,
                                        "CDF(x,m,s,a,b)=CDF(" + x + "," + mu + "," + sigma + "," + a + "," + b + ")");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Test sampling for the truncated normal distribution, also for one-sided truncation.
     */
    @Test
    public void testTruncatedNormalSamples()
    {
        assertEquals(0.0, phi(Double.POSITIVE_INFINITY), 0.001);
        assertEquals(0.0, phi(Double.NEGATIVE_INFINITY), 0.001);
        assertEquals(1.0, PHI(Double.POSITIVE_INFINITY), 0.001);
        assertEquals(0.0, PHI(Double.NEGATIVE_INFINITY), 0.001);

        this.stream = new MersenneTwister(10L);
        for (double[] params : new double[][] {{0.0, 1.0, -1.0, 1.0}, {2.0, 0.5, -1.0, 1.0}, {2.0, 0.5, 0.0, 1.0},
                {2.0, 0.5, 0.0, 1.0}, {2.0, 0.5, 0.0, Double.POSITIVE_INFINITY}, {2.0, 0.5, Double.NEGATIVE_INFINITY, 1.0},
                {2.0, 0.5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}})
        {
            double mu = params[0];
            double sigma = params[1];
            double a = params[2];
            double b = params[3];
            DistNormalTrunc dist = new DistNormalTrunc(this.stream, mu, sigma, a, b);

            double alpha = (a - mu) / sigma;
            double beta = (b - mu) / sigma;
            double z = PHI(beta) - PHI(alpha);
            if (Double.isFinite(a) && Double.isFinite(b))
            {
                double expectedMean = mu + sigma * (phi(alpha) - phi(beta)) / z;
                double expectedVariance = sigma * sigma
                        * (1 + ((alpha * phi(alpha) - beta * phi(beta)) / z - Math.pow((phi(alpha) - phi(beta)) / z, 2)));
                testDist("TruncatedStandardNormal(m,s,a,b)=(" + mu + ", " + sigma + ", " + a + ", " + b + ")", dist,
                        expectedMean, expectedVariance, a, b, 0.05);
            }
            else if (Double.isFinite(b)) // upper tail truncation only
            {
                double pbPB = phi(beta) / PHI(beta);
                double expectedMean = mu - sigma * pbPB;
                double expectedVariance = sigma * sigma * (1 - beta * pbPB - Math.pow(pbPB, 2));
                testDist("TruncatedStandardNormal(m,s,a,b)=(" + mu + ", " + sigma + ", " + a + ", " + b + ")", dist,
                        expectedMean, expectedVariance, a, b, 0.05);
            }
            else if (Double.isFinite(a)) // lower tail truncation only
            {
                double expectedMean = mu + sigma * phi(alpha) / z;
                double expectedVariance = sigma * sigma * (1 + alpha * phi(alpha) / z - Math.pow(phi(alpha) / z, 2));
                testDist("TruncatedStandardNormal(m,s,a,b)=(" + mu + ", " + sigma + ", " + a + ", " + b + ")", dist,
                        expectedMean, expectedVariance, a, b, 0.05);
            }
            else // no truncation
            {
                double expectedMean = mu;
                double expectedVariance = sigma * sigma;
                testDist("TruncatedStandardNormal(m,s,a,b)=(" + mu + ", " + sigma + ", " + a + ", " + b + ")", dist,
                        expectedMean, expectedVariance, a, b, 0.05);
            }
        }
    }

    /**
     * Test the exceptions for the truncated normal distribution.
     */
    @Test
    public void testTruncatedNormalExceptions()
    {
        this.stream = new MersenneTwister(20L);
        Try.testFail(() -> {
            new DistNormalTrunc(null, 1.0, 2.0);
        }, NullPointerException.class);
        Try.testFail(() -> {
            new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, 0.0, 1, 2);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, -1.0, 1, 2);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, 1.0, 1, 1);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, 1.0, 1, 0);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, 0.1, 20, 21);
        }, IllegalArgumentException.class);

        DistNormalTrunc dist = new DistNormalTrunc(NormalDistributionTest.this.stream, 2.0, 0.1, 1.0, 2.0);
        assertEquals(1.0, dist.getInverseCumulativeProbability(0), 0.0001);
        assertEquals(2.0, dist.getInverseCumulativeProbability(1), 0.0001);
        Try.testFail(() -> {
            dist.getInverseCumulativeProbability(-0.1);
        }, IllegalArgumentException.class);
        Try.testFail(() -> {
            dist.getInverseCumulativeProbability(1.1);
        }, IllegalArgumentException.class);

    }

    /**
     * phi function is the pdf for the standard normal distribution.
     * @param xi value to calculate the pdf for
     * @return phi(xi)
     */
    private static double phi(final double xi)
    {
        return Math.exp(-0.5 * xi * xi) / Math.sqrt(2.0 * Math.PI);
    }

    /**
     * PHI function is the CDF for the standard normal distribution.
     * @param x value to calculate the CDF for
     * @return PHI(x)
     */
    @SuppressWarnings("checkstyle:methodname")
    private static double PHI(final double x)
    {
        return 0.5 + 0.5 * ProbMath.erf(x / Math.sqrt(2));
    }

    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */

    /**
     * Calculate fac(n) = n * (n-1) * (n-2) * ... 1, where fac(0) = 1.
     * @param n param
     * @return n!
     */
    private static long fac(final int n)
    {
        return n == 0 ? 1L : n * fac(n - 1);
    }

    /**
     * Calculate probability density of LogNormal(mu, sigma) for value x. From:
     * https://en.wikipedia.org/wiki/Log-normal_distribution.
     * @param mu shape parameter
     * @param sigma scale parameter
     * @param x value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double lnpdf(final double mu, final double sigma, final double x)
    {
        return (1.0 / (x * sigma * Math.sqrt(2.0 * Math.PI)))
                * Math.exp(-Math.pow((Math.log(x) - mu), 2) / (2.0 * sigma * sigma));
    }

    /**
     * @param x double
     * @return -1 if not even
     */
    private static int even(final int x)
    {
        return ((int) 2.0 * Math.floor(x / 2.0)) == x ? 1 : -1;
    }

    /**
     * Approximates erf(z).
     * @param z the value to calculate erf for
     * @return erf(z)
     */
    private static double erf(final double z)
    {
        double zpos = Math.abs(z);
        double d = zpos;
        for (int i = 1; i < 64; i++)
        {
            double term = Math.pow(zpos, 2 * i + 1) / ((2 * i + 1) * fac(i));
            d += even(i) * term;
            if (term < 1E-16)
            { break; }
        }
        if (z < 0)
        { d = -d; }
        return (2.0 / Math.sqrt(Math.PI)) * d;
    }

    /**
     * Calculate cumulative probability density of LogNormal(mu, sigma) for value x. From:
     * https://en.wikipedia.org/wiki/Log-normal_distribution.
     * @param mu shape parameter
     * @param sigma scale parameter
     * @param x value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double lncdf(final double mu, final double sigma, final double x)
    {
        return 0.5 + 0.5 * erf((Math.log(x) - mu) / (Math.sqrt(2.0) * sigma));
    }

    /**
     * Calculate probability density of Normal(mu, sigma) for value x. From: https://en.wikipedia.org/wiki/Normal_distribution.
     * @param mu shape parameter
     * @param sigma scale parameter
     * @param x value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double normpdf(final double mu, final double sigma, final double x)
    {
        return (1.0 / (sigma * Math.sqrt(2.0 * Math.PI))) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2));
    }

    /**
     * Calculate cumulative probability density of Normal(mu, sigma) for value x. From:
     * https://en.wikipedia.org/wiki/Normal_distribution.
     * @param mu shape parameter
     * @param sigma scale parameter
     * @param x value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double normcdf(final double mu, final double sigma, final double x)
    {
        return 0.5 + 0.5 * ProbMath.erf((x - mu) / (Math.sqrt(2.0) * sigma));
    }

    // @formatter:off
    /** z-values. */
    private static final double[] Z_VALUES = new double[] {
            -4.26, 1.02213451839841E-05, 
            -4.25, 1.06885257749344E-05, 
            -4.24, 1.11759893321205E-05, 
            -4.23, 1.16845655947074E-05, 
            -4.22, 0.000012215115925253, 
            -4.21, 1.27685344137349E-05, 
            -4.2, 1.33457490159063E-05, 
            -4.19, 1.39477227268812E-05, 
            -4.18, 0.000014575454790867, 
            -4.17, 1.52299819479779E-05, 
            -4.16, 1.59123797190822E-05, 
            -4.15, 1.66237637296522E-05, 
            -4.14, 0.000017365291073604, 
            -4.13, 1.81381617181309E-05, 
            -4.12, 1.89436199505532E-05, 
            -4.11, 0.000019782955868224, 
            -4.1, 2.06575069125467E-05, 
            -4.09, 2.15686594481806E-05, 
            -4.08, 2.25178503885254E-05, 
            -4.07, 2.35065688685956E-05, 
            -4.06, 2.45363579664097E-05, 
            -4.05, 2.56088164740415E-05, 
            -4.04, 0.000026725600719492, 
            -4.03, 2.78884264405639E-05, 
            -4.02, 0.000029099070711931, 
            -4.01, 3.03593739266182E-05, 
            -4, 3.16712418331199E-05, 
            -3.99, 3.30366476294024E-05, 
            -3.98, 3.44576341150531E-05, 
            -3.97, 3.59363159028537E-05, 
            -3.96, 3.74748816910734E-05, 
            -3.95, 3.90755965977875E-05, 
            -3.94, 4.07408045585507E-05, 
            -3.93, 4.24729307887611E-05, 
            -3.92, 4.42744843120707E-05, 
            -3.91, 4.61480605562088E-05, 
            -3.9, 4.80963440176026E-05, 
            -3.89, 5.01221109961884E-05, 
            -3.88, 5.22282324018201E-05, 
            -3.87, 5.44176766336996E-05, 
            -3.86, 5.66935125342565E-05, 
            -3.85, 5.90589124189224E-05, 
            -3.84, 6.15171551832552E-05, 
            -3.83, 6.40716294888744E-05, 
            -3.82, 6.67258370296847E-05, 
            -3.81, 6.94833958798651E-05, 
            -3.8, 0.00007234804392512, 
            -3.79, 7.53236423786832E-05, 
            -3.78, 7.84141793835851E-05, 
            -3.77, 8.16237737026861E-05, 
            -3.76, 8.49566784979979E-05, 
            -3.75, 8.84172852008038E-05, 
            -3.74, 9.20101274741054E-05, 
            -3.73, 9.57398852689145E-05, 
            -3.72, 9.96113889759165E-05, 
            -3.71, 0.000103629623674031, 
            -3.7, 0.000107799733477388, 
            -3.69, 0.000112127025982247, 
            -3.68, 0.000116616976815368, 
            -3.67, 0.000121275234285358, 
            -3.66, 0.000126107624138486, 
            -3.65, 0.000131120154420484, 
            -3.64, 0.000136319020445802, 
            -3.63, 0.000141710609875819, 
            -3.62, 0.000147301507907472, 
            -3.61, 0.000153098502573755, 
            -3.6, 0.000159108590157534, 
            -3.59, 0.00016533898072011, 
            -3.58, 0.00017179710374593, 
            -3.57, 0.000178490613904847, 
            -3.56, 0.000185427396933278, 
            -3.55, 0.000192615575635633, 
            -3.54, 0.00020006351600732, 
            -3.53, 0.000207779833480621, 
            -3.52, 0.000215773399294717, 
            -3.51, 0.000224053346991093, 
            -3.5, 0.000232629079035525, 
            -3.49, 0.000241510273567836, 
            -3.48, 0.000250706891280538, 
            -3.47, 0.000260229182427467, 
            -3.46, 0.000270087693963474, 
            -3.45, 0.000280293276816177, 
            -3.44, 0.000290857093290743, 
            -3.43, 0.000301790624608637, 
            -3.42, 0.0003131056785812, 
            -3.41, 0.000324814397418877, 
            -3.4, 0.000336929265676881, 
            -3.39, 0.000349463118337971, 
            -3.38, 0.000362429149033044, 
            -3.37, 0.000375840918400083, 
            -3.36, 0.000389712362582032, 
            -3.35, 0.000404057801864021, 
            -3.34, 0.00041889194945037, 
            -3.33, 0.000434229920381655, 
            -3.32, 0.000450087240592118, 
            -3.31, 0.000466479856107549, 
            -3.3, 0.000483424142383777, 
            -3.29, 0.000500936913785722, 
            -3.28, 0.000519035433206972, 
            -3.27, 0.000537737421829695, 
            -3.26, 0.000557061069024621, 
            -3.25, 0.000577025042390766, 
            -3.24, 0.000597648497934416, 
            -3.23, 0.000618951090386834, 
            -3.22, 0.000640952983660055, 
            -3.21, 0.000663674861439967, 
            -3.2, 0.000687137937915847, 
            -3.19, 0.000711363968645363, 
            -3.18, 0.00073637526155393, 
            -3.17, 0.000762194688067235, 
            -3.16, 0.000788845694375572, 
            -3.15, 0.000816352312828562, 
            -3.14, 0.000844739173458626, 
            -3.13, 0.000874031515631567, 
            -3.12, 0.00090425519982234, 
            -3.11, 0.0009354367195141, 
            -3.1, 0.000967603213218356, 
            -3.09, 0.00100078247661401, 
            -3.08, 0.00103500297480284, 
            -3.07, 0.00107029385467892, 
            -3.06, 0.00110668495740925, 
            -3.05, 0.0011442068310227, 
            -3.04, 0.0011828907431044, 
            -3.03, 0.00122276869359226, 
            -3.02, 0.0012638734276723, 
            -3.01, 0.00130623844876947, 
            -3, 0.00134989803163009, 
            -2.99, 0.00139488723549225, 
            -2.98, 0.00144124191734001, 
            -2.97, 0.00148899874523746, 
            -2.96, 0.00153819521173806, 
            -2.95, 0.00158886964736487, 
            -2.94, 0.001641061234157, 
            -2.93, 0.00169481001927726, 
            -2.92, 0.0017501569286761, 
            -2.91, 0.00180714378080643, 
            -2.9, 0.00186581330038404, 
            -2.89, 0.00192620913218786, 
            -2.88, 0.00198837585489433, 
            -2.87, 0.00205235899493975, 
            -2.86, 0.00211820504040462, 
            -2.85, 0.00218596145491324, 
            -2.84, 0.00225567669154232, 
            -2.83, 0.00232740020673155, 
            -2.82, 0.00240118247418925, 
            -2.81, 0.00247707499878586, 
            -2.8, 0.00255513033042793, 
            -2.79, 0.00263540207790495, 
            -2.78, 0.00271794492270126, 
            -2.77, 0.00280281463276502, 
            -2.76, 0.00289006807622615, 
            -2.75, 0.00297976323505456, 
            -2.74, 0.00307195921865049, 
            -2.73, 0.00316671627735779, 
            -2.72, 0.00326409581589131, 
            -2.71, 0.00336416040666919, 
            -2.7, 0.00346697380304066, 
            -2.69, 0.00357260095239974, 
            -2.68, 0.00368110800917498, 
            -2.67, 0.00379256234768549, 
            -2.66, 0.00390703257485277, 
            -2.65, 0.0040245885427583, 
            -2.64, 0.00414530136103604, 
            -2.63, 0.00426924340908935, 
            -2.62, 0.00439648834812131, 
            -2.61, 0.00452711113296732, 
            -2.6, 0.00466118802371875, 
            -2.59, 0.00479879659712618, 
            -2.58, 0.00494001575777064, 
            -2.57, 0.00508492574899104, 
            -2.56, 0.00523360816355578, 
            -2.55, 0.00538614595406669, 
            -2.54, 0.0055426234430826, 
            -2.53, 0.0057031263329507, 
            -2.52, 0.00586774171533256, 
            -2.51, 0.00603655808041267, 
            -2.5, 0.00620966532577613, 
            -2.49, 0.00638715476494317, 
            -2.48, 0.00656911913554676, 
            -2.47, 0.00675565260714065, 
            -2.46, 0.00694685078862431, 
            -2.45, 0.00714281073527141, 
            -2.44, 0.00734363095534835, 
            -2.43, 0.0075494114163092, 
            -2.42, 0.00776025355055364, 
            -2.41, 0.00797626026073372, 
            -2.4, 0.00819753592459613, 
            -2.39, 0.00842418639934569, 
            -2.38, 0.00865631902551654, 
            -2.37, 0.00889404263033677, 
            -2.36, 0.00913746753057267, 
            -2.35, 0.00938670553483857, 
            -2.34, 0.00964186994535833, 
            -2.33, 0.00990307555916424, 
            -2.32, 0.0101704386687197, 
            -2.31, 0.0104440770619511, 
            -2.3, 0.0107241100216758, 
            -2.29, 0.0110106583244114, 
            -2.28, 0.0113038442385528, 
            -2.27, 0.0116037915219035, 
            -2.26, 0.0119106254185471, 
            -2.25, 0.0122244726550447, 
            -2.24, 0.0125454614359466, 
            -2.23, 0.012873721438602, 
            -2.22, 0.0132093838072563, 
            -2.21, 0.01355258114642, 
            -2.2, 0.0139034475134986, 
            -2.19, 0.0142621184106689, 
            -2.18, 0.0146287307759893, 
            -2.17, 0.0150034229737322, 
            -2.16, 0.0153863347839255, 
            -2.15, 0.0157776073910905, 
            -2.14, 0.0161773833721661, 
            -2.13, 0.016585806683605, 
            -2.12, 0.0170030226476328, 
            -2.11, 0.0174291779376571, 
            -2.1, 0.0178644205628165, 
            -2.09, 0.018308899851659, 
            -2.08, 0.0187627664349377, 
            -2.07, 0.0192261722275173, 
            -2.06, 0.0196992704093769, 
            -2.05, 0.0201822154057044, 
            -2.04, 0.02067516286607, 
            -2.03, 0.0211782696426723, 
            -2.02, 0.0216916937676468, 
            -2.01, 0.0222155944294315, 
            -2, 0.0227501319481792, 
            -1.99, 0.0232954677502118, 
            -1.98, 0.0238517643415085, 
            -1.97, 0.0244191852802225, 
            -1.96, 0.0249978951482204, 
            -1.95, 0.0255880595216386, 
            -1.94, 0.0261898449404527, 
            -1.93, 0.0268034188770549, 
            -1.92, 0.0274289497038368, 
            -1.91, 0.0280666066597725, 
            -1.9, 0.0287165598160018, 
            -1.89, 0.0293789800404094, 
            -1.88, 0.0300540389611998, 
            -1.87, 0.030741908929466, 
            -1.86, 0.0314427629807527, 
            -1.85, 0.0321567747956137, 
            -1.84, 0.0328841186591639, 
            -1.83, 0.0336249694196283, 
            -1.82, 0.03437950244589, 
            -1.81, 0.0351478935840388, 
            -1.8, 0.0359303191129258, 
            -1.79, 0.0367269556987263, 
            -1.78, 0.0375379803485168, 
            -1.77, 0.0383635703628712, 
            -1.76, 0.0392039032874826, 
            -1.75, 0.0400591568638171, 
            -1.74, 0.0409295089788074, 
            -1.73, 0.0418151376135949, 
            -1.72, 0.0427162207913289, 
            -1.71, 0.0436329365240319, 
            -1.7, 0.044565462758543, 
            -1.69, 0.0455139773215498, 
            -1.68, 0.0464786578637201, 
            -1.67, 0.0474596818029473, 
            -1.66, 0.0484572262667228, 
            -1.65, 0.0494714680336481, 
            -1.64, 0.0505025834741037, 
            -1.63, 0.0515507484900894, 
            -1.62, 0.0526161384542521, 
            -1.61, 0.0536989281481197, 
            -1.6, 0.054799291699558, 
            -1.59, 0.0559174025194694, 
            -1.58, 0.0570534332377542, 
            -1.57, 0.058207555638553, 
            -1.56, 0.059379940594793, 
            -1.55, 0.060570758002059, 
            -1.54, 0.0617801767118119, 
            -1.53, 0.0630083644639784, 
            -1.52, 0.0642554878189358, 
            -1.51, 0.0655217120889165, 
            -1.5, 0.0668072012688581, 
            -1.49, 0.0681121179667254, 
            -1.48, 0.0694366233333317, 
            -1.47, 0.0707808769916855, 
            -1.46, 0.0721450369658938, 
            -1.45, 0.0735292596096484, 
            -1.44, 0.0749336995343271, 
            -1.43, 0.0763585095367391, 
            -1.42, 0.0778038405265463, 
            -1.41, 0.0792698414533924, 
            -1.4, 0.0807566592337711, 
            -1.39, 0.0822644386776689, 
            -1.38, 0.0837933224150143, 
            -1.37, 0.0853434508219669, 
            -1.36, 0.086914961947085, 
            -1.35, 0.088507991437402, 
            -1.34, 0.0901226724644525, 
            -1.33, 0.0917591356502808, 
            -1.32, 0.0934175089934718, 
            -1.31, 0.095097917795239, 
            -1.3, 0.0968004845856103, 
            -1.29, 0.0985253290497478, 
            -1.28, 0.100272567954442, 
            -1.27, 0.102042315074819, 
            -1.26, 0.1038346811213, 
            -1.25, 0.105649773666855, 
            -1.24, 0.107487697074587, 
            -1.23, 0.109348552425692, 
            -1.22, 0.111232437447835, 
            -1.21, 0.113139446443977, 
            -1.2, 0.115069670221708, 
            -1.19, 0.117023196023109, 
            -1.18, 0.119000107455201, 
            -1.17, 0.121000484421018, 
            -1.16, 0.123024403051343, 
            -1.15, 0.12507193563715, 
            -1.14, 0.127143150562798, 
            -1.13, 0.129238112240018, 
            -1.12, 0.131356881042731, 
            -1.11, 0.133499513242747, 
            -1.1, 0.135666060946383, 
            -1.09, 0.137856572032035, 
            -1.08, 0.140071090088769, 
            -1.07, 0.142309654355939, 
            -1.06, 0.14457229966391, 
            -1.05, 0.146859056375896, 
            -1.04, 0.149169950330981, 
            -1.03, 0.151505002788344, 
            -1.02, 0.153864230372735, 
            -1.01, 0.156247645021255, 
            -1, 0.158655253931457, 
            -0.99, 0.161087059510831, 
            -0.98, 0.163543059327692, 
            -0.97, 0.16602324606353, 
            -0.96, 0.168527607466838, 
            -0.95, 0.171056126308482, 
            -0.94, 0.173608780338625, 
            -0.93, 0.176185542245258, 
            -0.92, 0.178786379614372, 
            -0.91, 0.181411254891797, 
            -0.9, 0.18406012534676, 
            -0.89, 0.186732943037173, 
            -0.88, 0.189429654776712, 
            -0.87, 0.192150202103696, 
            -0.86, 0.194894521251808, 
            -0.85, 0.197662543122692, 
            -0.84, 0.20045419326045, 
            -0.83, 0.203269391828068, 
            -0.82, 0.206108053585813, 
            -0.81, 0.208970087871602, 
            -0.8, 0.211855398583397, 
            -0.79, 0.214763884163637, 
            -0.78, 0.217695437585733, 
            -0.77, 0.22064994634265, 
            -0.76, 0.223627292437599, 
            -0.75, 0.226627352376868, 
            -0.74, 0.229649997164791, 
            -0.73, 0.232695092300897, 
            -0.72, 0.235762497779251, 
            -0.71, 0.238852068089987, 
            -0.7, 0.241963652223073, 
            -0.69, 0.245097093674309, 
            -0.68, 0.24825223045357, 
            -0.67, 0.25142889509531, 
            -0.66, 0.254626914671336, 
            -0.65, 0.257846110805865, 
            -0.64, 0.261086299692862, 
            -0.63, 0.264347292115677, 
            -0.62, 0.267628893468983, 
            -0.61, 0.270930903783006, 
            -0.6, 0.274253117750074, 
            -0.59, 0.277595324753465, 
            -0.58, 0.280957308898564, 
            -0.57, 0.284338849046324, 
            -0.56, 0.287739718849027, 
            -0.55, 0.291159686788346, 
            -0.54, 0.294598516215698, 
            -0.53, 0.298055965394876, 
            -0.52, 0.301531787546966, 
            -0.51, 0.305025730897519, 
            -0.5, 0.308537538725987, 
            -0.49, 0.312066949417391, 
            -0.48, 0.315613696516223, 
            -0.47, 0.319177508782556, 
            -0.46, 0.322758110250348, 
            -0.45, 0.32635522028792, 
            -0.44, 0.329968553660594, 
            -0.43, 0.333597820595458, 
            -0.42, 0.33724272684825, 
            -0.41, 0.340902973772323, 
            -0.4, 0.344578258389676, 
            -0.39, 0.348268273464018, 
            -0.38, 0.351972707575837, 
            -0.37, 0.355691245199453, 
            -0.36, 0.359423566782009, 
            -0.35, 0.363169348824381, 
            -0.34, 0.366928263963972, 
            -0.33, 0.370699981059346, 
            -0.32, 0.37448416527668, 
            -0.31, 0.378280478177981, 
            -0.3, 0.382088577811047, 
            -0.29, 0.385908118801123, 
            -0.28, 0.389738752444203, 
            -0.27, 0.39358012680196, 
            -0.26, 0.397431886798239, 
            -0.25, 0.401293674317076, 
            -0.24, 0.405165128302204, 
            -0.23, 0.409045884857994, 
            -0.22, 0.412935577351785, 
            -0.21, 0.416833836517558, 
            -0.2, 0.420740290560897, 
            -0.19, 0.424654565265205, 
            -0.18, 0.428576284099099, 
            -0.17, 0.432505068324962, 
            -0.16, 0.436440537108567, 
            -0.15, 0.440382307629757, 
            -0.14, 0.444329995194094, 
            -0.13, 0.448283213345439, 
            -0.12, 0.452241573979416, 
            -0.11, 0.456204687457683, 
            -0.1, 0.460172162722971, 
            -0.09, 0.464143607414828, 
            -0.08, 0.468118627986013, 
            -0.07, 0.472096829819479, 
            -0.06, 0.476077817345893, 
            -0.05, 0.480061194161628, 
            -0.04, 0.484046563147169, 
            -0.03, 0.488033526585887, 
            -0.02, 0.492021686283098, 
            -0.01, 0.496010643685368, 
            0, 0.5, 
            0.01, 0.503989356314632, 
            0.02, 0.507978313716902, 
            0.03, 0.511966473414113, 
            0.04, 0.515953436852831, 
            0.05, 0.519938805838372, 
            0.06, 0.523922182654107, 
            0.07, 0.527903170180521, 
            0.08, 0.531881372013987, 
            0.09, 0.535856392585172, 
            0.1, 0.539827837277029, 
            0.11, 0.543795312542317, 
            0.12, 0.547758426020584, 
            0.13, 0.551716786654561, 
            0.14, 0.555670004805906, 
            0.15, 0.559617692370243, 
            0.16, 0.563559462891433, 
            0.17, 0.567494931675038, 
            0.18, 0.571423715900901, 
            0.19, 0.575345434734795, 
            0.2, 0.579259709439103, 
            0.21, 0.583166163482442, 
            0.22, 0.587064422648215, 
            0.23, 0.590954115142006, 
            0.24, 0.594834871697796, 
            0.25, 0.598706325682924, 
            0.26, 0.602568113201761, 
            0.27, 0.606419873198039, 
            0.28, 0.610261247555797, 
            0.29, 0.614091881198877, 
            0.3, 0.617911422188953, 
            0.31, 0.621719521822019, 
            0.32, 0.62551583472332, 
            0.33, 0.629300018940654, 
            0.34, 0.633071736036028, 
            0.35, 0.636830651175619, 
            0.36, 0.640576433217991, 
            0.37, 0.644308754800547, 
            0.38, 0.648027292424163, 
            0.39, 0.651731726535982, 
            0.4, 0.655421741610324, 
            0.41, 0.659097026227677, 
            0.42, 0.66275727315175, 
            0.43, 0.666402179404542, 
            0.44, 0.670031446339406, 
            0.45, 0.67364477971208, 
            0.46, 0.677241889749652, 
            0.47, 0.680822491217444, 
            0.48, 0.684386303483777, 
            0.49, 0.687933050582609, 
            0.5, 0.691462461274013, 
            0.51, 0.694974269102481, 
            0.52, 0.698468212453034, 
            0.53, 0.701944034605124, 
            0.54, 0.705401483784302, 
            0.55, 0.708840313211654, 
            0.56, 0.712260281150973, 
            0.57, 0.715661150953676, 
            0.58, 0.719042691101436, 
            0.59, 0.722404675246535, 
            0.6, 0.725746882249926, 
            0.61, 0.729069096216994, 
            0.62, 0.732371106531017, 
            0.63, 0.735652707884322, 
            0.64, 0.738913700307138, 
            0.65, 0.742153889194135, 
            0.66, 0.745373085328664, 
            0.67, 0.74857110490469, 
            0.68, 0.75174776954643, 
            0.69, 0.754902906325691, 
            0.7, 0.758036347776927, 
            0.71, 0.761147931910013, 
            0.72, 0.764237502220749, 
            0.73, 0.767304907699103, 
            0.74, 0.770350002835209, 
            0.75, 0.773372647623132, 
            0.76, 0.776372707562401, 
            0.77, 0.77935005365735, 
            0.78, 0.782304562414267, 
            0.79, 0.785236115836363, 
            0.8, 0.788144601416603, 
            0.81, 0.791029912128398, 
            0.82, 0.793891946414187, 
            0.83, 0.796730608171932, 
            0.84, 0.79954580673955, 
            0.85, 0.802337456877308, 
            0.86, 0.805105478748192, 
            0.87, 0.807849797896304, 
            0.88, 0.810570345223288, 
            0.89, 0.813267056962827, 
            0.9, 0.81593987465324, 
            0.91, 0.818588745108203, 
            0.92, 0.821213620385628, 
            0.93, 0.823814457754742, 
            0.94, 0.826391219661375, 
            0.95, 0.828943873691518, 
            0.96, 0.831472392533162, 
            0.97, 0.83397675393647, 
            0.98, 0.836456940672308, 
            0.99, 0.838912940489169, 
            1, 0.841344746068543, 
            1.01, 0.843752354978745, 
            1.02, 0.846135769627265, 
            1.03, 0.848494997211656, 
            1.04, 0.850830049669019, 
            1.05, 0.853140943624104, 
            1.06, 0.85542770033609, 
            1.07, 0.857690345644061, 
            1.08, 0.859928909911231, 
            1.09, 0.862143427967965, 
            1.1, 0.864333939053617, 
            1.11, 0.866500486757253, 
            1.12, 0.868643118957269, 
            1.13, 0.870761887759982, 
            1.14, 0.872856849437202, 
            1.15, 0.87492806436285, 
            1.16, 0.876975596948657, 
            1.17, 0.878999515578982, 
            1.18, 0.880999892544799, 
            1.19, 0.882976803976891, 
            1.2, 0.884930329778292, 
            1.21, 0.886860553556023, 
            1.22, 0.888767562552165, 
            1.23, 0.890651447574308, 
            1.24, 0.892512302925413, 
            1.25, 0.894350226333145, 
            1.26, 0.8961653188787, 
            1.27, 0.897957684925181, 
            1.28, 0.899727432045558, 
            1.29, 0.901474670950252, 
            1.3, 0.90319951541439, 
            1.31, 0.904902082204761, 
            1.32, 0.906582491006528, 
            1.33, 0.908240864349719, 
            1.34, 0.909877327535548, 
            1.35, 0.911492008562598, 
            1.36, 0.913085038052915, 
            1.37, 0.914656549178033, 
            1.38, 0.916206677584986, 
            1.39, 0.917735561322331, 
            1.4, 0.919243340766229, 
            1.41, 0.920730158546608, 
            1.42, 0.922196159473454, 
            1.43, 0.923641490463261, 
            1.44, 0.925066300465673, 
            1.45, 0.926470740390352, 
            1.46, 0.927854963034106, 
            1.47, 0.929219123008314, 
            1.48, 0.930563376666668, 
            1.49, 0.931887882033275, 
            1.5, 0.933192798731142, 
            1.51, 0.934478287911084, 
            1.52, 0.935744512181064, 
            1.53, 0.936991635536022, 
            1.54, 0.938219823288188, 
            1.55, 0.939429241997941, 
            1.56, 0.940620059405207, 
            1.57, 0.941792444361447, 
            1.58, 0.942946566762246, 
            1.59, 0.944082597480531, 
            1.6, 0.945200708300442, 
            1.61, 0.94630107185188, 
            1.62, 0.947383861545748, 
            1.63, 0.948449251509911, 
            1.64, 0.949497416525896, 
            1.65, 0.950528531966352, 
            1.66, 0.951542773733277, 
            1.67, 0.952540318197053, 
            1.68, 0.95352134213628, 
            1.69, 0.95448602267845, 
            1.7, 0.955434537241457, 
            1.71, 0.956367063475968, 
            1.72, 0.957283779208671, 
            1.73, 0.958184862386405, 
            1.74, 0.959070491021193, 
            1.75, 0.959940843136183, 
            1.76, 0.960796096712517, 
            1.77, 0.961636429637129, 
            1.78, 0.962462019651483, 
            1.79, 0.963273044301274, 
            1.8, 0.964069680887074, 
            1.81, 0.964852106415961, 
            1.82, 0.96562049755411, 
            1.83, 0.966375030580372, 
            1.84, 0.967115881340836, 
            1.85, 0.967843225204386, 
            1.86, 0.968557237019247, 
            1.87, 0.969258091070534, 
            1.88, 0.9699459610388, 
            1.89, 0.970621019959591, 
            1.9, 0.971283440183998, 
            1.91, 0.971933393340227, 
            1.92, 0.972571050296163, 
            1.93, 0.973196581122945, 
            1.94, 0.973810155059547, 
            1.95, 0.974411940478361, 
            1.96, 0.97500210485178, 
            1.97, 0.975580814719777, 
            1.98, 0.976148235658492, 
            1.99, 0.976704532249788, 
            2, 0.977249868051821, 
            2.01, 0.977784405570569, 
            2.02, 0.978308306232353, 
            2.03, 0.978821730357328, 
            2.04, 0.97932483713393, 
            2.05, 0.979817784594296, 
            2.06, 0.980300729590623, 
            2.07, 0.980773827772483, 
            2.08, 0.981237233565062, 
            2.09, 0.981691100148341, 
            2.1, 0.982135579437183, 
            2.11, 0.982570822062343, 
            2.12, 0.982996977352367, 
            2.13, 0.983414193316395, 
            2.14, 0.983822616627834, 
            2.15, 0.98422239260891, 
            2.16, 0.984613665216075, 
            2.17, 0.984996577026268, 
            2.18, 0.985371269224011, 
            2.19, 0.985737881589331, 
            2.2, 0.986096552486501, 
            2.21, 0.98644741885358, 
            2.22, 0.986790616192744, 
            2.23, 0.987126278561398, 
            2.24, 0.987454538564053, 
            2.25, 0.987775527344955, 
            2.26, 0.988089374581453, 
            2.27, 0.988396208478097, 
            2.28, 0.988696155761447, 
            2.29, 0.988989341675589, 
            2.3, 0.989275889978324, 
            2.31, 0.989555922938049, 
            2.32, 0.98982956133128, 
            2.33, 0.990096924440836, 
            2.34, 0.990358130054642, 
            2.35, 0.990613294465161, 
            2.36, 0.990862532469427, 
            2.37, 0.991105957369663, 
            2.38, 0.991343680974483, 
            2.39, 0.991575813600654, 
            2.4, 0.991802464075404, 
            2.41, 0.992023739739266, 
            2.42, 0.992239746449446, 
            2.43, 0.992450588583691, 
            2.44, 0.992656369044652, 
            2.45, 0.992857189264729, 
            2.46, 0.993053149211376, 
            2.47, 0.993244347392859, 
            2.48, 0.993430880864453, 
            2.49, 0.993612845235057, 
            2.5, 0.993790334674224, 
            2.51, 0.993963441919587, 
            2.52, 0.994132258284667, 
            2.53, 0.994296873667049, 
            2.54, 0.994457376556917, 
            2.55, 0.994613854045933, 
            2.56, 0.994766391836444, 
            2.57, 0.994915074251009, 
            2.58, 0.995059984242229, 
            2.59, 0.995201203402874, 
            2.6, 0.995338811976281, 
            2.61, 0.995472888867033, 
            2.62, 0.995603511651879, 
            2.63, 0.995730756590911, 
            2.64, 0.995854698638964, 
            2.65, 0.995975411457242, 
            2.66, 0.996092967425147, 
            2.67, 0.996207437652315, 
            2.68, 0.996318891990825, 
            2.69, 0.9964273990476, 
            2.7, 0.996533026196959, 
            2.71, 0.996635839593331, 
            2.72, 0.996735904184109, 
            2.73, 0.996833283722642, 
            2.74, 0.99692804078135, 
            2.75, 0.997020236764945, 
            2.76, 0.997109931923774, 
            2.77, 0.997197185367235, 
            2.78, 0.997282055077299, 
            2.79, 0.997364597922095, 
            2.8, 0.997444869669572, 
            2.81, 0.997522925001214, 
            2.82, 0.997598817525811, 
            2.83, 0.997672599793268, 
            2.84, 0.997744323308458, 
            2.85, 0.997814038545087, 
            2.86, 0.997881794959595, 
            2.87, 0.99794764100506, 
            2.88, 0.998011624145106, 
            2.89, 0.998073790867812, 
            2.9, 0.998134186699616, 
            2.91, 0.998192856219194, 
            2.92, 0.998249843071324, 
            2.93, 0.998305189980723, 
            2.94, 0.998358938765843, 
            2.95, 0.998411130352635, 
            2.96, 0.998461804788262, 
            2.97, 0.998511001254763, 
            2.98, 0.99855875808266, 
            2.99, 0.998605112764508, 
            3, 0.99865010196837, 
            3.01, 0.998693761551231, 
            3.02, 0.998736126572328, 
            3.03, 0.998777231306408, 
            3.04, 0.998817109256896, 
            3.05, 0.998855793168977, 
            3.06, 0.998893315042591, 
            3.07, 0.998929706145321, 
            3.08, 0.998964997025197, 
            3.09, 0.998999217523386, 
            3.1, 0.999032396786782, 
            3.11, 0.999064563280486, 
            3.12, 0.999095744800178, 
            3.13, 0.999125968484368, 
            3.14, 0.999155260826541, 
            3.15, 0.999183647687171, 
            3.16, 0.999211154305624, 
            3.17, 0.999237805311933, 
            3.18, 0.999263624738446, 
            3.19, 0.999288636031355, 
            3.2, 0.999312862062084, 
            3.21, 0.99933632513856, 
            3.22, 0.99935904701634, 
            3.23, 0.999381048909613, 
            3.24, 0.999402351502066, 
            3.25, 0.999422974957609, 
            3.26, 0.999442938930975, 
            3.27, 0.99946226257817, 
            3.28, 0.999480964566793, 
            3.29, 0.999499063086214, 
            3.3, 0.999516575857616, 
            3.31, 0.999533520143892, 
            3.32, 0.999549912759408, 
            3.33, 0.999565770079618, 
            3.34, 0.99958110805055, 
            3.35, 0.999595942198136, 
            3.36, 0.999610287637418, 
            3.37, 0.9996241590816, 
            3.38, 0.999637570850967, 
            3.39, 0.999650536881662, 
            3.4, 0.999663070734323, 
            3.41, 0.999675185602581, 
            3.42, 0.999686894321419, 
            3.43, 0.999698209375391, 
            3.44, 0.999709142906709, 
            3.45, 0.999719706723184, 
            3.46, 0.999729912306036, 
            3.47, 0.999739770817572, 
            3.48, 0.99974929310872, 
            3.49, 0.999758489726432, 
            3.5, 0.999767370920964, 
            3.51, 0.999775946653009, 
            3.52, 0.999784226600705, 
            3.53, 0.999792220166519, 
            3.54, 0.999799936483993, 
            3.55, 0.999807384424364, 
            3.56, 0.999814572603067, 
            3.57, 0.999821509386095, 
            3.58, 0.999828202896254, 
            3.59, 0.99983466101928, 
            3.6, 0.999840891409842, 
            3.61, 0.999846901497426, 
            3.62, 0.999852698492093, 
            3.63, 0.999858289390124, 
            3.64, 0.999863680979554, 
            3.65, 0.999868879845579, 
            3.66, 0.999873892375862, 
            3.67, 0.999878724765715, 
            3.68, 0.999883383023185, 
            3.69, 0.999887872974018, 
            3.7, 0.999892200266523, 
            3.71, 0.999896370376326, 
            3.72, 0.999900388611024, 
            3.73, 0.999904260114731, 
            3.74, 0.999907989872526, 
            3.75, 0.999911582714799, 
            3.76, 0.999915043321502, 
            3.77, 0.999918376226297, 
            3.78, 0.999921585820616, 
            3.79, 0.999924676357621, 
            3.8, 0.999927651956075, 
            3.81, 0.99993051660412, 
            3.82, 0.99993327416297, 
            3.83, 0.999935928370511, 
            3.84, 0.999938482844817, 
            3.85, 0.999940941087581, 
            3.86, 0.999943306487466, 
            3.87, 0.999945582323366, 
            3.88, 0.999947771767598, 
            3.89, 0.999949877889004, 
            3.9, 0.999951903655982, 
            3.91, 0.999953851939444, 
            3.92, 0.999955725515688, 
            3.93, 0.999957527069211, 
            3.94, 0.999959259195441, 
            3.95, 0.999960924403402, 
            3.96, 0.999962525118309, 
            3.97, 0.999964063684097, 
            3.98, 0.999965542365885, 
            3.99, 0.999966963352371, 
            4, 0.999968328758167, 
            4.01, 0.999969640626073, 
            4.02, 0.999970900929288, 
            4.03, 0.999972111573559, 
            4.04, 0.999973274399281, 
            4.05, 0.999974391183526, 
            4.06, 0.999975463642034, 
            4.07, 0.999976493431131, 
            4.08, 0.999977482149611, 
            4.09, 0.999978431340552, 
            4.1, 0.999979342493088, 
            4.11, 0.999980217044132, 
            4.12, 0.999981056380049, 
            4.13, 0.999981861838282, 
            4.14, 0.999982634708926, 
            4.15, 0.99998337623627, 
            4.16, 0.999984087620281, 
            4.17, 0.999984770018052, 
            4.18, 0.999985424545209, 
            4.19, 0.999986052277273, 
            4.2, 0.999986654250984, 
            4.21, 0.999987231465586, 
            4.22, 0.999987784884075, 
            4.23, 0.999988315434405, 
            4.24, 0.999988824010668, 
            4.25, 0.999989311474225, 
            4.26, 0.999989778654816, 
    };
    // @formatter:on

}
