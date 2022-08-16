package nl.tudelft.simulation.jstats.distributions.empirical;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * Test the CumulativeProbabilities helper class.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class CumulativeProbabilitiesTest
{
    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and double[] values.
     */
    @Test
    public void testCreateCumulativeDoubleDoubleNotInterpolated()
    {
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntry(0.1));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getFloorEntry(1.0));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getCeilingEntry(1.0));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        assertEquals(new DistributionEntry(1.0, 0.1), de.getPrevEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getPrevEntry(0.5));
        assertNull(de.getPrevEntry(0.1));
        assertNull(de.getPrevEntry(0.0));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getNextEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getNextEntry(0.5));
        assertNull(de.getNextEntry(1.0));
        assertNull(de.getNextEntry(1.1));
        assertNull(de.getFloorEntryForValue(0.0));
        assertNull(de.getCeilingEntryForValue(5.0));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getFloorEntryForValue(2.0));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntryForValue(2.0));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntryForValue(1.0));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntryForValue(1.0));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntryForValue(-1.0));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getFloorEntryForValue(4.0));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getFloorEntryForValue(8.0));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getCeilingEntryForValue(4.0));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getFloorEntryForValue(3.5));
        assertEquals(new DistributionEntry(4.0, 1.0), de.getCeilingEntryForValue(3.5));
        CumulativeProbabilities.createDiscreteDistribution(new Double[] {10.0}, new double[] {1.0});

        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.2, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {-0.1, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.0, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.1, 0.5, 0.2, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.5, 1.001}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.5, 0.9}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution((double[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, (double[]) null); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(new double[] {2, 3, 1, 4}, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, new double[] {}); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(new double[] {}, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(new double[] {}, new double[] {}); });
    }

    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and double[] values.
     */
    @Test
    public void testCreateCumulativeDoubleDoubleInterpolated()
    {
        double[] cpd = {0.0, 0.5, 0.8, 1.0};
        double[] vd = {-2.0, 2.0, 3.0, 4.0};
        InterpolatedEmpiricalDistribution de = CumulativeProbabilities.createInterpolatedDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(new DistributionEntry(-2.0, 0.0), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(-2.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);

        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.2, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {-0.1, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.0, 0.0, 0.5, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.0, 0.5, 0.2, 1.0}); });
        Try.testFail(
                () -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.5, 1.001}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.5, 0.9}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution((double[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, (double[]) null); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {0.1, 0.2, 1.0}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(new double[] {2, 3, 1, 4}, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, new double[] {}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(new double[] {}, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(new double[] {}, new double[] {}); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(new double[] {1.0}, new double[] {1.0}); });
    }

    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and Number[] values.
     */
    @Test
    public void testCreateCumulativeDoubleNumberNotInterpolated()
    {
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        Number[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().toArray(new Number[0]));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntry(0.1));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution((Double[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and Number[] values.
     */
    @Test
    public void testCreateCumulativeDoubleNumberInterpolated()
    {
        double[] cpd = {0.0, 0.5, 0.8, 1.0};
        Number[] vd = {-2.0, 2.0, 3.0, 4.0};
        InterpolatedEmpiricalDistribution de = CumulativeProbabilities.createInterpolatedDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().toArray(new Number[0]));
        assertEquals(new DistributionEntry(-2.0, 0.0), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(-2.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution((Double[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and long[] values.
     */
    @Test
    public void testCreateCumulativeLongNumberNotInterpolated()
    {
        double[] cpd = {0.1, 0.5, 0.8, 1.0};
        long[] vd = {1L, 2L, 3L, 4L};
        DiscreteEmpiricalDistribution de = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getCeilingEntry(0.1));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1L, de.getLowestValue().longValue());
        assertEquals(4L, de.getHighestValue().longValue());
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution((long[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a cumulative distribution with double[] cumulative distribution and long[] values.
     */
    @Test
    public void testCreateCumulativeLongNumberInterpolated()
    {
        double[] cpd = {0.0, 0.5, 0.8, 1.0};
        long[] vd = {-2L, 2L, 3L, 4L};
        InterpolatedEmpiricalDistribution de = CumulativeProbabilities.createInterpolatedDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertArrayEquals(cpd, de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(-2L, 0.0), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(3L, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(2L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(3L, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(-2L, de.getLowestValue().longValue());
        assertEquals(4L, de.getHighestValue().longValue());
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution((long[]) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a cumulative distribution with List&lt;Double&gt; cumulative distribution and
     * List&lt;Double&gt; values.
     */
    @Test
    public void testCreateCumulativeListNotInterpolated()
    {
        List<Double> cpd = Arrays.asList(0.1, 0.5, 0.8, 1.0);
        List<Double> vd = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        DiscreteEmpiricalDistribution de = CumulativeProbabilities.createDiscreteDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertEquals(cpd, de.getCumulativeProbabilities());
        assertEquals(vd, de.getValues());
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntry(0.1));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution((List<Double>) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(vd, (List<Double>) null); });
    }

    /**
     * Test the construction from a cumulative distribution with List&lt;Double&gt; cumulative distribution and
     * List&lt;Double&gt; values.
     */
    @Test
    public void testCreateCumulativeListInterpolated()
    {
        List<Double> cpd = Arrays.asList(0.0, 0.5, 0.8, 1.0);
        List<Double> vd = Arrays.asList(-2.0, 2.0, 3.0, 4.0);
        InterpolatedEmpiricalDistribution de = CumulativeProbabilities.createInterpolatedDistribution(vd, cpd);
        assertEquals(4, de.size());
        assertEquals(cpd, de.getCumulativeProbabilities());
        assertEquals(vd, de.getValues());
        assertEquals(new DistributionEntry(-2.0, 0.0), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(-2.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution((List<Double>) null, cpd); });
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(vd, (List<Double>) null); });
    }

    /**
     * Test the construction from a cumulative distribution with values from a SortedMap.
     */
    @Test
    public void testCreateCumulativeMapNotInterpolated()
    {
        SortedMap<Double, Double> map = new TreeMap<>();
        map.put(1.0, 0.1);
        map.put(2.0, 0.5);
        map.put(3.0, 0.8);
        map.put(4.0, 1.0);
        DiscreteEmpiricalDistribution de = CumulativeProbabilities.createDiscreteDistribution(map);
        assertEquals(4, de.size());
        assertEquals(Arrays.asList(0.1, 0.5, 0.8, 1.0), de.getCumulativeProbabilities());
        assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), de.getValues());
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntry(0.1));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(4.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { CumulativeProbabilities.createDiscreteDistribution(null); });
    }

    /**
     * Test the construction from a cumulative distribution with values from a SortedMap.
     */
    @Test
    public void testCreateCumulativeMapInterpolated()
    {
        SortedMap<Double, Double> map = new TreeMap<>();
        map.put(-2.0, 0.0);
        map.put(2.0, 0.5);
        map.put(3.0, 0.8);
        map.put(4.0, 1.0);
        InterpolatedEmpiricalDistribution de = CumulativeProbabilities.createInterpolatedDistribution(map);
        assertEquals(4, de.size());
        assertEquals(Arrays.asList(0.0, 0.5, 0.8, 1.0), de.getCumulativeProbabilities());
        assertEquals(Arrays.asList(-2.0, 2.0, 3.0, 4.0), de.getValues());
        assertEquals(new DistributionEntry(-2.0, 0.0), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(3.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        Try.testFail(() -> { CumulativeProbabilities.createInterpolatedDistribution(null); });
    }

}
