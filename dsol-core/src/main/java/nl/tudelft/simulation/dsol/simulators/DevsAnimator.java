package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The reference implementation of the animator.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 * @since 1.5
 */
public class DevsAnimator<T extends Number & Comparable<T>> extends DevsSimulator<T> implements AnimatorInterface
{
    /** */
    private static final long serialVersionUID = 20140804L;

    /**
     * Create a new DevsAnimator.
     * @param id the id of the simulator, used in logging and firing of events.
     */
    public DevsAnimator(final Serializable id)
    {
        super(id);
    }

    /** AnimationDelay refers to the delay in milliseconds between timeSteps. */
    private long animationDelay = AnimatorInterface.DEFAULT_ANIMATION_DELAY;

    @Override
    public long getAnimationDelay()
    {
        return this.animationDelay;
    }

    @Override
    public void setAnimationDelay(final long animationDelay)
    {
        this.animationDelay = animationDelay;
        this.fireEvent(ANIMATION_DELAY_CHANGED_EVENT, animationDelay);
    }

    @Override
    public void updateAnimation()
    {
        this.fireTimedEvent(AnimatorInterface.UPDATE_ANIMATION_EVENT, null, this.simulatorTime);
    }

    @Override
    public void run()
    {
        AnimationThread animationThread = new AnimationThread(this);
        animationThread.start();
        // set the run flag semaphore to signal to startImpl() that the run method has started
        this.runflag = true;
        while (!isStoppingOrStopped() && !this.eventList.isEmpty()
                && this.simulatorTime.compareTo(this.replication.getEndTime()) <= 0)
        {
            while (!this.eventList.isEmpty() && !isStoppingOrStopped())
            {
                synchronized (super.semaphore)
                {
                    int cmp = this.eventList.first().getAbsoluteExecutionTime().compareTo(this.runUntilTime);
                    if ((cmp == 0 && !this.runUntilIncluding) || cmp > 0)
                    {
                        this.simulatorTime = SimTime.copy(this.runUntilTime);
                        this.runState = RunState.STOPPING;
                        break;
                    }

                    SimEventInterface<T> event = this.eventList.removeFirst();
                    if (event.getAbsoluteExecutionTime().compareTo(super.simulatorTime) != 0)
                    {
                        super.fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null,
                                event.getAbsoluteExecutionTime());
                    }
                    this.simulatorTime = event.getAbsoluteExecutionTime();
                    try
                    {
                        event.execute();
                        if (this.eventList.isEmpty())
                        {
                            this.simulatorTime = SimTime.copy(this.runUntilTime);
                            this.runState = RunState.STOPPING;
                            break;
                        }
                    }
                    catch (Exception exception)
                    {
                        handleSimulationException(exception);
                    }
                }
            }
            this.fireTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
        }
        updateAnimation();
        animationThread.stopAnimation();
    }

}
