package nl.tudelft.simulation.dsol.simulators.time;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevDessSimulator;

/**
 * TimeDevDessSimulator is an extension of the TimeDevDessSimulator that works with Duration and Time.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TimeDevDessSimulator extends DevDessSimulator<Duration> implements TimeDevsSimulatorInterface
{
    /** The absolute startTime of the simulator. */
    private final Time startTime;

    /**
     * Instantiate a TimeDevDessSimulator.
     * @param id the id of the simulator
     * @param initialTimeStep the initial time step to use in the integration
     * @param startTime the startTime of the simulator as a Time
     * @throws SimRuntimeException when initialTimeStep &lt;= 0, NaN, or Infinity
     */
    public TimeDevDessSimulator(final Serializable id, final Duration initialTimeStep, final Time startTime)
            throws SimRuntimeException
    {
        super(id, initialTimeStep);
        this.startTime = startTime;
    }

    @Override
    public Time getStartTime()
    {
        return this.startTime;
    }

}
