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
 * Test the ProbabilityDensities helper class.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProbabilityDensitiesTest
{
    /**
     * Test the construction from a density with double[] density and double[] values.
     */
    @Test
    public void testCreateDensityDoubleDoubleNotInterpolated()
    {
        double[] dd = {0.1, 0.4, 0.3, 0.2};
        double[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = ProbabilityDensities.createDiscreteDistribution(vd, dd);
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
        ProbabilityDensities.createDiscreteDistribution(new Double[] {10.0}, new double[] {1.0});

        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.2, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {-0.1, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.0, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.1, 0.5, 0.2, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.5, 1.001}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 0.5, 0.9}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution((double[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, (double[]) null); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {0.1, 0.2, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(new double[] {2, 3, 1, 4}, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, new double[] {}); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(new double[] {}, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(new double[] {}, new double[] {}); });
    }

    /**
     * Test the construction from a density with double[] density and double[] values.
     */
    @Test
    public void testCreateDensityDoubleDoubleInterpolated()
    {
        double[] dd = {0.1, 0.2, 0.3, 0.1};
        double[] vd = {1.0, 2.0, 4.0, 5.0, 7.0};
        InterpolatedEmpiricalDistribution de = ProbabilityDensities.createInterpolatedDistribution(vd, dd);
        assertEquals(5, de.size());
        assertArrayEquals(new double[] {0.0, 0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(new DistributionEntry(2.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(4.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(7.0, de.getHighestValue().doubleValue(), 1E-6);

        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.3, 0.2}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {-0.1, 0.2, 0.5, 1.0}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {0.2, 0.3, 0.5, 0.2}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {0.2, 0.2, 0.5, 1.001}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.5}); });
        Try.testFail(
                () -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {0.0, 0.2, 0.1, 0.1, 0.2}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution((double[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, (double[]) null); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(new double[] {2, 3, 1, 4}, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, new double[] {}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(new double[] {}, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(new double[] {}, new double[] {}); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(new double[] {1.0}, new double[] {1.0}); });
    }

    /**
     * Test the construction from a density with double[] density and Number[] values.
     */
    @Test
    public void testCreateDensityDoubleNumberNotInterpolated()
    {
        double[] dd = {0.1, 0.4, 0.3, 0.2};
        Number[] vd = {1.0, 2.0, 3.0, 4.0};
        DiscreteEmpiricalDistribution de = ProbabilityDensities.createDiscreteDistribution(vd, dd);
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
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution((Double[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a density with double[] density and Number[] values.
     */
    @Test
    public void testCreateDensityDoubleNumberInterpolated()
    {
        double[] dd = {0.1, 0.2, 0.3, 0.1};
        Number[] vd = {1.0, 2.0, 4.0, 5.0, 7.0};
        InterpolatedEmpiricalDistribution de = ProbabilityDensities.createInterpolatedDistribution(vd, dd);
        assertEquals(5, de.size());
        assertArrayEquals(new double[] {0.0, 0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().toArray(new Number[0]));
        assertEquals(new DistributionEntry(2.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(4.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(7.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution((Double[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a density with double[] density and long[] values.
     */
    @Test
    public void testCreateDensityDoubleLongNotInterpolated()
    {
        double[] dd = {0.1, 0.4, 0.3, 0.2};
        long[] vd = {1L, 2L, 3L, 4L};
        DiscreteEmpiricalDistribution de = ProbabilityDensities.createDiscreteDistribution(vd, dd);
        assertEquals(4, de.size());
        assertArrayEquals(new double[] {0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
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
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution((long[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a density with double[] density and Long[] values.
     */
    @Test
    public void testCreateDensityDoubleLongInterpolated()
    {
        double[] dd = {0.1, 0.2, 0.3, 0.1};
        long[] vd = {1L, 2L, 4L, 5L, 7L};
        InterpolatedEmpiricalDistribution de = ProbabilityDensities.createInterpolatedDistribution(vd, dd);
        assertEquals(5, de.size());
        assertArrayEquals(new double[] {0.0, 0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertArrayEquals(vd, de.getValues().stream().mapToLong(v -> v.longValue()).toArray());
        assertEquals(new DistributionEntry(2L, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(5L, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(4L, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(5L, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1L, de.getLowestValue().longValue());
        assertEquals(7L, de.getHighestValue().longValue());
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution((long[]) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, (double[]) null); });
    }

    /**
     * Test the construction from a density with List&lt;Double&gt; density and List&lt;Double&gt; values.
     */
    @Test
    public void testCreateDensityListNotInterpolated()
    {
        List<Double> dd = Arrays.asList(0.1, 0.4, 0.3, 0.2);
        List<Double> vd = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        DiscreteEmpiricalDistribution de = ProbabilityDensities.createDiscreteDistribution(vd, dd);
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
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution((List<Double>) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(vd, (List<Double>) null); });
    }

    /**
     * Test the construction from a density with List&lt;Double&gt; density and List&lt;Double&gt; values.
     */
    @Test
    public void testCreateDensityListInterpolated()
    {
        List<Double> dd = Arrays.asList(0.1, 0.2, 0.3, 0.1);
        List<Double> vd = Arrays.asList(1.0, 2.0, 4.0, 5.0, 7.0);
        InterpolatedEmpiricalDistribution de = ProbabilityDensities.createInterpolatedDistribution(vd, dd);
        assertEquals(5, de.size());
        assertArrayEquals(new double[] {0.0, 0.1, 0.5, 0.8, 1.0},
                de.getCumulativeProbabilities().stream().mapToDouble(v -> v.doubleValue()).toArray(), 0.001);
        assertEquals(vd, de.getValues());
        assertEquals(new DistributionEntry(2.0, 0.1), de.getFloorEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getFloorEntry(0.8));
        assertEquals(new DistributionEntry(4.0, 0.5), de.getCeilingEntry(0.2));
        assertEquals(new DistributionEntry(5.0, 0.8), de.getCeilingEntry(0.8));
        assertNull(de.getFloorEntry(-0.1));
        assertNull(de.getCeilingEntry(1.1));
        assertEquals(1.0, de.getLowestValue().doubleValue(), 1E-6);
        assertEquals(7.0, de.getHighestValue().doubleValue(), 1E-6);
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution((List<Double>) null, dd); });
        Try.testFail(() -> { ProbabilityDensities.createInterpolatedDistribution(vd, (List<Double>) null); });
    }

    /**
     * Test the construction from a density with values from a SortedMap.
     */
    @Test
    public void testCreateDensityMapNotInterpolated()
    {
        SortedMap<Double, Double> map = new TreeMap<>();
        map.put(1.0, 0.1);
        map.put(2.0, 0.4);
        map.put(3.0, 0.3);
        map.put(6.0, 0.2);
        DiscreteEmpiricalDistribution de = ProbabilityDensities.createDiscreteDistribution(map);
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
        Try.testFail(() -> { ProbabilityDensities.createDiscreteDistribution(null); });
    }

}
