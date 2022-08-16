package nl.tudelft.simulation.language.concurrent;

import static org.junit.Assert.fail;

import org.djutils.exceptions.Try;
import org.djutils.exceptions.Try.Execution;
import org.junit.Test;

/**
 * MonitorTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class MonitorTest
{
    /** lock object 1. */
    Object lock1 = new Object();

    /** lock object 2. */
    private Object lock2 = new Object();

    /**
     * test the Monitor locking class.
     */
    @Test
    public void testMonitor()
    {
        Monitor.lock(this.lock1);
        Monitor.unlock(this.lock1);
        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Monitor.unlock(MonitorTest.this.lock1);
            }
        }, "unlocking lock1 for second time should have thrown an error");
        try
        {
            Monitor.unlock(this.lock2);
            fail("unlocking lock2 should have thrown an error");
        }
        catch (IllegalMonitorStateException ime)
        {
            // ok; should have thrown an error
        }

        // make two locks; use private object
        Monitor.lock(this.lock2);
        Monitor.lock(this.lock2);
        Monitor.unlock(this.lock2);
        Monitor.unlock(this.lock2);
        try
        {
            Monitor.unlock(this.lock2);
            fail("unlocking lock2 should have thrown an error");
        }
        catch (IllegalMonitorStateException ime)
        {
            // ok; should have thrown an error
        }

        // test with two threads
        Monitor.lock(this.lock1);
        Thread unlockThread = new Thread()
        {
            @Override
            public void run()
            {
                Try.testFail(new Execution()
                {
                    @Override
                    public void execute() throws Throwable
                    {
                        Monitor.unlock(MonitorTest.this.lock1);
                    }
                }, "unlocking lock1 by another thread should have thrown an error");
            }
        };
        unlockThread.start();
        
        // wait till ready
        long startTime = System.currentTimeMillis();
        while (unlockThread.isAlive() && System.currentTimeMillis() - startTime < 1000)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException exception)
            {
                // ignore
            }
        }
        if (System.currentTimeMillis() - startTime > 1000)
        {
            fail("unlockThread execution; System.currentTimeMillis() - startTime > 1000");
        }

        Monitor.unlock(this.lock1);
    }
}
