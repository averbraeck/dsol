package net.jodah.concurrentunit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import net.jodah.concurrentunit.internal.ReentrantCircuit;

/**
 * Waits on a test, carrying out assertions, until being resumed.<br>
 * <br>
 * Copyright 2010-2016 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License<br>
 * @author Jonathan Halterman
 */
public class Waiter
{
    /** the message to show when there is a timeout of the thread. */
    private static final String TIMEOUT_MESSAGE =
            "Test timed out while waiting for an expected result, expectedResumes: %d, actualResumes: %d";

    /** the number of remaining resumes. */
    private AtomicInteger remainingResumes = new AtomicInteger(0);

    /** the circuit ith open() and close() calls. */
    private final ReentrantCircuit circuit = new ReentrantCircuit();

    /** the failure when it occurs. */
    private volatile Throwable failure;

    /**
     * Creates a new Waiter.
     */
    public Waiter()
    {
        this.circuit.open();
    }

    /**
     * Asserts that the {@code expected} values equals the {@code actual} value.
     * @param expected the expected value
     * @param actual the actual value
     * @throws AssertionError when the assertion fails
     */
    public void assertEquals(final Object expected, final Object actual)
    {
        if (expected == null && actual == null)
        {
            return;
        }
        if (expected != null && expected.equals(actual))
        {
            return;
        }
        fail(format(expected, actual));
    }

    /**
     * Asserts that the {@code expected} values equals the {@code actual} value.
     * @param expected the expected value
     * @param actual the actual value
     * @param delta the allowed tolerance
     * @throws AssertionError when the assertion fails
     */
    public void assertEquals(final double expected, final double actual, final double delta)
    {
        try
        {
            org.junit.Assert.assertEquals(expected, actual, delta);
        }
        catch (AssertionError ae)
        {
            fail(format(expected, actual));
        }
    }

    /**
     * Asserts that the {@code condition} is false.
     * @param condition boolean to test to be false
     * @throws AssertionError when the assertion fails
     */
    public void assertFalse(final boolean condition)
    {
        if (condition)
        {
            fail("expected false");
        }
    }

    /**
     * Asserts that the {@code object} is not null.
     * @param object the object to test
     * @throws AssertionError when the assertion fails
     */
    public void assertNotNull(final Object object)
    {
        if (object == null)
        {
            fail("expected not null");
        }
    }

    /**
     * Asserts that the {@code object} is null.
     * @param object the object to test
     * @throws AssertionError when the assertion fails
     */
    public void assertNull(final Object object)
    {
        if (object != null)
        {
            fail(format("null", object));
        }
    }

    /**
     * Asserts that the {@code condition} is true.
     * @param condition boolean to test to be true
     * @throws AssertionError when the assertion fails
     */
    public void assertTrue(final boolean condition)
    {
        if (!condition)
        {
            fail("expected true");
        }
    }

    /**
     * Asserts that {@code actual} satisfies the condition specified by {@code matcher}.
     * @param actual the value to test
     * @param matcher the condition
     * @param <T> the type of the actual
     * @throws AssertionError when the assertion fails
     */
    public <T> void assertThat(final T actual, final org.hamcrest.Matcher<? super T> matcher)
    {
        try
        {
            org.hamcrest.MatcherAssert.assertThat(actual, matcher);
        }
        catch (AssertionError e)
        {
            fail(e);
        }
    }

    /**
     * Waits until {@link #resume()} is called, or the test is failed.
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    public void await() throws TimeoutException, InterruptedException
    {
        await(0, TimeUnit.MILLISECONDS, 1);
    }

    /**
     * Waits until the {@code delay} has elapsed, {@link #resume()} is called, or the test is failed.
     * @param delay Delay to wait in milliseconds
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    public void await(final long delay) throws TimeoutException, InterruptedException
    {
        await(delay, TimeUnit.MILLISECONDS, 1);
    }

    /**
     * Waits until the {@code delay} has elapsed, {@link #resume()} is called, or the test is failed.
     * @param delay Delay to wait for
     * @param timeUnit TimeUnit to delay for
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    public void await(final long delay, final TimeUnit timeUnit) throws TimeoutException, InterruptedException
    {
        await(delay, timeUnit, 1);
    }

    /**
     * Waits until the {@code delay} has elapsed, {@link #resume()} is called {@code expectedResumes} times, or the test is
     * failed.
     * @param delay Delay to wait for in milliseconds
     * @param expectedResumes Number of times {@link #resume()} is expected to be called before the awaiting thread is resumed
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    public void await(final long delay, final int expectedResumes) throws TimeoutException, InterruptedException
    {
        await(delay, TimeUnit.MILLISECONDS, expectedResumes);
    }

    /**
     * Waits until the {@code delay} has elapsed, {@link #resume()} is called {@code expectedResumes} times, or the test is
     * failed.
     * @param delay Delay to wait for
     * @param timeUnit TimeUnit to delay for
     * @param expectedResumes Number of times {@link #resume()} is expected to be called before the awaiting thread is resumed
     * @throws TimeoutException if the operation times out while waiting
     * @throws InterruptedException if the operations is interrupted while waiting
     * @throws AssertionError if any assertion fails while waiting
     */
    public void await(final long delay, final TimeUnit timeUnit, final int expectedResumes)
            throws TimeoutException, InterruptedException
    {
        try
        {
            if (this.failure == null)
            {
                synchronized (this)
                {
                    int remaining = this.remainingResumes.addAndGet(expectedResumes);
                    if (remaining > 0)
                    {
                        this.circuit.open();
                    }
                }

                if (delay == 0)
                {
                    this.circuit.await();
                }
                else if (!this.circuit.await(delay, timeUnit))
                {
                    final int actualResumes = expectedResumes - this.remainingResumes.get();
                    throw new TimeoutException(String.format(TIMEOUT_MESSAGE, expectedResumes, actualResumes));
                }
            }
        }
        finally
        {
            this.remainingResumes.set(0);
            this.circuit.open();
            if (this.failure != null)
            {
                Throwable f = this.failure;
                this.failure = null;
                sneakyThrow(f);
            }
        }
    }

    /**
     * Resumes the waiter when the expected number of {@link #resume()} calls have occurred.
     */
    public synchronized void resume()
    {
        if (this.remainingResumes.decrementAndGet() <= 0)
        {
            this.circuit.close();
        }
    }

    /**
     * Fails the current test.
     * @throws AssertionError as a result of failure
     */
    public void fail()
    {
        fail(new AssertionError());
    }

    /**
     * Fails the current test for the given {@code reason}.
     * @param reason the reason for failure
     * @throws AssertionError wrapping the reason
     */
    public void fail(final String reason)
    {
        fail(new AssertionError(reason));
    }

    /**
     * Fails the current test with the given {@code reason}, sets the number of expected resumes to 0, and throws the
     * {@code reason} as an {@code AssertionError} in the main test thread and in the current thread.
     * @param reason the reason for failure
     * @throws AssertionError wrapping the {@code reason}
     */
    public void fail(final Throwable reason)
    {
        AssertionError ae = null;
        if (reason instanceof AssertionError)
        {
            ae = (AssertionError) reason;
        }
        else
        {
            ae = new AssertionError();
            ae.initCause(reason);
        }

        this.failure = ae;
        this.circuit.close();
        throw ae;
    }

    /**
     * Rethrows the {@code failure} in the main test thread and in the current thread. Differs from {@link #fail(Throwable)}
     * which wraps a failure in an AssertionError before throwing.
     * @param rethrownFailure the reason for failure throws Throwable the {@code failure}
     */
    public void rethrow(final Throwable rethrownFailure)
    {
        this.failure = rethrownFailure;
        this.circuit.close();
        sneakyThrow(rethrownFailure);
    }

    /**
     * Throw the throwable without having to specify it in the signature.
     * @param t the throwable
     */
    private static void sneakyThrow(final Throwable t)
    {
        Waiter.<Error> sneakyThrow2(t);
    }

    /**
     * Throw the throwable T.
     * @param t Throwable; the exception to throw
     * @throws T the thrown exception
     * @param <T> the Throwable type
     */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow2(final Throwable t) throws T
    {
        throw (T) t;
    }

    /**
     * Format the expected - actual string of JUnit.
     * @param expected the expected value
     * @param actual the actual value
     * @return a formatted string
     */
    private String format(final Object expected, final Object actual)
    {
        return "expected:<" + expected + "> but was:<" + actual + ">";
    }
}
