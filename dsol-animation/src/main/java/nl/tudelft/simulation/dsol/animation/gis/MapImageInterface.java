package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;

/**
 * This interface defines the map image, which acts as the basic 'canvas' for the drawing process. The size does not matter that
 * much, as it will be scaled to screen dimensions and clipped with the proper viewport. 
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface MapImageInterface extends Serializable
{
    /**
     * Return the backgroundColor.
     * @return Color; the value of property backgroundColor.
     */
    Color getBackgroundColor();

    /**
     * Set the backgroundColor.
     * @param backgroundColor Color; New value of backgroundColor.
     */
    void setBackgroundColor(Color backgroundColor);

    /**
     * Getter for the canvas size.
     * @return Dimension the value of the canvas size.
     */
    Dimension getSize();

    /**
     * Setter for the canvas size.
     * @param size Dimension; New value of the canvas size.
     */
    void setSize(Dimension size);

}
