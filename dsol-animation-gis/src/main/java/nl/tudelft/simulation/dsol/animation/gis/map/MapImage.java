package nl.tudelft.simulation.dsol.animation.gis.map;

import java.awt.Color;
import java.awt.Dimension;

import nl.tudelft.simulation.dsol.animation.gis.MapImageInterface;

/**
 * This class defines the map image, which acts as the basic 'canvas' for the drawing process. The size does not matter that
 * much, as it will be scaled to screen dimensions and clipped with the proper viewport. 
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MapImage implements MapImageInterface
{
    /** */
    private static final long serialVersionUID = 20201015L;

    /** background color; default is fully transparent. */
    private Color backgroundColor = new Color(255, 255, 255, 127);

    /** canvas image size; default is HD screen size. */
    private Dimension size = new Dimension(1920, 1080);

    /** {@inheritDoc} */
    @Override
    public Color getBackgroundColor()
    {
        return this.backgroundColor;
    }

    /** {@inheritDoc} */
    @Override
    public void setBackgroundColor(final Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    /** {@inheritDoc} */
    @Override
    public Dimension getSize()
    {
        return this.size;
    }

    /** {@inheritDoc} */
    @Override
    public void setSize(final Dimension size)
    {
        this.size = size;
    }
}
