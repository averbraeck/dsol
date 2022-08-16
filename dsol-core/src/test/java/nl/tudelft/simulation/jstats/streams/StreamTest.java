package nl.tudelft.simulation.jstats.streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 * The test script for the random generator classes.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class StreamTest
{
    /**
     * Test the Random Number Generator classes for double.
     */
    @Test
    public void testStreamDouble()
    {
        int nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            double sum = 0.0;
            double min = 1.0;
            double max = 0.0;
            for (int i = 0; i < nr; i++)
            {
                double value = stream.nextDouble();
                Assert.assertTrue(value >= 0.0);
                Assert.assertTrue(value <= 1.0);
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            assertEquals(0.5, sum / (1.0 * nr), 0.01);
            assertEquals(0.0, min, 0.01);
            assertEquals(1.0, max, 0.01);
        }
    }

    /**
     * Test the Random Number Generator classes for float.
     */
    @Test
    public void testStreamFloat()
    {
        int nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            float sum = 0.0f;
            float min = 1.0f;
            float max = 0.0f;
            for (int i = 0; i < nr; i++)
            {
                float value = stream.nextFloat();
                Assert.assertTrue(value >= 0.0f);
                Assert.assertTrue(value <= 1.0f);
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            assertEquals(0.5f, sum / (1.0f * nr), 0.01);
            assertEquals(0.0f, min, 0.01);
            assertEquals(1.0f, max, 0.01);
        }
    }

    /**
     * Test the Random Number Generator classes for int.
     */
    @Test
    public void testStreamInt()
    {
        int nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            double sum = 0.0;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < nr; i++)
            {
                int value = stream.nextInt();
                sum += value / (1.0 * Integer.MAX_VALUE);
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            double avg = (1.0 * sum) / (1.0d * nr);
            double dMin = (1.0d * min) / (1.0d * Integer.MAX_VALUE);
            double dMax = (1.0d * max) / (1.0d * Integer.MAX_VALUE);
            assertEquals(0.0, avg, 0.01);
            assertEquals(-1.0, dMin, 0.01);
            assertEquals(1.0, dMax, 0.01);
        }
    }

    /**
     * Test the Random Number Generator classes for int.
     */
    @Test
    public void testStreamIntEqualBits()
    {
        // System.out.println("\nINT EQUAL NUMBER OF BITS");
        int nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            int nrBins = 32;
            int[] bins = new int[nrBins];
            for (int i = 0; i < nr; i++)
            {
                int value = stream.nextInt();
                for (int j = 0; j < nrBins; j++)
                {
                    if ((value & 1) == 1)
                    {
                        bins[j]++;
                    }
                    value = value >>> 1;
                }
            }
            for (int i = 0; i < nrBins; i++)
            {
                double frac = 1.0 * bins[i] / (1.0 * nr);
                assertEquals(0.5, frac, 0.01);
            }
        }
    }

    /**
     * Test the Random Number Generator classes for int.
     */
    @Test
    public void testStreamInt0to10()
    {
        int nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            long sum = 0L;
            int nrBins = 10;
            int[] bins = new int[nrBins];
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < nr; i++)
            {
                int value = stream.nextInt(0, 9);
                bins[value]++;
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            double avg = (1.0 * sum) / (1.0d * nr);
            for (int i = 0; i < nrBins; i++)
            {
                double perc = 100.0 * bins[i] / (1.0 * nr);
                assertEquals(10.0, perc, 0.2);
            }
            assertEquals(4.5, avg, 0.01);
            assertEquals(0, min);
            assertEquals(9, max);
        }
    }

    /**
     * Test the Random Number Generator classes for boolean.
     */
    @Test
    public void testStreamBoolean()
    {
        int nr = 100000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            double sum = 0.0;
            for (int i = 0; i < nr; i++)
            {
                boolean value = stream.nextBoolean();
                sum += value ? 1.0 : 0.0;
            }
            double avg = sum / (1.0 * nr);
            // System.out.println(String.format("%-18s boolean avg = %8.6f", stream.getClass().getSimpleName(), avg));
            assertEquals(0.5, avg, 0.01);
        }
    }

    /**
     * Test the Random Number Generator classes for long.
     */
    @Test
    public void testStreamLong()
    {
        long nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            double sum = 0.0;
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            for (int i = 0; i < nr; i++)
            {
                long value = stream.nextLong();
                sum += value / (1.0d * Long.MAX_VALUE);
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            double avg = (1.0d * sum) / (1.0d * nr);
            double dMin = (1.0d * min) / (1.0d * Long.MAX_VALUE);
            double dMax = (1.0d * max) / (1.0d * Long.MAX_VALUE);
            assertEquals(0.0, avg, 0.01);
            assertEquals(-1.0, dMin, 0.01);
            assertEquals(1.0, dMax, 0.01);
        }
    }

    /**
     * Test the Random Number Generator classes for long.
     */
    @Test
    public void testStreamLongEqualBits()
    {
        long nr = 1000000;
        StreamInterface[] streams = {new Java2Random(100L), new MersenneTwister(101L), new DX120Generator(102L)};
        for (StreamInterface stream : streams)
        {
            int nrBins = 64;
            long[] bins = new long[nrBins];
            for (int i = 0; i < nr; i++)
            {
                long value = stream.nextLong();
                for (int j = 0; j < nrBins; j++)
                {
                    if ((value & 1) == 1)
                    {
                        bins[j]++;
                    }
                    value = value >>> 1;
                }
            }
            for (int i = 0; i < nrBins; i++)
            {
                double frac = 1.0d * bins[i] / (1.0 * nr);
                assertEquals(0.5, frac, 0.01);
            }
        }
    }

    /**
     * Test the seed management for the Random Number Generator classes.
     */
    @Test
    public void testSeedManagement()
    {
        StreamInterface[] streams = {new Java2Random(14L), new MersenneTwister(14L), new DX120Generator(14L)};
        for (StreamInterface stream : streams)
        {
            assertEquals(14L, stream.getSeed());
            assertEquals(14L, stream.getOriginalSeed());
            stream.reset();
            assertEquals(14L, stream.getSeed());
            assertEquals(14L, stream.getOriginalSeed());
            double d = stream.nextDouble();
            assertEquals(14L, stream.getSeed());
            assertEquals(14L, stream.getOriginalSeed());
            stream.reset();
            assertEquals(d, stream.nextDouble(), 0.0);
            stream.setSeed(15L);
            stream.reset();
            assertNotEquals(d, stream.nextDouble());
            assertEquals(15L, stream.getSeed());
            assertEquals(14L, stream.getOriginalSeed());
            stream.setSeed(stream.getOriginalSeed());
            assertEquals(14L, stream.getSeed());
            assertEquals(14L, stream.getOriginalSeed());
            stream.reset();
            assertEquals(d, stream.nextDouble(), 0.0);
        }
    }
    
}
