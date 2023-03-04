package nl.tudelft.simulation.dsol.eventList;

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
 * Copyright (c) 2021-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class EventListPerformanceTest2
{
    /** test eventlist. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private EventListPerformanceTest2()
    {
        EventListInterface[] testLists = {new RedBlackTree<Double>(), new EventListPriorityQueue<Double>()};
        Test test = new Test();
        int max = 1_000_000;
        for (EventListInterface<Double> elist : testLists)
        {
            System.out.println("\nTesting with " + elist.getClass().getSimpleName());
            long t0 = System.currentTimeMillis();
            StreamInterface stream = new MersenneTwister(42 + max);
            DistUniform dist = new DistUniform(stream, 1.0, 1000.0);
            for (int i = 0; i < max; i++)
            {
                elist.add(new SimEvent<Double>(dist.draw(), test, "mArg1", new Object[] {12.7}));
            }
            long t1 = System.currentTimeMillis();
            double d1 = (t1 - t0) / 1000.0;
            System.out.println("Adding  " + max + " events into the event list = " + d1 + " seconds");
            while (!elist.isEmpty())
            {
                elist.removeFirst().execute();
            }
            long t2 = System.currentTimeMillis();
            double d2 = (t2 - t1) / 1000.0;
            System.out.println("Execing " + max + " events from the event list = " + d2 + " seconds");
        }
    }

    /**
     * @param args empty
     */
    public static void main(final String[] args)
    {
        new EventListPerformanceTest2();
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
            {
                throw new RuntimeException("12.7!");
            }
        }
    }
}
