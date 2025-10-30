package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

import org.djutils.draw.bounds.Bounds2d;

/**
 * Feature contains an element of a layer, defined by a key value combination, with its own colors.<br>
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public interface FeatureInterface
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// GENERIC METHODS FOR FEATURE /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return the key that defines a feature in a layer, can be "*" if no features are defined.
     * @return the key that defines a feature in a layer, can be "*" if no features are defined
     */
    String getKey();

    /**
     * Set the key that defines a feature in a layer, can be "*" if no features are defined.
     * @param key the key that defines a feature in a layer, can be "*" if no features are defined.
     */
    void setKey(String key);

    /**
     * Return the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source
     * that have the correct key have to be drawn.
     * @return the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source
     *         that have the correct key have to be drawn.
     */
    String getValue();

    /**
     * Set the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source
     * that have the correct key have to be drawn.
     * @param value the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data
     *            source that have the correct key have to be drawn.
     */
    void setValue(String value);

    /**
     * Return the z-index of this feature. The z-index indicates the drawing order, from low to high. Any system can be used to
     * indicate drawing order.
     * @return the z-index of this feature
     */
    double getZIndex();

    /**
     * Set the z-index of this feature. The z-index indicates the drawing order, from low to high. Any system can be used to
     * indicate drawing order.
     * @param zIndex the z-index of this feature
     */
    void setZIndex(double zIndex);

    /**
     * Return the layer to which this feature belongs.
     * @return the layer to which this feature belongs
     */
    LayerInterface getLayer();

    /**
     * Return whether the data has been initialized for this feature.
     * @return whether the data has been initialized for this feature
     */
    boolean isInitialized();

    /**
     * Set whether the data has been initialized for this feature.
     * @param initialized whether the data has been initialized for this feature
     */
    void setInitialized(boolean initialized);

    /**
     * Return whether the shapes for this feature need to be transformed or not.
     * @return whether the shapes for this feature need to be transformed
     */
    default boolean isTransform()
    {
        return getLayer().isTransform();
    }

    /**
     * Return whether the shapes for this feature need to be displayed or not.
     * @return whether the shapes for this feature need to be displayed
     */
    default boolean isDisplay()
    {
        return getLayer().isDisplay();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// METHODS FOR SHAPES (Path2D) /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * claar the collection of shapes for this feature.
     */
    void clearShapes();

    /**
     * Add a shape for this feature.
     * @param shape the shape to add
     */
    void addShape(Path2D shape);

    /**
     * Add a shape and array of attributes for this feature.
     * @param shape the shape to add
     * @param attributes the attributes to add
     */
    void addShape(Path2D shape, String[] attributes);

    /**
     * Return the number of shapes for this feature.
     * @return the number of shapes for this feature
     */
    int getNumShapes();

    /**
     * Return an iterator for the complete list of shapes for this feature.
     * @return an iterator for the complete list of shapes for this feature
     */
    Iterator<Path2D> shapeIterator();

    /**
     * Return an iterator for the list of shapes for this feature that might have an overlap with the given rectangle.
     * @param rectangle the bounds in world coordinates for which to check the shapes
     * @return an iterator for the list of shapes for this feature that might have an overlap with the given rectangle
     */
    Iterator<Path2D> shapeIterator(Bounds2d rectangle);

    /**
     * Return the shape with the given index number for this feature.
     * @param index the index number
     * @return the shape with the given index number for this feature
     * @throws IndexOutOfBoundsException when index not in in the valid range
     */
    Path2D getShape(int index) throws IndexOutOfBoundsException;

    /**
     * Return the shape attribute with the given column number and index number for this feature.
     * @param index the index number
     * @param column the column number
     * @return the column attribute with the given index number for this feature
     * @throws IndexOutOfBoundsException when index or column are not in in their valid range
     */
    String getShapeAttribute(int index, int column) throws IndexOutOfBoundsException;

    /**
     * Return an array with all shape attributes of the shape with the given index number for this feature.
     * @param index the index number
     * @return the array of attributes for the shape with the given index number for this feature
     * @throws IndexOutOfBoundsException when index is not in in the valid range
     */
    String[] getShapeAttributes(int index) throws IndexOutOfBoundsException;

    /**
     * Return the style to apply for all shapes in this feature.
     * @return the style to apply for the shapes in this feature
     */
    Style getShapeStyle();

    /**
     * Set the style to apply for all shapes in this feature.
     * @param shapeStyle the style to apply for the shapes in this feature
     */
    void setShapeStyle(Style shapeStyle);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// METHODS FOR POINTS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return the number of point for this feature.
     * @return the number of points for this feature
     */
    int getNumPoints();

    /**
     * Add a point for this feature.
     * @param point the point to add
     */
    void addPoint(Point2D point);

    /**
     * Add a shape and array of attributes for this feature.
     * @param point the point to add
     * @param attributes the attributes to add
     */
    void addPoint(Point2D point, String[] attributes);

    /**
     * Return an iterator for the complete list of points for this feature.
     * @return an iterator for the complete list of points for this feature
     */
    Iterator<Point2D> pointIterator();

    /**
     * Return an iterator for the list of points for this feature that might have an overlap with the given rectangle.
     * @param rectangle the bounds in world coordinates for which to check the points
     * @return an iterator for the list of points for this feature that might have an overlap with the given rectangle
     */
    Iterator<Point2D> pointIterator(Bounds2d rectangle);

    /**
     * Return the point with the given index number for this feature.
     * @param index the index number
     * @return the point with the given index number for this feature
     * @throws IndexOutOfBoundsException when index not in in the valid range
     */
    Point2D getPoint(int index) throws IndexOutOfBoundsException;

    /**
     * Return the point attribute with the given column number and index number for this feature.
     * @param index the index number
     * @param column the column number
     * @return the column attribute with the given index number for this feature
     * @throws IndexOutOfBoundsException when index or column are not in in their valid range
     */
    String getPointAttribute(int index, int column) throws IndexOutOfBoundsException;

    /**
     * Return an array with all point attributes of the point with the given index number for this feature.
     * @param index the index number
     * @return the array of attributes for the point with the given index number for this feature
     * @throws IndexOutOfBoundsException when index is not in in the valid range
     */
    String[] getPointAttributes(int index) throws IndexOutOfBoundsException;

    /**
     * Return the point marker to display point elements for this feature.
     * @return the point marker to display point elements for this feature
     */
    MarkerInterface getPointMarker();

    /**
     * Set the point marker to display point elements for this feature.
     * @param pointMarker the point marker to display point elements for this feature
     */
    void setPointMarker(MarkerInterface pointMarker);

    /**
     * Return the style to apply for all point markers in this feature.
     * @return the style to apply for the point markers in this feature
     */
    Style getMarkerStyle();

    /**
     * Set the style to apply for all point markers in this feature.
     * @param markerStyle the style to apply for the point markers in this feature
     */
    void setMarkerStyle(Style markerStyle);

}
