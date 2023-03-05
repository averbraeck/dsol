package nl.tudelft.simulation.dsol.swing.animation.D2;

import java.rmi.RemoteException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.event.Event;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.DSOLException;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The AnimationPanel to display animated (Locatable) objects. Added the possibility to witch layers on and off. By default all
 * layers will be drawn, so no changes to existing software need to be made. <br>
 * <br>
 * <b>Asynchronous and synchronous calls:</b><br>
 * The internal functions of the AnimationPanel are handled in a synchronous way inside the animation panel, possibly through
 * (mouse or keyboard) listeners and handlers that implement the functions.There are several exceptions, though:
 * <ul>
 * <li><i>Clicking on one or more objects:</i> what has to happen is very much dependent on the implementation. Therefore, the
 * click on an object will lead to firing of an event, where the listener(s), if any, can decide what to do. This can be
 * dependent on whether CTRL, SHIFT, or ALT were pressed at the same time as the mouse button. Example behaviors could be:
 * pop-up with properties of the object; showing properties in a special pane; highlighting the object; or setting the auto-pan
 * on the clicked object. The event to use is the ANIMATION_MOUSE_CLICK_EVENT.</li>
 * <li><i>Right click on one or more objects:</i> what has to happen is very much dependent on the implementation. Therefore,
 * the click on an object will lead to firing of an event, where the listener(s), if any, can decide what to do. The event to
 * use is the ANIMATION_MOUSE_POPUP_EVENT.</li>
 * </ul>
 * Furthermore, the AnimationPanel is an event listener, and listens, e.g., to the event of a searched object: the
 * ANIMATION_SEARCH_OBJECT_EVENT to highlight the object, or, in case of an AutoPanAnimationPanel, to keep the object in the
 * middle of the screen.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
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
     * @throws DSOLException when the simulator is not implementing the AnimatorInterface
     */
    public AnimationPanel(final Bounds2d homeExtent, final SimulatorInterface<?> simulator)
            throws RemoteException, DSOLException
    {
        super(homeExtent, simulator);
        Throw.when(!(simulator instanceof AnimatorInterface), DSOLException.class,
                "Simulator must implement the AnimatorInterface");
        this.simulator = simulator;
        simulator.addListener(this, Replication.START_REPLICATION_EVENT);
    }

    /** {@inheritDoc} */
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
