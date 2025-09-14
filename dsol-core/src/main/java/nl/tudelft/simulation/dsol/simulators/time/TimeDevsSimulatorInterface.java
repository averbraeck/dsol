package nl.tudelft.simulation.dsol.simulators.time;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.Executable;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * TimeSimulatorInterface is an interface to use the Time + Duration rather than a pure duration-based simulator. The Time
 * provides an absolute offset for the simulation Duration.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public interface TimeDevsSimulatorInterface extends DevsSimulatorInterface<Duration>
{
    /**
     * Return the start time of the simulator as a 'calendar-aware' Time.
     * @return the start time of the simulator as a 'calendar-aware' Time
     */
    Time getStartTime();

    /**
     * Return the simulator time as a 'Time' object rather than a 'Duration' object.
     * @return the simulator time as a 'Time' object rather than a 'Duration' object
     */
    default Time getSimulatorTimeAsTime()
    {
        return new Time(getStartTime().plus(getSimulatorTime()));
    }

    /**
     * schedules a lambda expression at an absolute time with normal priority.
     * @param time the exact time to schedule the method on the simulator.
     * @param executable the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time time, final Executable executable)
            throws SimRuntimeException
    {
        return scheduleEventAbs(time, SimEventInterface.NORMAL_PRIORITY, executable);
    }

    /**
     * schedules a lambda expression at an absolute time.
     * @param time the exact time to schedule the method on the simulator.
     * @param priority the priority compared to other events scheduled at the same time.
     * @param executable the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time time, final short priority, final Executable executable)
            throws SimRuntimeException
    {
        // avoid negative time due to rounding errors when subtracting
        if (time.eq(getSimulatorTimeAsTime()))
            return scheduleEventNow(priority, executable);
        return scheduleEventAbs(time.minus(getStartTime()), priority, executable);
    }

    /**
     * schedules a methodCall at an absolute time with normal priority.
     * @param time the exact time to schedule the method on the simulator.
     * @param target the target object to schedule the method on
     * @param method the method to execute at the time
     * @param args the arguments for the method call
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time time, final Object target, final String method,
            final Object[] args) throws SimRuntimeException
    {
        return scheduleEventAbs(time, SimEventInterface.NORMAL_PRIORITY, target, method, args);
    }

    /**
     * schedules a methodCall at an absolute time.
     * @param time the exact time to schedule the method on the simulator.
     * @param priority the priority compared to other events scheduled at the same time.
     * @param target the target object to schedule the method on
     * @param method the method to execute at the time
     * @param args the arguments for the method call
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time time, final short priority, final Object target,
            final String method, final Object[] args) throws SimRuntimeException
    {
        // avoid negative time due to rounding errors when subtracting
        if (time.eq(getSimulatorTimeAsTime()))
            return scheduleEventNow(priority, target, method, args);
        return scheduleEventAbs(time.minus(getStartTime()), priority, target, method, args);
    }

}
