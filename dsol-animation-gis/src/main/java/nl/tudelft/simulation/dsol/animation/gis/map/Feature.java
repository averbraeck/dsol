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
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.language.d2.Shape;

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
public class Feature implements FeatureInterface
{
    /** */
    private static final long serialVersionUID = 20210201L;

    /** the key that defines a feature in a layer, can be "*" if no features are defined. */
    private String key = "*";

    /**
     * the value belonging to the key that defines the feature in a layer, can be "*" if all elements in the data source that
     * have the correct key have to be drawn.
     */
    private String value = "*";

//    /** the dataSource to use. */
//    private DataSourceInterface dataSource;

    /** the list of shapes that have been retrieved for this feature. */
    private List<GisObject> shapes = new ArrayList<>();

    /** the fillColor of the layer, by default no fill. */
    private Color fillColor = null;

    /** the outlineColor. */
    private Color outlineColor = Color.BLACK;
    
    /** whether the shapes have been read or not. */
    private boolean initialized = false;

    /** {@inheritDoc} */
    @Override
    public final String getKey()
    {
        return this.key;
    }

    /** {@inheritDoc} */
    @Override
    public final void setKey(final String key)
    {
        this.key = key;
    }

    /** {@inheritDoc} */
    @Override
    public final String getValue()
    {
        return this.value;
    }

//    /** {@inheritDoc} */
//    @Override
//    public DataSourceInterface getDataSource()
//    {
//        return this.dataSource;
//    }

    /** {@inheritDoc} */
    @Override
    public boolean isInitialized()
    {
        return this.initialized;
    }

    /** {@inheritDoc} */
    @Override
    public void setInitialized(final boolean initialized)
    {
        this.initialized = initialized;
    }

    /** {@inheritDoc} */
    @Override
    public int getNumShapes()
    {
        return this.shapes.size();
    }

    /** {@inheritDoc} */
    @Override
    public GisObject getShape(final int index) throws IndexOutOfBoundsException
    {
        return this.shapes.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public List<GisObject> getShapes()
    {
        return this.shapes;
    }

    /** {@inheritDoc} */
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

//    /** {@inheritDoc} */
//    @Override
//    public void setDataSource(final DataSourceInterface dataSource)
//    {
//        this.dataSource = dataSource;
//    }

    /** {@inheritDoc} */
    @Override
    public final void setValue(final String value)
    {
        this.value = value;
    }

    /** {@inheritDoc} */
    @Override
    public Color getFillColor()
    {
        return this.fillColor;
    }

    /** {@inheritDoc} */
    @Override
    public void setFillColor(final Color fillColor)
    {
        this.fillColor = fillColor;
    }

    /** {@inheritDoc} */
    @Override
    public Color getOutlineColor()
    {
        return this.outlineColor;
    }

    /** {@inheritDoc} */
    @Override
    public void setOutlineColor(final Color outlineColor)
    {
        this.outlineColor = outlineColor;
    }

}
