package nl.tudelft.simulation.dsol.interpreter;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class CalcInterpreterTest
{
    /**
     * Make arrays and test operations.
     */
    @Test
    public void calcTest()
    {
        Assert.assertEquals("dValue", dValue(), (double) Interpreter.invoke(this, "dValue", new Object[] {}, new Class<?>[] {}),
                0.0);
        Assert.assertEquals("fValue", fValue(), (float) Interpreter.invoke(this, "fValue", new Object[] {}, new Class<?>[] {}),
                0.0);
        Assert.assertEquals("lValue", lValue(), (long) Interpreter.invoke(this, "lValue", new Object[] {}, new Class<?>[] {}));
        Assert.assertEquals("iValue", iValue(), (int) Interpreter.invoke(this, "iValue", new Object[] {}, new Class<?>[] {}));
        Assert.assertEquals("sValue", sValue(),
                (short) (int) Interpreter.invoke(this, "sValue", new Object[] {}, new Class<?>[] {}));

        double d1 = newDouble(4.77d);
        Object od = Interpreter.invoke(this, "newDouble", new Object[] {Double.valueOf(4.77d)}, new Class<?>[] {double.class});
        Assert.assertEquals("double return is Double class", od.getClass(), Double.class);
        double d2 = (double) od;
        Assert.assertEquals("4.77d ^ 2 == 4.77d ^ 2", d1, d2, 0.0);

        boolean b1 = newBool(true);
        Object ob = Interpreter.invoke(this, "newBool", new Object[] {Boolean.valueOf(true)}, new Class<?>[] {boolean.class});
        // boolean is stored on the stack as an int.
        // Assert.assertEquals("boolean return is Boolean class", ob.getClass(), Boolean.class);
        Assert.assertEquals("boolean return is Integer class", ob.getClass(), Integer.class);
        boolean b2 = ((int) ob) == 1;
        Assert.assertEquals("false == false", b1, b2);

        float f1 = newFloat(33.6673f);
        Object of = Interpreter.invoke(this, "newFloat", new Object[] {Float.valueOf(33.6673f)}, new Class<?>[] {float.class});
        Assert.assertEquals("float return is Float class", of.getClass(), Float.class);
        float f2 = (float) of;
        Assert.assertEquals("33.6673f ^ 2 == 33.6673f ^ 2", f1, f2, 0.0);

        long l1 = newLong(8332L);
        Object ol = Interpreter.invoke(this, "newLong", new Object[] {Long.valueOf(8332L)}, new Class<?>[] {long.class});
        Assert.assertEquals("long return is Long class", ol.getClass(), Long.class);
        long l2 = (long) ol;
        Assert.assertEquals("8332L ^ 2 == 8332L ^ 2", l1, l2);

        short s1 = newShort((short) 55);
        Object os =
                Interpreter.invoke(this, "newShort", new Object[] {Short.valueOf((short) 55)}, new Class<?>[] {short.class});
        // short is stored on the stack as an int.
        // Assert.assertEquals("short return is Short class", os.getClass(), Short.class);
        Assert.assertEquals("short return is Integer class", os.getClass(), Integer.class);
        short s2 = (short) (int) os;
        Assert.assertEquals("8332L ^ 2 == 8332L ^ 2", s1, s2);

        double df1 = dfCalc();
        double df2 = (double) Interpreter.invoke(this, "dfCalc", new Object[] {}, new Class<?>[] {});
        Assert.assertEquals("dfCalc1 == dfCalc2", df1, df2, 0.0);

        double cd1 = complexCalc();
        double cd2 = (double) Interpreter.invoke(this, "complexCalc", new Object[] {}, new Class<?>[] {});
        Assert.assertEquals("complexCalc1 == complexCalc2", cd1, cd2, 0.0);
    }

    /**
     * @return simple value
     */
    protected double dValue()
    {
        double d = 12345.678d;
        return d;
    }

    /**
     * @return simple value
     */
    protected float fValue()
    {
        float f = 12345.678f;
        return f;
    }

    /**
     * @return simple value
     */
    protected long lValue()
    {
        long l = 12345678;
        return l;
    }

    /**
     * @return simple value
     */
    protected int iValue()
    {
        int i = 12345678;
        return i;
    }

    /**
     * @return simple value
     */
    protected short sValue()
    {
        short s = 12345;
        return s;
    }

    /**
     * Make a calculation with double - float type conversions.
     * @return a number based on the type conversions.
     */
    @SuppressWarnings("cast")
    protected double dfCalc()
    {
        double d = 4.77d;
        float f = 33.6673f;

        double d2 = newDouble(d) - d;
        float f2 = newFloat(f) - f;

        double d3 = (double) f2;
        float f3 = (float) d2;

        double r = d3 + f3;
        return r;
    }

    /**
     * Make a calculation with many type conversions.
     * @return a number based on the type conversions.
     */
    @SuppressWarnings("cast")
    protected double complexCalc()
    {
        double d = 4.77d;
        int i = 77;
        long l = 24L;
        boolean b = true;
        char c = 'F';
        float f = 33.6673f;
        short s = 57;
        byte y = 4;

        d = newDouble(d);
        i = newInt(i);
        l = newLong(l);
        b = newBool(b);
        c = newChar(c);
        s = newShort(s);

        float f2 = (float) d;
        double d2 = (double) f;

        int i2 = (int) s;
        short s2 = (short) i;

        long l2 = (long) i2;
        int i3 = (int) l2;

        short s3 = (short) l;
        long l3 = (long) s2;

        int i4 = (int) y;
        byte b2 = (byte) i;

        int i5 = (int) f2;
        float f3 = (float) i3;

        int i6 = (int) d2;
        double d3 = (double) i5;

        long l4 = (long) f3;
        float f4 = (float) l3;

        long l5 = (long) d3;
        double d4 = (double) l4;

        int i7 = (int) c;

        double r = ((d4 + f4) * (d3 + f3)) + (1.0d * (l4 + l5 - i7)) + (2.0d * (i4 + i5 + i6 - s3 + b2));

        return r;
    }

    /**
     * get a double.
     * @param d the value to get
     * @return modified value
     */
    protected double newDouble(final double d)
    {
        return d * d;
    }

    /**
     * get a float.
     * @param f the value to get
     * @return modified value
     */
    protected float newFloat(final float f)
    {
        return f * f;
    }

    /**
     * get an int.
     * @param i the value to get
     * @return modified value
     */
    protected int newInt(final int i)
    {
        return i * i;
    }

    /**
     * get a short.
     * @param s the value to get
     * @return modified value
     */
    protected short newShort(final short s)
    {
        return (short) (s + s);
    }

    /**
     * get a long.
     * @param l the value to get
     * @return modified value
     */
    protected long newLong(final long l)
    {
        return l * l;
    }

    /**
     * get a char.
     * @param c the value to get
     * @return modified value
     */
    protected char newChar(final char c)
    {
        return (char) (((byte) c) + 1);
    }

    /**
     * get a boolean.
     * @param b the value to get
     * @return modified value
     */
    protected boolean newBool(final boolean b)
    {
        return !b;
    }

}
