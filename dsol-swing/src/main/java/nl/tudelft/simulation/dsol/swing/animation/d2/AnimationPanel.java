package nl.tudelft.simulation.dsol.swing.animation.d2;

import java.rmi.RemoteException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.event.Event;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.DsolException;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The AnimationPanel to display animated (Locatable) objects as an extension of the VisualizationPanel. The difference is that
 * the AnimationPanel is Simulator and Replication aware. When a new replication starts, a context for that replication is
 * created, and Renderable objects for that replication are stored in a subcontext that is specific for that replication. This
 * means that even when multiple replications run in parallel, animations can be stored internally and the user could
 * theoretically shift between the different animations (or show multiple animations at once).
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.peter-jacobs.com">Peter Jacobs </a>
 */
public class AnimationPanel extends VisualizationPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the simulator. */
    private SimulatorInterface<?> simulator;

    /**
     * constructs a new AnimationPanel.
     * @param homeExtent Bounds2d; the home (initial) extent of the panel
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator of which we want to know the events for animation
     * @throws RemoteException on network error for one of the listeners
     * @throws DsolException when the simulator is not implementing the AnimatorInterface
     */
    public AnimationPanel(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DsolException
    {
        super(homeExtent, simulator);
        Throw.when(!(simulator instanceof AnimatorInterface), DsolException.class,
                "Simulator must implement the AnimatorInterface");
        this.simulator = simulator;
        simulator.addListener(this, Replication.START_REPLICATION_EVENT);
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        super.notify(event);

        if (event.getType().equals(Replication.START_REPLICATION_EVENT))
        {
            synchronized (this.elementList)
            {
                this.elements.clear();
                try
                {
                    if (this.context != null)
                    {
                        this.context.removeListener(this, ContextInterface.OBJECT_ADDED_EVENT);
                        this.context.removeListener(this, ContextInterface.OBJECT_REMOVED_EVENT);
                    }
                    this.context =
                            ContextUtil.lookupOrCreateSubContext(this.simulator.getReplication().getContext(), "animation/2D");
                    subscribeToContext();
                }
                catch (Exception exception)
                {
                    this.simulator.getLogger().always().warn(exception, "notify");
                }
            }
        }
    }

}
