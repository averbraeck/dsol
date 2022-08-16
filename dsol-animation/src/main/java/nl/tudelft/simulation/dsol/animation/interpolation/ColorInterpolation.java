package nl.tudelft.simulation.dsol.animation.interpolation;

import java.awt.Color;

/**
 * ColorInterpolation. This class works on two given colors and will, based on a start and end time transform from starting
 * color to the destination color.
 * <p>
 * copyright (c) 2004-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a>.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="mailto:stijnh@tbm.tudelft.nl">Stijn-Pieter van Houten </a>
 */
public class ColorInterpolation
{
    /** the origin. */
    private Color origin = null;

    /** the destination. */
    private Color destination = null;

    /** the calculated RGB values. */
    private int[] calculatedRGBValues = {0, 0, 0};

    /** the original starting RGB values. */
    private int[] originalStartingRGBValues = {0, 0, 0};

    /** the original destination RGB values. */
    private int[] originalDestinationRGBValues = {0, 0, 0};

    /** the start time. */
    private double startTime = Double.NaN;

    /** the end time. */
    private double endTime = Double.NaN;

    /**
     * constructs a new SimulatedLinearInterpolation.
     * @param originalStartingColor Color; the origin
     * @param originalDestinationColor Color; the destination
     * @param startTime double; the startTime for the interpolation
     * @param endTime double; the endTime of the interpolation
     */
    public ColorInterpolation(final Color originalStartingColor, final Color originalDestinationColor, final double startTime,
            final double endTime)
    {
        super();
        if (endTime < startTime)
        {
            throw new IllegalArgumentException("endTime < startTime");
        }
        this.origin = originalStartingColor;
        this.destination = originalDestinationColor;
        this.startTime = startTime;
        this.endTime = endTime;
        // save rgb values of origin and destination
        this.originalStartingRGBValues[0] = originalStartingColor.getRed();
        this.originalStartingRGBValues[1] = originalStartingColor.getGreen();
        this.originalStartingRGBValues[2] = originalStartingColor.getBlue();
        this.originalDestinationRGBValues[0] = originalDestinationColor.getRed();
        this.originalDestinationRGBValues[1] = originalDestinationColor.getGreen();
        this.originalDestinationRGBValues[2] = originalDestinationColor.getBlue();
        for (int i = 0; i < this.calculatedRGBValues.length; i++)
        {
            if (this.originalStartingRGBValues[i] == this.originalDestinationRGBValues[i])
            {
                this.calculatedRGBValues[i] = this.originalStartingRGBValues[i];
            }
        }
    }

    /**
     * returns a color based on the interpolation between the original and end destination.
     * @param time double; the time
     * @return the color to return
     */
    public Color getColor(final double time)
    {
        if (time <= this.startTime)
        {
            return this.origin;
        }
        if (time >= this.endTime)
        {
            return this.destination;
        }
        double fraction = (time - this.startTime) / (this.endTime - this.startTime);
        for (int i = 0; i < this.calculatedRGBValues.length; i++)
        {
            // 0 --> 255 (if you choose black -> white)
            if (this.originalStartingRGBValues[i] < this.originalDestinationRGBValues[i])
            {
                this.calculatedRGBValues[i] = this.originalStartingRGBValues[i]
                        + (int) ((this.originalDestinationRGBValues[i] - this.originalStartingRGBValues[0]) * fraction);
            }
            // 255 -> 0 (if you choose white -> black)
            if (this.originalStartingRGBValues[i] > this.originalDestinationRGBValues[i])
            {
                this.calculatedRGBValues[i] = this.originalStartingRGBValues[i]
                        - (int) ((this.originalStartingRGBValues[i] - this.originalDestinationRGBValues[i]) * fraction);
            }
        }
        // calculate alpha
        int alpha = this.origin.getAlpha();
        // only if there is a difference a new value must be calculated
        if (this.origin.getAlpha() != this.destination.getAlpha())
        {
            if (this.origin.getAlpha() < this.destination.getAlpha())
            {
                alpha = this.origin.getAlpha() + (int) ((this.destination.getAlpha() - this.origin.getAlpha()) * fraction);
            }
            if (this.origin.getAlpha() > this.destination.getAlpha())
            {
                alpha = this.origin.getAlpha() - (int) ((this.origin.getAlpha() - this.destination.getAlpha()) * fraction);
            }
        }
        return new Color(this.calculatedRGBValues[0], this.calculatedRGBValues[1], this.calculatedRGBValues[2], alpha);
    }
}
