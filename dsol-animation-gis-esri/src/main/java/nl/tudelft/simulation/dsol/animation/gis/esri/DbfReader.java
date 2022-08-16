package nl.tudelft.simulation.dsol.animation.gis.esri;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import nl.tudelft.simulation.dsol.animation.gis.io.Endianness;
import nl.tudelft.simulation.dsol.animation.gis.io.ObjectEndianInputStream;

/**
 * This class reads a dbf file (in dBase III format), as used in ESRI ShapeFiles.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * <p>
 * The dsol-animation-gis project is based on the gisbeans project that has been part of DSOL since 2002, originally by Peter
 * Jacobs and Paul Jacobs.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DbfReader implements Serializable
{
    /** */
    private static final long serialVersionUID = 20201223L;

    /** the FBFFile. */
    private URL dbfFile;

    /** the numberofColumns. */
    private int[] columnLength;

    /** the names of the columns. */
    private String[] columnNames;

    /** the headerLength. */
    private int headerLength = 0;

    /** the number of columns. */
    private int numColumns = 0;

    /** the number of records. */
    private int numRecords = 0;

    /** the length of the records. */
    private int recordLength = 0;

    /**
     * Construct a DbfReader.
     * @param dbfFile URL; the URL of the dbfFile
     * @throws IOException whenever url does not occur to exist.
     */
    public DbfReader(final URL dbfFile) throws IOException
    {
        this.dbfFile = dbfFile;
        ObjectEndianInputStream dbfInput = new ObjectEndianInputStream(dbfFile.openStream());
        if (dbfInput.readByte() != 3)
        {
            throw new IOException("dbf file does not seem to be a Dbase III file");
        }

        dbfInput.skipBytes(3);
        dbfInput.setEndianness(Endianness.LITTLE_ENDIAN);

        this.numRecords = dbfInput.readInt();
        this.headerLength = dbfInput.readShort();
        this.numColumns = (this.headerLength - 33) / 32;
        this.recordLength = dbfInput.readShort();

        this.columnLength = new int[this.numColumns];
        this.columnNames = new String[this.numColumns];

        dbfInput.skipBytes(20);
        for (int i = 0; i < this.numColumns; i++)
        {
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < 10; j++)
            {
                byte b = dbfInput.readByte();
                if (b > 31)
                {
                    buffer.append((char) b);
                }
            }
            this.columnNames[i] = buffer.toString();
            dbfInput.setEndianness(Endianness.BIG_ENDIAN);
            dbfInput.readChar();

            dbfInput.skipBytes(4);

            this.columnLength[i] = dbfInput.readByte();
            if (this.columnLength[i] < 0)
            {
                this.columnLength[i] += 256;
            }
            dbfInput.skipBytes(15);
        }
        dbfInput.close();
    }

    /**
     * returns the columnNames.
     * @return String[] the columnNames
     */
    public String[] getColumnNames()
    {
        return this.columnNames;
    }

    /**
     * returns the row.
     * @param rowNumber int; the rowNumber
     * @return String[] the attributes of the row
     * @throws IOException on read failure
     * @throws IndexOutOfBoundsException whenever the rowNumber &gt; numRecords
     */
    public String[] getRow(final int rowNumber) throws IOException, IndexOutOfBoundsException
    {
        if (rowNumber > this.numRecords)
        {
            throw new IndexOutOfBoundsException("dbfFile : rowNumber > numRecords");
        }

        // Either we may not cache of the cache is still empty
        String[] row = new String[this.numColumns];

        ObjectEndianInputStream dbfInput = new ObjectEndianInputStream(this.dbfFile.openStream());
        dbfInput.skipBytes(this.headerLength + 1);
        dbfInput.skipBytes(rowNumber * this.recordLength);
        for (int i = 0; i < this.numColumns; i++)
        {
            byte[] bytes = new byte[this.columnLength[i]];
            dbfInput.read(bytes);
            row[i] = new String(bytes);
        }
        dbfInput.close();
        return row;
    }

    /**
     * returns a table of all attributes stored for the particular dbf-file.
     * @return String[][] a table of attributes
     * @throws IOException an IOException
     */
    public String[][] getRows() throws IOException
    {
        String[][] result = new String[this.numRecords][this.numColumns];
        ObjectEndianInputStream dbfInput = new ObjectEndianInputStream(this.dbfFile.openStream());
        dbfInput.skipBytes(this.headerLength + 1);
        for (int row = 0; row < this.numRecords; row++)
        {
            for (int col = 0; col < this.numColumns; col++)
            {
                byte[] bytes = new byte[this.columnLength[col]];
                dbfInput.read(bytes);
                result[row][col] = new String(bytes);
                if (col == this.numColumns - 1)
                {
                    dbfInput.skipBytes(1);
                }
            }
        }
        dbfInput.close();
        return result;
    }

    /**
     * returns the array of rowNumbers belonging to a attribute/column pair.
     * @param attribute String; the attribute value
     * @param columnName String; the name of the column
     * @return int[] the array of shape numbers.
     * @throws IOException on read failure
     */
    public int[] getRowNumbers(final String attribute, final String columnName) throws IOException
    {
        ArrayList<Integer> result = new ArrayList<>();
        String[][] rows = this.getRows();
        for (int col = 0; col < this.numColumns; col++)
        {
            if (this.columnNames[col].equals(columnName))
            {
                for (int row = 0; row < this.numRecords; row++)
                {
                    if (rows[row][col].equals(attribute))
                    {
                        result.add(Integer.valueOf(row));
                    }
                }
            }
        }
        int[] array = new int[result.size()];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = result.get(i).intValue();
        }
        return array;
    }

}
