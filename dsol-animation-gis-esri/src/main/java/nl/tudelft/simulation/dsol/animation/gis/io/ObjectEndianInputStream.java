package nl.tudelft.simulation.dsol.animation.gis.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class enables the object inputstream to be switched from big endian (default in Java) to little endian. The class works
 * exactly like an ObjectInputStream.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ObjectEndianInputStream implements DataInput
{
    /** the datainput stream. */
    private DataInputStream dataInputStream;

    /** the inputStream. */
    private InputStream inputStream;

    /** an 8byte buffer. */
    private byte[] buffer = new byte[8];

    /** the code. */
    private Endianness endianness = Endianness.BIG_ENDIAN;

    /**
     * constructs a new ObjectEndianInputStream.
     * @param inputStream InputStream; the inputStream to use
     */
    public ObjectEndianInputStream(final InputStream inputStream)
    {
        this.inputStream = inputStream;
        this.dataInputStream = new DataInputStream(inputStream);
    }

    /** {@inheritDoc} */
    @Override
    public short readShort() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readShort();
        }
        this.dataInputStream.readFully(this.buffer, 0, 2);
        return (short) ((this.buffer[1] & 0xff) << 8 | (this.buffer[0] & 0xff));
    }

    /** {@inheritDoc} */
    @Override
    public int readUnsignedShort() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readUnsignedShort();
        }
        this.dataInputStream.readFully(this.buffer, 0, 2);
        return ((this.buffer[1] & 0xff) << 8 | (this.buffer[0] & 0xff));
    }

    /** {@inheritDoc} */
    @Override
    public char readChar() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readChar();
        }
        this.dataInputStream.readFully(this.buffer, 0, 2);
        return (char) ((this.buffer[1] & 0xff) << 8 | (this.buffer[0] & 0xff));
    }

    /** {@inheritDoc} */
    @Override
    public int readInt() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readInt();
        }
        this.dataInputStream.readFully(this.buffer, 0, 4);
        return (this.buffer[3]) << 24 | (this.buffer[2] & 0xff) << 16 | (this.buffer[1] & 0xff) << 8 | (this.buffer[0] & 0xff);
    }

    /** {@inheritDoc} */
    @Override
    public long readLong() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readLong();
        }
        this.dataInputStream.readFully(this.buffer, 0, 8);
        return (long) (this.buffer[7]) << 56 | (long) (this.buffer[6] & 0xff) << 48 | (long) (this.buffer[5] & 0xff) << 40
                | (long) (this.buffer[4] & 0xff) << 32 | (long) (this.buffer[3] & 0xff) << 24
                | (long) (this.buffer[2] & 0xff) << 16 | (long) (this.buffer[1] & 0xff) << 8 | (this.buffer[0] & 0xff);
    }

    /** {@inheritDoc} */
    @Override
    public float readFloat() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readFloat();
        }
        return Float.intBitsToFloat(readInt());
    }

    /** {@inheritDoc} */
    @Override
    public double readDouble() throws IOException
    {
        if (this.endianness.equals(Endianness.BIG_ENDIAN))
        {
            return this.dataInputStream.readDouble();
        }
        return Double.longBitsToDouble(readLong());
    }

    /**
     * reads b from the stream.
     * @param b byte[]; byte
     * @return in the value
     * @throws IOException on failure
     */
    public int read(final byte[] b) throws IOException
    {
        return this.inputStream.read(b);
    }

    /**
     * reads b from the stream.
     * @param b byte[]; byte
     * @param off int; offset
     * @param len int; length
     * @return in the value
     * @throws IOException on failure
     */
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        return this.inputStream.read(b, off, len);
    }

    /** {@inheritDoc} */
    @Override
    public void readFully(final byte[] b) throws IOException
    {
        this.dataInputStream.readFully(b, 0, b.length);
    }

    /** {@inheritDoc} */
    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException
    {
        this.dataInputStream.readFully(b, off, len);
    }

    /** {@inheritDoc} */
    @Override
    public int skipBytes(final int n) throws IOException
    {
        return this.dataInputStream.skipBytes(n);
    }

    /** {@inheritDoc} */
    @Override
    public boolean readBoolean() throws IOException
    {
        return this.dataInputStream.readBoolean();
    }

    /** {@inheritDoc} */
    @Override
    public byte readByte() throws IOException
    {
        return this.dataInputStream.readByte();
    }

    /** {@inheritDoc} */
    @Override
    public int readUnsignedByte() throws IOException
    {
        return this.dataInputStream.readUnsignedByte();
    }

    /** {@inheritDoc} */
    @Override
    public String readUTF() throws IOException
    {
        return this.dataInputStream.readUTF();
    }

    /** {@inheritDoc} */
    @Override
    public String readLine()
    {
        throw new UnsupportedOperationException("Binary reading does not support readLine method");
    }

    /**
     * reads UTF from the stream.
     * @param dataInput DataInput; data input
     * @return String the value
     * @throws IOException on read failure
     */
    public static final String readUTF(final DataInput dataInput) throws IOException
    {
        return DataInputStream.readUTF(dataInput);
    }

    /**
     * @throws IOException on close failure
     */
    public void close() throws IOException
    {
        this.dataInputStream.close();
    }

    /**
     * Return the Endianness, i.e., big endian or little endian encoding.
     * @return Endianness; big endian or little endian encoding
     */
    public Endianness getEndianness()
    {
        return this.endianness;
    }

    /**
     * Set the Endianness, i.e., big endian or little endian encoding.
     * @param endianness Endianness; big endian or little endian encoding
     */
    public void setEndianness(final Endianness endianness)
    {
        this.endianness = endianness;
    }

}
