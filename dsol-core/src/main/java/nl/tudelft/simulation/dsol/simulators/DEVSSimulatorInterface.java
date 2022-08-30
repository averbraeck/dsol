package nl.tudelft.simulation.dsol.simulators;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.eventlists.EventListInterface;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.Executable;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * The DEVS defines the interface of the DEVS simulator. DEVS stands for the Discrete Event System Specification. More
 * information on Discrete Event Simulation can be found in "Theory of Modeling and Simulation" by Bernard Zeigler et.al.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public interface DEVSSimulatorInterface<T extends Number & Comparable<T>> extends SimulatorInterface<T>
{
    /** The EVENTLIST_CHANGED_EVENT is fired when the eventList is changed. */
    EventType EVENTLIST_CHANGED_EVENT = new EventType(new MetaData("EVENTLIST_CHANGED_EVENT", "Eventlist changed"));

    /**
     * cancels an event from the event list.
     * @param event SimEventInterface&lt;T&gt;; a simulation event to be canceled.
     * @return boolean the succes of the operation.
     */
    boolean cancelEvent(SimEventInterface<T> event);

    /**
     * returns the eventlist of the simulator.
     * @return the eventlist.
     */
    EventListInterface<T> getEventList();

    /**
     * Method scheduleEvent schedules an event on the eventlist.
     * @param event SimEventInterface&lt;T&gt;; a simulation event
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever event is scheduled in past.
     */
    SimEventInterface<T> scheduleEvent(SimEventInterface<T> event) throws SimRuntimeException;

    /**
     * schedules a methodCall at a relative duration. The executionTime is thus simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay T; the relativeDelay in timeUnits of the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventRel(T relativeDelay, short priority, Object source, Object target, String method,
            Object[] args) throws SimRuntimeException;

    /**
     * schedules a methodCall at a relative duration. The executionTime is thus simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay T; the relativeDelay in timeUnits of the simulator.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventRel(T relativeDelay, Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /**
     * schedules a methodCall at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventAbs(T absoluteTime, short priority, Object source, Object target, String method,
            Object[] args) throws SimRuntimeException;

    /**
     * schedules a methodCall at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventAbs(T absoluteTime, Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /**
     * schedules a methodCall immediately.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventNow(short priority, Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /**
     * schedules a methodCall immediately.
     * @param source Object; the source of the event
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventNow(Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /**
     * schedules a lambda expression at a relative duration. The executionTime is thus
     * simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay T; the relativeDelay in timeUnits of the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventRel(T relativeDelay, short priority, Executable executable) throws SimRuntimeException;

    /**
     * schedules a lambda expression at a relative duration. The executionTime is thus
     * simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay T; the relativeDelay in timeUnits of the simulator.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventRel(T relativeDelay, Executable executable) throws SimRuntimeException;

    /**
     * schedules a lambda expression at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventAbs(T absoluteTime, short priority, Executable executable) throws SimRuntimeException;

    /**
     * schedules a lambda expression at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventAbs(T absoluteTime, Executable executable) throws SimRuntimeException;

    /**
     * schedules a lambda expression immediately.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventNow(short priority, Executable executable) throws SimRuntimeException;

    /**
     * schedules a lambda expression immediately.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    SimEventInterface<T> scheduleEventNow(Executable executable) throws SimRuntimeException;

    /**
     * Method setEventList sets the eventlist.
     * @param eventList EventListInterface&lt;T&gt;; the eventList for the simulator.
     * @throws SimRuntimeException whenever simulator.isRunning()==true
     */
    void setEventList(EventListInterface<T> eventList) throws SimRuntimeException;

    /**
     * DEPRECATED: Replaced by getErrorHandler() and getErrorLogLevel().
     * @return pauseOnError whether we pause on an error or not.
     */
    @Deprecated
    boolean isPauseOnError();

    /**
     * DEPRECATED: Replaced by setErrorHandler() and setErrorLogLevel(). Set the boolean whether we pause on an error or not.
     * @param pauseOnError boolean; set true or false.
     */
    @Deprecated
    void setPauseOnError(boolean pauseOnError);
}
