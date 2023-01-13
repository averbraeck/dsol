package nl.tudelft.simulation.dsol.formalisms.flow;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.logger.Cat;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The schedule is an extension to the generate which accepts a schedule of interarrival times. Instead of generating with a
 * continuous interarrival distribution we submit a map consiting of keys (execution times). Each key indicates the <i>starting
 * time </i> of a new interval, while the value in the map is the continuous distribution function to use to draw the
 * interarrival times. If no values have to be generated in a certain interval, use a large interarrival time value in the
 * distribution function, or use DistConstant(stream, 1E20) to indicate that the next drawing will take place <i>after </i> the
 * end of the interval.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public class Schedule<T extends Number & Comparable<T>> extends Generate<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /**
     * schedule is a time sorted map of distributions.
     */
    private SortedMap<T, DistContinuousSimulationTime<T>> schedule =
            Collections.synchronizedSortedMap(new TreeMap<T, DistContinuousSimulationTime<T>>());

    /**
     * constructs a new Schedule.
     * @param id Serializable; the id of the Station
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; is the on which the construction of the objects must be scheduled.
     * @param myClass Class&lt;?&gt;; is the class of which entities are created
     * @param constructorArguments Object[]; are the parameters for the constructor of myClass. of arguments.
     *            <code>constructorArgument[n]=Integer.valueOf(12)</code> may have constructorArgumentClasses[n]=int.class;
     * @throws SimRuntimeException on constructor invocation.
     */
    public Schedule(final Serializable id, final DevsSimulatorInterface<T> simulator, final Class<?> myClass,
            final Object[] constructorArguments) throws SimRuntimeException
    {
        super(id, simulator, myClass, constructorArguments);
    }

    /**
     * returns the schedule.
     * @return SortedMap the schedule
     */
    public SortedMap<T, DistContinuousSimulationTime<T>> getSchedule()
    {
        return this.schedule;
    }

    /**
     * sets the schedule.
     * @param map SortedMap&lt;T, DistContinuousSimulationTime&lt;R&gt;&gt;; is the new map
     */
    public synchronized void setSchedule(final SortedMap<T, DistContinuousSimulationTime<T>> map)
    {
        this.schedule = map;
        this.changeIntervalTime();
    }

    /**
     * changes the intervalTime of the schedule.
     */
    public synchronized void changeIntervalTime()
    {
        try
        {
            if (!this.schedule.isEmpty())
            {
                this.simulator.cancelEvent(super.nextEvent);
                this.interval = this.schedule.values().iterator().next();
                this.schedule.remove(this.schedule.firstKey());
                this.simulator.scheduleEvent(new SimEvent<T>(this.schedule.firstKey(), this,"changeIntervalTime", null));
                this.generate(this.constructorArguments);
                this.simulator.getLogger().filter(Cat.DSOL).trace("changeIntervalTime: set the intervalTime to {}",
                        this.interval);
            }
        }
        catch (Exception exception)
        {
            this.simulator.getLogger().always().warn(exception, "changeIntervalTime");
        }
    }

}
