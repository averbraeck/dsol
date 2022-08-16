package nl.tudelft.simulation.language.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * WorkerThreadTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class WorkerThreadTest
{
    /** test value that will be incremented by a worker thread. */
    protected int value = 0;

    /** thread. */
    WorkerThread wt;
    
    /**
     * Test the WorkerThread.
     */
    @Test
    public void testWorkerThread()
    {
        this.value = 0;
        this.wt = new WorkerThread("job", new Job());
        assertTrue(this.wt.isAlive());
        assertFalse(this.wt.isInterrupted());
        assertFalse(this.wt.isRunning());
        assertEquals("job", this.wt.getName());
        this.wt.interrupt();

        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException exception)
        {
            // ignore
        }
        
        this.wt.cleanUp();

        long startTime = System.currentTimeMillis();
        while (this.wt.isAlive() && System.currentTimeMillis() - startTime < 1000)
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
            fail("WorkerThread execution; System.currentTimeMillis() - startTime > 1000");
        }

        assertFalse(this.wt.isAlive());
        assertEquals(1, this.value);
    }

    /**
     * Test the WorkerThread.
     */
    @Test
    public void testWorkerThreadException()
    {
        this.wt = new WorkerThread("exception job", new ExceptionJob());
        assertEquals("exception job", this.wt.getName());
        this.wt.interrupt();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException exception)
        {
            // ignore
        }
        this.wt.cleanUp();
        long startTime = System.currentTimeMillis();
        while (this.wt.isAlive() && System.currentTimeMillis() - startTime < 1000)
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
            fail("WorkerThread execution; System.currentTimeMillis() - startTime > 1000");
        }
        assertFalse(this.wt.isAlive());
    }
    
    /** The worker job. */
    class Job implements Runnable
    {
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            assertTrue(WorkerThreadTest.this.wt.isRunning());
            WorkerThreadTest.this.value++;
        }
    }
    
    /** The worker job that throws an exception. */
    class ExceptionJob implements Runnable
    {
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            throw new RuntimeException("this thread does not work; test successfully passed");
        }
    }
}
