package nl.tudelft.simulation.dsol.interpreter;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ArrayInterpreterTest
{
    /**
     * Make arrays and test operations.
     */
    @Test
    public void arrayTest()
    {
        String[] a1 = stringArrayContent();
        String[] a2 = (String[]) Interpreter.invoke(this, "stringArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertEquals("length  array1 == array2", a1.length, a2.length);
        List<String> l1 = Arrays.asList(a1);
        List<String> l2 = Arrays.asList(a2);
        boolean same = l1.equals(l2);
        Assert.assertTrue("Content array1 == content array2", same);

        String s1 = getStringContentAt(a1, 2);
        String s2 = (String) Interpreter.invoke(this, "getStringContentAt", new Object[] {a1, Integer.valueOf(2)},
                new Class<?>[] {String[].class, int.class});
        Assert.assertEquals("String array1[2] == 'ccc'", s2, "ccc");
        Assert.assertEquals("String array1[2] == array2[2]", s1, s2);

        int i1 = getLength(a1);
        int i2 = (int) Interpreter.invoke(this, "getLength", new Object[] {a1}, new Class<?>[] {String[].class});
        Assert.assertEquals("String array1.length == 4", i1, 4);
        Assert.assertEquals("String array1.length == array2.length", i1, i2);

        // double
        double[] double1 = doubleArrayContent();
        double[] double2 = (double[]) Interpreter.invoke(this, "doubleArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertArrayEquals("double arrays equal", double1, double2, 0.0);
        double doubleValue1 = getDoubleContentAt(double1, 3);
        double doubleValue2 = (double) Interpreter.invoke(this, "getDoubleContentAt", new Object[] {double1, Integer.valueOf(3)},
                new Class<?>[] {double[].class, int.class});
        Assert.assertEquals("Double array2[3] == '4'", doubleValue2, 4.0d, 0.0);
        Assert.assertEquals("Double array1[3] == array2[3]", doubleValue1, doubleValue2, 0.0);

        // float
        float[] float1 = floatArrayContent();
        float[] float2 = (float[]) Interpreter.invoke(this, "floatArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertArrayEquals("float arrays equal", float1, float2, 0.0f);
        float floatValue1 = getFloatContentAt(float1, 3);
        float floatValue2 = (float) Interpreter.invoke(this, "getFloatContentAt", new Object[] {float1, Integer.valueOf(3)},
                new Class<?>[] {float[].class, int.class});
        Assert.assertEquals("Float array2[3] == '4'", floatValue2, 4.0f, 0.0);
        Assert.assertEquals("Float array1[3] == array2[3]", floatValue1, floatValue2, 0.0);

        // int
        int[] int1 = intArrayContent();
        int[] int2 = (int[]) Interpreter.invoke(this, "intArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertArrayEquals("int arrays equal", int1, int2);
        int intValue1 = getIntegerContentAt(int1, 3);
        int intValue2 = (int) Interpreter.invoke(this, "getIntegerContentAt", new Object[] {int1, Integer.valueOf(3)},
                new Class<?>[] {int[].class, int.class});
        Assert.assertEquals("Integer array2[3] == '4'", intValue2, 4);
        Assert.assertEquals("Integer array1[3] == array2[3]", intValue1, intValue2);

        // long
        long[] long1 = longArrayContent();
        long[] long2 = (long[]) Interpreter.invoke(this, "longArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertArrayEquals("long arrays equal", long1, long2);
        long longValue1 = getLongContentAt(long1, 3);
        long longValue2 = (long) Interpreter.invoke(this, "getLongContentAt", new Object[] {long1, Integer.valueOf(3)},
                new Class<?>[] {long[].class, int.class});
        Assert.assertEquals("Long array2[3] == '4'", longValue2, 4L);
        Assert.assertEquals("Long array1[3] == array2[3]", longValue1, longValue2);

        /*-
         *  TODO FAILS on SASHORT
        // short
        short[] short1 = shortArrayContent();
        short[] short2 = (short[]) Interpreter.invoke(this, "shortArrayContent", new Object[]{}, new Class<?>[]{});
        Assert.assertArrayEquals("short arrays equal", short1, short2);
        short shortValue1 = getShortContentAt(short1, 3);
        short shortValue2 =
                (short) (int) Interpreter.invoke(this, "getShortContentAt", new Object[]{short1, Integer.valueOf(3)},
                        new Class<?>[]{short[].class, int.class});
        Assert.assertEquals("Short array2[3] == '4'", shortValue2, (short) 4);
        Assert.assertEquals("Short array1[3] == array2[3]", shortValue1, shortValue2);
         */

        // byte
        byte[] byte1 = byteArrayContent();
        byte[] byte2 = (byte[]) Interpreter.invoke(this, "byteArrayContent", new Object[] {}, new Class<?>[] {});
        Assert.assertArrayEquals("byte arrays equal", byte1, byte2);
        byte byteValue1 = getByteContentAt(byte1, 3);
        byte byteValue2 = (byte) Interpreter.invoke(this, "getByteContentAt", new Object[] {byte1, Integer.valueOf(3)},
                new Class<?>[] {byte[].class, int.class});
        Assert.assertEquals("Byte array2[3] == '4'", byteValue2, 4);
        Assert.assertEquals("Byte array1[3] == array2[3]", byteValue1, byteValue2);

    }

    /**
     * @return an array of Strings
     */
    protected String[] stringArrayContent()
    {
        String[] array = new String[] {"a", "bb", "c", "dddd"};
        array[2] = "ccc";
        return array;
    }

    /**
     * @param s String[]; String array data
     * @param i int; index
     * @return a String index
     */
    protected String getStringContentAt(final String[] s, final int i)
    {
        return s[i];
    }

    /**
     * @param s String[]; double array data
     * @return the String length
     */
    protected int getLength(final String[] s)
    {
        return s.length;
    }

    /**
     * @return an array
     */
    protected double[] doubleArrayContent()
    {
        double[] array = new double[] {1.0d, 2.0d, 3.0d, 0.0d};
        array[3] = 4.0d;
        return array;
    }

    /**
     * @param a double[]; double array data
     * @param i int; index
     * @return an array content at location
     */
    protected double getDoubleContentAt(final double[] a, final int i)
    {
        return a[i];
    }

    /**
     * @return an array
     */
    protected float[] floatArrayContent()
    {
        float[] array = new float[] {1.0f, 2.0f, 3.0f, 0.0f};
        array[3] = 4.0f;
        return array;
    }

    /**
     * @param a float[]; float array data
     * @param i int; index
     * @return an array content at location
     */
    protected float getFloatContentAt(final float[] a, final int i)
    {
        return a[i];
    }

    /**
     * @return an array
     */
    protected int[] intArrayContent()
    {
        int[] array = new int[] {1, 2, 3, 0};
        array[3] = 4;
        return array;
    }

    /**
     * @param a int[]; int array data
     * @param i int; index
     * @return an array content at location
     */
    protected int getIntegerContentAt(final int[] a, final int i)
    {
        return a[i];
    }

    /**
     * @return an array
     */
    protected long[] longArrayContent()
    {
        long[] array = new long[] {1, 2, 3, 0};
        array[3] = 4;
        return array;
    }

    /**
     * @param a long[]; long array data
     * @param i int; index
     * @return an array content at location
     */
    protected long getLongContentAt(final long[] a, final int i)
    {
        return a[i];
    }

    /**
     * @return an array
     */
    protected short[] shortArrayContent()
    {
        short[] array = new short[] {1, 2, 3, 0};
        array[3] = 4;
        return array;
    }

    /**
     * @param a short[]; short array data
     * @param i int; index
     * @return an array content at location
     */
    protected short getShortContentAt(final short[] a, final int i)
    {
        return a[i];
    }

    /**
     * @return an array
     */
    protected byte[] byteArrayContent()
    {
        byte[] array = new byte[] {1, 2, 3, 0};
        array[3] = 4;
        return array;
    }

    /**
     * @param a byte[]; byte array data
     * @param i int; index
     * @return an array content at location
     */
    protected byte getByteContentAt(final byte[] a, final int i)
    {
        return a[i];
    }

}
