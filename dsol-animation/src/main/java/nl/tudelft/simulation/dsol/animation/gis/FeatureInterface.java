package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;

/**
 * Feature contains an element of a layer, defined by a key value combination, with its own colors.<br>
 * TODO: minimum scale and maximum scale to draw features has to be added again, but first, scale needs to be defined properly.
 * <p>
 * Copyright (c) 2021-2021 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface FeatureInterface extends Serializable
{
    /**
     * Return the key that defines a feature in a layer, can be "*" if no features are defined.
     * @return String; the key that defines a feature in a layer, can be "*" if no features are defined
     */
    String getKey();

    /**
     * Set the key that defines a feature in a layer, can be "*" if no features are defined.
     * @param key String; the key that defines a feature in a layer, can be "*" if no features are defined.
     */
    void setKey(String key);

    /**
     * Return the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source
     * that have the correct key have to be drawn.
     * @return String; the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the
     *         data source that have the correct key have to be drawn.
     */
    String getValue();
//
//    /**
//     * Return the data source, which contains the location of the GIS datasource.
//     * @return DataSourceInterface the data source, contains the location of the GIS datasource
//     */
//    DataSourceInterface getDataSource();

    /**
     * Return whether the data has been initialized for this feature.
     * @return boolean; whether the data has been initialized for this feature
     */
    boolean isInitialized();
    
    /**
     * Set whether the data has been initialized for this feature.
     * @param initialized boolean; whether the data has been initialized for this feature
     */
    void setInitialized(boolean initialized);
    
    /**
     * Return the number of shapes for this feature at this moment.
     * @return int; the number of shapes in the data source
     */
    int getNumShapes();

    /**
     * Return a GisObject.
     * @param index int; the number of the shape to be returned for this feature
     * @return GisObject returns a <code>nl.tudelft.simulation.dsol.animation.gis.GisObject</code>
     * @throws IndexOutOfBoundsException whenever index &gt; numShapes or index &lt; 0
     */
    GisObject getShape(int index) throws IndexOutOfBoundsException;

    /**
     * Return all the shapes of the particular data source for this feature.
     * @return List the resulting List of <code>nl.tudelft.simulation.dsol.animation.gis.GisObject</code>
     */
    List<GisObject> getShapes();

    /**
     * Return the shapes of the particular data source for this feature, bound to a particular extent.
     * @param rectangle Bounds2d; the extent of the box (in geo-coordinates)
     * @return List the resulting List of <code>nl.tudelft.simulation.dsol.animation.gis.GisObject</code>
     */
     List<GisObject> getShapes(Bounds2d rectangle);

//    /**
//     * Set the data source, which contains the location of the GIS data.
//     * @param dataSource DataSourceInterface; the data source, contains the location of the GIS data
//     */
//    void setDataSource(DataSourceInterface dataSource);

    /**
     * Set the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source
     * that have the correct key have to be drawn.
     * @param value String; the value belonging to the key that defines the feature in a layer, can be "*" if all elements in
     *            the data source that have the correct key have to be drawn.
     */
    void setValue(String value);

    /**
     * Return the fill color for the layer.
     * @return Color; the rgb(a) fill color for the layer
     */
    Color getFillColor();

    /**
     * Set the fill color for the layer.
     * @param fillColor Color; the rgb(a) fill color for the layer
     */
    void setFillColor(Color fillColor);

    /**
     * Return the outline (line) color for the layer.
     * @return Color; the rgb(a) outline (line) color for the layer
     */
    Color getOutlineColor();

    /**
     * Set the outline (line) color for the layer.
     * @param outlineColor Color; the rgb(a) outline (line) color for the layer
     */
    void setOutlineColor(Color outlineColor);

}
