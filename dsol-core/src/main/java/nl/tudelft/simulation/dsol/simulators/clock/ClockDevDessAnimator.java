package nl.tudelft.simulation.dsol.simulators.clock;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevDessAnimator;

/**
 * ClockDevDessAnimator is an extension of the ClockDevDessAnimator that works with Duration and ClockTime.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ClockDevDessAnimator extends DevDessAnimator<Duration> implements ClockDevsSimulatorInterface
{
    /** The absolute startTime of the simulator. */
    private ClockTime startTime;

    /**
     * Instantiate a ClockDevDessAnimator.
     * @param id the id of the simulator
     * @param initialTimeStep the initial time step to use in the integration
     * @param startTime the startTime of the simulator as a ClockTime
     * @throws SimRuntimeException when initialTimeStep &lt;= 0, NaN, or Infinity
     */
    public ClockDevDessAnimator(final Serializable id, final Duration initialTimeStep, final ClockTime startTime)
            throws SimRuntimeException
    {
        super(id, initialTimeStep);
        this.startTime = startTime;
    }

    @Override
    public ClockTime getStartClockTime()
    {
        return this.startTime;
    }

    @Override
    public void setStartClockTime(final ClockTime startTime)
    {
        Throw.when(isInitialized(), SimRuntimeException.class, "Start time cannot be set after initialization");
        this.startTime = startTime;
    }

}
