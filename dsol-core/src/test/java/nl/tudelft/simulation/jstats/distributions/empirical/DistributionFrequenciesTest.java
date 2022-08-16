package nl.tudelft.simulation.jstats.distributions.empirical;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * Test the DistributionFrequencies helper class.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionFrequenciesTest
{
    /**
     * Test the construction from a density with double[] weight and double[] values.
     */
    @Test
    public void testCreateWeightDoubleDouble()
    {
        double[] wd = {10.0, 40.0, 30.0, 20.0};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vd, wd);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
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
        DistributionFrequencies.createDiscreteDistribution(new Double[] {10.0}, new double[] {1.0});

        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new double[] {-0.1, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new double[] {0.0, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((double[]) null, wd); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, (double[]) null); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 1.0}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new double[] {}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, wd); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, new double[] {}); });
    }

    /**
     * Test the construction from a density with long[] frequencies and double[] values.
     */
    @Test
    public void testCreateFrequencyLongDouble()
    {
        long[] fl = {10L, 40L, 30L, 20L};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vd, fl);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
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
        DistributionFrequencies.createDiscreteDistribution(new double[] {10.0}, new long[] {100L});

        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new long[] {-10L, 20L, 50L, 80L}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new long[] {0L, 20L, 50L, 80L}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((double[]) null, fl); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, (long[]) null); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new long[] {10L, 20L, 10L}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new long[] {}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, fl); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, new long[] {}); });
    }

    /**
     * Test the construction from a density with int[] frequencies and double[] values.
     */
    @Test
    public void testCreateFrequencyIntDouble()
    {
        int[] fi = {10, 40, 30, 20};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vd, fi);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
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
        DistributionFrequencies.createDiscreteDistribution(new double[] {10.0}, new int[] {100});

        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new int[] {-10, 20, 50, 80}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new int[] {0, 20, 50, 80}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((double[]) null, fi); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, (int[]) null); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new int[] {10, 20, 10}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, new int[] {}); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, fi); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(new double[] {}, new int[] {}); });
    }

    /**
     * Test the construction from a frequency with double[] weight and Number[] values.
     */
    @Test
    public void testCreateFrequencyDoubleNumber()
    {
        double[] wd = {10.0, 40.0, 30.0, 20.0};
        Number[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vd, wd);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().toArray(new Number[0]));
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
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((Double[]) null, wd); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a frequency with double[] weights and long[] values.
     */
    @Test
    public void testCreateFrequencyDoubleLong()
    {
        double[] fd = {10.0, 40.0, 30.0, 20.0};
        long[] vl = {1L, 2L, 3L, 4L};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vl, fd);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vl, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getCeilingEntry(0.1));
        assertEquals(new DistributionEntry(4L, 1.0), de.getFloorEntry(1.0));
        assertEquals(new DistributionEntry(4L, 1.0), de.getCeilingEntry(1.0));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1L, de.getLowestValue().longValue());
        assertEquals(4L, de.getHighestValue().longValue());
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((long[]) null, fd); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vl, (double[]) null); });
    }

    /**
     * Test the construction from a frequency with long[] frequency and long[] values.
     */
    @Test
    public void testCreateFrequencyLongLong()
    {
        long[] fl = {10L, 40L, 30L, 20L};
        long[] vl = {1L, 2L, 3L, 4L};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vl, fl);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vl, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getCeilingEntry(0.1));
        assertEquals(new DistributionEntry(4L, 1.0), de.getFloorEntry(1.0));
        assertEquals(new DistributionEntry(4L, 1.0), de.getCeilingEntry(1.0));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1L, de.getLowestValue().longValue());
        assertEquals(4L, de.getHighestValue().longValue());
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((long[]) null, fl); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vl, (double[]) null); });
    }

    /**
     * Test the construction from a frequency with int[] frequency and long[] values.
     */
    @Test
    public void testCreateFrequencyIntLong()
    {
        int[] fi = {10, 40, 30, 20};
        long[] vl = {1L, 2L, 3L, 4L};
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vl, fi);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vl, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1L, 0.1), de.getCeilingEntry(0.1));
        assertEquals(new DistributionEntry(4L, 1.0), de.getFloorEntry(1.0));
        assertEquals(new DistributionEntry(4L, 1.0), de.getCeilingEntry(1.0));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1L, de.getLowestValue().longValue());
        assertEquals(4L, de.getHighestValue().longValue());
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((long[]) null, fi); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vl, (double[]) null); });
    }

    /**
     * Test the construction from a frequency with List&lt;Double&gt; frequency and List&lt;Double&gt; values.
     */
    @Test
    public void testCreateFrequencyListNumber()
    {
        List<Double> fd = Arrays.asList(10.0, 40.0, 30.0, 20.0);
        List<Double> vd = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(vd, fd);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(vd, de.getValues());
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
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution((List<Double>) null, fd); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, (List<Double>) null); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, Arrays.asList(10.0, 0.0, 30.0, 20.0)); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, Arrays.asList(10.0, 40.0, -30.0, 20.0)); });
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(vd, Arrays.asList(10.0, 30.0, 20.0)); });
    }

    /**
     * Test the construction from a frequency with values from a SortedMap.
     */
    @Test
    public void testCreateFrequencyMap()
    {
        SortedMap<Double, Double> map = new TreeMap<>();
        map.put(1.0, 10.0);
        map.put(2.0, 40.0);
        map.put(3.0, 30.0);
        map.put(6.0, 20.0);
        DiscreteEmpiricalDistribution de = DistributionFrequencies.createDiscreteDistribution(map);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(new ArrayList<Double>(map.keySet()), de.getValues());
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getFloorEntry(0.1));
        assertEquals(new DistributionEntry(2.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(1.0, 0.1), de.getCeilingEntry(0.1));
        assertEquals(new DistributionEntry(6.0, 1.0), de.getFloorEntry(1.0));
        assertEquals(new DistributionEntry(6.0, 1.0), de.getCeilingEntry(1.0));
        assertNull(de.getFloorEntry(0.0));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(6.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { DistributionFrequencies.createDiscreteDistribution(null); });
    }

}
