package nl.tudelft.simulation.dsol.animation.gis.map;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.LayerInterface;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.language.d2.Shape;

/**
 * Feature contains an element of a layer, defined by a key value combination, with its own colors.<br>
 * TODO: minimum scale and maximum scale to draw features has to be added again, but first, scale needs to be defined properly.
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

    /** the z-index of this feature. The z-index indicates the drawing order, from low to high. */
    private double zIndex = 0.0;

    /** the list of shapes that have been retrieved for this feature. */
    private List<GisObject> shapes = new ArrayList<>();

    /** the fillColor of the layer, by default no fill. */
    private Color fillColor = null;

    /** the outlineColor. */
    private Color outlineColor = Color.BLACK;

    /** the line width in px. */
    private int lineWidthPx = 1;

    /** the line width in meters. */
    private double lineWidthM = Double.NaN;
    
    /** the scale threshold in m/px above which the feature should not be drawn. */
    private double scaleThresholdMetersPerPx = 1.0E6; // default = draw always

    /** whether the shapes have been read or not. */
    private boolean initialized = false;

    /**
     * Create a feature as part of a layer.
     * @param layer the layer to which this feature beongs
     */
    public Feature(final LayerInterface layer)
    {
        super();
        this.layer = layer;
    }

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
    public int getNumShapes()
    {
        return this.shapes.size();
    }

    @Override
    public GisObject getShape(final int index) throws IndexOutOfBoundsException
    {
        return this.shapes.get(index);
    }

    @Override
    public List<GisObject> getShapes()
    {
        return this.shapes;
    }

    @Override
    public List<GisObject> getShapes(final Bounds2d rectangle)
    {
        List<GisObject> result = new ArrayList<>();
        Rectangle2D rectangle2D = rectangle.toRectangle2D();
        for (GisObject shape : this.shapes)
        {
            if (shape.getShape() instanceof SerializablePath)
            {
                if (Shape.overlaps(rectangle2D, ((SerializablePath) shape.getShape()).getBounds2D()))
                {
                    result.add(shape);
                }
            }
            else if (shape.getShape() instanceof Point2D)
            {
                if (rectangle2D.contains((Point2D) shape.getShape()))
                {
                    result.add(shape);
                }
            }
            else
            {
                CategoryLogger.always().error("unknown shape in cached content " + shape);
            }
        }
        return result;
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
    public Color getFillColor()
    {
        return this.fillColor;
    }

    @Override
    public void setFillColor(final Color fillColor)
    {
        this.fillColor = fillColor;
    }

    @Override
    public Color getOutlineColor()
    {
        return this.outlineColor;
    }

    @Override
    public void setOutlineColor(final Color outlineColor)
    {
        this.outlineColor = outlineColor;
    }

    @Override
    public int getLineWidthPx()
    {
        return this.lineWidthPx;
    }

    @Override
    public void setLineWidthPx(final int lineWidthPx)
    {
        this.lineWidthPx = lineWidthPx;
    }

    @Override
    public double getLineWidthM()
    {
        return this.lineWidthM;
    }

    @Override
    public void setLineWidthM(final double lineWidthM)
    {
        this.lineWidthM = lineWidthM;
    }

    @Override
    public double getScaleThresholdMetersPerPx()
    {
        return this.scaleThresholdMetersPerPx;
    }

    @Override
    public void setScaleThresholdMetersPerPx(final double scaleThresholdMetersPerPx)
    {
        this.scaleThresholdMetersPerPx = scaleThresholdMetersPerPx;
    }

    @Override
    public String toString()
    {
        return "Feature [key=" + this.key + ", value=" + this.value + "]";
    }

}
