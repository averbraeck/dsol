package nl.tudelft.simulation.language.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.djutils.exceptions.Try;
import org.djutils.exceptions.Try.Execution;
import org.junit.Test;

/**
 * BitSetTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BitSetTest
{
    /**
     * Test the BitSet class.
     */
    @Test
    public void testBitSet()
    {
        BitSet bs1 = BitUtil.fromBoolean(true);
        assertEquals(true, bs1.get(0));
        bs1 = BitUtil.fromBoolean(false);
        assertEquals(false, bs1.get(0));

        bs1 = BitUtil.fromInt(0, 31);
        for (int i = 0; i < 31; i++)
        {
            assertFalse(bs1.get(i));
        }
        bs1 = BitUtil.fromInt(Integer.MAX_VALUE, 31);
        for (int i = 0; i < 31; i++)
        {
            assertTrue(bs1.get(i));
        }

        bs1 = BitUtil.fromInt(1 + 4 + 16 + 64, 8);
        assertTrue(bs1.get(0));
        assertFalse(bs1.get(1));
        assertTrue(bs1.get(2));
        assertFalse(bs1.get(3));
        assertTrue(bs1.get(4));
        assertFalse(bs1.get(5));
        assertTrue(bs1.get(6));
        assertFalse(bs1.get(7));

        assertEquals(bs1, BitUtil.fromByteArray(bs1.toByteArray()));
        assertEquals(bs1, BitUtil.fromByteArray(BitUtil.toByteArray(bs1)));
        assertEquals(bs1, BitUtil.fromString(bs1.toString()));
        assertEquals(bs1, BitUtil.fromInt(BitUtil.toInt(bs1, 8), 8));

        String s = "10101010";
        BitSet bs2 = BitUtil.fromString(s);
        assertEquals(bs1, bs2);
        s = "{0, 2, 4, 6}";
        bs2 = BitUtil.fromString(s);
        assertEquals(bs1, bs2);
        s = " {  0,  2,4,   6  } ";
        bs2 = BitUtil.fromString(s);
        assertEquals(bs1, bs2);
        bs2 = BitUtil.fromString("{}");
        assertEquals(0, BitUtil.toInt(bs2, 8));

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.toInt(BitUtil.fromString("1001"), 32);
            }
        }, "BitUtil.toInt max 31 bits");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.toInt(BitUtil.fromString("1001"), 0);
            }
        }, "BitUtil.toInt positive number of bits");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.fromInt(10, 32);
            }
        }, "BitUtil.toInt max 31 bits");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.fromInt(10, 0);
            }
        }, "BitUtil.fromInt positive number of bits");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.fromInt(-1, 30);
            }
        }, "BitUtil.fromInt positive value only");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.fromString("{1,3,5");
            }
        }, "BitUtil.fromInt positive value only");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                BitUtil.fromString("0012001");
            }
        }, "BitUtil.fromInt positive value only");

    }
}
