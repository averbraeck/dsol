package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.animation.D2.RenderableScale;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * This renderable draws CAD/GIS objects.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface GisRenderable2D extends Renderable2DInterface<GisRenderable2D>, Locatable
{
    /** {@inheritDoc} */
    @Override
    void paintComponent(Graphics2D graphics, Bounds2d extent, Dimension screen, RenderableScale renderableScale,
            ImageObserver observer);

    /** {@inheritDoc} */
    @Override
    default GisRenderable2D getSource()
    {
        return this;
    }

    /**
     * @return map the Shapefile map
     */
    GisMapInterface getMap();

    /**
     * destroys an RenderableObject by unsubscribing it from the context.
     */
    @Override
    void destroy(Contextualized contextProvider);

    /** {@inheritDoc} */
    @Override
    default boolean contains(final Point2d pointWorldCoordinates, final Bounds2d extent)
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    default long getId()
    {
        return -1; // drawn before the rest in case all z-values are the same
    }

}
