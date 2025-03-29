package nl.tudelft.simulation.dsol.eventList;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.simulation.dsol.eventlists.EventListInterface;
import nl.tudelft.simulation.dsol.eventlists.EventListPriorityQueue;
import nl.tudelft.simulation.dsol.eventlists.RedBlackTree;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * EventListPerformanceTest.java.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public final class EventListPerformanceTest
{
    /** test eventlist. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private EventListPerformanceTest()
    {
        Test test = new Test();
        List<String[]> results = new ArrayList<>();
        EventListInterface[] testLists = {new RedBlackTree<Double>(), new EventListPriorityQueue<Double>()};
        String[] line = new String[2 * testLists.length + 1];
        results.add(line);
        line[0] = "nr_events";
        int eventlistCount = 1;
        for (EventListInterface<Double> elist : testLists)
        {
            line[eventlistCount] = elist.getClass().getSimpleName() + ".add";
            line[eventlistCount + 1] = elist.getClass().getSimpleName() + ".exec";
            eventlistCount += 2;
        }

        for (int max : new int[] {100, 1000, 10000, 100_000, 1_000_000})
        {
            System.out.println("Testing with #events = " + max);
            line = new String[2 * testLists.length + 1];
            results.add(line);
            line[0] = Integer.toString(max);
            eventlistCount = 1;
            for (EventListInterface<Double> elist : testLists)
            {
                long t0 = System.currentTimeMillis();
                StreamInterface stream = new MersenneTwister(42 + max);
                DistUniform dist = new DistUniform(stream, 1.0, 1000.0);
                for (int i = 0; i < max; i++)
                {
                    elist.add(new SimEvent<Double>(dist.draw(), test, "mArg1", new Object[] {12.7}));
                }
                long t1 = System.currentTimeMillis();
                line[eventlistCount] = Double.toString((t1 - t0) / 1000.0);
                while (!elist.isEmpty())
                {
                    elist.removeFirst().execute();
                }
                long t2 = System.currentTimeMillis();
                line[eventlistCount + 1] = Double.toString((t2 - t1) / 1000.0);
                eventlistCount += 2;
            }
        }
        System.out.println();

        for (int i = 0; i < results.size(); i++)
        {
            line = results.get(i);
            for (int j = 0; j < line.length; j++)
            {
                System.out.print(line[j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * @param args empty
     */
    public static void main(final String[] args)
    {
        new EventListPerformanceTest();
    }

    /** Test class. */
    static class Test
    {
        /**
         * Callable m_Arg1 method.
         * @param arg1 float arg
         */
        public void mArg1(final double arg1)
        {
            if (arg1 != 12.7)
            { throw new RuntimeException("12.7!"); }
        }
    }
}
