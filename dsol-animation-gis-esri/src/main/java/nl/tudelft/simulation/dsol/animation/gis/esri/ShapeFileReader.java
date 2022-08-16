package nl.tudelft.simulation.dsol.animation.gis.esri;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.animation.gis.DataSourceInterface;
import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.dsol.animation.gis.SerializableRectangle2D;
import nl.tudelft.simulation.dsol.animation.gis.io.Endianness;
import nl.tudelft.simulation.dsol.animation.gis.io.ObjectEndianInputStream;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;
import nl.tudelft.simulation.language.d2.Shape;

/**
 * This class reads ESRI-shapefiles and returns the shape objects.
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
public class ShapeFileReader implements DataSourceInterface
{
    /** */
    private static final long serialVersionUID = 20201223L;

    /** the URL for the shape file to be read. */
    private URL shpFile = null;

    /** the URL for the shape index file to be read. */
    private URL shxFile = null;

    /** the URL for the dbase-III format file with texts to be read. */
    private URL dbfFile = null;

    /** the type of shape we are working on. */
    private int currentType = GisMapInterface.POLYGON;

    /** our DBF reader. */
    private DbfReader dbfReader;

    /** the NULLSHAPE as defined by ESRI. */
    public static final int NULLSHAPE = 0;

    /** the POINT as defined by ESRI. */
    public static final int POINT = 1;

    /** the POLYLINE as defined by ESRI. */
    public static final int POLYLINE = 3;

    /** the POLYGON as defined by ESRI. */
    public static final int POLYGON = 5;

    /** the MULTIPOINT as defined by ESRI. */
    public static final int MULTIPOINT = 8;

    /** the POINTZ as defined by ESRI. */
    public static final int POINTZ = 11;

    /** the POLYLINEZ as defined by ESRI. */
    public static final int POLYLINEZ = 13;

    /** the POLYGONZ as defined by ESRI. */
    public static final int POLYGONZ = 15;

    /** the MULTIPOINTZ as defined by ESRI. */
    public static final int MULTIPOINTZ = 18;

    /** the POINM as defined by ESRI. */
    public static final int POINTM = 21;

    /** the POLYLINEM as defined by ESRI. */
    public static final int POLYLINEM = 23;

    /** the POLYGONM as defined by ESRI. */
    public static final int POLYGONM = 25;

    /** the MULTIPOINTM as defined by ESRI. */
    public static final int MULTIPOINTM = 28;

    /** the MULTIPATCH as defined by ESRI. */
    public static final int MULTIPATCH = 31;

    /** number of shapes in the current file. */
    private final int numShapes;

    /** an optional transformation of the lat/lon (or other) coordinates. */
    private final CoordinateTransform coordinateTransform;

    /** the features to read by this OpenStreeetMap reader. */
    private final List<FeatureInterface> featuresToRead;

    /**
     * Construct a reader for an ESRI ShapeFile.
     * @param shapeUrl URL; URL may or may not end with their extension.
     * @param coordinateTransform CoordinateTransform; the transformation of (x, y) coordinates to (x', y') coordinates.
     * @param featuresToRead the features to read
     * @throws IOException throws an IOException if the shxFile is not accessible
     */
    public ShapeFileReader(final URL shapeUrl, final CoordinateTransform coordinateTransform,
            final List<FeatureInterface> featuresToRead) throws IOException
    {
        this.coordinateTransform = coordinateTransform;
        this.featuresToRead = featuresToRead;
        String fileName = shapeUrl.toString();
        if (fileName.endsWith(".shp") || fileName.endsWith(".shx") || fileName.endsWith(".dbf"))
        {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        this.shpFile = new URL(fileName + ".shp");
        this.shxFile = new URL(fileName + ".shx");
        this.dbfFile = new URL(fileName + ".dbf");
        try
        {
            URLConnection connection = this.shxFile.openConnection();
            connection.connect();
            this.numShapes = (connection.getContentLength() - 100) / 8;
            this.dbfReader = new DbfReader(this.dbfFile);
        }
        catch (IOException exception)
        {
            throw new IOException("Can't read " + this.shxFile.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<FeatureInterface> getFeatures()
    {
        return this.featuresToRead;
    }

    /** {@inheritDoc} */
    @Override
    public void populateShapes() throws IOException
    {
        Throw.when(this.featuresToRead.size() != 1, IOException.class,
                "Trying to read ESROI shapes, but number of features is not 1");
        List<GisObject> shapes = readAllShapes();
        this.featuresToRead.get(0).getShapes().clear();
        this.featuresToRead.get(0).getShapes().addAll(shapes);
    }

    /**
     * Read a particular shape directly from the shape file, without caching (the cache is stored at the Features).
     * @param index int; the index of the shape to read from the shape file, without using any caching
     * @return GisObject; the shape belonging to the index
     * @throws IOException when there is a problem reading the ESRI files.
     */
    public synchronized GisObject readShape(final int index) throws IOException
    {
        if (index > this.numShapes || index < 0)
        {
            throw new IndexOutOfBoundsException("Index =" + index + ", while number of shapes in layer :" + this.numShapes);
        }

        ObjectEndianInputStream indexInput = new ObjectEndianInputStream(this.shxFile.openStream());
        indexInput.skipBytes(8 * index + 100);
        int offset = 2 * indexInput.readInt();
        indexInput.close();
        ObjectEndianInputStream shapeInput = new ObjectEndianInputStream(this.shpFile.openStream());
        shapeInput.skipBytes(offset);
        Object shape = this.readShape(shapeInput);
        shapeInput.close();
        return new GisObject(shape, this.dbfReader.getRow(index));
    }

    /**
     * Read all shapes directly from the shape file, without caching (the cache is stored at the Features).
     * @return List&lt;GisObject&gt;; the shapes that are directly read from the shape file
     * @throws IOException when there is a problem reading the ESRI files.
     */
    public synchronized List<GisObject> readAllShapes() throws IOException
    {
        ObjectEndianInputStream shapeInput = new ObjectEndianInputStream(this.shpFile.openStream());

        shapeInput.skipBytes(100);
        ArrayList<GisObject> results = new ArrayList<>(this.numShapes);
        String[][] attributes = this.dbfReader.getRows();
        for (int i = 0; i < this.numShapes; i++)
        {
            Object shape = this.readShape(shapeInput);
            if (shape != null) // skip Null Shape type 0
            {
                results.add(new GisObject(shape, attributes[i]));
            }
        }
        shapeInput.close();
        return results;
    }

    /**
     * Read all shapes for a certain extent directly from the shape file, without caching (the cache is stored at the Features).
     * @param extent Bounds2d; the extent for which to read the shapes
     * @return List&lt;GisObject&gt;; the shapes for the given extent that are directly read from the shape file
     * @throws IOException when there is a problem reading the ESRI files.
     */
    public synchronized List<GisObject> readShapes(final Bounds2d extent) throws IOException
    {
        ObjectEndianInputStream shapeInput = new ObjectEndianInputStream(this.shpFile.openStream());

        shapeInput.skipBytes(100);
        ArrayList<GisObject> results = new ArrayList<>();

        String[][] attributes = this.dbfReader.getRows();
        for (int i = 0; i < this.numShapes; i++)
        {
            shapeInput.setEndianness(Endianness.BIG_ENDIAN);
            int shapeNumber = shapeInput.readInt();
            int contentLength = shapeInput.readInt();
            shapeInput.setEndianness(Endianness.LITTLE_ENDIAN);

            // the null type is properly skipped
            int type = shapeInput.readInt();
            if (type != 0 && type != 1 && type != 11 && type != 21)
            {
                double[] min = this.coordinateTransform.doubleTransform(shapeInput.readDouble(), shapeInput.readDouble());
                double[] max = this.coordinateTransform.doubleTransform(shapeInput.readDouble(), shapeInput.readDouble());
                double minX = Math.min(min[0], max[0]);
                double minY = Math.min(min[1], max[1]);
                double width = Math.max(min[0], max[0]) - minX;
                double height = Math.max(min[1], max[1]) - minY;
                SerializableRectangle2D bounds = new SerializableRectangle2D.Double(minX, minY, width, height);
                if (Shape.overlaps(extent.toRectangle2D(), bounds))
                {
                    results.add(
                            new GisObject(this.readShape(shapeInput, shapeNumber, contentLength, type, false), attributes[i]));
                }
                else
                {
                    shapeInput.skipBytes((2 * contentLength) - 36);
                }
            }
            else if (type != 0)
            {
                Point2D temp = (Point2D) this.readShape(shapeInput, shapeNumber, contentLength, type, false);
                if (extent.toRectangle2D().contains(temp))
                {
                    results.add(new GisObject(temp, attributes[i]));
                }
            }
        }
        shapeInput.close();
        return results;
    }

    /**
     * Return the shapes based on a particular value of the attributes.
     * @param attribute String; the value of the attribute
     * @param columnName String; the columnName
     * @return List the resulting ArrayList of <code>nl.tudelft.simulation.dsol.animation.gis.GisObject</code>
     * @throws IOException on file IO or database connection failure
     */
    public synchronized List<GisObject> getShapes(final String attribute, final String columnName) throws IOException
    {
        List<GisObject> result = new ArrayList<>();
        int[] shapeNumbers = this.dbfReader.getRowNumbers(attribute, columnName);
        for (int i = 0; i < shapeNumbers.length; i++)
        {
            result.add(this.readShape(i));
        }
        return result;
    }

    /**
     * Read a shape.
     * @param input ObjectEndianInputStream; the inputStream
     * @return Object; the shape
     * @throws IOException on file IO or database connection failure
     */
    private Object readShape(final ObjectEndianInputStream input) throws IOException
    {
        return readShape(input, -1, -1, -1, true);
    }

    /**
     * @param input ObjectEndianInputStream; the input stream.
     * @param fixedShapeNumber int; the shape number, if -1, read from input
     * @param fixedContentLength int; the length of the content, if -1, read from input
     * @param fixedType int; shape type; if -1, read from input
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the shape
     * @throws IOException on I/O error reading from the shape file
     */
    private Object readShape(final ObjectEndianInputStream input, final int fixedShapeNumber, final int fixedContentLength,
            final int fixedType, final boolean skipBoundingBox) throws IOException
    {
        input.setEndianness(Endianness.BIG_ENDIAN);
        @SuppressWarnings("unused")
        int shapeNumber = fixedShapeNumber == -1 ? input.readInt() : fixedShapeNumber;

        int contentLength = fixedContentLength == -1 ? input.readInt() : fixedContentLength;

        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int type = fixedType == -1 ? input.readInt() : fixedType;

        switch (type)
        {
            case 0:
                return readNullShape(input);
            case 1:
                return readPoint(input);
            case 3:
                return readPolyLine(input, skipBoundingBox);
            case 5:
                return readPolygon(input, skipBoundingBox);
            case 8:
                return readMultiPoint(input, skipBoundingBox);
            case 11:
                return readPointZ(input, contentLength);
            case 13:
                return readPolyLineZ(input, contentLength, skipBoundingBox);
            case 15:
                return readPolygonZ(input, contentLength, skipBoundingBox);
            case 18:
                return readMultiPointZ(input, contentLength, skipBoundingBox);
            case 21:
                return readPointM(input, contentLength);
            case 23:
                return readPolyLineM(input, contentLength, skipBoundingBox);
            case 25:
                return readPolygonM(input, contentLength, skipBoundingBox);
            case 28:
                return readMultiPointM(input, contentLength, skipBoundingBox);
            case 31:
                return readMultiPatch(input, contentLength, skipBoundingBox);
            default:
                throw new IOException("Unknown shape type or shape type not supported");
        }
    }

    /**
     * Read a Null Shape.
     * <p>
     * A shape type of 0 indicates a null shape, with no geometric data for the shape. Each feature type (point, line, polygon,
     * etc.) supports nulls¾it is valid to have points and null points in the same shapefile. Often null shapes are place
     * holders; they are used during shapefile creation and are populated with geometric data soon after they are created.
     * </p>
     * @param input ObjectEndianInputStream; the inputStream
     * @return null to indicate this is not a valid shape
     */
    private synchronized Object readNullShape(final ObjectEndianInputStream input)
    {
        return null;
    }

    /**
     * Read a Point.
     * <p>
     * A point consists of a pair of double-precision coordinates in the order X,Y.
     * </p>
     * 
     * <pre>
     *   All byte orders are Little Endian.
     *   Integer ShapeType  // byte  0; Value 1 for Point
     *   Point
     *   {
     *     Double X         // byte  4; X coordinate (8 bytes)
     *     Double Y         // byte 12; Y coordinate (8 bytes)
     *   }
     * </pre>
     * 
     * @param input ObjectEndianInputStream; the inputStream
     * @return Point2D.Double; the point
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPoint(final ObjectEndianInputStream input) throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        double[] point = this.coordinateTransform.doubleTransform(input.readDouble(), input.readDouble());
        return new Point2D.Double(point[0], point[1]);
    }

    /**
     * Read a PolyLine.
     * <p>
     * A PolyLine is an ordered set of vertices that consists of one or more parts. A part is a connected sequence of two or
     * more points. Parts may or may not be connected to one another. Parts may or may not intersect one another. Because this
     * specification does not forbid consecutive points with identical coordinates, shapefile readers must handle such cases. On
     * the other hand, the degenerate, zero length parts that might result are not allowed.
     * </p>
     * 
     * <pre>
     *   All byte orders are Little Endian.
     *   Integer ShapeType         // byte  0; Value 8 for PolyLine
     *   PolyLine
     *   {
     *     Double[4] Box           // byte  4; Bounding Box, consisting of {Xmin, Ymin, Xmax, Ymax} (32 bytes)
     *     Integer NumParts        // byte 36; Number of Parts in the PolyLine (4 bytes)
     *     Integer NumPoints       // byte 40; Total Number of Points, summed for all parts (4 bytes)
     *     Integer[NumParts] Parts // byte 44; Index array to first point in in the points array (4 * NumParts)
     *     Point[NumPoints] Points // Points for all parts; no delimiter between points of different parts (16 * NumPoints)
     *   }
     *   
     *   where a point consists of a pair of double-precision coordinates in the order X,Y.
     *   Point
     *   {
     *     Double X                // X coordinate (8 bytes)
     *     Double Y                // Y coordinate (8 bytes)
     *   }
     * </pre>
     * 
     * @param input ObjectEndianInputStream; the inputStream
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the shape as a SerializablePath
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolyLine(final ObjectEndianInputStream input, final boolean skipBoundingBox)
            throws IOException
    {
        this.currentType = GisMapInterface.LINE;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int[] partBegin = new int[numParts + 1];

        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();

        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
            }
        }
        return result;
    }

    /**
     * reads a Polygon.
     * @param input ObjectEndianInputStream; the inputStream
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolygon(final ObjectEndianInputStream input, final boolean skipBoundingBox)
            throws IOException
    {
        this.currentType = GisMapInterface.POLYGON;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int[] partBegin = new int[numParts + 1];

        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();
        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
            }
        }

        return result;
    }

    /**
     * reads a readMultiPoint.
     * @param input ObjectEndianInputStream; the inputStream
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readMultiPoint(final ObjectEndianInputStream input, final boolean skipBoundingBox)
            throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        Point2D[] result = new Point2D.Double[input.readInt()];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (Point2D) readPoint(input);
        }

        return result;
    }

    /**
     * reads a readPointZ.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPointZ(final ObjectEndianInputStream input, final int contentLength) throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        Object point = this.readPoint(input);
        input.skipBytes((contentLength * 2) - 20);

        return point;
    }

    /**
     * reads a readPolyLineZ.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolyLineZ(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.LINE;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int byteCounter = 44;
        int[] partBegin = new int[numParts + 1];

        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();
            byteCounter += 4;
        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            byteCounter += 16;
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
                byteCounter += 16;
            }
        }
        input.skipBytes((contentLength * 2) - byteCounter);

        return result;
    }

    /**
     * reads a readPolygonZ.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolygonZ(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.POLYGON;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int byteCounter = 44;
        int[] partBegin = new int[numParts + 1];
        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();
            byteCounter += 4;
        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            byteCounter += 16;
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
                byteCounter += 16;
            }
        }
        input.skipBytes((contentLength * 2) - byteCounter);

        return result;
    }

    /**
     * reads a readMultiPointZ.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readMultiPointZ(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        Point2D[] result = new Point2D.Double[input.readInt()];
        int byteCounter = 40;
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (Point2D) readPoint(input);
            byteCounter += 16;
        }
        input.skipBytes((contentLength * 2) - byteCounter);

        return result;
    }

    /**
     * reads a readPointM.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPointM(final ObjectEndianInputStream input, final int contentLength) throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        Object point = this.readPoint(input);
        input.skipBytes((contentLength * 2) - 20);
        return point;
    }

    /**
     * reads a readPolyLineM.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolyLineM(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.LINE;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int byteCounter = 44;
        int[] partBegin = new int[numParts + 1];
        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();
            byteCounter += 4;
        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            byteCounter += 16;
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
                byteCounter += 16;
            }
        }
        input.skipBytes((contentLength * 2) - byteCounter);
        return result;
    }

    /**
     * reads a readPolyLineM.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readPolygonM(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.POLYGON;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        int numParts = input.readInt();
        int numPoints = input.readInt();
        int byteCounter = 44;
        int[] partBegin = new int[numParts + 1];

        for (int i = 0; i < partBegin.length - 1; i++)
        {
            partBegin[i] = input.readInt();
            byteCounter += 4;
        }
        partBegin[partBegin.length - 1] = numPoints;

        SerializablePath result = new SerializablePath(Path2D.WIND_NON_ZERO, numPoints);
        for (int i = 0; i < numParts; i++)
        {
            float[] mf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
            result.moveTo(mf[0], mf[1]);
            byteCounter += 16;
            for (int ii = (partBegin[i] + 1); ii < partBegin[i + 1]; ii++)
            {
                float[] lf = this.coordinateTransform.floatTransform(input.readDouble(), input.readDouble());
                result.lineTo(lf[0], lf[1]);
                byteCounter += 16;
            }
        }
        input.skipBytes((contentLength * 2) - byteCounter);
        return result;
    }

    /**
     * reads a readMultiPointM.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readMultiPointM(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        this.currentType = GisMapInterface.POINT;
        if (skipBoundingBox)
        {
            input.skipBytes(32);
        }
        input.setEndianness(Endianness.LITTLE_ENDIAN);
        Point2D[] result = new Point2D.Double[input.readInt()];
        int byteCounter = 40;
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (Point2D) readPoint(input);
            byteCounter += 16;
        }
        input.skipBytes((contentLength * 2) - byteCounter);

        return result;
    }

    /**
     * reads a readMultiPatch.
     * @param input ObjectEndianInputStream; the inputStream
     * @param contentLength int; the contentLength
     * @param skipBoundingBox boolean; whether to skip the bytes of the bounding box because they have not yet been read
     * @return the java2D PointShape
     * @throws IOException on file IO or database connection failure
     */
    private synchronized Object readMultiPatch(final ObjectEndianInputStream input, final int contentLength,
            final boolean skipBoundingBox) throws IOException
    {
        if (input != null || contentLength != 0 || skipBoundingBox)
        {
            throw new IOException(
                    "Please inform <a href=\"mailto:support@javel.nl\">support@javel.nl</a> that you need MultiPatch support");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDynamic()
    {
        return false; // OSM data is static
    }

    /**
     * Return the key names of the attribute data. The attribute values are stored in the GisObject together with the shape.
     * @return String[]; the key names of the attribute data
     */
    public String[] getAttributeKeyNames()
    {
        return this.dbfReader.getColumnNames();
    }

    /** {@inheritDoc} */
    @Override
    public URL getURL()
    {
        return this.shpFile;
    }

}
