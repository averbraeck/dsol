package nl.tudelft.simulation.dsol.swing.animation.D2;

import java.rmi.RemoteException;

import org.djunits.unit.FrequencyUnit;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djutils.event.EventListener;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.LocalEventProducer;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;

/**
 * AnimationUpdaterThread is a class that sends UPDATE_ANIMATION_EVENT events with a certain frequency. The class can be
 * extended to work differently, or to react to external events. By calling start(), the updates of the visualization start, and
 * they can be stopped by calling the pause() method.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class AnimationUpdaterThread extends Thread implements EventProducer
{
    /** */
    private static final long serialVersionUID = 20230305L;

    /** Delegate object that sends the UPDATE_ANIMATION_EVENTs. */
    private final EventProducer eventProducer;

    /** Frequency of update. */
    private Frequency updateFrequency = new Frequency(30, FrequencyUnit.PER_SECOND);

    /** Running or paused mode. */
    private boolean running = false;

    /**
     * Construct an UpdaterThread with a default LocalEventProducer.
     */
    public AnimationUpdaterThread()
    {
        this(new LocalEventProducer());
    }

    /**
     * Construct an UpdaterThread with a specific EventProducer.
     * @param eventProducer EventProducer; the event producer to use (remote, local, etc.)
     */
    public AnimationUpdaterThread(final EventProducer eventProducer)
    {
        this.eventProducer = eventProducer;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        this.running = true;
        while (this.running)
        {
            try
            {
                sleep((long) (1000.0 / this.updateFrequency.si));
            }
            catch (InterruptedException exception)
            {
                // okay -- the pause() method interrupts the sleep
            }
            try
            {
                this.eventProducer.fireEvent(AnimatorInterface.UPDATE_ANIMATION_EVENT);
            }
            catch (RemoteException exception)
            {
                // ignore when event cannot be sent due to e.g., network problems
            }
        }
    }

    /**
     * Pause the updates.
     */
    public void pause()
    {
        this.running = false;
        // TODO: interrupt the sleep in the run() method
    }

    /**
     * Add another listener for the UPDATE_ANIMATION_EVENT.
     * @param listener EventListener; a panel that listen to this UpdaterThread
     * @throws RemoteException on network error for remote listener
     */
    void addListener(final EventListener listener) throws RemoteException
    {
        this.eventProducer.addListener(listener, AnimatorInterface.UPDATE_ANIMATION_EVENT);
    }

    /**
     * Return the update frequency.
     * @return Frequency; the update frequency
     */
    public Frequency getUpdateFrequency()
    {
        return this.updateFrequency;
    }

    /**
     * Set a new update frequency.
     * @param updateFrequency Frequency; the new update frequency
     */
    public void setUpdateFrequency(final Frequency updateFrequency)
    {
        this.updateFrequency = updateFrequency;
    }

    /** {@inheritDoc} */
    @Override
    public EventListenerMap getEventListenerMap() throws RemoteException
    {
        return this.eventProducer.getEventListenerMap();
    }

}
