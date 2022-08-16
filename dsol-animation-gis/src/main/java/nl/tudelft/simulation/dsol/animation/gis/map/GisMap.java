package nl.tudelft.simulation.dsol.animation.gis.map;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableHashMap;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.immutablecollections.ImmutableMap;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.gis.DSOLGisException;
import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisMapInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapImageInterface;
import nl.tudelft.simulation.dsol.animation.gis.MapUnits;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.dsol.animation.gis.SerializableRectangle2D;

/**
 * Provides the implementation of a Map.
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
public class GisMap implements GisMapInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the extent of the map. */
    private Bounds2d extent;

    /** the map of layer names to layers. */
    private Map<String, LayerInterface> layerMap = new LinkedHashMap<>();

    /** the total list of layers of the map. */
    private List<LayerInterface> allLayers = new ArrayList<>();

    /** the visible layers of the map. */
    private List<LayerInterface> visibleLayers = new ArrayList<>();

    /** same set to false after layer change. */
    private boolean same = false;

    /** the mapfileImage. */
    private MapImageInterface image = new MapImage();

    /** the name of the mapFile. */
    private String name;

    /** the map units. */
    private MapUnits units = MapUnits.METERS;

    /** draw the background? */
    private boolean drawBackground = true;

    /** the screen resolution. */
    private static final int RESOLUTION = 72;

    /**
     * constructs a new Map.
     */
    public GisMap()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void addLayer(final LayerInterface layer)
    {
        this.visibleLayers.add(layer);
        this.allLayers.add(layer);
        this.layerMap.put(layer.getName(), layer);
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void setLayers(final List<LayerInterface> layers)
    {
        this.allLayers = new ArrayList<>(layers);
        this.visibleLayers = new ArrayList<>(layers);
        this.layerMap.clear();
        for (LayerInterface layer : layers)
        {
            this.layerMap.put(layer.getName(), layer);
        }
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void setLayer(final int index, final LayerInterface layer)
    {
        this.allLayers.set(index, layer);
        if (this.allLayers.size() == this.visibleLayers.size())
        {
            this.visibleLayers.add(index, layer);
        }
        else
        {
            this.visibleLayers.add(layer);
        }
        this.layerMap.put(layer.getName(), layer);
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void hideLayer(final LayerInterface layer)
    {
        this.visibleLayers.remove(layer);
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void hideLayer(final String layerName) throws RemoteException
    {
        if (this.layerMap.keySet().contains(layerName))
        {
            hideLayer(this.layerMap.get(layerName));
        }
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void showLayer(final LayerInterface layer)
    {
        this.visibleLayers.add(layer);
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public void showLayer(final String layerName) throws RemoteException
    {
        if (this.layerMap.keySet().contains(layerName))
        {
            showLayer(this.layerMap.get(layerName));
        }
        this.same = false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSame() throws RemoteException
    {
        boolean ret = this.same;
        this.same = true;
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:methodlength")
    public Graphics2D drawMap(final Graphics2D graphics) throws DSOLGisException
    {
        if (this.drawBackground)
        {
            // We fill the background.
            graphics.setColor(this.getImage().getBackgroundColor());
            graphics.fillRect(0, 0, (int) this.getImage().getSize().getWidth(), (int) this.getImage().getSize().getHeight());
        }

        // We compute the transform of the map
        AffineTransform transform = new AffineTransform();
        transform.scale(this.getImage().getSize().getWidth() / this.extent.getDeltaX(),
                -this.getImage().getSize().getHeight() / this.extent.getDeltaY());
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

        // we cache the scale
        double scale = this.getScale();
        // XXX: define how we use this -- System.out.println("scale = " + scale);

        // we set the rendering hints
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // We loop over the layers
        for (Iterator<LayerInterface> i = this.visibleLayers.iterator(); i.hasNext();)
        {
            Layer layer = (Layer) i.next();
            try
            {
                if (layer.isDisplay()) // TODO: && layer.getMaxScale() < scale && layer.getMinScale() > scale)
                {
                    for (FeatureInterface feature : layer.getFeatures())
                    {
                        List<GisObject> shapes = feature.getShapes(this.extent);
                        SerializablePath shape = null;
                        for (Iterator<GisObject> shapeIterator = shapes.iterator(); shapeIterator.hasNext();)
                        {
                            GisObject gisObject = shapeIterator.next();
//                            if (feature.getDataSource().getType() == POINT)
//                            {
//                                shape = new SerializablePath();
//                                Point2D point = (Point2D) gisObject.getShape();
//                                shape.moveTo((float) point.getX(), (float) point.getY());
//                                // TODO: points are not drawn -- we have to do this differently
//                            }
//                            else
//                            {
                                shape = (SerializablePath) gisObject.getShape();
//                            }
                            if (layer.isTransform())
                            {
                                shape.transform(transform);
                            }
                            if (/*feature.getDataSource().getType() == POLYGON &&*/ feature.getFillColor() != null)
                            {
                                graphics.setColor(feature.getFillColor());
                                graphics.fill(shape);
                            }
                            if (feature.getOutlineColor() != null)
                            {
                                graphics.setColor(feature.getOutlineColor());
                                graphics.draw(shape);
                            }
                            if (layer.isTransform())
                            {
                                shape.transform(antiTransform);
                            }
                        }
                    }
                }
            }
            catch (Exception exception)
            {
                CategoryLogger.always().error(exception);
                throw new DSOLGisException(exception.getMessage());
            }
        }
        return graphics;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds2d getExtent()
    {
        return this.extent;
    }

    /** {@inheritDoc} */
    @Override
    public MapImageInterface getImage()
    {
        return this.image;
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableList<LayerInterface> getAllLayers()
    {
        return new ImmutableArrayList<>(this.allLayers);
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableList<LayerInterface> getVisibleLayers()
    {
        return new ImmutableArrayList<>(this.visibleLayers);
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableMap<String, LayerInterface> getLayerMap() throws RemoteException
    {
        return new ImmutableHashMap<>(this.layerMap);
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public double getScale()
    {
        return (this.getImage().getSize().getWidth() / (2.54 * RESOLUTION)) * this.extent.getDeltaX();
    }

    /**
     * returns the scale of the Image.
     * @return double the unitPerPixel
     */
    @Override
    public double getUnitImageRatio()
    {
        return Math.min(this.extent.getDeltaX() / this.image.getSize().getWidth(),
                this.extent.getDeltaY() / this.image.getSize().getHeight());
    }

    /** {@inheritDoc} */
    @Override
    public MapUnits getUnits()
    {
        return this.units;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtent(final Bounds2d extent)
    {
        this.extent = extent;
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(final MapImageInterface image)
    {
        this.image = image;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public void setUnits(final MapUnits units)
    {
        this.units = units;
    }

    /** {@inheritDoc} */
    @Override
    public void zoom(final double zoomFactor)
    {
        double correcteddZoomFactor = (zoomFactor == 0) ? 1 : zoomFactor;

        double maxX = (getUnitImageRatio() * this.getImage().getSize().getWidth()) + this.extent.getMinX();
        double maxY = (getUnitImageRatio() * this.getImage().getSize().getHeight()) + this.extent.getMinY();

        double centerX = (maxX - this.extent.getMinX()) / 2 + this.extent.getMinX();
        double centerY = (maxY - this.extent.getMinY()) / 2 + this.extent.getMinY();

        double width = (1.0 / correcteddZoomFactor) * (maxX - this.extent.getMinX());
        double height = (1.0 / correcteddZoomFactor) * (maxY - this.extent.getMinY());

        this.extent =
                new Bounds2d(centerX - 0.5 * width, centerX + 0.5 * width, centerY - 0.5 * height, centerY + 0.5 * height);
    }

    /** {@inheritDoc} */
    @Override
    public void zoomPoint(final Point2D pixelPosition, final double zoomFactor)
    {
        double correcteddZoomFactor = (zoomFactor == 0) ? 1 : zoomFactor;

        double maxX = (getUnitImageRatio() * this.getImage().getSize().getWidth()) + this.extent.getMinX();
        double maxY = (getUnitImageRatio() * this.getImage().getSize().getHeight()) + this.extent.getMinY();

        double centerX = (pixelPosition.getX() / this.getImage().getSize().getWidth()) * (maxX - this.extent.getMinX())
                + this.extent.getMinX();
        double centerY = maxY - (pixelPosition.getY() / this.getImage().getSize().getHeight()) * (maxY - this.extent.getMinY());

        double width = (1.0 / correcteddZoomFactor) * (maxX - this.extent.getMinX());
        double height = (1.0 / correcteddZoomFactor) * (maxY - this.getExtent().getMinY());

        this.extent =
                new Bounds2d(centerX - 0.5 * width, centerX + 0.5 * width, centerY - 0.5 * height, centerY + 0.5 * height);
    }

    /** {@inheritDoc} */
    @Override
    public void zoomRectangle(final SerializableRectangle2D rectangle)
    {

        double maxX = (getUnitImageRatio() * this.getImage().getSize().getWidth()) + this.extent.getMinX();
        double maxY = (getUnitImageRatio() * this.getImage().getSize().getHeight()) + this.extent.getMinY();

        double width = maxX - this.extent.getMinX();
        double height = maxY - this.extent.getMinY();

        double minX = this.extent.getMinX() + (rectangle.getMinX() / this.getImage().getSize().getWidth()) * width;
        double minY = this.extent.getMinY()
                + ((this.getImage().getSize().getHeight() - rectangle.getMaxY()) / this.getImage().getSize().getHeight())
                        * height;

        maxX = minX + (rectangle.getWidth() / this.getImage().getSize().getWidth()) * width;
        maxY = minY + ((this.getImage().getSize().getHeight() - rectangle.getHeight()) / this.getImage().getSize().getHeight())
                * height;
        this.extent = new Bounds2d(minX, maxX, minY, maxY);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDrawBackground()
    {
        return this.drawBackground;
    }

    /** {@inheritDoc} */
    @Override
    public void setDrawBackground(final boolean drawBackground)
    {
        this.drawBackground = drawBackground;
    }

}
