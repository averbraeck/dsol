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
public class IntegerInterpreterTest
{
    /**
     * Compare a number of methods when executed and when interpreted.
     */
    @Test
    public void testInterpretation()
    {
        System.out.println("IntegerInterpreterTest");

        IntegerMethods e1 = new IntegerMethods(1);
        IntegerMethods i1 = new IntegerMethods(1);

        // integer methods
        e1.add(2);
        Interpreter.invoke(i1, "add", new Integer[] {2}, new Class<?>[] {int.class});
        assertEquals("test Value(1).add(2) == 3", e1.getValue(), i1.getValue());

        e1.subtract(4);
        Interpreter.invoke(i1, "subtract", new Integer[] {4}, new Class<?>[] {int.class});
        assertEquals("test Value(3).subtract(4) == -1", e1.getValue(), i1.getValue());

        e1.multiplyBy(-10);
        Interpreter.invoke(i1, "multiplyBy", new Integer[] {-10}, new Class<?>[] {int.class});
        assertEquals("test Value(-1).multiplyBy(-10) == 10", e1.getValue(), i1.getValue());

        e1.divideBy(3);
        Interpreter.invoke(i1, "divideBy", new Integer[] {3}, new Class<?>[] {int.class});
        assertEquals("test Value(10).divideBy(3) == 3", e1.getValue(), i1.getValue());

        // static methods
        assertEquals("IntegerMethods.plus(2, 3)", ((Integer) Interpreter.invoke(IntegerMethods.class, "plus",
                new Integer[] {2, 3}, new Class<?>[] {int.class, int.class})).intValue(), IntegerMethods.plus(2, 3));
        assertEquals("IntegerMethods.minus(2, 3)", ((Integer) Interpreter.invoke(IntegerMethods.class, "minus",
                new Integer[] {2, 3}, new Class<?>[] {int.class, int.class})).intValue(), IntegerMethods.minus(2, 3));
        assertEquals("IntegerMethods.product(2, 3)", ((Integer) Interpreter.invoke(IntegerMethods.class, "product",
                new Integer[] {2, 3}, new Class<?>[] {int.class, int.class})).intValue(), IntegerMethods.product(2, 3));
        assertEquals("IntegerMethods.divide(2, 3)", ((Integer) Interpreter.invoke(IntegerMethods.class, "divide",
                new Integer[] {2, 3}, new Class<?>[] {int.class, int.class})).intValue(), IntegerMethods.divide(2, 3));
    }
}
