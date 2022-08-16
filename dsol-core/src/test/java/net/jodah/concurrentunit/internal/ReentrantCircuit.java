/*
 * Copyright 2010-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.jodah.concurrentunit.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * A circuit that accepts re-entrant {@link #open()} and {@link #close()} calls, allows waiting threads to be interrupted, and
 * ensures fairness when releasing {@link #await() waiting} threads.
 * @author Jonathan Halterman
 */
public class ReentrantCircuit
{
    /** Store the synchronization state. */
    private final Sync sync = new Sync();

    /**
     * Synchronization state of 0 = closed, 1 = open.
     */
    static final class Sync extends AbstractQueuedSynchronizer
    {
        /** */
        private static final long serialVersionUID = 992522674231731445L;

        /**
         * Closes the circuit.
         * @param ignored int; release does not use the int param.
         */
        @Override
        public boolean tryReleaseShared(final int ignored)
        {
            setState(0);
            return true;
        }

        /**
         * Opens the circuit if not a test.
         * @param acquires when 0, return whether circuit is closed. When 1, open call
         */
        @Override
        protected int tryAcquireShared(final int acquires)
        {
            // Check to make sure the acquisition is not barging in front of a queued thread
            Thread queuedThread = getFirstQueuedThread();
            if (queuedThread != null && queuedThread != Thread.currentThread())
            {
                return -1;
            }

            // If await test
            if (acquires == 0)
            {
                return isClosed() ? 1 : -1;
            }

            // Explicit open call
            setState(1);
            return 1;
        }

        /**
         * @return whether state == 0
         */
        boolean isClosed()
        {
            return getState() == 0;
        }

        /**
         * set state to 1.
         */
        void open()
        {
            setState(1);
        }
    }

    /**
     * Waits for the circuit to be closed, aborting if interrupted.
     * @throws InterruptedException when wait is interrupted
     */
    public void await() throws InterruptedException
    {
        this.sync.acquireSharedInterruptibly(0);
    }

    /**
     * Waits for the {@code waitDuration} until the circuit has been closed, aborting if interrupted, returning true if the
     * circuit is closed else false.
     * @param waitDuration the maximum time
     * @param timeUnit the unit of the time
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException when wait is interrupted
     */
    public boolean await(final long waitDuration, final TimeUnit timeUnit) throws InterruptedException
    {
        return this.sync.tryAcquireSharedNanos(0, timeUnit.toNanos(waitDuration));
    }

    /**
     * Closes the circuit, releasing any waiting threads.
     */
    public void close()
    {
        this.sync.releaseShared(1);
    }

    /**
     * Interrupts waiting threads.
     */
    public void interruptWaiters()
    {
        for (Thread t : this.sync.getSharedQueuedThreads())
        {
            t.interrupt();
        }
    }

    /**
     * Returns whether the circuit is closed.
     * @return boolean; whether the circuit is closed.
     */
    public boolean isClosed()
    {
        return this.sync.isClosed();
    }

    /**
     * Opens the circuit.
     */
    public void open()
    {
        this.sync.open();
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return isClosed() ? "closed" : "open";
    }
}
