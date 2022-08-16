package nl.tudelft.simulation.jstats.distributions.benchmark;

import java.util.Random;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.DX120Generator;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Benchmark the random generators of DSOL.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class StreamBenchmark
{
    /**
     * 
     */
    private StreamBenchmark()
    {
        //
    }

    /**
     * @param args args
     */
    public static void main(final String[] args)
    {
        Random random = new Random(10);
        long timeMs = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++)
        {
            random.nextGaussian();
        }
        System.out.println("1000000x random.nextGaussian() takes " + (System.currentTimeMillis() - timeMs) + " msec.");

        StreamInterface si = new MersenneTwister(10);
        DistContinuous dist = new DistNormal(si);
        timeMs = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++)
        {
            dist.draw();
        }
        System.out.println(
                "1000000x DistNormal() with MersenneTwister takes " + (System.currentTimeMillis() - timeMs) + " msec.");

        si = new Java2Random(10);
        dist = new DistNormal(si);
        timeMs = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++)
        {
            dist.draw();
        }
        System.out.println("1000000x DistNormal() with Java2Random takes " + (System.currentTimeMillis() - timeMs) + " msec.");

        si = new DX120Generator(10);
        dist = new DistNormal(si);
        timeMs = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++)
        {
            dist.draw();
        }
        System.out.println("1000000x DistNormal() with DX120 takes " + (System.currentTimeMillis() - timeMs) + " msec.");
    }

}
