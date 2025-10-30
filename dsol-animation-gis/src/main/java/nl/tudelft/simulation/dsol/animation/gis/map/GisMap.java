package nl.tudelft.simulation.dsol.animation.gis.map;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableHashMap;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.immutablecollections.ImmutableMap;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.gis.DsolGisException;
import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapImageInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapUnits;

/**
 * Provides the implementation of a Map.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * <p>
 * The dsol-animation-gis project is based on the gisbeans project that has been part of DSOL since 2002, originally by Peter
 * Jacobs and Paul Jacobs.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class GisMap implements GisMapInterface
{
    /** the extent of the map. */
    private Bounds2d extent;

    /** the complete map of layer names to layers. */
    private Map<String, LayerInterface> layerMap = new LinkedHashMap<>();

    /** the complete list of layer names of the map in the order they are displayed. */
    private List<String> layerNames = new ArrayList<>();

    /** the set of visible layers of the map. */
    private Set<LayerInterface> visibleLayers = new LinkedHashSet<>();

    /** The z-sorted map of all features. */
    private SortedMap<Double, List<FeatureInterface>> sortedFeatureMap = new TreeMap<>();

    /** the set of visible features to draw. */
    private Set<FeatureInterface> visibleFeatures = new LinkedHashSet<>();

    /** same set to false after layer change. */
    private boolean same = false;

    /** the mapfileImage. */
    private MapImageInterface image = new MapImage();

    /** the name of the mapFile. */
    private String name;

    /** the map units. */
    private MapUnits mapUnits = MapUnits.METERS;

    /** draw the background? */
    private boolean drawBackground = true;

    /** the mapping of one map unit to the equivalent number of meters in the x-direction. */
    private final double metersPerUnitX;

    /** the mapping of one map unit to the equivalent number of meters in the y-direction. */
    private final double metersPerUnitY;

    /**
     * Construct a new Map, defining the size of a map unit in meters. The size of a map unit can then be used to draw scalable
     * lines with a certain width, and to make certain features disappear if the meter-to-pixel ratio is passing a certain
     * threshold. In WGS84 maps, typically the latitude is used, since it does not vary dependent on the location.
     * @param metersPerUnitX the mapping of one map unit to the equivalent number of meters in the x-direction
     * @param metersPerUnitY the mapping of one map unit to the equivalent number of meters in the y-direction
     */
    public GisMap(final double metersPerUnitX, final double metersPerUnitY)
    {
        this.metersPerUnitX = metersPerUnitX;
        this.metersPerUnitY = metersPerUnitY;
    }

    /**
     * Construct a new Map, with WGS84 as the scale definition, with values for the equator. One degree of latitude is
     * approximately 111,120 meters, while one degree of longitude varies from approximately 111,120 meters at the equator to 0
     * meters at the poles.
     */
    public GisMap()
    {
        this(111120.0, 111120.0);
    }

    @Override
    public void addLayer(final LayerInterface layer)
    {
        this.layerMap.put(layer.getName(), layer);
        this.layerNames.add(layer.getName());
        this.visibleLayers.add(layer);
        for (var feature : layer.getFeatures())
        {
            var featureList = this.sortedFeatureMap.get(feature.getZIndex());
            if (featureList == null)
            {
                featureList = new ArrayList<>();
                this.sortedFeatureMap.put(feature.getZIndex(), featureList);
            }
            featureList.add(feature);
            this.visibleFeatures.add(feature);
        }
        this.same = false;
    }

    @Override
    public void setLayers(final List<LayerInterface> layers)
    {
        this.layerMap.clear();
        this.layerNames.clear();
        this.visibleLayers.clear();
        this.sortedFeatureMap.clear();
        this.visibleFeatures.clear();
        for (var layer : layers)
            addLayer(layer);
    }

    @Override
    public void setLayer(final int index, final LayerInterface layer)
    {
        this.layerMap.put(layer.getName(), layer);
        this.layerNames.set(index, layer.getName());
        this.visibleLayers.add(layer);
        for (var feature : layer.getFeatures())
        {
            var featureList = this.sortedFeatureMap.get(feature.getZIndex());
            if (featureList == null)
            {
                featureList = new ArrayList<>();
                this.sortedFeatureMap.put(feature.getZIndex(), featureList);
            }
            featureList.add(feature);
            this.visibleFeatures.add(feature);
        }
        this.same = false;
    }

    @Override
    public void hideLayer(final LayerInterface layer)
    {
        this.visibleLayers.remove(layer);
        for (var feature : layer.getFeatures())
        {
            this.visibleFeatures.remove(feature);
        }
        this.same = false;
    }

    @Override
    public void hideLayer(final String layerName)
    {
        if (this.layerMap.keySet().contains(layerName))
        {
            hideLayer(this.layerMap.get(layerName));
        }
        this.same = false;
    }

    @Override
    public void showLayer(final LayerInterface layer)
    {
        this.visibleLayers.add(layer);
        for (var feature : layer.getFeatures())
        {
            this.visibleFeatures.add(feature);
        }
        this.same = false;
    }

    @Override
    public void showLayer(final String layerName)
    {
        if (this.layerMap.keySet().contains(layerName))
        {
            var layer = this.layerMap.get(layerName);
            showLayer(layer);
        }
        this.same = false;
    }

    @Override
    public boolean isSame()
    {
        boolean ret = this.same;
        this.same = true;
        return ret;
    }

    @Override
    @SuppressWarnings("checkstyle:methodlength")
    public Graphics2D drawMap(final Graphics2D graphics) throws DsolGisException
    {
        if (this.drawBackground)
        {
            // fill the background.
            graphics.setColor(getImage().getBackgroundColor());
            graphics.fillRect(0, 0, (int) getImage().getSize().getWidth(), (int) getImage().getSize().getHeight());
        }

        // compute the transform of the map
        AffineTransform transform = new AffineTransform();
        transform.scale(getImage().getSize().getWidth() / this.extent.getDeltaX(),
                -getImage().getSize().getHeight() / this.extent.getDeltaY());
        transform.translate(-this.extent.getMinX(), -this.extent.getMinY() - this.extent.getDeltaY());
        AffineTransform antiTransform = null;
        try
        {
            antiTransform = transform.createInverse();
        }
        catch (NoninvertibleTransformException e)
        {
            CategoryLogger.always().error(e);
        }

        // set the rendering hints
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // loop over the features and see if they have to be drawn
        for (var featureList : this.sortedFeatureMap.values())
        {
            for (var feature : featureList)
            {
                if (this.visibleFeatures.contains(feature) && feature.isDisplay()
                        && getMetersPerPixelY() < feature.getShapeStyle().getScaleThresholdMetersPerPx())
                {
                    try
                    {
                        var shapeStyle = feature.getShapeStyle();
                        if (shapeStyle.getOutlineColor() != null)
                        {
                            if (!Double.isNaN(shapeStyle.getLineWidthM()))
                            {
                                graphics.setStroke(
                                        new BasicStroke(Math.max(1, (int) (shapeStyle.getLineWidthM() / getMetersPerPixelY())),
                                                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            }
                            else if (shapeStyle.getLineWidthPx() > 1)
                                graphics.setStroke(new BasicStroke(shapeStyle.getLineWidthPx(), BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND));
                            else
                                graphics.setStroke(new BasicStroke(1));
                        }
                        var shapeIterator = feature.shapeIterator(this.extent);
                        while (shapeIterator.hasNext())
                        {
                            Path2D shape = shapeIterator.next();
                            if (feature.isTransform())
                            {
                                shape.transform(transform);
                            }
                            if (shapeStyle.getFillColor() != null)
                            {
                                graphics.setColor(shapeStyle.getFillColor());
                                graphics.fill(shape);
                            }
                            if (shapeStyle.getOutlineColor() != null)
                            {
                                graphics.setColor(shapeStyle.getOutlineColor());
                                graphics.draw(shape);
                            }
                            if (feature.isTransform())
                            {
                                shape.transform(antiTransform);
                            }
                        }
                        graphics.setStroke(new BasicStroke(1));
                    }
                    catch (Exception exception)
                    {
                        CategoryLogger.always().error(exception);
                        throw new DsolGisException(exception);
                    }
                }
            }
        }
        return graphics;
    }

    @Override
    public Bounds2d getExtent()
    {
        return this.extent;
    }

    @Override
    public MapImageInterface getImage()
    {
        return this.image;
    }

    @Override
    public ImmutableList<LayerInterface> getAllLayers()
    {
        return new ImmutableArrayList<>(this.layerMap.values());
    }

    @Override
    public ImmutableList<LayerInterface> getVisibleLayers()
    {
        return new ImmutableArrayList<>(this.visibleLayers);
    }

    @Override
    public ImmutableMap<String, LayerInterface> getLayerMap()
    {
        return new ImmutableHashMap<>(this.layerMap);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public double getScaleX()
    {
        return this.extent.getDeltaX() / getImage().getSize().getWidth();
    }

    @Override
    public double getScaleY()
    {
        return this.extent.getDeltaY() / getImage().getSize().getHeight();
    }

    @Override
    public double getMetersPerUnitX()
    {
        return this.metersPerUnitX;
    }

    @Override
    public double getMetersPerUnitY()
    {
        return this.metersPerUnitY;
    }

    @Override
    public MapUnits getMapUnits()
    {
        return this.mapUnits;
    }

    @Override
    public void setExtent(final Bounds2d extent)
    {
        this.extent = extent;
    }

    @Override
    public void setImage(final MapImageInterface image)
    {
        this.image = image;
    }

    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public void setUnits(final MapUnits units)
    {
        this.mapUnits = units;
    }

    @Override
    public void zoom(final double zoomFactor)
    {
        double correcteddZoomFactor = (zoomFactor == 0) ? 1 : zoomFactor;

        double maxX = getImage().getSize().getWidth() / getScaleX() + this.extent.getMinX();
        double maxY = getImage().getSize().getHeight() / getScaleY() + this.extent.getMinY();

        double centerX = (maxX - this.extent.getMinX()) / 2 + this.extent.getMinX();
        double centerY = (maxY - this.extent.getMinY()) / 2 + this.extent.getMinY();

        double width = (1.0 / correcteddZoomFactor) * (maxX - this.extent.getMinX());
        double height = (1.0 / correcteddZoomFactor) * (maxY - this.extent.getMinY());

        this.extent =
                new Bounds2d(centerX - 0.5 * width, centerX + 0.5 * width, centerY - 0.5 * height, centerY + 0.5 * height);
    }

    @Override
    public void zoomPoint(final Point2D pixelPosition, final double zoomFactor)
    {
        double correcteddZoomFactor = (zoomFactor == 0) ? 1 : zoomFactor;

        double maxX = getImage().getSize().getWidth() / getScaleX() + this.extent.getMinX();
        double maxY = getImage().getSize().getHeight() / getScaleY() + this.extent.getMinY();

        double centerX = (pixelPosition.getX() / getImage().getSize().getWidth()) * (maxX - this.extent.getMinX())
                + this.extent.getMinX();
        double centerY = maxY - (pixelPosition.getY() / getImage().getSize().getHeight()) * (maxY - this.extent.getMinY());

        double width = (1.0 / correcteddZoomFactor) * (maxX - this.extent.getMinX());
        double height = (1.0 / correcteddZoomFactor) * (maxY - getExtent().getMinY());

        this.extent =
                new Bounds2d(centerX - 0.5 * width, centerX + 0.5 * width, centerY - 0.5 * height, centerY + 0.5 * height);
    }

    @Override
    public void zoomRectangle(final Rectangle2D rectangle)
    {

        double maxX = getImage().getSize().getWidth() / getScaleX() + this.extent.getMinX();
        double maxY = getImage().getSize().getHeight() / getScaleY() + this.extent.getMinY();

        double width = maxX - this.extent.getMinX();
        double height = maxY - this.extent.getMinY();

        double minX = this.extent.getMinX() + (rectangle.getMinX() / getImage().getSize().getWidth()) * width;
        double minY = this.extent.getMinY()
                + ((getImage().getSize().getHeight() - rectangle.getMaxY()) / getImage().getSize().getHeight()) * height;

        maxX = minX + (rectangle.getWidth() / getImage().getSize().getWidth()) * width;
        maxY = minY + ((getImage().getSize().getHeight() - rectangle.getHeight()) / getImage().getSize().getHeight()) * height;
        this.extent = new Bounds2d(minX, maxX, minY, maxY);
    }

    @Override
    public boolean isDrawBackground()
    {
        return this.drawBackground;
    }

    @Override
    public void setDrawBackground(final boolean drawBackground)
    {
        this.drawBackground = drawBackground;
    }

}
