package nl.tudelft.simulation.dsol.animation;

import nl.tudelft.simulation.dsol.animation.d2.Renderable2d;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;

/**
 * SimRenderable2D binds the animation objects to the context in simulator.getReplication().
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <L> The Locatable source type
 */
public abstract class SimRenderable2d<L extends Locatable> extends Renderable2d<L>
{
    /** */
    private static final long serialVersionUID = 20220205L;

    /**
     * Constructs a new Renderable2D.
     * @param source T; the Locatable source type that provides the location and bounds
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator to provide the context via the replication
     */
    public SimRenderable2d(final L source, final SimulatorInterface<?> simulator)
    {
        super(source, simulator.getReplication());
    }

    /**
     * destroys an RenderableObject by unsubscribing it from the context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator to provide the context via the replication
     */
    public void destroy(final SimulatorInterface<?> simulator)
    {
        super.destroy(simulator.getReplication());
    }
}
