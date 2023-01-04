package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simtime.SimTime;

/**
 * The reference implementation of the realTimeClock. The realTime clock is a DEVS simulator which runs at a ratio of realTime.
 * If the executionTime exceeds the timeStep, a catchup mechanism can be triggered to make up lost time in consecutive steps.
 * <p>
 * Copyright (c) 2004-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public abstract class DEVSRealTimeAnimator<T extends Number & Comparable<T>> extends DEVSAnimator<T>
        implements DEVSSimulatorInterface<T>
{
    /** */
    private static final long serialVersionUID = 20150428L;

    // TODO: Fire the BACKLOG_EVENT when we are behind in the run thread
    /** the backlog event. */
    public static final EventType BACKLOG_EVENT =
            new EventType(new MetaData("BACKLOG_EVENT", "Real time simulation is behind"));

    /** the speed factor event. */
    public static final EventType CHANGE_SPEED_FACTOR_EVENT = new EventType(new MetaData("CHANGE_SPEED_FACTOR_EVENT",
            "Change speed factor", new ObjectDescriptor("newSpeedFactor", "New speed factor", Double.class)));

    /** the speed factor compared to real time clock. &lt;1 is slower, &gt;1 is faster, 1 is real time speed. */
    private double speedFactor = 1.0;

    /**
     * The relative update delay in milliseconds is the step size in wall clock time by which the time is updated between
     * events. Default, this value is set at 10 msec, which means that the simulation updates its clock with 100 Hz between
     * events. When this is too course, set e.g. to 1, which means that the clock will be updated with 1 kHz between events. The
     * latter can be important in real time simulations. Note that the housekeeping of the simulation clock takes time as well,
     * so 1 kHz can be too fine grained in some situations. This factor takes care that animation updates between events happen
     * fine grained and in small increments.
     */
    private int updateMsec = 10;

    /** catch up or not catch up after running behind. */
    private boolean catchup = true;

    /** Start an animation thread or not. */
    private Boolean animation = true;

    /** the current animation thread; null if none. */
    private AnimationThread animationThread = null;

    /**
     * Calculate the how much simulation duration corresponds to the number of wall clock milliseconds indicated in the
     * parameter. When the DEVSRealTimeClock works with djunits Time or Duration, and the simulation is scaled to milliseconds,
     * the simulatorTimeForWallClockMillis of a millisecond is a Duration of 1 millisecond. When 1 simulated time unit stands
     * for a second, the simulatorTimeForWallClockMillis is 0.001.
     * @param wallMilliseconds double; the number of milliseconds to calculate the corresponding simulation time for
     * @return the relative time step.
     */
    protected abstract T simulatorTimeForWallClockMillis(final double wallMilliseconds);

    /**
     * Constructs a new DEVSRealTimeClock.
     * @param id the id of the simulator, used in logging and firing of events.
     */
    public DEVSRealTimeAnimator(final Serializable id)
    {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({"checkstyle:designforextension", "checkstyle:methodlength"})
    public void run()
    {
        synchronized (this.animation)
        {
            if (this.animation)
            {
                this.animationThread = new AnimationThread(this);
                this.animationThread.start();
            }
        }

        // set the run flag semaphore to signal to startImpl() that the run method has started
        this.runflag = true;

        /* Baseline point for the wallclock time. */
        long wallTime0 = System.currentTimeMillis();

        /* Baseline point for the simulator time. */
        T simTime0 = SimTime.copy(this.simulatorTime);

        /* Speed factor is simulation seconds per 1 wallclock second. */
        double currentSpeedFactor = this.speedFactor;

        /* wall clock milliseconds per 1 simulation clock millisecond. */
        double msec1 = simulatorTimeForWallClockMillis(1.0).doubleValue();

        while (!isStoppingOrStopped() && !this.eventList.isEmpty() && this.simulatorTime.compareTo(this.runUntilTime) <= 0)
        {
            // check if speedFactor has changed. If yes: re-baseline.
            if (currentSpeedFactor != this.speedFactor)
            {
                wallTime0 = System.currentTimeMillis();
                simTime0 = SimTime.copy(this.simulatorTime);
                currentSpeedFactor = this.speedFactor;
            }

            // check if we are behind; wantedSimTime is the needed current time on the wall-clock
            double wantedSimTime = (System.currentTimeMillis() - wallTime0) * msec1 * currentSpeedFactor;
            double simTimeSinceBaseline = SimTime.minus(this.simulatorTime, simTime0).doubleValue();

            if (simTimeSinceBaseline < wantedSimTime)
            {
                // we are behind
                if (!this.catchup)
                {
                    // if no catch-up: re-baseline.
                    wallTime0 = System.currentTimeMillis();
                    simTime0 = SimTime.copy(this.simulatorTime);
                }
                else
                {
                    // jump to the required wall-clock related time or to the time of the next event, or to the runUntil time,
                    // whichever comes first
                    synchronized (super.semaphore)
                    {
                        T delta = simulatorTimeForWallClockMillis((wantedSimTime - simTimeSinceBaseline) / msec1);
                        T absSyncTime = SimTime.plus(this.simulatorTime, delta);
                        T eventOrUntilTime = this.eventList.first().getAbsoluteExecutionTime();
                        if (this.runUntilTime.compareTo(eventOrUntilTime) < 0)
                        {
                            eventOrUntilTime = this.runUntilTime;
                        }
                        if (absSyncTime.compareTo(eventOrUntilTime) < 0)
                        {
                            this.simulatorTime = SimTime.copy(absSyncTime);
                            fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                        }
                        else
                        {
                            this.simulatorTime = SimTime.copy(eventOrUntilTime);
                            fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                        }
                    }
                }
            }

            // peek at the first event and determine the time difference relative to RT speed; that determines
            // how long we have to wait.
            SimEventInterface<T> nextEvent = this.eventList.first();
            T nextEventOrUntilTime = nextEvent.getAbsoluteExecutionTime();
            boolean isRunUntil = false;
            if (this.runUntilTime.compareTo(nextEventOrUntilTime) < 0)
            {
                nextEventOrUntilTime = this.runUntilTime;
                isRunUntil = true;
            }
            double wallMillisNextEventSinceBaseline =
                    (nextEventOrUntilTime.doubleValue() - simTime0.doubleValue()) / (msec1 * currentSpeedFactor);

            // wallMillisNextEventSinceBaseline gives the number of milliseconds on the wall clock since baselining for the
            // expected execution time of the next event on the event list .
            if (wallMillisNextEventSinceBaseline >= (System.currentTimeMillis() - wallTime0))
            {
                while (wallMillisNextEventSinceBaseline > System.currentTimeMillis() - wallTime0)
                {
                    try
                    {
                        Thread.sleep(this.updateMsec);
                    }
                    catch (InterruptedException ie)
                    {
                        // do nothing
                        ie = null;
                        Thread.interrupted(); // clear the flag
                    }

                    // did we stop running between events?
                    if (isStoppingOrStopped())
                    {
                        wallMillisNextEventSinceBaseline = 0.0; // jump out of the while loop for sleeping
                        break;
                    }

                    // check if speedFactor has changed. If yes: rebaseline. Try to avoid a jump.
                    if (currentSpeedFactor != this.speedFactor)
                    {
                        // rebaseline
                        wallTime0 = System.currentTimeMillis();
                        simTime0 = SimTime.copy(this.simulatorTime);
                        currentSpeedFactor = this.speedFactor;
                        wallMillisNextEventSinceBaseline =
                                (nextEventOrUntilTime.doubleValue() - simTime0.doubleValue()) / (msec1 * currentSpeedFactor);
                    }

                    // check if an event has been inserted. In a real-time situation this can be done by other threads
                    if (!nextEvent.equals(this.eventList.first())) // event inserted by a thread...
                    {
                        nextEvent = this.eventList.first();
                        nextEventOrUntilTime = nextEvent.getAbsoluteExecutionTime();
                        isRunUntil = false;
                        if (this.runUntilTime.compareTo(nextEventOrUntilTime) < 0)
                        {
                            nextEventOrUntilTime = this.runUntilTime;
                            isRunUntil = true;
                        }
                        wallMillisNextEventSinceBaseline =
                                (nextEventOrUntilTime.doubleValue() - simTime0.doubleValue()) / (msec1 * currentSpeedFactor);
                    }

                    // make a small time step for the animation during wallclock waiting, but never beyond the next event
                    // time. Changed 2019-04-30: this is now recalculated based on latest system time after the 'sleep'.
                    synchronized (super.semaphore)
                    {
                        T nextEventSimTime = SimTime.copy(nextEventOrUntilTime);
                        T deltaToWall0inSimTime =
                                simulatorTimeForWallClockMillis((System.currentTimeMillis() - wallTime0) * currentSpeedFactor);
                        T currentWallSimTime = SimTime.plus(simTime0, deltaToWall0inSimTime);
                        if (nextEventSimTime.compareTo(currentWallSimTime) < 0)
                        {
                            if (nextEventSimTime.compareTo(this.simulatorTime) > 0) // don't go back in time
                            {
                                this.simulatorTime = SimTime.copy(nextEventSimTime);
                                fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                            }
                            wallMillisNextEventSinceBaseline = 0.0; // force breakout of the loop
                        }
                        else
                        {
                            if (currentWallSimTime.compareTo(this.simulatorTime) > 0) // don't go back in time
                            {
                                this.simulatorTime = SimTime.copy(currentWallSimTime);
                                fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                            }
                        }
                    }
                }
            }

            // only execute an event if we are still running, and if we do not 'run until'...
            if (isRunUntil)
            {
                this.simulatorTime = nextEventOrUntilTime;
            }
            else if (!isStoppingOrStopped())
            {
                synchronized (super.semaphore)
                {
                    if (nextEvent.getAbsoluteExecutionTime().compareTo(this.simulatorTime) != 0)
                    {
                        fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null,
                                nextEvent.getAbsoluteExecutionTime());
                    }
                    this.simulatorTime = SimTime.copy(nextEvent.getAbsoluteExecutionTime());

                    // carry out all events scheduled on this simulation time, as long as we are still running.
                    while (!isStoppingOrStopped() && !this.eventList.isEmpty()
                            && nextEvent.getAbsoluteExecutionTime().compareTo(this.simulatorTime) == 0)
                    {
                        nextEvent = this.eventList.removeFirst();
                        try
                        {
                            nextEvent.execute();
                            if (this.eventList.isEmpty())
                            {
                                this.simulatorTime = SimTime.copy(this.runUntilTime);
                                this.runState = RunState.STOPPING;
                                this.replicationState = ReplicationState.ENDING;
                                fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                                break;
                            }
                            int cmp = this.eventList.first().getAbsoluteExecutionTime().compareTo(this.runUntilTime);
                            if ((cmp == 0 && !this.runUntilIncluding) || cmp > 0)
                            {
                                this.simulatorTime = SimTime.copy(this.runUntilTime);
                                this.runState = RunState.STOPPING;
                                fireUnverifiedTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);
                                break;
                            }
                        }
                        catch (Exception exception)
                        {
                            handleSimulationException(exception);
                        }
                        if (!this.eventList.isEmpty())
                        {
                            // peek at next event for while loop.
                            nextEvent = this.eventList.first();
                            nextEventOrUntilTime = nextEvent.getAbsoluteExecutionTime();
                            isRunUntil = false;
                            if (this.runUntilTime.compareTo(nextEventOrUntilTime) < 0)
                            {
                                nextEventOrUntilTime = this.runUntilTime;
                                isRunUntil = true;
                            }
                        }
                    }
                }
            }
        }
        fireTimedEvent(SimulatorInterface.TIME_CHANGED_EVENT, null, this.simulatorTime);

        synchronized (this.animation)
        {
            if (this.animation && this.animationThread != null)
            {
                updateAnimation();
                this.animationThread.stopAnimation();
            }
        }
    }

    /**
     * Indicate whether we support animation or not.
     * @param animation boolean; whether we support animation or not
     */
    public void setAnimation(final boolean animation)
    {
        synchronized (this.animation)
        {
            if (this.animation == animation)
            {
                return; // nothing changed
            }
            if (this.animation)
            {
                if (this.animationThread != null)
                {
                    this.animationThread.stopAnimation();
                    this.animationThread = null;
                }
            }
            else
            {
                if (isStartingOrRunning())
                {
                    this.animationThread = new AnimationThread(this);
                    this.animationThread.start();
                }
            }
            this.animation = animation;
        }
    }

    /**
     * @return animation boolean; whether we support animation or not
     */
    public boolean isAnimation()
    {
        return this.animation;
    }

    /**
     * @return speedFactor
     */
    public double getSpeedFactor()
    {
        return this.speedFactor;
    }

    /**
     * Set the speedFactor, and send a CHANGE_SPEED_FACTOR event.
     * @param newSpeedFactor double; the new speed factor to set
     * @param fireChangeSpeedFactorEvent boolean; whether to fire a CHANGE_SPEED_FACTOR event or not
     */
    public void setSpeedFactor(final double newSpeedFactor, final boolean fireChangeSpeedFactorEvent)
    {
        this.speedFactor = newSpeedFactor;
        if (fireChangeSpeedFactorEvent)
        {
            this.fireEvent(CHANGE_SPEED_FACTOR_EVENT, newSpeedFactor);
        }
    }

    /**
     * Set the speedFactor, and send a CHANGE_SPEED_FACTOR event.
     * @param newSpeedFactor double; set speedFactor
     */
    public void setSpeedFactor(final double newSpeedFactor)
    {
        setSpeedFactor(newSpeedFactor, true);
    }

    /**
     * @return catchup
     */
    public boolean isCatchup()
    {
        return this.catchup;
    }

    /**
     * @param catchup boolean; set catchup
     */
    public void setCatchup(final boolean catchup)
    {
        this.catchup = catchup;
    }

    /**
     * The relative update delay in milliseconds is the step size in wall clock time by which the time is updated between
     * events. Default, this value is set at 10 msec, which means that the simulation updates its clock with 100 Hz between
     * events.
     * @return the relative update delay in milliseconds
     */
    public int getUpdateMsec()
    {
        return this.updateMsec;
    }

    /**
     * The relative update delay in milliseconds is the step size in wall clock time by which the time is updated between
     * events. Default, this value is set at 10 msec, which means that the simulation updates its clock with 100 Hz between
     * events. When this is too course, set e.g. to 1, which means that the clock will be updated with 1 kHz between events. The
     * latter can be important in real time simulations. Note that the housekeeping of the simulation clock takes time as well,
     * so 1 kHz can be too fine grained in some situations.
     * @param updateMsec int; set the relative update delay in milliseconds
     */
    public void setUpdateMsec(final int updateMsec)
    {
        this.updateMsec = updateMsec;
    }

    /***********************************************************************************************************/
    /************************************* EASY ACCESS CLASS EXTENSIONS ****************************************/
    /***********************************************************************************************************/

    /** Easy access class RealTimeClock&lt;Double&gt;. */
    public static class TimeDouble extends DEVSRealTimeAnimator<Double> implements DEVSSimulatorInterface<Double>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * the translation from a millisecond on the wall clock to '1.0' in the simulation time. This means that if the wall
         * clock runs in seconds, the factor should be 0.001.
         */
        private final double msecWallClockToSimTimeUnit;

        /**
         * Construct a DEVSRealTimeClock&lt;Double&gt;.
         * @param id the id of the simulator, used in logging and firing of events.
         * @param msecWallClockToSimTimeUnit double; the translation between a millisecond on the clock and '1.0' in the
         *            simulation time.
         */
        public TimeDouble(final Serializable id, final double msecWallClockToSimTimeUnit)
        {
            super(id);
            this.msecWallClockToSimTimeUnit = msecWallClockToSimTimeUnit;
        }

        /** {@inheritDoc} */
        @Override
        public Double simulatorTimeForWallClockMillis(final double wallMilliseconds)
        {
            return this.msecWallClockToSimTimeUnit * wallMilliseconds;
        }
    }

    /** Easy access class RealTimeClock&lt;Float&gt;. */
    public abstract static class TimeFloat extends DEVSRealTimeAnimator<Float> implements DEVSSimulatorInterface<Float>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * Construct a DEVSRealTimeClock&lt;Float&gt;.
         * @param id the id of the simulator, used in logging and firing of events.
         */
        public TimeFloat(final Serializable id)
        {
            super(id);
        }
    }

    /** Easy access class RealTimeClock&lt;Long&gt;. */
    public abstract static class TimeLong extends DEVSRealTimeAnimator<Long> implements DEVSSimulatorInterface<Long>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * Construct a DEVSRealTimeClock&lt;Long&gt;.
         * @param id the id of the simulator, used in logging and firing of events.
         */
        public TimeLong(final Serializable id)
        {
            super(id);
        }
    }

    /** Easy access class RealTimeClock&lt;Duration&gt;. */
    public static class TimeDoubleUnit extends DEVSRealTimeAnimator<Duration> implements DEVSSimulatorInterface<Duration>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * Construct a DEVSRealTimeClock&lt;Duration&gt;.
         * @param id the id of the simulator, used in logging and firing of events.
         */
        public TimeDoubleUnit(final Serializable id)
        {
            super(id);
        }

        /** {@inheritDoc} */
        @Override
        public Duration simulatorTimeForWallClockMillis(final double wallMilliseconds)
        {
            return new Duration(wallMilliseconds, DurationUnit.MILLISECOND);
        }
    }

    /** Easy access class RealTimeClock&lt;FloatDuration&gt;. */
    public static class TimeFloatUnit extends DEVSRealTimeAnimator<FloatDuration>
            implements DEVSSimulatorInterface<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * Construct a DEVSRealTimeClock&lt;FloatDuration&gt;.
         * @param id the id of the simulator, used in logging and firing of events.
         */
        public TimeFloatUnit(final Serializable id)
        {
            super(id);
        }

        /** {@inheritDoc} */
        @Override
        public FloatDuration simulatorTimeForWallClockMillis(final double wallMilliseconds)
        {
            return new FloatDuration((float) wallMilliseconds, DurationUnit.MILLISECOND);
        }
    }

}
