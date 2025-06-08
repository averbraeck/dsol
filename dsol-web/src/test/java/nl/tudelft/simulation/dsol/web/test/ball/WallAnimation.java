package nl.tudelft.simulation.dsol.web.test.ball;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

import nl.tudelft.simulation.dsol.animation.SimRenderable2d;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * The animation of the wall.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WallAnimation extends SimRenderable2d<Wall>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param source wall
     * @param simulator simulator
     */
    public WallAnimation(final Wall source, final SimulatorInterface<?> simulator)
    {
        super(source, simulator);
        setScaleY(true);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        graphics.setColor(getSource().getColor());
        var b = getSource().getRelativeBounds();
        var rect = new Rectangle2D.Double(b.getMinX(), -b.getMaxY(), b.getDeltaX(), b.getDeltaY());
        graphics.fill(rect);
    }

}
