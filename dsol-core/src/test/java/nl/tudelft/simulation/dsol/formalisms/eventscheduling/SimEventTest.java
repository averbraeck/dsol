package nl.tudelft.simulation.dsol.formalisms.eventscheduling;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * SimEventTest.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class SimEventTest
{
    /** generated ids. */
    Set<Long> usedIds = new HashSet<>();

    /** active threads. */
    AtomicInteger activeThreads = new AtomicInteger(0);

    /** errors. */
    AtomicInteger errors = new AtomicInteger(0);

    /**
     * Test parallel threads creating simevents.
     * @throws InterruptedException on interrupt error
     */
    @Test
    public void testParallel() throws InterruptedException
    {
        for (int i = 0; i < 8; i++)
        {
            new ThreadRunner().start();
        }
        while (this.activeThreads.get() > 0)
        {
            Thread.sleep(10);
        }
        if (this.errors.get() > 0)
            fail("repeated id in SimEvent using parallel construction; total " + this.errors.get() + " errors");
    }

    /** thread to create 100000 events each. */
    class ThreadRunner extends Thread
    {
        @Override
        public void run()
        {
            SimEventTest.this.activeThreads.incrementAndGet();
            try
            {
                for (int i = 0; i < 100000; i++)
                {
                    SimEvent<Double> e = new SimEvent<>(0.0, this, "test", null);
                    if (SimEventTest.this.usedIds.contains(e.getId()))
                        SimEventTest.this.errors.incrementAndGet();
                    SimEventTest.this.usedIds.add(e.getId());
                }
            }
            finally
            {
                SimEventTest.this.activeThreads.decrementAndGet();
            }
        }

        /** */
        void test()
        {
            //
        }
    }
}
