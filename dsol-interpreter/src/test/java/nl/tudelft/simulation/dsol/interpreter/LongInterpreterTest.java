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
public class LongInterpreterTest
{
    /**
     * Compare a number of methods when executed and when interpreted.
     */
    @Test
    public void testInterpretation()
    {
        System.out.println("LongInterpreterTest");

        LongMethods e1 = new LongMethods(1L);
        LongMethods i1 = new LongMethods(1L);

        // long methods
        e1.add(2);
        Interpreter.invoke(i1, "add", new Long[] {2L}, new Class<?>[] {long.class});
        assertEquals("test Value(1).add(2) == 3", e1.getValue(), i1.getValue());

        e1.subtract(4);
        Interpreter.invoke(i1, "subtract", new Long[] {4L}, new Class<?>[] {long.class});
        assertEquals("test Value(3).subtract(4) == -1", e1.getValue(), i1.getValue());

        e1.multiplyBy(-10);
        Interpreter.invoke(i1, "multiplyBy", new Long[] {-10L}, new Class<?>[] {long.class});
        assertEquals("test Value(-1).multiplyBy(-10) == 10", e1.getValue(), i1.getValue());

        e1.divideBy(3);
        Interpreter.invoke(i1, "divideBy", new Long[] {3L}, new Class<?>[] {long.class});
        assertEquals("test Value(10).divideBy(3) == 3", e1.getValue(), i1.getValue());

        // static methods
        assertEquals("LongMethods.plus(2, 3)", ((Long) Interpreter.invoke(LongMethods.class, "plus", new Long[] {2L, 3L},
                new Class<?>[] {long.class, long.class})).longValue(), LongMethods.plus(2L, 3L));
        assertEquals("LongMethods.minus(2, 3)", ((Long) Interpreter.invoke(LongMethods.class, "minus", new Long[] {2L, 3L},
                new Class<?>[] {long.class, long.class})).longValue(), LongMethods.minus(2L, 3L));
        assertEquals("LongMethods.product(2, 3)", ((Long) Interpreter.invoke(LongMethods.class, "product", new Long[] {2L, 3L},
                new Class<?>[] {long.class, long.class})).longValue(), LongMethods.product(2L, 3L));
        assertEquals("LongMethods.divide(2, 3)", ((Long) Interpreter.invoke(LongMethods.class, "divide", new Long[] {2L, 3L},
                new Class<?>[] {long.class, long.class})).longValue(), LongMethods.divide(2L, 3L));
    }
}
