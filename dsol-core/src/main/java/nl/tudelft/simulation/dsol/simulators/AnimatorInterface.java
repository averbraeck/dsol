package nl.tudelft.simulation.dsol.simulators;

import java.rmi.RemoteException;

import org.djutils.event.EventType;
import org.djutils.event.TimedEventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * The AnimatorInterface defines the methods for a DEVSDESS simulator with wallclock delay between the consecutive time steps.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface AnimatorInterface
{
    /** DEFAULT_ANIMATION_DELAY of 100 milliseconds used in the animator. */
    long DEFAULT_ANIMATION_DELAY = 100L;

    /** UPDATE_ANIMATION_EVENT is fired to wake up animatable components. */
    TimedEventType UPDATE_ANIMATION_EVENT = new TimedEventType(new MetaData("UPDATE_ANIMATION_EVENT", "Animation update"));

    /** ANIMATION_DELAY_CHANGED_EVENT is fired when the time step is set. */
    EventType ANIMATION_DELAY_CHANGED_EVENT = new EventType(new MetaData("ANIMATION_DELAY_CHANGED_EVENT",
            "Animation delay changed", new ObjectDescriptor("newDelay", "new animation delay", Long.class)));

    /**
     * returns the animation delay in milliseconds between each consecutive animation update.
     * @return long; the animation delay in milliseconds of wallclock time
     * @throws RemoteException on network failure
     */
    long getAnimationDelay() throws RemoteException;

    /**
     * sets the animationDelay using wallclock time in milliseconds.
     * @param milliseconds long; the animation delay in milliseconds
     * @throws RemoteException on network failure
     */
    void setAnimationDelay(long milliseconds) throws RemoteException;

    /**
     * updateAnimation takes care of firing the UPDATE_ANIMATION_EVENT.
     */
    void updateAnimation();

    /**
     * The separate thread that takes care of the animation.
     * <p>
     * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    class AnimationThread extends Thread
    {
        /** is the animator running? */
        private boolean running = true;

        /** the animator. */
        private final AnimatorInterface animator;

        /**
         * @param animator AnimatorInterface; the animator.
         */
        public AnimationThread(final AnimatorInterface animator)
        {
            super();
            this.animator = animator;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            long lastTime = System.nanoTime();
            while (this.running)
            {
                try
                {
                    long delta = System.nanoTime() - lastTime;
                    sleep(Math.max(1, this.animator.getAnimationDelay() - delta / 1000000));
                    lastTime = System.nanoTime();
                    this.animator.updateAnimation();
                }
                catch (InterruptedException exception)
                {
                    // if interrupted by stopAnimation, this.running is false and the animation stops.
                    this.running = false;
                }
                catch (RemoteException exception)
                {
                    this.running = false;
                }
            }
        }

        /**
         * Stop the animation.
         */
        public void stopAnimation()
        {
            this.running = false;
            interrupt();
        }
    }
}
