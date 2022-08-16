package nl.tudelft.simulation.dsol.interpreter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DoubleInterpreterTest
{
    /** precision */
    private static final double DELTA = 0.0;

    /**
     * Compare a number of methods when executed and when interpreted.
     */
    @Test
    public void testInterpretation()
    {
        System.out.println("DoubleInterpreterTest");

        DoubleMethods e1 = new DoubleMethods(1.0d);
        DoubleMethods i1 = new DoubleMethods(1.0d);

        // integer methods
        e1.add(2.0d);
        Interpreter.invoke(i1, "add", new Double[] {2.0d}, new Class<?>[] {double.class});
        assertEquals("test Value(1.0d).add(2.0d) == 3", e1.getValue(), i1.getValue(), DELTA);

        e1.subtract(4.0d);
        Interpreter.invoke(i1, "subtract", new Double[] {4.0d}, new Class<?>[] {double.class});
        assertEquals("test Value(3.0d).subtract(4.0d) == -1", e1.getValue(), i1.getValue(), DELTA);

        e1.multiplyBy(-10.0d);
        Interpreter.invoke(i1, "multiplyBy", new Double[] {-10.0d}, new Class<?>[] {double.class});
        assertEquals("test Value(-1).multiplyBy(-10.0d) == 10.0", e1.getValue(), i1.getValue(), DELTA);

        e1.divideBy(3.0d);
        Interpreter.invoke(i1, "divideBy", new Double[] {3.0d}, new Class<?>[] {double.class});
        assertEquals("test Value(10).divideBy(3.0d) == 3", e1.getValue(), i1.getValue(), DELTA);

        // static methods
        assertEquals(
                "DoubleMethods.plus(2.0d, 3.0d)", ((Double) Interpreter.invoke(DoubleMethods.class, "plus",
                        new Double[] {2.0d, 3.0d}, new Class<?>[] {double.class, double.class})).doubleValue(),
                DoubleMethods.plus(2.0d, 3.0d), DELTA);
        assertEquals(
                "DoubleMethods.minus(2.0d, 3.0d)", ((Double) Interpreter.invoke(DoubleMethods.class, "minus",
                        new Double[] {2.0d, 3.0d}, new Class<?>[] {double.class, double.class})).doubleValue(),
                DoubleMethods.minus(2.0d, 3.0d), DELTA);
        assertEquals(
                "DoubleMethods.product(2.0d, 3.0d)", ((Double) Interpreter.invoke(DoubleMethods.class, "product",
                        new Double[] {2.0d, 3.0d}, new Class<?>[] {double.class, double.class})).doubleValue(),
                DoubleMethods.product(2.0d, 3.0d), DELTA);
        assertEquals(
                "DoubleMethods.divide(2.0d, 3.0d)", ((Double) Interpreter.invoke(DoubleMethods.class, "divide",
                        new Double[] {2.0d, 3.0d}, new Class<?>[] {double.class, double.class})).doubleValue(),
                DoubleMethods.divide(2.0d, 3.0d), DELTA);
    }
}
