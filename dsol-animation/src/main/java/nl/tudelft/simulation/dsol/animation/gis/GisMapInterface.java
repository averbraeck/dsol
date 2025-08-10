package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.immutablecollections.ImmutableMap;

/**
 * This interface defines the map.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public interface GisMapInterface extends Serializable
{
    /** TEXT. */
    byte TEXT = 0;

    /** ANGLEDEG. */
    byte ANGLEDEG = 1;

    /** ANGLERAD. */
    byte ANGLERAD = 2;

    /** IMAGE. */
    byte IMAGE = 3;

    /** AIRPHOTO. */
    byte AIRPHOTO = 4;

    /** POLYGON. */
    byte POLYGON = 0;

    /** POINT. */
    byte POINT = 1;

    /** LINE. */
    byte LINE = 2;

    /** FEET_TO_METER. */
    double FEET_TO_METER = 0.3048;

    /** INCH_TO_METER. */
    double INCH_TO_METER = 0.0254;

    /** KILOMETER_TO_METER. */
    double KILOMETER_TO_METER = 1000;

    /** MILES_TO_METER. */
    double MILES_TO_METER = 1609.34;

    /** DD_TO_METER (DD stands for decimal degrees). */
    double DD_TO_METER = 111119;

    /** CENTIMETER_PER_INCH. */
    double CENTIMETER_PER_INCH = 2.54;

    /**
     * Draw the map on a graphics canvas.
     * @param graphics the graphics canvas
     * @return Graphics2D
     * @throws DsolGisException on drawing failure
     * @throws RemoteException on network failure
     */
    Graphics2D drawMap(Graphics2D graphics) throws DsolGisException, RemoteException;

    /**
     * Return the extent of the drawn map as a (dx, dy) bounds (world coordinates).
     * @return the extent of the drawn map as a (dx, dy) bounds
     */
    Bounds2d getExtent();

    /**
     * Return the image information of the map (using screen coordinates).
     * @return the image information of the map (using screen coordinates)
     */
    MapImageInterface getImage();

    /**
     * Return the map from layer name to layer.
     * @return the map from layer name to layer
     */
    ImmutableMap<String, LayerInterface> getLayerMap();

    /**
     * Return a list of all layers, sorted by the name of the layer.
     * @return a list of all layers, sorted by the name of the layer
     */
    ImmutableList<LayerInterface> getAllLayers();

    /**
     * Return a list of all visible layers, sorted by the name of the layer.
     * @return a list of all visible layers, sorted by the name of the layer
     */
    ImmutableList<LayerInterface> getVisibleLayers();

    /**
     * Return whether the map has not been changed, and reset the same parameter to true.
     * @return whether the map has not been changed, and reset the same parameter to true
     */
    boolean isSame();

    /**
     * Return the name of the map.
     * @return the name of the map
     */
    String getName();

    /**
     * Return the x-scale of the map, which is the horizontal extent (dx) divided by the number of horizontal pixels of the
     * viewport. In other words, the x-scale is the number of horizontal units per pixel. As an example for WGS84, suppose 0.1
     * degree at the equator is drawn on 1080 pixels, then the x-scale is 1/10800. If one degree at the equator is drawn (map
     * zoomed OUT by a factor 10), the value is 1/1080. A larger scale is equivalent to a higher zoom factor. A larger scale
     * means things are 'bigger' on the map.
     * @return the x-scale of the map as the number of horizontal units per pixel
     */
    double getScaleX();

    /**
     * Return the y-scale of the map, which is the vertical extent (dy) divided by the number of vertical pixels of the
     * viewport. In other words, the y-scale is the number of vertical units per pixel. As an example for WGS84, suppose 0.1
     * degree at the equator is drawn on 1080 pixels, then the y-scale is 1/10800. If one degree at the equator is drawn (map
     * zoomed OUT by a factor 10), the value is 1/1080. A larger scale is equivalent to a higher zoom factor. A larger scale
     * means things are 'bigger' on the map.
     * @return the y-scale of the map as the number of vertical units per pixel
     */
    double getScaleY();

    /**
     * Return the number of meters of one horizontal unit.
     * @return the number of meters of one horizontal unit
     */
    double getMetersPerUnitX();

    /**
     * Return the number of meters of one vertical unit.
     * @return the number of meters of one vertical unit
     */
    double getMetersPerUnitY();

    /**
     * Return the number of meters that one horizontal pixel represents on the map.
     * @return the number of meters that one horizontal pixel represents on the map
     */
    default double getMetersPerPixelX()
    {
        return getScaleX() * getMetersPerUnitX();
    }

    /**
     * Return the number of meters that one vertical pixel represents on the map.
     * @return the number of meters that one vertical pixel represents on the map
     */
    default double getMetersPerPixelY()
    {
        return getScaleY() * getMetersPerUnitY();
    }

    /**
     * Getter for property units.
     * @return MapUnits the value of property units.
     */
    MapUnits getMapUnits();

    /**
     * Setter for property extent.
     * @param extent New value of the map extent.
     */
    void setExtent(Bounds2d extent);

    /**
     * Setter for the map image, which acts as the basic 'canvas' for the drawing process. The image has a background color, but
     * could also have a background picture or watermark.
     * @param image New value of the map image, which acts as the basic 'canvas' for the drawing process.
     */
    void setImage(MapImageInterface image);

    /**
     * Setter for property layers.
     * @param layers New value of property layers.
     */
    void setLayers(List<LayerInterface> layers);

    /**
     * Setter for property layers.
     * @param index Index value of layer
     * @param layer New value of property layers.
     */
    void setLayer(int index, LayerInterface layer);

    /**
     * Setter for property layers.
     * @param layer New value of property layers.
     */
    void addLayer(LayerInterface layer);

    /**
     * Hide a layer.
     * @param layer the layer to hide
     */
    void hideLayer(LayerInterface layer);

    /**
     * Show a layer.
     * @param layer the layer to show
     */
    void showLayer(LayerInterface layer);

    /**
     * Hide a layer.
     * @param layerName the name of the layer to hide
     */
    void hideLayer(String layerName);

    /**
     * Show a layer.
     * @param layerName the name of the layer to show
     */
    void showLayer(String layerName);

    /**
     * Setter for property name.
     * @param name new value of property name.
     */
    void setName(String name);

    /**
     * Setter for property units.
     * @param units new value of property units.
     */
    void setUnits(MapUnits units);

    /**
     * zooms the map with a particular factor.
     * @param zoomFactor (0=1)
     */
    void zoom(double zoomFactor);

    /**
     * zooms the map based on a given position in the image.
     * @param pixelPosition the position in the image
     * @param zoomFactor the zoomFactor (0=1)
     */
    void zoomPoint(Point2D pixelPosition, double zoomFactor);

    /**
     * zooms the map based on a given rectangle.
     * @param rectangle a rectangle in the map (image units)
     */
    void zoomRectangle(SerializableRectangle2d rectangle);

    /**
     * return whether background is drawn or not.
     * @return drawBackground
     */
    boolean isDrawBackground();

    /**
     * set whether background is drawn or not.
     * @param drawBackground set drawBackground
     */
    void setDrawBackground(boolean drawBackground);

}
