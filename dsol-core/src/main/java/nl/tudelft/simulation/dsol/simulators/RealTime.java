package nl.tudelft.simulation.dsol.simulators;

/**
 * RealTime is an interface for simulators that can run at a speed relative to the real time clock.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time unit
 */
public interface RealTime<T extends Number & Comparable<T>>
{
    /**
     * Calculate the how much simulation duration corresponds to the number of wall clock milliseconds indicated in the
     * parameter. When the DevsRealTimeClock works with djunits Time or Duration, and the simulation is scaled to milliseconds,
     * the simulatorTimeForWallClockMillis of a millisecond is a Duration of 1 millisecond. When 1 simulated time unit stands
     * for a second, the simulatorTimeForWallClockMillis is 0.001.
     * @param wallMilliseconds the number of milliseconds to calculate the corresponding simulation time for
     * @return the relative time step.
     */
    T simulatorTimeForWallClockMillis(final double wallMilliseconds);

    /**
     * Return the speed factor. A speed factor &lt;1 is slower than real time, &gt;1 is faster than real time, 1 is equal to
     * real time speed.
     * @return the speed factor of the simulation
     */
    double getSpeedFactor();

    /**
     * Set the speedFactor. A speed factor &lt;1 is slower than real time, &gt;1 is faster than real time, 1 is equal to real
     * time speed. Send a CHANGE_SPEED_FACTOR event when fireChangeSpeedFactorEvent is true.
     * @param newSpeedFactor the new speed factor of the simulation
     * @param fireChangeSpeedFactorEvent whether to fire a CHANGE_SPEED_FACTOR event or not
     */
    void setSpeedFactor(final double newSpeedFactor, final boolean fireChangeSpeedFactorEvent);

    /**
     * Set the speedFactor, and send a CHANGE_SPEED_FACTOR event. A speed factor &lt;1 is slower than real time, &gt;1 is faster
     * than real time, 1 is equal to real time speed.
     * @param newSpeedFactor the new speed factor of the simulation
     */
    default void setSpeedFactor(final double newSpeedFactor)
    {
        setSpeedFactor(newSpeedFactor, true);
    }

    /**
     * Return whether the simulation should catch up with the wall clock time (with a factor determined by the speed factor).
     * @return whether the simulation should catch up with the scaled wall clock time
     */
    boolean isCatchup();

    /**
     * Set whether the simulation should catch up with the wall clock time (with a factor determined by the speed factor).
     * @param catchup true when the simulation should catch up with the scaled wall clock time
     */
    void setCatchup(final boolean catchup);

    /**
     * The relative update delay in milliseconds is the step size in wall clock time by which the time is updated between
     * events. Default, this value is set at 10 msec, which means that the simulation updates its clock with 100 Hz between
     * events.
     * @return the relative update delay in milliseconds
     */
    int getUpdateMsec();

    /**
     * The relative update delay in milliseconds is the step size in wall clock time by which the time is updated between
     * events. Default, this value is set at 10 msec, which means that the simulation updates its clock with 100 Hz between
     * events. When this is too course, set e.g. to 1, which means that the clock will be updated with 1 kHz between events. The
     * latter can be important in real time simulations. Note that the housekeeping of the simulation clock takes time as well,
     * so 1 kHz can be too fine grained in some situations.
     * @param updateMsec set the relative update delay in milliseconds
     */
    void setUpdateMsec(final int updateMsec);

}
