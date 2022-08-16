package nl.tudelft.simulation.language.concurrent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Monitor class. In the Java programming language there is a lock associated with every object. The language does not provide a
 * way to perform separate lock and unlock operations; instead, they are implicitly performed by high-level constructs that
 * always arrange to pair such operations correctly. This Monitor class, however, provides separate monitorenter and monitorexit
 * instructions that implement the lock and unlock operations. The class is final for now, as it is not the idea that the class
 * should be extended. It has only static methods.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="mailto:phmjacobs@hotmail.com">Peter H.M. Jacobs</a>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class Monitor
{
    /** the locks held. */
    private static Map<Object, MonitorThread> locks = new LinkedHashMap<>();

    /**
     * constructs a new Monitor.
     */
    private Monitor()
    {
        // unreachable code
    }

    /**
     * locks an object for the current thread.
     * @param object Object; the object to lock
     */
    public static void lock(final Object object)
    {
        Monitor.lock(object, Thread.currentThread());
    }

    /**
     * locks an object for the given requestor.
     * @param object Object; the object to lock.
     * @param requestor Thread; the requesting thread.
     */
    public static void lock(final Object object, final Thread requestor)
    {
        synchronized (Monitor.locks)
        {
            if (Monitor.get(object) == null)
            {
                Monitor.locks.put(object, new MonitorThread(requestor, object));
            }
            else
            {
                MonitorThread thread = Monitor.get(object);
                if (thread.getOwner().equals(requestor))
                {
                    thread.increaseCounter();
                }
                else
                {
                    synchronized (object)
                    {
                        // We wait until we gained access to the monitor
                        Monitor.locks.put(object, new MonitorThread(requestor, object));
                    }
                }
            }
        }
    }

    /**
     * unlocks an object locked by the current Thread.
     * @param object Object; the object to unlock
     */
    public static void unlock(final Object object)
    {
        Monitor.unlock(object, Thread.currentThread());
    }

    /**
     * unlocks an object locked by owner.
     * @param object Object; the object to unlock.
     * @param owner Thread; the owning thread.
     */
    public static void unlock(final Object object, final Thread owner)
    {
        synchronized (Monitor.locks)
        {
            MonitorThread thread = Monitor.get(object);
            if (thread == null)
            {
                throw new IllegalMonitorStateException("object(" + object + ") is not locked");
            }
            if (!thread.getOwner().equals(owner))
            {
                throw new IllegalMonitorStateException(owner + " cannot" + " unlock object owned by " + thread.getOwner());
            }
            thread.decreaseCounter();
            if (thread.getCounter() == 0)
            {
                thread.interrupt();
                Monitor.locks.remove(object);
            }
        }
    }

    /**
     * returns the MonitorThread for a specific key.
     * @param key Object; the key to resolve
     * @return the MonitorThread
     */
    private static MonitorThread get(final Object key)
    {
        return locks.get(key);
    }

    /**
     * A MonitorThread is used to lock an object.
     */
    private static class MonitorThread extends Thread
    {
        /** the monitor to use. */
        private Object object = null;

        /** the owning thread. */
        private Thread owner = null;

        /** the counter. */
        private int counter = 0;

        /**
         * constructs a new MonitorThread.
         * @param owner Thread; the owning thread
         * @param object Object; the object
         */
         MonitorThread(final Thread owner, final Object object)
        {
            super("MonitorThread on " + object.getClass());
            this.setDaemon(true);
            this.owner = owner;
            synchronized (object)
            {
                this.object = object;
                increaseCounter();
                this.start();
            }
            synchronized (owner)
            {
                try
                {
                    this.owner.wait();
                }
                catch (InterruptedException exception)
                {
                    exception = null;
                    /*
                     * This interrupted exception is thrown because this monitor thread has started and interrupted its
                     * constructor. We now know object is locked and may therefore return.
                     */
                }
            }
        }

        /**
         * @return Returns the counter.
         */
        public synchronized int getCounter()
        {
            return this.counter;
        }

        /**
         * decreases the counter with one.
         */
        public synchronized void decreaseCounter()
        {
            this.counter = Math.max(0, this.counter - 1);
        }

        /**
         * increases the counter of this thread with one.
         */
        public synchronized void increaseCounter()
        {
            this.counter++;
        }

        /**
         * @return Returns the owning thread.
         */
        public Thread getOwner()
        {
            return this.owner;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            try
            {
                // We lock the object
                synchronized (this.object)
                {
                    // Since we have locked the object, we can now return
                    // the constructor
                    this.owner.interrupt();

                    // We join
                    this.join();
                }
            }
            catch (Exception exception)
            {
                // This is OK.. We use this construction in the
                // MonitorTest.unlock to release a lock
                exception = null;
            }
        }
    }
}
