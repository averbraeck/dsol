package nl.tudelft.simulation.dsol.animation.gis;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.Point2d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * This renderable draws CAD/GIS objects.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public interface GisRenderable2d extends Renderable2dInterface<GisRenderable2d>, Locatable
{
    @Override
    void paintComponent(Graphics2D graphics, Bounds2d extent, Dimension screen, RenderableScale renderableScale,
            ImageObserver observer);

    @Override
    default GisRenderable2d getSource()
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

    @Override
    default int getId()
    {
        return -1; // drawn before the rest in case all z-values are the same
    }

    @Override
    default boolean contains(final Point2d pointWorldCoordinates, final Bounds2d extent)
    {
        return false;
    }

    @Override
    default boolean contains(final Point2D pointScreenCoordinates, final Bounds2d extent, final Dimension screenSize,
            final RenderableScale scale, final double worldMargin, final double pixelMargin)
    {
        return false;
    }

    @Override
    default boolean isFlip()
    {
        return false;
    }

    @Override
    default void setFlip(final boolean flip)
    {
        // ignore
    }

    @Override
    default boolean isRotate()
    {
        return false;
    }

    @Override
    default void setRotate(final boolean rotate)
    {
        // ignore
    }

    @Override
    default boolean isScale()
    {
        return false;
    }

    @Override
    default void setScale(final boolean scale)
    {
        // ignore
    }

    @Override
    default boolean isScaleY()
    {
        return false;
    }

    @Override
    default void setScaleY(final boolean scaleY)
    {
        // ignore
    }

    @Override
    default boolean isScaleObject()
    {
        return false;
    }

    @Override
    default void setScaleObject(final boolean scaleY)
    {
        // ignore
    }

    @Override
    default boolean isTranslate()
    {
        return false;
    }

    @Override
    default void setTranslate(final boolean translate)
    {
        // ignore
    }

}
