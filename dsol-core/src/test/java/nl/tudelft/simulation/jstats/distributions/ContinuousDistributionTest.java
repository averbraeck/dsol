package nl.tudelft.simulation.jstats.distributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.djutils.stats.summarizers.Tally;
import org.junit.Test;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistributionTest tests the correct statistics of the Normal and LogNormal distributions, based on a tally of their values.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ContinuousDistributionTest
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
        testDist("DistBeta", new DistBeta(this.stream, 1.0, 2.0), 1.0 / (1.0 + 2.0),
                (1.0 * 2.0) / ((1.0 + 2.0) * (1.0 + 2.0) * (1.0 + 2.0 + 1.0)), 0.0, 1.0, 0.01);
        testDist("DistConstant", new DistConstant(this.stream, 12.1), 12.1, 0.0, 12.1, 12.1, 0.001);
        testDist("DistErlang", new DistErlang(this.stream, 2.0, 1), 2.0, 2.0 * 2.0, 0.0, nan, 0.05);
        testDist("DistErlang", new DistErlang(this.stream, 0.5, 4), 4.0 * 0.5, 4.0 * 0.5 * 0.5, 0.0, nan, 0.05);
        testDist("DistErlang", new DistErlang(this.stream, 0.5, 40), 40.0 * 0.5, 40.0 * 0.5 * 0.5, 0.0, nan, 0.05);
        testDist("DistExponential", new DistExponential(this.stream, 1.2), 1.2, 1.2 * 1.2, 0.0, nan, 0.01);
        testDist("DistGamma", new DistGamma(this.stream, 2.0, 4.0), 2.0 * 4.0, 2.0 * 4.0 * 4.0, 0.0, nan, 0.05);
        testDist("DistGamma", new DistGamma(this.stream, 3.0, 4.0), 3.0 * 4.0, 3.0 * 4.0 * 4.0, 0.0, nan, 0.05);
        testDist("DistGamma", new DistGamma(this.stream, 0.999, 2.0), 0.999 * 2.0, 0.999 * 2.0 * 2.0, 0.0, nan, 0.05);
        testDist("DistGamma", new DistGamma(this.stream, 1.0, 4.0), 1.0 * 4.0, 1.0 * 4.0 * 4.0, 0.0, nan, 0.05);
        testDist("DistGamma", new DistGamma(this.stream, 0.5, 0.2), 0.5 * 0.2, 0.5 * 0.2 * 0.2, 0.0, nan, 0.001);
        testDist("DistPearson5", new DistPearson5(this.stream, 3, 1), 0.5, 0.25, 0.0, nan, 0.01);
        testDist("DistPearson6", new DistPearson6(this.stream, 2, 3, 4), 4.0 * 2 / (3 - 1),
                4.0 * 4 * 2 * (2 + 3 - 1) / ((3 - 1) * (3 - 1) * (3 - 2)), 0.0, nan, 0.05);
        testDist("DistTriangular", new DistTriangular(this.stream, 1, 4, 9), (1 + 4 + 9) / 3.0,
                (1 * 1 + 4 * 4 + 9 * 9 - 1 * 4 - 1 * 9 - 4 * 9) / 18.0, 1.0, 9.0, 0.01);
        testDist("DistUniform", new DistUniform(this.stream, 0, 1), 0.5, 1.0 / 12.0, 0.0, 1.0, 0.01);
        testDist("DistWeibull", new DistWeibull(this.stream, 1.5, 1), 0.9027, 0.3756, 0.0, nan, 0.01);
    }

    /**
     * @param name String; the name of the distribution to test
     * @param dist DistContinuous; the distribution to test
     * @param expectedMean double; the expected mean of a large number of samples
     * @param expectedVariance double; the expected variance of a large number of samples; test the standard deviations
     * @param expectedMin double; the expected lowest value of a large number of samples, or NaN if unbounded
     * @param expectedMax double; the expected highest value of a large number of samples, or NaN if unbounded
     * @param precision double; the precision for mean, standard deviation, min and max
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
     * Test the Beta distribution.
     */
    @Test
    public void testBeta()
    {
        this.stream = new MersenneTwister(10L);
        DistBeta dist = new DistBeta(this.stream, 1.5, 2.5);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1.5, dist.getAlpha1(), 0.0001);
        assertEquals(2.5, dist.getAlpha2(), 0.0001);
        assertTrue(dist.toString().contains("Beta"));
        assertTrue(dist.toString().contains("1.5"));
        assertTrue(dist.toString().contains("2.5"));
        double value = dist.draw();
        assertTrue(value >= 0 && value <= 1);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(distBeta(1.5, 2.5, 0.5), dist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(distBeta(1.5, 2.5, 0.25), dist.getProbabilityDensity(0.25), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(-0.1), 0.0001);

        Try.testFail(() ->
        { new DistBeta(null, 0.1, 2.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistBeta(ContinuousDistributionTest.this.stream, -0.1, 1.0); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistBeta(ContinuousDistributionTest.this.stream, 2.0, -1.0); }, IllegalArgumentException.class);

        DistBeta dist1 = new DistBeta(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
        Try.testFail(() ->
        { dist1.setStream(null); });
        Try.testFail(() ->
        { new DistBeta(null, 1, 2); });
    }

    /**
     * Test the Constant distribution.
     */
    @Test
    public void testConstant()
    {
        this.stream = new MersenneTwister(10L);
        DistConstant dist = new DistConstant(this.stream, 7.1);
        assertEquals(this.stream, dist.getStream());
        assertEquals(7.1, dist.getConstant(), 0.0001);
        assertTrue(dist.toString().contains("Constant"));
        assertTrue(dist.toString().contains("7.1"));
        double value = dist.draw();
        assertTrue(value == 7.1);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(1.0, dist.getProbabilityDensity(7.1), 0.0001);

        Try.testFail(() ->
        { new DistConstant(null, 2.0); }, NullPointerException.class);

        DistConstant dist1 = new DistConstant(new MersenneTwister(10L), 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Erlang distribution.
     */
    @Test
    public void testErlang()
    {
        this.stream = new MersenneTwister(10L);
        DistErlang dist = new DistErlang(this.stream, 2.5, 3);
        assertEquals(this.stream, dist.getStream());
        assertEquals(3, dist.getK(), 0.0001);
        assertEquals(2.5, dist.getScale(), 0.0001);
        assertTrue(dist.toString().contains("Erlang"));
        assertTrue(dist.toString().contains("3"));
        assertTrue(dist.toString().contains("2.5"));
        double value = dist.draw();
        assertTrue(value > 0);
        assertEquals(0.0, dist.getProbabilityDensity(-0.1), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(distErlang(3, 2.5, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(distErlang(3, 2.5, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(distErlang(3, 2.5, 3.0), dist.getProbabilityDensity(3.0), 0.0001);
        assertEquals(distErlang(3, 2.5, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(distErlang(3, 2.5, 5.0), dist.getProbabilityDensity(5.0), 0.0001);

        dist = new DistErlang(this.stream, 2.5, 30); // above gamma threshold
        value = dist.draw();
        assertTrue(value > 0);

        Try.testFail(() ->
        { new DistErlang(null, 2.0, 1); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistErlang(ContinuousDistributionTest.this.stream, 1.0, 0); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistErlang(ContinuousDistributionTest.this.stream, -1.0, 5); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistErlang(ContinuousDistributionTest.this.stream, 0.0, 5); }, IllegalArgumentException.class);

        DistErlang dist1 = new DistErlang(new MersenneTwister(10L), 1, 2); // below gamma threshold
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);

        dist1 = new DistErlang(new MersenneTwister(10L), 2, 15); // above gamma threshold
        v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Exponential distribution.
     */
    @Test
    public void testExponential()
    {
        this.stream = new MersenneTwister(10L);
        DistExponential dist = new DistExponential(this.stream, 2.5);
        assertEquals(this.stream, dist.getStream());
        assertEquals(2.5, dist.getMean(), 0.0001);
        assertTrue(dist.toString().contains("Exponential"));
        assertTrue(dist.toString().contains("2.5"));
        double value = dist.draw();
        assertTrue(value >= 0);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);
        double l = 1 / 2.5;
        assertEquals(l * Math.exp(-l * 0.0), dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(l * Math.exp(-l * 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(l * Math.exp(-l * 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(l * Math.exp(-l * 3.0), dist.getProbabilityDensity(3.0), 0.0001);
        assertEquals(l * Math.exp(-l * 4.0), dist.getProbabilityDensity(4.0), 0.0001);

        Try.testFail(() ->
        { new DistExponential(null, 2.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistExponential(ContinuousDistributionTest.this.stream, -0.1); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistExponential(ContinuousDistributionTest.this.stream, 0.0); }, IllegalArgumentException.class);

        DistExponential dist1 = new DistExponential(new MersenneTwister(10L), 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Gamma distribution.
     */
    @Test
    public void testGamma()
    {
        this.stream = new MersenneTwister(10L);
        DistGamma dist = new DistGamma(this.stream, 1.5, 2.5);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1.5, dist.getShape(), 0.0001);
        assertEquals(2.5, dist.getScale(), 0.0001);
        assertTrue(dist.toString().contains("Gamma"));
        assertTrue(dist.toString().contains("1.5"));
        assertTrue(dist.toString().contains("2.5"));
        double value = dist.draw();
        assertTrue(value > 0);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(distGamma(1.5, 2.5, 0.5), dist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(distGamma(1.5, 2.5, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(distGamma(1.5, 2.5, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(distGamma(1.5, 2.5, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(distGamma(1.5, 2.5, 8.0), dist.getProbabilityDensity(8.0), 0.0001);

        dist = new DistGamma(this.stream, 0.5, 2.5);
        value = dist.draw();
        assertTrue(value > 0);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);
        assertEquals(distGamma(0.5, 2.5, 0.5), dist.getProbabilityDensity(0.5), 0.0001);
        assertEquals(distGamma(0.5, 2.5, 1.0), dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(distGamma(0.5, 2.5, 2.0), dist.getProbabilityDensity(2.0), 0.0001);
        assertEquals(distGamma(0.5, 2.5, 4.0), dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(distGamma(0.5, 2.5, 8.0), dist.getProbabilityDensity(8.0), 0.0001);

        dist = new DistGamma(this.stream, 100.0, 0.1);
        value = dist.draw();
        assertTrue(value > 0);

        dist = new DistGamma(this.stream, 0.9999, 0.1);
        value = dist.draw();
        assertTrue(value > 0);

        dist = new DistGamma(this.stream, 0.1, 100.0);
        value = dist.draw();
        assertTrue(value > 0);

        Try.testFail(() ->
        { new DistGamma(null, 1.0, 2.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistGamma(ContinuousDistributionTest.this.stream, -0.1, 1.0); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistGamma(ContinuousDistributionTest.this.stream, 2.0, -1.0); }, IllegalArgumentException.class);

        DistGamma dist1 = new DistGamma(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    // @formatter:off
    /** Calculation of Pearson5(2, 1) probability density function in R with the following script.<br>
     * <pre>
     * library(PearsonDS)
     * options(digits=16)
     * cat(dpearsonV(seq(0.1,4.0,by=0.1),shape=2,scale=1,location=0), sep="\n")
     * </pre>
     */
    private static final double[] PEARSON5_R = {
        0, 0, 
        0.1, 0.0453999297624848, 
        0.2, 0.842243374885683, 
        0.3, 1.3212590128612, 
        0.4, 1.28257810349841, 
        0.5, 1.0826822658929, 
        0.6, 0.874424087210934, 
        0.7, 0.698691068343369, 
        0.8, 0.559579681367558, 
        0.9, 0.451567884510158, 
        1, 0.367879441171442, 
        1.1, 0.302697461704833, 
        1.2, 0.25150359288604, 
        1.3, 0.21091004516667, 
        1.4, 0.178404394882271, 
        1.5, 0.152123590824471, 
        1.6, 0.130679059697019, 
        1.7, 0.113027961123946, 
        1.8, 0.0983802161758286, 
        1.9, 0.0861317267679299, 
        2, 0.0758163324640791, 
        2.1, 0.0670710676617483, 
        2.2, 0.0596108582776372, 
        2.3, 0.0532099442823959, 
        2.4, 0.04768812429112, 
        2.5, 0.0429004829462809, 
        2.6, 0.0387296539783446, 
        2.7, 0.0350799446465025, 
        2.8, 0.0318728378906309, 
        2.9, 0.0290435225286957, 
        3, 0.0265381966879181, 
        3.1, 0.0243119573016754, 
        3.2, 0.0223271371138501, 
        3.3, 0.0205519858340651, 
        3.4, 0.0189596177746153, 
        3.5, 0.0175271671854294, 
        3.6, 0.0162351064899898, 
        3.7, 0.0150666930573313, 
        3.8, 0.014007517979912, 
        3.9, 0.0130451362502105, 
        4, 0.0121687622354907};
    // @formatter:on

    /**
     * Test the Pearson5 distribution.
     */
    @Test
    public void testPearson5()
    {
        this.stream = new MersenneTwister(10L);
        DistPearson5 dist = new DistPearson5(this.stream, 4, 2);
        assertEquals(this.stream, dist.getStream());
        assertEquals(4, dist.getAlpha(), 0.0001);
        assertEquals(2, dist.getBeta(), 0.0001);
        assertTrue(dist.toString().contains("Pearson5"));
        assertTrue(dist.toString().contains("4"));
        assertTrue(dist.toString().contains("2"));
        double value = dist.draw();
        assertTrue(value >= 0);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);

        dist = new DistPearson5(this.stream, 2, 1);
        for (int i = 0; i < PEARSON5_R.length / 2; i++)
        {
            assertEquals(PEARSON5_R[2 * i + 1], dist.getProbabilityDensity(PEARSON5_R[2 * i]), 0.0001);
        }

        Try.testFail(() ->
        { new DistPearson5(null, 2.0, 1.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistPearson5(ContinuousDistributionTest.this.stream, -0.1, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson5(ContinuousDistributionTest.this.stream, 0.0, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson5(ContinuousDistributionTest.this.stream, 4, -2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson5(ContinuousDistributionTest.this.stream, 4, 0); }, IllegalArgumentException.class);

        DistPearson5 dist1 = new DistPearson5(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    // @formatter:off
    /** Calculation of Pearson6(2, 3, 4) probability density function in R with the following script.<br>
     * <pre>
     * library(PearsonDS)
     * options(digits=16)
     * cat(dpearsonVI(seq(0.0,10.0,by=0.1),a=2,b=3,scale=4,location=0), sep="\n")
     * </pre>
     */
    private static final double[] PEARSON6_R = {
            0, 0, 
            0.1, 0.0662890715707137, 
            0.2, 0.117528924970268, 
            0.3, 0.156725692278776, 
            0.4, 0.186276396917746, 
            0.5, 0.208098358989991, 
            0.6, 0.22372953088423, 
            0.7, 0.234406672738247, 
            0.8, 0.241126543209876, 
            0.9, 0.244694005031216, 
            1, 0.24576, 
            1.1, 0.24485164420618, 
            1.2, 0.242396166908613, 
            1.3, 0.23874001913263, 
            1.4, 0.234164177104221, 
            1.5, 0.228896436532527, 
            1.6, 0.223121318498244, 
            1.7, 0.216988073020216, 
            1.8, 0.210617162402749, 
            1.9, 0.204105525863515, 
            2, 0.19753086419753, 
            2.1, 0.190955134197302, 
            2.2, 0.184427404081944, 
            2.3, 0.177986190901477, 
            2.4, 0.171661376953125, 
            2.5, 0.16547578327628, 
            2.6, 0.159446463205313, 
            2.7, 0.153585766922561, 
            2.8, 0.147902218321985, 
            2.9, 0.142401237764448, 
            3, 0.137085738085321, 
            3.1, 0.131956616196296, 
            3.2, 0.127013158563227, 
            3.3, 0.122253375549485, 
            3.4, 0.117674276938075, 
            3.5, 0.113272098765432, 
            3.6, 0.109042489819672, 
            3.7, 0.104980664699523, 
            3.8, 0.101081529136102, 
            3.9, 0.0973397822988683, 
            4, 0.09375, 
            4.1, 0.0903067020460723, 
            4.2, 0.0870044064365618, 
            4.3, 0.0838376726543463, 
            4.4, 0.0808011359170598, 
            4.5, 0.0778895339460241, 
            4.6, 0.0750977275502468, 
            4.7, 0.0724207161073927, 
            4.8, 0.0698536488441549, 
            4.9, 0.0673918326688645, 
            5, 0.0650307371843723, 
            5.1, 0.0627659974050269, 
            5.2, 0.060593414614479, 
            5.3, 0.0585089557282037, 
            5.4, 0.0565087514636845, 
            5.5, 0.0545890935701682, 
            5.6, 0.0527464313271604, 
            5.7, 0.0509773674850205, 
            5.8, 0.0492786537910088, 
            5.9, 0.0476471862189985, 
            6, 0.0460799999999999, 
            6.1, 0.0445742645330009, 
            6.2, 0.0431272782408612, 
            6.3, 0.0417364634236433, 
            6.4, 0.0403993611514356, 
            6.5, 0.0391136262301054, 
            6.6, 0.0378770222662346, 
            6.7, 0.0366874168515116, 
            6.8, 0.0355427768818907, 
            6.9, 0.0344411640227219, 
            7, 0.0333807303276602, 
            7.1, 0.032359714016377, 
            7.2, 0.0313764354138156, 
            7.3, 0.030429293051873, 
            7.4, 0.029516759932897, 
            7.5, 0.0286373799531814, 
            7.6, 0.0277897644836961, 
            7.7, 0.0269725891045436, 
            7.8, 0.0261845904890693, 
            7.9, 0.025424563433126, 
            8, 0.0246913580246913, 
            8.1, 0.0239838769488323, 
            8.2, 0.023301072922885, 
            8.3, 0.0226419462566635, 
            8.4, 0.0220055425325047, 
            8.5, 0.0213909504, 
            8.6, 0.0207972994803356, 
            8.7, 0.0202237583752675, 
            8.8, 0.0196695327758789, 
            8.9, 0.0191338636664075, 
            9, 0.0186160256185815, 
            9.1, 0.0181153251720602, 
            9.2, 0.0176310992967414, 
            9.3, 0.0171627139328636, 
            9.40000000000001, 0.0167095626050008, 
            9.5, 0.0162710651062133, 
            9.6, 0.0158466662487842, 
            9.70000000000001, 0.0154358346781359, 
            9.80000000000001, 0.0150380617466766, 
            9.90000000000001, 0.0146528604444849, 
            10, 0.0142797643838876};
    // @formatter:on

    /**
     * Test the Pearson6 distribution.
     */
    @Test
    public void testPearson6()
    {
        this.stream = new MersenneTwister(10L);
        DistPearson6 dist = new DistPearson6(this.stream, 4, 3, 2);
        assertEquals(this.stream, dist.getStream());
        assertEquals(4, dist.getAlpha1(), 0.0001);
        assertEquals(3, dist.getAlpha2(), 0.0001);
        assertEquals(2, dist.getBeta(), 0.0001);
        assertTrue(dist.toString().contains("Pearson6"));
        assertTrue(dist.toString().contains("4"));
        assertTrue(dist.toString().contains("3"));
        assertTrue(dist.toString().contains("2"));
        double value = dist.draw();
        assertTrue(value >= 0);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);

        dist = new DistPearson6(this.stream, 2, 3, 4);
        for (int i = 0; i < PEARSON6_R.length / 2; i++)
        {
            assertEquals(PEARSON6_R[2 * i + 1], dist.getProbabilityDensity(PEARSON6_R[2 * i]), 0.0001);
        }

        Try.testFail(() ->
        { new DistPearson6(null, 2.0, 1.0, 3.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, -0.1, 2, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, 0.0, 2, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, 4, -2, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, 4, 0, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, 4, 2, -3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistPearson6(ContinuousDistributionTest.this.stream, 4, 3, 0); }, IllegalArgumentException.class);

        DistPearson6 dist1 = new DistPearson6(new MersenneTwister(10L), 3, 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Triangular distribution.
     */
    @Test
    public void testTriangular()
    {
        this.stream = new MersenneTwister(10L);
        DistTriangular dist = new DistTriangular(this.stream, 1, 2, 4);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1, dist.getMin(), 0.0001);
        assertEquals(2, dist.getMode(), 0.0001);
        assertEquals(4, dist.getMax(), 0.0001);
        assertTrue(dist.toString().contains("Triangular"));
        assertTrue(dist.toString().contains("1"));
        assertTrue(dist.toString().contains("2"));
        assertTrue(dist.toString().contains("4"));
        double value = dist.draw();
        assertTrue(value >= 1 && value <= 4);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(1.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(4.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(5.0), 0.0001);

        for (double x = 0; x <= 5; x += 0.02)
        {
            if (x <= 2 && x >= 1)
            {
                assertEquals(2 * (x - 1) / ((4 - 1) * (2 - 1)), dist.getProbabilityDensity(x), 0.0001);
            }
            else if (x >= 2 && x <= 4)
            {
                assertEquals(2 * (4 - x) / ((4 - 1) * (4 - 2)), dist.getProbabilityDensity(x), 0.0001);
            }
            else
            {
                assertEquals(0, dist.getProbabilityDensity(x), 0.0001);
            }
        }

        Try.testFail(() ->
        { new DistTriangular(null, 1.0, 2.0, 3.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistTriangular(ContinuousDistributionTest.this.stream, 2, 1, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistTriangular(ContinuousDistributionTest.this.stream, 2, 2, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistTriangular(ContinuousDistributionTest.this.stream, 2, 4, 3); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistTriangular(ContinuousDistributionTest.this.stream, 5, 5, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistTriangular(ContinuousDistributionTest.this.stream, 5, 2, 2); }, IllegalArgumentException.class);

        DistTriangular dist1 = new DistTriangular(new MersenneTwister(10L), 1, 2, 3);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Uniform distribution.
     */
    @Test
    public void testUniform()
    {
        this.stream = new MersenneTwister(10L);
        DistUniform dist = new DistUniform(this.stream, 1, 4);
        assertEquals(this.stream, dist.getStream());
        assertEquals(1, dist.getMin(), 0.0001);
        assertEquals(4, dist.getMax(), 0.0001);
        assertTrue(dist.toString().contains("Uniform"));
        assertTrue(dist.toString().contains("1"));
        assertTrue(dist.toString().contains("4"));
        double value = dist.draw();
        assertTrue(value >= 1 && value <= 4);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);
        assertEquals(1. / 3., dist.getProbabilityDensity(1), 0.0001);
        assertEquals(1. / 3., dist.getProbabilityDensity(4), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(0.9999), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(4.0001), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(5.0), 0.0001);

        for (double x = 0; x <= 5; x += 0.02)
        {
            if (x >= 1 && x <= 4)
            {
                assertEquals(1.0 / (4 - 1), dist.getProbabilityDensity(x), 0.0001);
            }
            else
            {
                assertEquals(0, dist.getProbabilityDensity(x), 0.0001);
            }
        }

        Try.testFail(() ->
        { new DistUniform(null, 1.0, 2.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistUniform(ContinuousDistributionTest.this.stream, 2, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistUniform(ContinuousDistributionTest.this.stream, 3, 2); }, IllegalArgumentException.class);

        DistUniform dist1 = new DistUniform(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /**
     * Test the Weibull distribution.
     */
    @Test
    public void testWeibull()
    {
        this.stream = new MersenneTwister(10L);
        DistWeibull dist = new DistWeibull(this.stream, 3, 1);
        assertEquals(this.stream, dist.getStream());
        assertEquals(3, dist.getAlpha(), 0.0001);
        assertEquals(1, dist.getBeta(), 0.0001);
        assertTrue(dist.toString().contains("Weibull"));
        assertTrue(dist.toString().contains("3"));
        assertTrue(dist.toString().contains("1"));
        double value = dist.draw();
        assertTrue(value >= 0);
        assertEquals(0.0, dist.getProbabilityDensity(-1.0), 0.0001);
        assertEquals(0.0, dist.getProbabilityDensity(0.0), 0.0001);

        dist = new DistWeibull(this.stream, 3, 2);
        for (double a = 0.5; a <= 5; a += 0.5)
        {
            for (double b = 0.5; b <= 5; b += 0.5)
            {
                dist = new DistWeibull(this.stream, a, b);
                for (double x = 0.02; x <= 10; x += 0.02)
                {
                    assertEquals("a=" + a + ", b=" + b + ", x=" + x,
                            a * Math.pow(b, -a) * Math.pow(x, a - 1) * Math.exp(-Math.pow(x / b, a)),
                            dist.getProbabilityDensity(x), 0.0001);
                }
            }
        }

        Try.testFail(() ->
        { new DistWeibull(null, 1.0, 2.0); }, NullPointerException.class);
        Try.testFail(() ->
        { new DistWeibull(ContinuousDistributionTest.this.stream, 0, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistWeibull(ContinuousDistributionTest.this.stream, -1, 2); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistWeibull(ContinuousDistributionTest.this.stream, 1, 0); }, IllegalArgumentException.class);
        Try.testFail(() ->
        { new DistWeibull(ContinuousDistributionTest.this.stream, 1, -2); }, IllegalArgumentException.class);

        DistWeibull dist1 = new DistWeibull(new MersenneTwister(10L), 1, 2);
        double v = dist1.draw();
        dist1.setStream(new MersenneTwister(10L));
        assertEquals(v, dist1.draw(), 1E-6);
    }

    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */
    /* ************************************************************************************************************** */

    /**
     * Calculate ln(Gamma(x)). Based on https://introcs.cs.princeton.edu/java/91float/Gamma.java.html.
     * @param x double; the value for which to calculate the logarithm of the Gamma function
     * @return double; ln(Gamma(x))
     */
    private static double logGamma(final double x)
    {
        // @formatter:off
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                         + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                         +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
        // @formatter:on
    }

    /**
     * Calculate Gamma(x). Based on https://introcs.cs.princeton.edu/java/91float/Gamma.java.html.
     * @param x double; the value to calculate the Gamma function of
     * @return double; Gamma(x)
     */
    private static double gamma(final double x)
    {
        return Math.exp(logGamma(x));
    }

    /**
     * Calculate Beta(p, q). From: https://mathworld.wolfram.com/BetaFunction.html.
     * @param a double; param 1
     * @param b double; param 2
     * @return Beta(p, q)
     */
    private static double beta(final double a, final double b)
    {
        return gamma(a) * gamma(b) / gamma(a + b);
    }

    /**
     * Calculate probability density of DistBeta(a, b) for value x. From: https://mathworld.wolfram.com/BetaDistribution.html.
     * @param a double; parameter 1
     * @param b double; parameter 2
     * @param x double; value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double distBeta(final double a, final double b, final double x)
    {
        return Math.pow(1 - x, b - 1) * Math.pow(x, a - 1) / beta(a, b);
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

    /**
     * Calculate probability density of DistErlank(k, b) for value x. From:
     * https://mathworld.wolfram.com/ErlangDistribution.html.
     * @param k double; parameter 1
     * @param scale double; parameter 2, 1/rate
     * @param x double; value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double distErlang(final int k, final double scale, final double x)
    {
        double lambda = 1.0 / scale;
        return lambda * Math.pow(lambda * x, k - 1) * Math.exp(-lambda * x) / fac(k - 1);
    }

    /**
     * Calculate probability density of DistGamma(alpha, theta) for value x. From:
     * https://mathworld.wolfram.com/GammaDistribution.html.
     * @param alpha double; shape parameter
     * @param theta double; scale parameter
     * @param x double; value
     * @return probability density of DistBeta(a, b) for value x
     */
    private static double distGamma(final double alpha, final double theta, final double x)
    {
        return Math.pow(x, alpha - 1) * Math.exp(-x / theta) / (gamma(alpha) * Math.pow(theta, alpha));
    }

}
