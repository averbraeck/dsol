package nl.tudelft.simulation.dsol.animation.gis.map;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.djutils.draw.bounds.Bounds2d;

import com.carrotsearch.hppc.FloatArrayList;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.MarkerInterface;
import nl.tudelft.simulation.dsol.animation.gis.Style;

/**
 * Feature contains an element of a layer, defined by a key value combination, with its own colors.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Feature implements FeatureInterface
{
    /** */
    private static final long serialVersionUID = 20210201L;

    /** the layer to which this feature belongs. */
    private final LayerInterface layer;

    /** the key that defines a feature in a layer, can be "*" if no features are defined. */
    private String key = "*";

    /**
     * the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source that
     * have the correct key have to be drawn.
     */
    private String value = "*";

    /** the list of shapes that have been retrieved for this feature. */
    private List<Path2D> shapes = new ArrayList<>();

    /** the shape style. */
    private Style shapeStyle;

    /** The names of the shape attributes per column. */
    private List<String> shapeAttributeNames;

    /** the shape attributes. */
    private List<String[]> shapeAttributes = null;

    /** the list of x-coordinates of the points that have been retrieved for this feature. */
    private FloatArrayList xList = new FloatArrayList();

    /** the list of y-coordinates of the points that have been retrieved for this feature. */
    private FloatArrayList yList = new FloatArrayList();

    /** the marker to use to draw points. */
    private MarkerInterface pointMarker;

    /** the style for the point markers. */
    private Style markerStyle;

    /** The names of the point attributes per column. */
    private List<String> pointAttributeNames;

    /** the point attributes. */
    private List<String[]> pointAttributes = null;

    /** the z-index of this feature. The z-index indicates the drawing order, from low to high. */
    private double zIndex = 0.0;

    /** whether the shapes have been read or not. */
    private boolean initialized = false;

    /**
     * Create a feature as part of a layer.
     * @param layer the layer to which this feature belongs
     */
    public Feature(final LayerInterface layer)
    {
        super();
        this.layer = layer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// METHODS FOR SHAPES (Path2D) /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void clearShapes()
    {
        this.shapes.clear();
    }
    
    @Override
    public void addShape(final Path2D shape)
    {
        this.shapes.add(shape);
    }
    
    @Override
    public void addShape(final Path2D shape, final String[] attributes)
    {
        this.shapes.add(shape);
        this.shapeAttributes.add(attributes);
    }

    @Override
    public int getNumShapes()
    {
        return this.shapes.size();
    }

    @Override
    public Path2D getShape(final int index) throws IndexOutOfBoundsException
    {
        return this.shapes.get(index);
    }

    @Override
    public Iterator<Path2D> shapeIterator()
    {
        return this.shapes.iterator();
    }

    @Override
    public Iterator<Path2D> shapeIterator(final Bounds2d rectangle)
    {
        final Rectangle2D view2D = rectangle.toRectangle2D();
        return this.shapes.stream().filter((s) -> view2D.intersects(s.getBounds2D())).iterator();
    }

    @Override
    public Style getShapeStyle()
    {
        return this.shapeStyle;
    }

    @Override
    public void setShapeStyle(final Style shapeStyle)
    {
        this.shapeStyle = shapeStyle;
    }

    @Override
    public String getShapeAttribute(final int index, final int column) throws IndexOutOfBoundsException
    {
        return this.shapeAttributes.get(index)[column];
    }

    @Override
    public String[] getShapeAttributes(final int index) throws IndexOutOfBoundsException
    {
        return this.shapeAttributes.get(index);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// METHODS FOR POINTS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getNumPoints()
    {
        return this.xList.size();
    }

    @Override
    public Style getMarkerStyle()
    {
        return this.markerStyle;
    }

    @Override
    public void setMarkerStyle(final Style marker)
    {
        this.markerStyle = marker;
    }

    @Override
    public MarkerInterface getPointMarker()
    {
        return this.pointMarker;
    }

    @Override
    public void setPointMarker(final MarkerInterface pointMarker)
    {
        this.pointMarker = pointMarker;
    }

    @Override
    public Iterator<Point2D> pointIterator()
    {
        return IntStream.range(0, getNumPoints() - 1)
                .mapToObj((i) -> (Point2D) new Point2D.Float(this.xList.get(i), this.yList.get(i))).iterator();
    }

    @Override
    public Iterator<Point2D> pointIterator(final Bounds2d rectangle)
    {
        final Rectangle2D view2D = rectangle.toRectangle2D();
        return IntStream.range(0, getNumPoints() - 1)
                .mapToObj((i) -> (Point2D) new Point2D.Float(this.xList.get(i), this.yList.get(i)))
                .filter((p) -> view2D.contains(p)).iterator();
    }

    @Override
    public Point2D getPoint(final int index) throws IndexOutOfBoundsException
    {
        return new Point2D.Float(this.xList.get(index), this.yList.get(index));
    }

    @Override
    public String getPointAttribute(final int index, final int column) throws IndexOutOfBoundsException
    {
        return this.pointAttributes.get(index)[column];
    }

    @Override
    public String[] getPointAttributes(final int index) throws IndexOutOfBoundsException
    {
        return this.pointAttributes.get(index);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// GENERIC METHODS FOR FEATURE /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public final String getKey()
    {
        return this.key;
    }

    @Override
    public final void setKey(final String key)
    {
        this.key = key;
    }

    @Override
    public final String getValue()
    {
        return this.value;
    }

    @Override
    public LayerInterface getLayer()
    {
        return this.layer;
    }

    @Override
    public boolean isInitialized()
    {
        return this.initialized;
    }

    @Override
    public void setInitialized(final boolean initialized)
    {
        this.initialized = initialized;
    }

    @Override
    public final void setValue(final String value)
    {
        this.value = value;
    }

    @Override
    public double getZIndex()
    {
        return this.zIndex;
    }

    @Override
    public void setZIndex(final double zIndex)
    {
        this.zIndex = zIndex;
    }

    @Override
    public String toString()
    {
        return "Feature [key=" + this.key + ", value=" + this.value + "]";
    }

}
