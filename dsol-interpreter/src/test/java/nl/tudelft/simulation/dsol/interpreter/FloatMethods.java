package nl.tudelft.simulation.dsol.interpreter;

/**
 * A number of float methods to test the interpreter bytecode for float.
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class FloatMethods
{
    /** value to use an the test */
    private float value;

    /**
     * Constructs a test float.
     * @param initialValue the initial value
     */
    public FloatMethods(final float initialValue)
    {
        this.value = initialValue;
    }

    /**
     * Add two numbers
     * @param a first number
     * @param b second number
     * @return the sum
     */
    public static float plus(final float a, final float b)
    {
        return a + b;
    }

    /**
     * Subtracts two numbers
     * @param a first number
     * @param b second number
     * @return the difference
     */
    public static float minus(final float a, final float b)
    {
        return a - b;
    }

    /**
     * Multiply two numbers
     * @param a first number
     * @param b second number
     * @return the product
     */
    public static float product(final float a, final float b)
    {
        return a * b;
    }

    /**
     * Integer divide two numbers
     * @param a first number
     * @param b second number
     * @return the division
     */
    public static float divide(final float a, final float b)
    {
        return a / b;
    }

    /**
     * Add a number to this value
     * @param a value to add
     * @return the current value
     */
    public float add(final float a)
    {
        this.value += a;
        return getValue();
    }

    /**
     * Subtract a number from this value
     * @param a value to subtract
     * @return the current value
     */
    public float subtract(final float a)
    {
        this.value -= a;
        return getValue();
    }

    /**
     * Multiply this value by a number
     * @param a value to multiply by
     * @return the current value
     */
    public float multiplyBy(final float a)
    {
        this.value *= a;
        return getValue();
    }

    /**
     * Divide this value by a number
     * @param a value to divide by
     * @return the current value
     */
    public float divideBy(final float a)
    {
        this.value = this.value / a;
        return getValue();
    }

    /**
     * @return value
     */
    public float getValue()
    {
        return this.value;
    }

}
