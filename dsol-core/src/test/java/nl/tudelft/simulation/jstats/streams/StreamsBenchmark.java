package nl.tudelft.simulation.jstats.streams;

/**
 * The StreamsBenchmark provides computational execution speed insight in the different streams.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public final class StreamsBenchmark
{
    /**
     * constructs a new StreamBenchmark.
     */
    private StreamsBenchmark()
    {
        super();
        // unreachable code
    }

    /**
     * benchmarks a stream by drawing 1000000 double values.
     * @param stream the stream to test
     * @return the execution time in milliseconds
     */
    public static long benchmark(final StreamInterface stream)
    {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++)
        {
            stream.nextDouble();
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * executes the benchmark.
     * @param args the commandline arguments
     */
    public static void main(final String[] args)
    {
        System.out.println("Java2Random : " + StreamsBenchmark.benchmark(new Java2Random()));
        System.out.println("MersenneTwister : " + StreamsBenchmark.benchmark(new MersenneTwister()));
        System.out.println("DX120Generator : " + StreamsBenchmark.benchmark(new DX120Generator()));
    }
}
