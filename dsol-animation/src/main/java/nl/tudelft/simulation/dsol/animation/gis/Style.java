package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Color;

/**
 * Style indicates how features for lines and shapes and markers for points need to be drawn.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
@SuppressWarnings("checkstyle:hiddenfield")
public class Style
{
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

    /**
     * Return the fill color for the layer.
     * @return the rgb(a) fill color for the layer
     */
    public Color getFillColor()
    {
        return this.fillColor;
    }

    /**
     * Set the fill color for the layer.
     * @param fillColor the rgb(a) fill color for the layer
     * @return the object for method chaining
     */
    public Style setFillColor(final Color fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    /**
     * Return the outline (line) color for the layer.
     * @return the rgb(a) outline (line) color for the layer
     */
    public Color getOutlineColor()
    {
        return this.outlineColor;
    }

    /**
     * Set the outline (line) color for the layer.
     * @param outlineColor the rgb(a) outline (line) color for the layer
     * @return the object for method chaining
     */
    public Style setOutlineColor(final Color outlineColor)
    {
        this.outlineColor = outlineColor;
        return this;
    }

    /**
     * Get the fixed line width of the (out)line in pixels.
     * @return the fixed line width of the outline in pixels
     */
    public int getLineWidthPx()
    {
        return this.lineWidthPx;
    }

    /**
     * Set the fixed line width of the (out)line in pixels.
     * @param lineWidthPx the fixed line width of the outline in pixels
     * @return the object for method chaining
     */
    public Style setLineWidthPx(final int lineWidthPx)
    {
        this.lineWidthPx = lineWidthPx;
        return this;
    }

    /**
     * Get the line width of the (out)line in meters.
     * @return the line width of the outline in meters
     */
    public double getLineWidthM()
    {
        return this.lineWidthM;
    }

    /**
     * Set the line width of the (out)line in meters.
     * @param lineWidthM the line width of the outline in meters
     * @return the object for method chaining
     */
    public Style setLineWidthM(final double lineWidthM)
    {
        this.lineWidthM = lineWidthM;
        return this;
    }

    /**
     * Get the scale value (zoom level) to use as a threshold for drawing as the number of meters per pixel. Suppose, the number
     * of meters per pixel is 5. When the scale (zoom level) of the map goes <b>below</b> 5 meters per pixel by zooming out, the
     * feature is no longer drawn.
     * @return the scale value (zoom level) to use as a threshold for drawing as the number of meters per pixel
     */
    public double getScaleThresholdMetersPerPx()
    {
        return this.scaleThresholdMetersPerPx;
    }

    /**
     * Set the scale value (zoom level) to use as a threshold for drawing as the number of meters per pixel. Suppose, the number
     * of meters per pixel is 5. When the scale (zoom level) of the map goes <b>below</b> 5 meters per pixel by zooming out, the
     * feature is no longer drawn.
     * @param scaleThresholdMetersPerPx the scale value (zoom level) to use as a threshold for drawing as the number of meters
     *            per pixel
     * @return the object for method chaining
     */
    public Style setScaleThresholdMetersPerPx(final double scaleThresholdMetersPerPx)
    {
        this.scaleThresholdMetersPerPx = scaleThresholdMetersPerPx;
        return this;
    }

}
