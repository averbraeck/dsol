package net.jodah.concurrentunit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Convenience support class, wrapping a {@link Waiter}.<br>
 * <br>
 * Copyright 2010-2016 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License<br>
 * @author Jonathan Halterman
 */
public abstract class ConcurrentTestCase
{
    /** the Waiter object. */
    private final Waiter waiter = new Waiter();

    /**
     * @see Waiter#assertEquals(Object, Object)
     * @param expected the expected value
     * @param actual the actual value
     * @throws AssertionError when the assertion fails
     */
    public void threadAssertEquals(final Object expected, final Object actual)
    {
        this.waiter.assertEquals(expected, actual);
    }

    /**
     * @see Waiter#assertTrue(boolean) Asserts that the {@code condition} is false.
     * @param b boolean to test to be false
     * @throws AssertionError when the assertion fails
     */
    public void threadAssertFalse(final boolean b)
    {
        this.waiter.assertFalse(b);
    }

    /**
     * @see Waiter#assertNotNull(Object)
     * @param object the object to test
     * @throws AssertionError when the assertion fails
     */
    public void threadAssertNotNull(final Object object)
    {
        this.waiter.assertNotNull(object);
    }

    /**
     * @see Waiter#assertNull(Object)
     * @param x the object to test
     * @throws AssertionError when the assertion fails
     */
    public void threadAssertNull(final Object x)
    {
        this.waiter.assertNull(x);
    }

    /**
     * @see Waiter#assertTrue(boolean)
     * @param b boolean to test to be true
     * @throws AssertionError when the assertion fails
     */
    public void threadAssertTrue(final boolean b)
    {
        this.waiter.assertTrue(b);
    }

    /**
     * @see Waiter#fail()
     */
    public void threadFail()
    {
        threadFail(new AssertionError());
    }

    /**
     * @see Waiter#fail(String)
     * @param reason the reason for failure
     * @throws AssertionError when the assertion fails
     */
    public void threadFail(final String reason)
    {
        threadFail(new AssertionError(reason));
    }

    /**
     * @see Waiter#fail(Throwable)
     * @param reason the reason for failure
     * @throws AssertionError wrapping the {@code reason}
     */
    public void threadFail(final Throwable reason)
    {
        this.waiter.fail(reason);
    }

    /**
     * @see Waiter#rethrow(Throwable)
     * @param reason the reason for failure throws Throwable the {@code failure}
     */
    public void rethrow(final Throwable reason)
    {
        this.waiter.rethrow(reason);
    }

    /**
     * @see Waiter#await()
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    protected void await() throws TimeoutException, InterruptedException
    {
        this.waiter.await();
    }

    /**
     * @see Waiter#await(long)
     * @param delay Delay to wait for
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    protected void await(final long delay) throws TimeoutException, InterruptedException
    {
        this.waiter.await(delay);
    }

    /**
     * @see Waiter#await(long, int)
     * @param delay Delay to wait for
     * @param expectedResumes Number of times {@link #resume()} is expected to be called before the awaiting thread is resumed
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    protected void await(final long delay, final int expectedResumes) throws TimeoutException, InterruptedException
    {
        this.waiter.await(delay, expectedResumes);
    }

    /**
     * @see Waiter#await(long, TimeUnit)
     * @param delay Delay to wait for
     * @param timeUnit TimeUnit to delay for
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    protected void await(final long delay, final TimeUnit timeUnit) throws TimeoutException, InterruptedException
    {
        this.waiter.await(delay, timeUnit);
    }

    /**
     * @see Waiter#await(long, TimeUnit, int)
     * @param delay Delay to wait for
     * @param timeUnit TimeUnit to delay for
     * @param expectedResumes Number of times {@link #resume()} is expected to be called before the awaiting thread is resumed
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    protected void await(final long delay, final TimeUnit timeUnit, final int expectedResumes)
            throws TimeoutException, InterruptedException
    {
        this.waiter.await(delay, timeUnit, expectedResumes);
    }

    /**
     * @see Waiter#resume()
     */
    protected void resume()
    {
        this.waiter.resume();
    }
}
