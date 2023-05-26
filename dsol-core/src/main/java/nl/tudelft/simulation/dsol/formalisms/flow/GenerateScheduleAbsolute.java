package nl.tudelft.simulation.dsol.formalisms.flow;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;

/**
 * The GenerateScheduleAbsolute is an extension to the Generate flow object which accepts a schedule of absolute inter-arrival
 * times. Instead of generating with a continuous interarrival distribution we submit a map consisting of keys (execution
 * times). Each key indicates the <i>starting time </i> of a new interval, while the value in the map is the continuous
 * distribution function to use to draw the interarrival times. If no values have to be generated in a certain interval, use a
 * large inter-arrival time value in the distribution function, or use DistConstant(stream, 1E20) to indicate that the next
 * drawing will take place <i>after </i> the end of the interval.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the time type
 */
public abstract class GenerateScheduleAbsolute<T extends Number & Comparable<T>> extends Generate<T>
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** absoluteSchedule is a time sorted map of inter-arrival distributions. */
    private SortedMap<T, DistContinuousSimulationTime<T>> absoluteSchedule =
            Collections.synchronizedSortedMap(new TreeMap<T, DistContinuousSimulationTime<T>>());

    /**
     * Construct a new generator for objects in a simulation. Constructed objects are sent to the 'destination' of the Generate
     * flow object when a destination has been indicated with the setDestination method.
     * @param id String; the id of the FlowObject
     * @param simulator DevsSimulatorInterface&lt;T&gt;; is the on which the construction of the objects must be scheduled.
     * @param absoluteSchedule SortedMap&lt;T, DistContinuousSimulationTime&lt;T&gt;&gt;; time sorted map of inter-arrival
     *            distributions
     * @param batchSize DistDiscrete; the distribution of the number of objects generated at each generation event
     */
    public GenerateScheduleAbsolute(final String id, final DevsSimulatorInterface<T> simulator,
            final SortedMap<T, DistContinuousSimulationTime<T>> absoluteSchedule, final DistDiscrete batchSize)
    {
        super(id, simulator, batchSize);
        setSchedule(absoluteSchedule);
    }

    /**
     * Return the remaining schedule.
     * @return SortedMap the schedule
     */
    public SortedMap<T, DistContinuousSimulationTime<T>> getRemainingSchedule()
    {
        return this.absoluteSchedule;
    }

    /**
     * Set a new schedule.
     * @param map SortedMap&lt;T, DistContinuousSimulationTime&lt;R&gt;&gt;; is the new map
     */
    public synchronized void setSchedule(final SortedMap<T, DistContinuousSimulationTime<T>> map)
    {
        this.absoluteSchedule.clear();
        this.absoluteSchedule.putAll(map);
        this.changeIntervalTime();
    }

    /**
     * Change the intervalTime of the schedule.
     */
    public synchronized void changeIntervalTime()
    {
        if (!this.absoluteSchedule.isEmpty())
        {
            setInterval(this.absoluteSchedule.values().iterator().next()); // schedules next generation
            this.absoluteSchedule.remove(this.absoluteSchedule.firstKey());
            this.simulator.scheduleEventAbs(this.absoluteSchedule.firstKey(), this, "changeIntervalTime", null);
        }
    }

}
