package nl.tudelft.simulation.language.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;

import org.djutils.exceptions.Throw;

/**
 * Utilities for the BitSet class.
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public final class BitUtil implements Serializable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** Constructor is not called for utility class. */
    private BitUtil()
    {
        // Utility class
    }

    /**
     * @param bits BitSet; the bitset to convert
     * @return Returns a byte array of at least length 1. The most significant bit in the result is guaranteed not to be a 1
     *         (since BitSet does not support sign extension). The byte-ordering of the result is big-endian which means the
     *         most significant bit is in element 0. The bit at index 0 of the bit set is assumed to be the least significant
     *         bit.
     */
    public static byte[] toByteArray(final BitSet bits)
    {
        synchronized (bits)
        {
            byte[] bytes = new byte[bits.length() / 8 + 1];
            for (int i = 0; i < bits.length(); i++)
            {
                if (bits.get(i))
                {
                    bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
                }
            }
            return bytes;
        }
    }

    /**
     * returns the bitset of an integer value.
     * @param value int; the value
     * @param length int; the length of the bitSet to produce
     * @return the BitSet
     */
    public static BitSet fromInt(final int value, final int length)
    {
        return BitUtil.fromInteger(Integer.valueOf(value), length);
    }

    /**
     * returns the bitset of an integer value.
     * @param value Integer; the value
     * @param length int; the length of
     * @return the BitSet
     */
    public static BitSet fromInteger(final Integer value, final int length) 
    {
        Throw.when(length <= 0, IllegalArgumentException.class, "BitUtil.fromInt should have a positive number of bits");
        Throw.when(length > 31, IllegalArgumentException.class, "BitUtil.fromInt can have maximum 31 bits");
        Throw.when(value.intValue() < 0, IllegalArgumentException.class, "BitUtil.fromInt can have only positive values");
        return BitUtil.fromByteArray(new BigInteger(value.toString()).toByteArray());
    }

    /**
     * @param bits BitSet; the bitset to convert
     * @param length int; the length of the set
     * @return Returns an int. The most significant bit in the result is guaranteed not to be a 1 (since BitSet does not support
     *         sign extension). The int-ordering of the result is big-endian which means the most significant bit is in element
     *         0. The bit at index 0 of the bit set is assumed to be the least significant bit.
     */
    public static int toInt(final BitSet bits, final int length)
    {
        Throw.when(length <= 0, IllegalArgumentException.class, "BitUtil.toInt should have a positive number of bits");
        Throw.when(length > 31, IllegalArgumentException.class, "BitUtil.toInt can have maximum 31 bits");
        byte[] bytes = BitUtil.toByteArray(bits);
        return new BigInteger(bytes).intValue();
    }

    /**
     * constructs a new BitSet from a string in the "110110" format, or the {0, 1, 3, 5, 8, 12} format. Note that for the binary
     * representation, the LEAST SIGNIFICANT BIT COMES FIRST. So, 001 represents the value 4 and not 1.
     * @param value String; the value
     * @return the BitSet
     */
    public static BitSet fromString(final String value)
    {
        if (!value.trim().startsWith("{"))
        {
            BitSet set = new BitSet(value.length());
            for (int i = 0; i < value.length(); i++)
            {
                if (value.charAt(i) == '1')
                {
                    set.set(i, true);
                }
                else if (value.charAt(i) == '0')
                {
                    set.set(i, false);
                }
                else
                {
                    throw new IllegalArgumentException("value should only contain ones and zeros. Try 110011");
                }
            }
            return set;
        }
        BitSet set = new BitSet();
        String array = value.trim();
        if (!array.endsWith("}"))
        {
            throw new IllegalArgumentException("value that starts with { should end with }");
        }
        array = array.substring(1, array.length() - 1).trim();
        if (array.length() == 0)
        {
            return set;
        }
        String[] bits = array.split(",");
        for (int i = 0; i < bits.length; i++)
        {
            bits[i] = bits[i].trim();
            set.set(Integer.valueOf(bits[i]).intValue());
        }
        return set;
    }

    /**
     * @param bytes byte[]; the byteArray
     * @return Returns a bitset containing the values in bytes.The byte-ordering of bytes must be big-endian which means the
     *         most significant bit is in element 0.
     */
    public static BitSet fromByteArray(final byte[] bytes)
    {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++)
        {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0)
            {
                bits.set(i);
            }
        }
        return bits;
    }

    /**
     * returns a one-size BitSet with value.
     * @param value boolean; the value of the bitSet
     * @return the BitSet
     */
    public static BitSet fromBoolean(final boolean value)
    {
        BitSet result = new BitSet(1);
        result.set(0, value);
        return result;
    }
}
