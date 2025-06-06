package nl.tudelft.simulation.language.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

import org.djutils.logger.CategoryLogger;

/**
 * The WorkerThread is a working thread. The thread sleeps while not interrupted. If interrupted the job.run operation is
 * invoked.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="mailto:phmjacobs@hotmail.com">Peter H.M. Jacobs</a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */

public class WorkerThread extends Thread
{
    /** the job to execute. */
    private Runnable job = null;

    /** finalized. */
    private boolean finalized = false;

    /** running. */
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * constructs a new SimulatorRunThread.
     * @param name the name of the thread
     * @param job the job to run
     */
    public WorkerThread(final String name, final Runnable job)
    {
        super(name);
        this.job = job;
        this.setDaemon(false);
        this.setPriority(Thread.NORM_PRIORITY);
        this.start();
    }

    /**
     * Clean up the worker thread. synchronized method, otherwise it does not own the Monitor on the wait.
     */
    public synchronized void cleanUp()
    {
        this.running.set(false);
        this.finalized = true;
        if (!this.isInterrupted())
        {
            this.notify(); // in case it is in the 'wait' state
        }
        this.job = null;
    }

    /**
     * @return whether the run method of the job is running or not
     */
    public synchronized boolean isRunning()
    {
        return this.running.get();
    }

    @Override
    public synchronized void run()
    {
        while (!this.finalized) // always until finalized
        {
            try
            {
                this.wait(); // as long as possible
            }
            catch (InterruptedException interruptedException)
            {
                if (!this.finalized)
                {
                    this.interrupt(); // set the status to interrupted
                    try
                    {
                        this.running.set(true);
                        this.job.run();
                        this.running.set(false);
                    }
                    catch (Exception exception)
                    {
                        CategoryLogger.always().error(exception);
                    }
                    Thread.interrupted();
                }
            }
        }
    }
}
