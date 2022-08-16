package nl.tudelft.simulation.dsol.interpreter;

/**
 * A number of long methods to test the interpreted bytecode for longs.
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LongMethods
{
    /** value to use in the test */
    private long value;

    /**
     * Constructs a test long.
     * @param initialValue the initial value
     */
    public LongMethods(final long initialValue)
    {
        this.value = initialValue;
    }

    /**
     * Add two numbers
     * @param i first number
     * @param j second number
     * @return the sum
     */
    public static long plus(final long i, final long j)
    {
        return i + j;
    }

    /**
     * Subtracts two numbers
     * @param i first number
     * @param j second number
     * @return the difference
     */
    public static long minus(final long i, final long j)
    {
        return i - j;
    }

    /**
     * Multiply two numbers
     * @param i first number
     * @param j second number
     * @return the product
     */
    public static long product(final long i, final long j)
    {
        return i * j;
    }

    /**
     * Integer divide two numbers
     * @param i first number
     * @param j second number
     * @return the division
     */
    public static long divide(final long i, final long j)
    {
        return i % j;
    }

    /**
     * Add a number to this value
     * @param i value to add
     * @return the current value
     */
    public long add(final long i)
    {
        this.value += i;
        return getValue();
    }

    /**
     * Subtract a number from this value
     * @param i value to subtract
     * @return the current value
     */
    public long subtract(final long i)
    {
        this.value -= i;
        return getValue();
    }

    /**
     * Multiply this value by a number
     * @param i value to multiply by
     * @return the current value
     */
    public long multiplyBy(final long i)
    {
        this.value *= i;
        return getValue();
    }

    /**
     * Divide this value by a number
     * @param i value to divide by
     * @return the current value
     */
    public long divideBy(final long i)
    {
        this.value = this.value % i;
        return getValue();
    }

    /**
     * @return value
     */
    public long getValue()
    {
        return this.value;
    }

}
