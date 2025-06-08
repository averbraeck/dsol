package nl.tudelft.simulation.examples.dsol.animation;

import java.awt.Color;

import org.djutils.draw.bounds.Bounds;
import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.point.DirectedPoint2d;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * Wall.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Wall implements Locatable
{
    /** origin location. */
    private final DirectedPoint2d location;

    /** the rectangle for the wall. */
    private final Bounds2d bounds;

    /** the color of the wall. */
    private final Color color;

    /** the id. */
    private final String id;

    /**
     * Construct a wall;
     * @param id the id
     * @param bounds the rectangle for the wall
     * @param color the color of the wall
     */
    public Wall(final String id, final Bounds2d bounds, final Color color)
    {
        this(id, bounds, color, new DirectedPoint2d(0, 0, 0));
    }

    /**
     * Construct a wall;
     * @param id the id
     * @param bounds the rectangle for the wall
     * @param color the color of the wall
     * @param location the origin
     */
    public Wall(final String id, final Bounds2d bounds, final Color color, final DirectedPoint2d location)
    {
        this.id = id;
        this.bounds = bounds;
        this.color = color;
        this.location = location;
    }

    /**
     * @return id
     */
    String getId()
    {
        return this.id;
    }

    /**
     * @return color
     */
    Color getColor()
    {
        return this.color;
    }

    /** {@inheritDoc} */
    @Override
    public Point<?> getLocation()
    {
        return this.location;
    }

    /** {@inheritDoc} */
    @Override
    public Bounds<?, ?> getRelativeBounds()
    {
        return this.bounds;
    }

}
