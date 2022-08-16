package nl.tudelft.simulation.dsol.interpreter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ObjectTest
{
    /**
     * Compare a number of methods when executed and when interpreted.
     */
    // TODO: @Test
    public void testInterpretation()
    {
        System.out.println("ObjectTest");

        String eabc = new String("ABC");
        String iabc = new String("ABC");
        ObjectMethods e1 = new ObjectMethods(eabc);
        ObjectMethods i1 = new ObjectMethods(iabc);
        ObjectMethods e2 = new ObjectMethods(eabc);
        ObjectMethods i2 = new ObjectMethods(iabc);
        ObjectMethods e3 = new ObjectMethods(new String("DEF"));
        ObjectMethods i3 = new ObjectMethods(new String("DEF"));
        ObjectMethods e4 = new ObjectMethods(new String("DEF"));
        ObjectMethods i4 = new ObjectMethods(new String("DEF"));

        // System.out.println("=========================================");
        // Interpreter.DEBUG = true;
        // Interpreter.invoke(i1, "getC", new Object[]{}, new Class<?>[]{});
        // System.out.println("=========================================");

        // object methods
        assertFalse("Object equals content (e)", e1.equals(e3));
        boolean iq = 1 == (int) Interpreter.invoke(i1, "equals", new Object[] {null}, new Class<?>[] {Object.class});
        assertFalse("Object equals content (i)", iq);

        assertTrue("Object equals same content (e)", e1.equals(e2));
        iq = 1 == (int) Interpreter.invoke(i1, "equals", new Object[] {i2}, new Class<?>[] {Object.class});
        assertTrue("Object equals same content (i)", iq);

        assertTrue("Object equals hashCode content (e)", e1.equalsHC(e2));
        iq = 1 == (int) Interpreter.invoke(i1, "equalsHC", new Object[] {i2}, new Class<?>[] {Object.class});
        assertTrue("Object equals hashCode content (i)", iq);

        assertFalse("Object equals hashCode other (e)", e1.equalsHC(e3));
        iq = 1 == (int) Interpreter.invoke(i1, "equalsHC", new Object[] {i3}, new Class<?>[] {Object.class});
        assertFalse("Object equals hashCode other (i)", iq);

        e4.set(null);
        assertTrue("Object null (e)", e4.get() == null);
        Interpreter.invoke(i4, "set", new Object[] {null}, new Class<?>[] {Object.class});
        assertTrue("Object null (i)", i4.get() == null);

        // TODO strings still contains an error
        /*-
        String se1 = ObjectMethods.concat5(e1, e2, e2, e3, e3);
        String se2 = ObjectMethods.concatN(e1, e2, e2, e3, e3);
        assertEquals("Object concat (e)", se1, se2);
        String si1 =
                (String) Interpreter.invoke(ObjectMethods.class, "concat5", new Object[]{e1, e2, e2, e3, e3},
                        new Class<?>[]{Object.class, Object.class, Object.class, Object.class, Object.class});
        String si2 =
                (String) Interpreter.invoke(ObjectMethods.class, "concatN", new Object[]{new Object[]{e1, e2, e2, e3,
                        e3}}, new Class<?>[]{Object[].class});
        assertEquals("Object concat (i)", si1, si2);
        */

    }
}
