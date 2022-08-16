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
public class SupSubTest
{
    /**
     * Call a number of methods and super-methods when executed and when interpreted.
     */
    @Test
    public void testInterpretation()
    {
        System.out.println("SupSubTest");

        // Interpreter.DEBUG = true;

        SupMethods sup = new SupMethods();
        SubMethods sub = new SubMethods();

        // integers
        assertEquals("Sub.iSub10 (e)", sub.iSub10(), 10);
        assertEquals("Sup.iSub10 (e)", sup.iSub10(), 10);
        assertEquals("Sub.iOver5 (e)", sub.iOver5(), 10);
        assertEquals("Sup.iOver5 (e)", sup.iOver5(), 5);
        assertEquals("Sup.iPlus4 (e)", sup.iPlus4(), 7);
        assertEquals("Sup.iPl123 (e)", sup.iPl123(0), 6);

        assertEquals("Sub.iSub10 (i)", (int) Interpreter.invoke(sub, "iSub10", new Object[] {}, new Class<?>[] {}), 10);
        assertEquals("Sup.iSub10 (i)", (int) Interpreter.invoke(sup, "iSub10", new Object[] {}, new Class<?>[] {}), 10);
        assertEquals("Sub.iOver5 (i)", (int) Interpreter.invoke(sub, "iOver5", new Object[] {}, new Class<?>[] {}), 10);
        assertEquals("Sup.iOver5 (i)", (int) Interpreter.invoke(sup, "iOver5", new Object[] {}, new Class<?>[] {}), 5);
        assertEquals("Sup.iPlus4 (i)", (int) Interpreter.invoke(sup, "iPlus4", new Object[] {}, new Class<?>[] {}), 7);
        assertEquals("Sup.iPl123 (i)",
                (int) Interpreter.invoke(sup, "iPl123", new Object[] {Integer.valueOf(0)}, new Class<?>[] {int.class}), 6);

        // TODO strings still contains an error
        /*-
        assertEquals("Sub.sSubABC (e)", sub.sSubABC(), "ABC");
        assertEquals("Sup.sSubABC (e)", sup.sSubABC(), "ABC");
        assertEquals("Sup.sPlusDEF (e)", sup.sPlusDEF("ABC"), "ABCDEFABC");
        
        assertEquals("Sub.sSubABC (i)", Interpreter.invoke(sub, "sSubABC", new Object[]{}, new Class<?>[]{}), "ABC");
        assertEquals("Sup.sSubABC (i)", Interpreter.invoke(sup, "sSubABC", new Object[]{}, new Class<?>[]{}), "ABC");
        assertEquals("Sup.splusDEF (i)",
                Interpreter.invoke(sup, "sPlusDEF", new Object[]{"ABC"}, new Class<?>[]{String.class}), "ABCDEFABC");
         */
        // new StringBuilder().append("abc");

    }
}
